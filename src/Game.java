import sun.rmi.runtime.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;


public class Game extends UnicastRemoteObject implements IGame, Serializable {


    private static final long serialVersionUID = 3L;

    //playerId which is the name of this game
    private String playerId;

    @Override
    public void setGameList(List<IGame> gameList) {
        this.gameList = gameList;
    }

    /**
     * An ordered list of all gamer in the game
     * Please note that index 0 is Master and index 1 is Slave
     */
    private List<IGame> gameList;

    private Map<IGame, String> iGamePlayerIdMap;

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
     * The thread is used by Master to ping all players
     */
    private Thread masterPingThread;

    /**
     * The thread is used by Slave to ping Master
     */
    private Thread slavePingThread;

    private Boolean gameStart = false;

    private Integer numOfStep = 0;

    private Boolean falseQuit = false;

    private String host;
    private int port;

    public Game(String host, int port, String playerId) throws RemoteException, AlreadyBoundException, NotBoundException {
        this.gameStart = false;
        this.gameList = new ArrayList<>();
        this.iGamePlayerIdMap = new HashMap<>();
        this.serverGameStatus = null;
        this.host = host;
        this.port = port;
        this.playerId = playerId;
    }

    public void connectToTracker(ITracker tracker) throws RemoteException, NotBoundException, MalformedURLException, WrongGameException, InterruptedException {
        this.tracker = tracker;
        askTrackerJoinGame();
        askMasterToJoinGame();
        startGameThread();

        if (isMaster)
        {
            startMasterPingThread();
        }else if (isSlave)
        {
            startSlavePingThread();
        }
    }

    private void askMasterToJoinGame() throws RemoteException, MalformedURLException, NotBoundException, WrongGameException, InterruptedException {

        Logging.printInfo("Current Number of Players " + gameList.size());

        if (isMaster) {
            //prepare for master start
            initGameStatus();
            serverGameStatus.prepareForNewPlayer(playerId);
            startGame(serverGameStatus);

        } else {
            try {
                IGame master = gameList.get(0);
                master.addNewPlayer(this.playerId);
            } catch (Exception ex) {
                gameList = tracker.getServerList();
                IGame master = gameList.get(0);
                master.addNewPlayer(this.playerId);
            }
        }

        updateIGamePlayerIdMap(gameList);

        if (getSlave() == null)
            GameView.printGameSummary(serverGameStatus, playerId, getMaster().getId(), "");
        else
            GameView.printGameSummary(serverGameStatus, playerId, getMaster().getId(), getSlave().getId());
    }

    @Override
    public synchronized void askTrackerJoinGame() throws RemoteException, NotBoundException, MalformedURLException {
        initGameStatus();
        //try and retry for 3 times
        try {
            this.gameList = tracker.joinGame(host, port, playerId);
        } catch (Exception ex) {
            try {
                this.gameList = tracker.joinGame(host, port, playerId);
            } catch (Exception e) {
                try {
                    this.gameList = tracker.joinGame(host, port, playerId);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    return;
                }
            }
        }

        updateIGamePlayerIdMap(gameList);


        if (gameList.size() == 1) {
            isMaster = true;
        } else if (gameList.size() == 2) {
            isSlave = true;
        }
    }

    @Override
    public void ping() throws RemoteException {
        return;
    }

