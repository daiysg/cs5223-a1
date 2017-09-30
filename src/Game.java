import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

/**
 * Created by ydai on 16/9/17.
 * <p>
 * Refer to Peer
 */
public class Game implements IGame, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

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
    private GameStatus serverGameStatus;

    //timer for ping
    protected Timer pingTimer;

    /**
     * Connected tracker
     */
    private ITracker tracker;

    /**
     * The thread responsible for prompting the user for input.
     * (This is run separately so as not to block other operations.)
     */
    private Thread gameInputThread;

    /**
     * The thread is used by Master player to ping all players
     */

    private Thread masterPingThread;

    private Boolean gameStart = false;

    private Integer numOfStep = 0;


    public Game(ITracker tracker, String playerId) throws RemoteException, AlreadyBoundException, NotBoundException {
        this.tracker = tracker;
        gameStart = false;
        gameList = new ArrayList<>();
        serverGameStatus = null;
        this.playerId = playerId;
        askTrackerJoinGame();
        askMasterToJoinGame();
        startGameThread();
    }

    private void askMasterToJoinGame() throws RemoteException {

        Logging.printInfo("Current Number of Players " + gameList.size());
        IGame master = gameList.get(0);

        if (isMaster) {
            //prepare for master start
            initGameStatus();
            serverGameStatus.prepareForNewPlayer(playerId);
            startGame(serverGameStatus);
        } else {
            master.addNewPlayer(this);
        }

    }

    @Override
    public synchronized void askTrackerJoinGame() throws RemoteException {
        gameList = tracker.joinGame(this);
    }

    @Override
    public void ping() throws RemoteException {
        Logging.printInfo("Ping from master to Player: " + playerId);
    }

    /**
     * Slave ping Master to check whether Master is still alive
     */
    @Override
    public void pingMaster() throws RemoteException, WrongGameException {

        IGame master = getMaster();
        try {
            if (master != this) {
                master.ping();
            }
        } catch (Exception ex) {
            Logging.printInfo("Master Failed, slave become master!! PlayerId " + playerId);
            slaveBecomeMaster();
        }

        Logging.printInfo("Ping to Master: " + playerId);
    }

    @Override
    public synchronized void updateGameStatus(GameStatus gameStatus) throws RemoteException {
        this.serverGameStatus = gameStatus;
        Logging.printInfo("Update Game Status to Player: " + playerId);
    }

    /**
     * Master assign new slave if slave down
     */
    @Override
    public synchronized void assignNewSlave(GameStatus gameStatus) throws RemoteException {

        //only Master can assign new Slave
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

    //assign game status to new Slave
    private synchronized GameStatus reassignedGameStatusForNewSlave(int i) throws RemoteException {
        List stillAvailGameList = gameList.subList(i, gameList.size());
        gameList = new ArrayList<>();
        gameList.add(this);
        gameList.addAll(stillAvailGameList);

        //update PlayerHashMap
        gameStatusUpdatePlayerList();

        //update Tracker Player List
        tracker.setServerList(gameList);
        return serverGameStatus;
    }

    //make slave become master
    @Override
    public synchronized void slaveBecomeMaster() throws RemoteException {
        if (isSlave) {
            isSlave = false;
            isMaster = true;

            gameList = gameList.subList(1, gameList.size());
            serverGameStatus.updatePlayerList(gameList.stream().map(gamer -> gamer.getId()).collect(Collectors.toList()));
            tracker.setServerList(gameList);
            try {
                assignNewSlave(serverGameStatus);
            } catch (Exception e) {
                Logging.printError("Failed to assign new Slave. ");
            }
        }

    }

    @Override
    public synchronized boolean addNewPlayer(IGame game) throws RemoteException {

        //if the player is not master, it means tracker call wrong gamer
        if (!isMaster) {
            Logging.printError("Call wrong master to add new Player!!! Player ID + " + playerId);
            return false;
        }
        gameList.add(game);

        //gameList = 1 means need to init game status for master
        if (gameList.size() == 1) {
            Logging.printInfo("Master init game status, player ID:" + game.getId());
            initGameStatus();
        }
        serverGameStatus.prepareForNewPlayer(game.getId());

        gameStatusUpdatePlayerList();

        if (gameList.size() == 2) {
            game.setSlave(true);
        }

        game.startGame(serverGameStatus);
        return true;
    }

    private void initGameStatus() throws RemoteException {
        int n = tracker.getN();
        int k = tracker.getK();

        serverGameStatus = new GameStatus(n, k);
    }

    @Override
    public synchronized void startGame(GameStatus gameStatus) throws RemoteException {
        this.serverGameStatus = gameStatus;
        this.gameStart = true;
    }


    public synchronized void startGameThread() throws RemoteException {

        Logging.printInfo("Start game Thread for player" + playerId);

        this.gameInputThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        movePlayerInput();
                    } catch (Exception e) {

                    }
                }
            }
        };
        this.gameInputThread.start();
    }

    private void movePlayerInput() throws InterruptedException, IOException, WrongGameException {


        if (!gameStart || serverGameStatus == null) {
            Thread.sleep(100);
            return;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        do {
            try {
                while (!br.ready()) {
                    Thread.sleep(200);
                }
                input = br.readLine();
                Logging.printInfo("Waiting for your input for Player :" + playerId);
            } catch (InterruptedException e) {
                return;
            }
        } while ("".equals(input));
        String move = input.replaceAll("\n", "");
        Direction direction = Direction.getDirection(move);

        //Player sends move request to Master
        Logging.printInfo("Your input Direction:" + direction.getDirecton() + " for player ID " + playerId);
        //Ask for player move
        IGame master = getMaster();
        GameView.printGameSummary(serverGameStatus, playerId, getMaster().getId());
        try {
            GameStatus gameStatus = master.move(this.playerId, direction, numOfStep);
            this.serverGameStatus = gameStatus;

            Logging.printInfo("Your move is finished :" + direction.getDirecton() + " for player ID " + playerId);
            numOfStep++;
        } catch (Exception ex) {
            //Master Failed, Slave becomes new Master
            Logging.printInfo("Master is down.");
            IGame newMaster = this.getSlave();
            if (newMaster != null) {
                //update slave game status
                newMaster.slaveBecomeMaster();
                GameStatus gameStatus = newMaster.move(this.playerId, direction, numOfStep);
                this.serverGameStatus = gameStatus;
                numOfStep++;
            }
        }
    }

    @Override
    public synchronized GameStatus move(String playerId, Direction direction, int numOfStep) throws RemoteException, WrongGameException {
        if (this.isMaster == false) {
            throw new WrongGameException("I am Not Master, please do not call me dude....");
        }

        if (direction == Direction.QUIT) {
            quitGame(playerId);
        }

        Logging.printInfo("Player ID " + playerId + " is asking for Move. Direction:" + direction + " master:" + this.playerId);

        serverGameStatus.movePlayer(playerId, direction, numOfStep);

        IGame slave = this.getSlave();
        if (slave != null) {
            //update slave game status
            try {
                slave.updateGameStatus(serverGameStatus);
            } catch (Exception ex) {
                //slave is down, need to assign new slave;
                assignNewSlave(serverGameStatus);
            }

        }

        Logging.printInfo("Player ID " + playerId + " move is finished!!. Direction:" + direction + " master:" + this.playerId);

        return serverGameStatus;
    }


    //TODO: QUIT GAME
    private void quitGame(String playerId) throws WrongGameException, RemoteException {
        if (isMaster) {
            if (gameList.size() == 1) {
                serverGameStatus.playerQuit(playerId);
                for (Iterator<IGame> iter = gameList.listIterator(); iter.hasNext(); ) {
                    iter.next();
                    iter.remove();
                }
                tracker.setServerList(gameList);
            } else {
                // when master wants to quit the game, assign slave as master
                IGame newMaster = this.getSlave();
                if (newMaster != null) {
                    //update slave game status
                    try {
                        newMaster.slaveBecomeMaster();
                        newMaster.getServerGameStatus().playerQuit(playerId);
                    } catch (Exception ex) {

                    }
                }
            }
        } else if (isSlave) {
            // slave wants to quit the game
            IGame master = this.getMaster();
            master.getServerGameStatus().playerQuit(playerId);
            master.assignNewSlave(serverGameStatus);
        } else {
            // a normal player wants to quit the game
            IGame master = this.getMaster();
            master.getServerGameStatus().playerQuit(playerId);
        }
    }


    /**
     * Heartbeat to players
     */
    private void pingAllPlayer() throws RemoteException {

        List<IGame> updatedGameList = new ArrayList<>();

        if (isMaster) {

            IGame slave = gameList.get(1);
            try {
                slave.ping();
            } catch (RemoteException ex) {
                removeFailedGamer(slave);
                assignNewSlave(serverGameStatus);
            }


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

        if (gameList.size() != updatedGameList.size()) {
            gameList = updatedGameList;
            tracker.setServerList(gameList);
        }
    }

    private void gameStatusUpdatePlayerList() {
        serverGameStatus.updatePlayerList(gameList.stream().map(gamer -> gamer.getId()).collect(Collectors.toList()));
    }


    /**
     * From master to update player and gamer status
     *
     * @param iGame
     */
    private synchronized void removeFailedGamer(IGame iGame) {
        //remove Failed Player
        serverGameStatus.playerQuit(iGame.getId());
        return;
    }

    private IGame getMaster() throws WrongGameException {
        if (isMaster) {
            return this;
        } else {
            //first is Master
            for (int i = 0; i < gameList.size(); i++) {
                if (gameList.get(i).getIsMaster()) {
                    return gameList.get(i);
                }
            }
            throw new WrongGameException("No valid master");
        }
    }


    private IGame getSlave() throws WrongGameException {
        if (isSlave) {
            return this;
        } else {
            //second is slave
            for (int i = 0; i < gameList.size(); i++) {
                if (gameList.get(i).getIsSlave()) {
                    return gameList.get(i);
                }
            }
            return null;
        }
    }

    @Override
    public void setSlave(Boolean slave) {
        isSlave = slave;
    }

    @Override
    public String getId() {
        return playerId;
    }

    @Override
    public boolean getIsMaster() {
        return isMaster;
    }

    @Override
    public boolean getIsSlave() {
        return isSlave;
    }

    @Override
    public void setServerGameStatus(GameStatus serverGameStatus) {
        this.serverGameStatus = serverGameStatus;
    }

    @Override
    public void setGameStart(Boolean gameStart) {
        this.gameStart = gameStart;
    }

    @Override
    public GameStatus getServerGameStatus() {
        return serverGameStatus;
    }

    @Override
    public void setMaster(Boolean master) {
        isMaster = master;
    }


}


