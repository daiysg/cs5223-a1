import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Created by ydai on 16/9/17.
 * <p>
 * Refer to Peer
 */
public class Game implements IGame {

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

    private Boolean gameStart = false;

    private Integer numOfStep = 0;


    public Game(ITracker tracker, String playerId) throws RemoteException, AlreadyBoundException, NotBoundException {
        this.tracker = tracker;
        gameStart = false;
        gameList = new ArrayList<>();

        if (isMaster) {
            startGame(serverGameStatus);
        }
    }

    @Override
    public void askTrackerJoinGame() throws RemoteException {
        tracker.joinGame(this);
    }

    @Override
    public void ping() throws RemoteException {
        Logging.printInfo("Ping from master to Player: " + player.getId());
    }

    /**
     * slave call master
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
    public void assignNewSlave(GameStatus gameStatus) {

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
    private synchronized GameStatus reassignedGameStatusForNewSlave(int i) {
        List stillAvailGameList = gameList.subList(i, gameList.size());
        gameList = new ArrayList<>();
        gameList.add(this);
        gameList.addAll(stillAvailGameList);

        //update PlayerHashMap
        serverGameStatus.setPlayerHashMap(Utils.convertGameListToPlayerHashMap(gameList));

        //update Tracker Player List
        tracker.setServerList(gameList);
        return serverGameStatus;
    }

    //make slave become master
    @Override
    public synchronized void slaveBecomeMaster() throws RemoteException {
        if (isSlave) {
            isMaster = true;

            gameList = gameList.subList(1, gameList.size());
            serverGameStatus.setPlayerHashMap(Utils.convertGameListToPlayerHashMap(gameList));
            tracker.setServerList(gameList);
            assignNewSlave(serverGameStatus);
        }
    }

    @Override
    public synchronized List<IGame> addNewPlayer(Game game) throws RemoteException {

        //if the player is not master, it means tracker call wrong gamer
        if (!isMaster) {
            Logging.printError("Traker call wrong master to add new Player!!! Player ID + " + playerId);
            return null;
        }
        gameList.add(game);
        serverGameStatus.setPlayerHashMap(Utils.convertGameListToPlayerHashMap(gameList));
        serverGameStatus.prepareForNewPlayer(game.getPlayer());
        if (gameList.size() == 2) {
            game.setSlave(true);
        }

        game.setGameStart(true);
        game.startGame(serverGameStatus);
        return gameList;
    }

    @Override
    public void startGame(GameStatus gameStatus) throws RemoteException {
        this.gameInputThread = new Thread() {
            public void run() {
                while (gameStart) {
                    try {
                        Logging.printInfo("Game start for player :" + playerId);
                        movePlayerInput();
                    } catch (Exception e) {
                    }
                }
            }
        };
        this.gameInputThread.start();
    }

    private void movePlayerInput() throws InterruptedException, IOException, WrongGameException {
        if (!gameStart) {
            Thread.sleep(100);
            return;
        }

        Logging.printInfo("Prepare for user input for move. Player :" + playerId);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input;
        do {
            try {
                while (!br.ready()) {
                    Thread.sleep(200);
                }
                input = br.readLine();
            } catch (InterruptedException e) {
                return;
            }
        } while ("".equals(input));
        String move = input.replaceAll("\n", "");
        Direction direction = Direction.getDirection(move);

        //Ask for player move
        IGame master = getMaster();

        try {
            GameStatus gameStatus = master.move(this.playerId, direction, numOfStep);
            this.serverGameStatus = gameStatus;
            numOfStep++;
        } catch (Exception ex) {
            //Master Failed, call slave
            gameList = gameList.subList(1, gameList.size()); //remove failed master
            IGame slave = getSlave();
            try {
                if (slave != null) {
                    slave.move(playerId, direction, numOfStep);
                    numOfStep++;
                } else {
                    Logging.printError("No master No slave, move failed. Need to get need GameList from Tracker");
                    gameList = tracker.getServerList();
                }
            } catch (Exception e) {
                Logging.printError("No master No slave, move failed. Need to get need GameList from Tracker");
                gameList = tracker.getServerList();
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

        serverGameStatus.movePlayer(playerId, direction, numOfStep);

        IGame slave = this.getSlave();
        if (slave != null) {
            //update slave game status
            try {
                slave.updateGameStatus(serverGameStatus);
            } catch (Exception ex) {
                //slave not valie, need to assign new slave;
                assignNewSlave(serverGameStatus);
            }

        }

        return serverGameStatus;
    }


    //TODO: QUIT GAME
    private void quitGame(String playerId) {
    }


    /**
     * Heartbeat to players
     */
    private void pingAllPlayer() {

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

    /**
     * From master to update player and gamer status
     *
     * @param iGame
     */
    private synchronized void removeFailedGamer(IGame iGame) {
        //remove Failed Player
        serverGameStatus.getPlayerHashMap().remove(iGame.getId());
        return;
    }

    private IGame getMaster() throws WrongGameException {
        if (isMaster) {
            return this;
        } else if (gameList.size() == 0) {
            throw new WrongGameException("No valid master");
        } else {
            //first is Master
            return gameList.get(0);
        }
    }


    private IGame getSlave() throws WrongGameException {
        if (isSlave) {
            return this;
        } else if (gameList.size() == 0) {
            return null;
        } else {
            //first is Slave
            return gameList.get(0);
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
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setServerGameStatus(GameStatus serverGameStatus) {
        this.serverGameStatus = serverGameStatus;
    }

    public void setGameStart(Boolean gameStart) {
        this.gameStart = gameStart;
    }
}