    private synchronized void updateIGamePlayerIdMap(List<IGame> gameList){

        List<IGame> newList = gameList;

        iGamePlayerIdMap.clear();

        for (IGame iGame : newList)
        {
            try {
                iGamePlayerIdMap.put(iGame,iGame.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        printPlayerIds();
    }

    private void printPlayerIds(){
        Logging.printDebug("printPlayerIds: gameList.size() = " + gameList.size());
        if (iGamePlayerIdMap.isEmpty())
        {
            Logging.printDebug("No playerId has been stored in iGamePlayerIdMap.");
            return;
        }

        Set<IGame> keys = iGamePlayerIdMap.keySet();
        int i = 0;
        for (IGame iGame : keys)
        {
            i++;
            Logging.printDebug("playerId " + i + ": " + iGamePlayerIdMap.get(iGame));
        }

    }

    /**
     * Master to ping all players to check whether they are still alive
     */
//    @Override
    private synchronized void pingAllPlayers() throws RemoteException, WrongGameException, InterruptedException {

        if (!gameStart || serverGameStatus == null) {
            Thread.sleep(100);
            return;
        }

        // Only Master can ping all players
        if (!isMaster) {
            Logging.printError("Wrong Master to ping all players!!! playerId = " + playerId);
            return;
        }


        if (gameList.size() == 1){
            // Master is the only player
            return;
        }


        if (gameList.size() >= 2){
            IGame slave = gameList.get(1);
            String slaveId = iGamePlayerIdMap.get(slave);
            try {
//                Logging.printDebug("Ping from Master to Slave: " + slaveId);
                slave.ping();
                Thread.sleep(500); //TODO: to calculate the interval
            } catch (Exception ex) {
                Logging.printDebug("Ping from Master to Slave failed! slaveId = " + slaveId);
                removeFailedGamer(slaveId);
                this.gameList.remove(slave);
                updateIGamePlayerIdMap(gameList);
                tracker.setServerList(new ArrayList<>(gameList));
                assignNewSlave(serverGameStatus);
                return;
//                Thread.sleep(500); //TODO: wait for new Slave to get ready
            }
        }

        if (gameList.size() > 2){
            for (IGame iGame : gameList.subList(2, gameList.size())) {
                String gameId = iGamePlayerIdMap.get(iGame);
                try {
//                    Logging.printDebug("Ping from Master to Player: " + gameId);
                    iGame.ping();
                    Thread.sleep(500); //TODO: to calculate the interval
                } catch (RemoteException e) {
                    Logging.printDebug("Ping from Master to Player failed! playerId = " + gameId);
                    Logging.printInfo("Player " + gameId + " is down.");
                    removeFailedGamer(gameId);
                    this.gameList.remove(iGame);
                    updateIGamePlayerIdMap(gameList);
                    tracker.setServerList(new ArrayList<>(gameList));
                    return;
                }
            }
        }
        
    }

    /**
     * Slave to ping Master to check whether Master is still alive
     */
    @Override
    public void pingMaster() throws RemoteException, WrongGameException, InterruptedException {

        // Only Slave can ping Master
        if (!isSlave) {
            Logging.printError("Wrong Slave to ping Master!!! playerId = " + playerId);
            return;
        }

        IGame master = getMaster();
        try {
            if (master != this) {
                Logging.printDebug("Ping from Slave (" + playerId + ") to Master (" + master.getId() + ")");
                master.ping();
                //Slave to ping Master every 0.5 sec
                Thread.sleep(500);
            }
        } catch (Exception ex) {

            Logging.printInfo("Master Failed, Slave become Master!! playerId " + playerId);
            slaveBecomeMaster(master.getId());
        }
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
            gameList = tracker.getServerList();
            updateIGamePlayerIdMap(gameList);

            // the 1st player responses will be the new Slave
            while (i < gameList.size()) {
                IGame iGame = gameList.get(i);
                String iGameId = iGame.getId();
                try {
                    iGame.ping();

                    //found new Slave, update it with current GameStatus
//                    gameStatus = reassignedGameStatusForNewSlave(i);
//                    iGame.updateGameStatus(gameStatus);

                    gameStatusUpdatePlayerList();
                    iGame.updateGameStatus(serverGameStatus);

                    iGame.setSlave(true);
                    gameList = tracker.setServerList(gameList);
                    updateIGamePlayerIdMap(gameList);
                    return;
                } catch (RemoteException e) {
                    //error for one gamer
                    Logging.printError("One player is down, playerId = " + iGameId);
                    removeFailedGamer(iGameId);
                    gameList = tracker.getServerList();
                    gameList.remove(iGame);
                    gameList = tracker.setServerList(new ArrayList<>(gameList));
                    updateIGamePlayerIdMap(gameList);
                }
            }

            //not found slave, need to get whole player List again and retry
            gameList = tracker.getServerList();
            updateIGamePlayerIdMap(gameList);

            if (gameList.size() > 2) {
                assignNewSlave(gameStatus);
            } else {
                Logging.printInfo("ONLY ONE PLAYER, NO Slave!!");
            }
        }
    }

    //assign game status to new Slave
/*    private synchronized GameStatus reassignedGameStatusForNewSlave(int i) throws RemoteException {
        List stillAvailGameList = gameList.subList(i, gameList.size());
        gameList = new ArrayList<>();
        gameList.add(this);
        gameList.addAll(stillAvailGameList);

        //update PlayerHashMap
        gameStatusUpdatePlayerList();

        //update Tracker Player List
        tracker.setServerList(new ArrayList<>(gameList));
        return serverGameStatus;
    }*/

    //make Slave the new Master
    @Override
    public synchronized void slaveBecomeMaster(String originalMasterPlayerId) throws RemoteException {
        if (isSlave) {
            isSlave = false;
            isMaster = true;

            gameList = tracker.getServerList(); //serverList from the Tracker might be outdated now.

            try {
                if (originalMasterPlayerId != null) {
                    IGame originalMaster = Utils.connectToGame(host, port, originalMasterPlayerId);
                    gameList.remove(originalMaster);
                }
            } catch (Exception ex) {
                Logging.printInfo("Original Master is already removed, can ignore this exception");
            }
            gameStatusUpdatePlayerList();
            gameList = tracker.setServerList(new ArrayList<>(gameList));
            updateIGamePlayerIdMap(gameList);

            //stop the thread used by old Slave to ping Master
            if (this.slavePingThread != null) {
                this.slavePingThread.interrupt();
            }

            //start the thread used by the new Master to ping all players
//            this.startMasterPingThread();

            try {
                assignNewSlave(serverGameStatus);
            } catch (Exception e) {
                Logging.printError("Failed to assign new Slave. ");
            }
        }
    }

    @Override
    public synchronized boolean addNewPlayer(String playerId) throws RemoteException, MalformedURLException, NotBoundException, InterruptedException {

        //if the player is not master, it means tracker call wrong gamer
        if (!isMaster) {
            Logging.printError("Call wrong master to add new Player!!! Player ID + " + playerId);
            return false;
        }

        IGame game = Utils.connectToGame(host, port, playerId);
        gameList.add(game);
        updateIGamePlayerIdMap(gameList);

        //gameList = 1 means need to init game status for master
        if (gameList.size() == 1) {
            Logging.printInfo("Master init game status, player ID:" + playerId);
            initGameStatus();
        }
        serverGameStatus.prepareForNewPlayer(playerId);

        if (gameList.size() == 2) {
            game.setSlave(true);
        }

        gameStatusUpdatePlayerList();
        updateIGamePlayerIdMap(gameList);

        game.startGame(serverGameStatus);
        tracker.setServerList(gameList);
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

        Logging.printInfo("Start game Thread for player " + playerId);

        this.gameInputThread = new Thread() {
            public void run() {
                while (!falseQuit) {
                    try {
                        movePlayerInput();
                    } catch (Exception e) {

                    }
                }
            }
        };
        this.gameInputThread.start();
    }

    public synchronized void startMasterPingThread() throws RemoteException {

        Logging.printInfo("Start masterPingThread for player " + playerId);

        // Only Master can ping all players
        if (!isMaster) {
            Logging.printError("Wrong Master to ping all players!!! playerId = " + playerId);
            return;
        }

        this.masterPingThread = new Thread() {
            public void run() {
                while (!falseQuit) {
                    try {
                        pingAllPlayers();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        this.masterPingThread.start();
    }


    public synchronized void startSlavePingThread() throws RemoteException {

        Logging.printInfo("Start slavePingThread for player " + playerId);

        // Only Slave can ping Master
        if (!isSlave) {
            Logging.printError("Wrong Slave to ping Master!!! playerId = " + playerId);
            return;
        }

        this.slavePingThread = new Thread() {
            public void run() {
                while (!falseQuit) {
                    try {
                        pingMaster(); //TODO
                    } catch (Exception e) {
                        //TODO: Slave becomes new Master
                    }
                }
            }
        };
//        this.slavePingThread.start();
    }

    private void movePlayerInput() throws InterruptedException, IOException, WrongGameException, NotBoundException {


        if (!gameStart || serverGameStatus == null) {
            Thread.sleep(100);
            return;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        do {
            try {
                Logging.printInfo("Player " + playerId + ", please enter your input (0: refresh, 1: West, 2: South, 3: East, 4: North, 9: Quit)");
                if (falseQuit) {
                    return;
                }
                while (!br.ready()) {
                    Thread.sleep(200);
                }
                input = br.readLine();
                Logging.printInfo("Waiting for your input for Player :" + playerId);

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        } while ("".equals(input));
        String move = input.replaceAll("\n", "");
        Direction direction = Direction.getDirection(move);

        //Player sends move request to Master
        Logging.printInfo("Your input Direction:" + direction.getDirecton() + " for player ID " + playerId);
        IGame master = null;
        try {
            master = getMaster();
        } catch (Exception ex) {
            List<IGame> newGameList = tracker.getServerList();
            while (newGameList == gameList)
            {
                Thread.sleep (200); //wait for 200ms for new Master to come online
                newGameList = tracker.getServerList();
            }
            gameList = newGameList;
            updateIGamePlayerIdMap(gameList);

            try {
                // original Master down, try to get new Master
                master = getMaster();
            } catch (Exception e) {
                Logging.printError("Player " + playerId + " can't find new Master!!!");
            }
        }

        try {
            //Ask for player move

            GameStatus gameStatus = master.move(this.playerId, direction, numOfStep);
            serverGameStatus = gameStatus;

            if (direction.getDirecton() != 9) {
                this.serverGameStatus = gameStatus;
                if (getSlave() == null)
                    GameView.printGameSummary(serverGameStatus, playerId, master.getId(), "");
                else
                    GameView.printGameSummary(serverGameStatus, playerId, master.getId(), getSlave().getId());
                Logging.printInfo("Your move is finished :" + direction.getDirecton() + " for player ID " + playerId);
                numOfStep++;
            }
        } catch (Exception ex) {
            //Master Failed, Slave becomes new Master
            Logging.printInfo("Master is down.");
            IGame newMaster = this.getSlave();
            if (newMaster != null) {
                //update slave game status
                if (master == null) {
                    newMaster.slaveBecomeMaster(null);
                } else {
                    newMaster.slaveBecomeMaster(master.getId());
                }
                GameStatus gameStatus = newMaster.move(this.playerId, direction, numOfStep);
                this.serverGameStatus = gameStatus;
                numOfStep++;
            }
        }
    }

    @Override
    public synchronized GameStatus move(String playerId, Direction direction, int numOfStep) throws RemoteException, WrongGameException, MalformedURLException, NotBoundException {
        if (this.isMaster == false) {
            throw new WrongGameException("I am Not Master, please do not call me dude....");
        }

        if (direction == Direction.QUIT) {
            try {
                quitGame(playerId);
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            return serverGameStatus;
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
    private void quitGame(String playerId) throws WrongGameException, RemoteException, MalformedURLException, NotBoundException, InterruptedException {

        IGame game = Utils.connectToGame(host, port, playerId);

        if (game.getIsMaster()) {
            // when master wants to quit the game, assign slave as master
            IGame newMaster = this.getSlave();
            if (newMaster != null) {
                //update slave game status
                try {
                    newMaster.slaveBecomeMaster(this.playerId);
                    newMaster.getServerGameStatus().playerQuit(playerId);
                } catch (Exception ex) {

                }
            }
        } else if (game.getIsSlave()) {
            // slave wants to quit the game
            IGame master = this.getMaster();
            master.getServerGameStatus().playerQuit(playerId);
            this.gameList.remove(game);
            updateIGamePlayerIdMap(gameList);
            gameList = tracker.setServerList(new ArrayList<>(gameList));
            master.assignNewSlave(serverGameStatus);

        } else {
            // a normal player wants to quit the game
            IGame master = this.getMaster();
            master.getServerGameStatus().playerQuit(playerId);
            this.gameList.remove(game);
            updateIGamePlayerIdMap(gameList);
            gameList = tracker.setServerList(new ArrayList<>(gameList));
        }

        game.quit();
    }

    private void gameStatusUpdatePlayerList() {
        serverGameStatus.updatePlayerList(gameList.stream().map(gamer -> {
            try {
                return gamer.getId();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).filter(id -> id != null).collect(Collectors.toList()));
    }

    /**
     * From master to update player and gamer status
     *
     * @param iGameId
     */
    private synchronized void removeFailedGamer(String iGameId) throws RemoteException {
        //remove Failed Player
        serverGameStatus.playerQuit(iGameId);
    }

    private IGame getMaster() throws WrongGameException, RemoteException {
        if (isMaster) {
            return this;
        } else {
            //first is Master
            for (int i = 0; i < gameList.size(); i++) {
                if (gameList.get(i).getIsMaster()) {
                    return gameList.get(i);
                }
            }

            //retry
            gameList = tracker.getServerList();
            updateIGamePlayerIdMap(gameList);
            for (int i = 0; i < gameList.size(); i++) {
                if (gameList.get(i).getIsMaster()) {
                    return gameList.get(i);
                }
            }
            throw new WrongGameException("No valid master");
        }
    }


    private IGame getSlave() throws WrongGameException, RemoteException {
        try {
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
        } catch (Exception ex) {
            gameList = tracker.getServerList();
            updateIGamePlayerIdMap(gameList);
            return getSlave();
        }
    }

    @Override
    public void setSlave(Boolean slave) throws RemoteException {
        isSlave = slave;
    }

    @Override
    public String getId() throws RemoteException {
        return playerId;
    }

    @Override
    public boolean getIsMaster() throws RemoteException {
        return isMaster;
    }

    @Override
    public boolean getIsSlave() throws RemoteException {
        return isSlave;
    }

    @Override
    public void setServerGameStatus(GameStatus serverGameStatus) throws RemoteException {
        this.serverGameStatus = serverGameStatus;
    }

    @Override
    public void setGameStart(Boolean gameStart) throws RemoteException {
        this.gameStart = gameStart;
    }

    @Override
    public GameStatus getServerGameStatus() throws RemoteException {
        return serverGameStatus;
    }

    @Override
    public void setMaster(Boolean master) throws RemoteException {
        isMaster = master;
    }

    @Override
    public void quit() throws NoSuchObjectException, RemoteException {
        try {
            UnicastRemoteObject.unexportObject(this, true);

            Logging.printInfo("Player QUIT, player ID: " + playerId);
            if (this.gameInputThread != null) {
                this.gameInputThread.interrupt();
            }
            if (this.pingTimer != null) {
                this.pingTimer.cancel();
            }

            if (this.masterPingThread != null) {
                this.masterPingThread.interrupt();
            }

            if (this.slavePingThread != null) {
                this.slavePingThread.interrupt();
            }

            falseQuit = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Main entry to create game.
     *
     * @param args
     * @throws RemoteException
     * @throws NotBoundException
     * @throws AlreadyBoundException
     * @throws InterruptedException
     */
    public static void main(String[] args)
            throws RemoteException, NotBoundException, AlreadyBoundException, InterruptedException, MalformedURLException, WrongGameException {
        // Get host and port
        String host = args.length > 0 ? args[0] : "localhost";
        String port = args.length > 1 ? args[1] : "1099";

        Random r = new Random();
        String s1 = String.valueOf((char) (r.nextInt(26) + 'a'));
        String s2 = String.valueOf((char) (r.nextInt(26) + 'a'));
        String playerId = args.length > 2 ? args[2] : s1 + s2;
        createAndConnectToTracker(host, port, playerId);
    }

    private static void createAndConnectToTracker(String host, String port, String playerId)
            throws RemoteException, NotBoundException, InterruptedException, AlreadyBoundException, MalformedURLException, WrongGameException {
     /*   Registry registry = LocateRegistry.getRegistry(host);

        Logging.printInfo("Ready for finding tracker!!");
        Tracker tracker = (Tracker) registry.lookup("tracker");

        Logging.printInfo("Found tracker!!");*/
        Logging.printInfo("Ready to look for tracker!!");
        String url = new String("//" + host + ":" + port + "/tracker");
        Logging.printDebug("tracker lookup url = " + url.toString());

        ITracker tracker = (ITracker) Naming.lookup(url);
        Logging.printInfo("Found tracker!!");

        Game game = new Game(host, Integer.valueOf(port), playerId);
        String url2 = new String("//" + host + ":" + port + "/" + playerId);
        Logging.printDebug("player binding url2 = " + url2.toString());
        Naming.rebind(url2, game);

        // DEBUG: to print out all names on rmiregistry
        int i = 0;
        for (String name : Naming.list(url2)) {
            i++;
            Logging.printDebug("rmiregistry entry " + i + ": " + name.toString());
        }

        game.connectToTracker(tracker);
    }
}


