import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * Created by ydai on 16/9/17.
 * <p>
 * Refer to Peer
 */
public class Game implements IGame {

    // Host and Port when create Igame
    private String host;
    private int port;

    //playerId which is the name of this game
    private String playerId;

    /**
     * An ordered list of all gamer in the game
     * Please note that MASTER_SERVER_INDEX is Master and SLAVE_SERVER_INDEX is Slave
     */
    private List<IGame> gameList;

    private Boolean isMaster = false;

    private Boolean isSlave = false;

    //shared info
    //add synchronize if change
    protected GameStatus serverGameStatus;

    //timer for pingh
    protected Timer pingTimer;

    /**
     * Associated Player
     */
    private Player player;

    /**
     * Connected tracker
     */
    private ITracker tracker;

    /**
     * The thread responsible for prompting the user for input.
     * (This is run separately so as not to block other operations.)
     */
    private Thread gameInputThread;

    @Override
    public void ping() throws RemoteException {
        Logging.printInfo("Ping from master to Player: " + player.getId());
    }

    /**
     * slave call master
     */
    @Override
    public void pingMaster() throws RemoteException {
        Logging.printInfo("Ping to Master: " + player.getId());
    }

    @Override
    public synchronized void updateGameStatus(GameStatus gameStatus) throws RemoteException {
        this.serverGameStatus = gameStatus;
        Logging.printInfo("Update Game Status to Player: " + player.getId());
    }

    /**
     * Master assign new slave if slave down
     */
    @Override
    public void assignNewSlave(GameStatus gameStatus) throws RemoteException {

        if (isMaster) {
            int i = 1;
            // for loop for first slave with response
            for (; i < gameList.size(); i++) {
                IGame iGame = gameList.get(i);
                try {
                    iGame.ping();
                    //found slave, update slave to be second of all game list
                    gameStatus = reassignedGameStatusForNewSlave(i);
                    iGame.updateGameStatus(gameStatus);
                    iGame.setSlave(true);
                    return;
                } catch (RemoteException e) {
                    //error for one gamer
                    Logging.printError("One Gamer down" + iGame.getId());
                    removeFailedGamer(iGame);
                }
            }

            //not found slave, need to get whole player List again and retry
            gameList = tracker.getServerList();
            assignNewSlave(gameStatus);

        }
    }

    private GameStatus reassignedGameStatusForNewSlave(int i) {
        List stillAvailGameList = gameList.subList(i, gameList.size());
        gameList = new ArrayList<>();
        gameList.add(this);
        gameList.addAll(stillAvailGameList);

        //update PlayerHashMap
        serverGameStatus.setPlayerHashMap( Utils.convertGameListToPlayerHashMap(gameList));
        return serverGameStatus;
    }

    //make slave become master
    @Override
    public void slaveBecomeMaster() throws RemoteException {
        if (isSlave) {
            isMaster = true;

            gameList = gameList.subList(1, gameList.size());
            serverGameStatus.setPlayerHashMap( Utils.convertGameListToPlayerHashMap(gameList));
            tracker.setServerList(gameList);
            assignNewSlave(serverGameStatus);
        }
    }

    @Override
    public List<Player> addNewPlayer(Game game) throws RemoteException {
        return null;
    }

    @Override
    public void startGame(GameStatus gameStatus) throws RemoteException {

    }

    @Override
    public GameStatus move(String playerId, Direction direction, int numOfStep) throws RemoteException {
        return null;
    }

    /**
     * Heartbeat to players
     */
    private void pingAllPlayer() {

        List<IGame> updatedGameList = new ArrayList<>();

        if (isMaster) {

            IGame slave = gameList

            for (IGame iGame : gameList.subList(2, gameList.size())) {
                try {
                    iGame.ping();
                    updatedGameList.add(iGame);
                } catch (RemoteException e) {
                    //error for one gamer
                    Logging.printError("One Gamer down" + iGame.getId());
                    removeFailedGamer(iGame);
                }
            }
        }

        gameList = updatedGameList;
    }

    /**
     * From master to update player and gamer status
     *
     * @param iGame
     */
    private void removeFailedGamer(IGame iGame) {
        //remove Failed Player
        serverGameStatus.getPlayerHashMap().remove(iGame.getId())
    }


    @Override
    public void setSlave(Boolean slave) {
        isSlave = slave;
    }

    @Override
    public String getId() {
        return playerId
    }

    @Override
    public Player getPlayer() {
        return player;
    }

}
