import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
//import java.util.stream.Collectors;

//import static java.rmi.server.UnicastRemoteObject.*;


public class Tracker extends UnicastRemoteObject implements ITracker, Serializable {

    private static final long serialVersionUID = 1L;

    private List<IGame> serverList;

    private Integer port;
    private Integer n;
    private Integer k;
//    private String  masterId;

    @Override
    public Integer getPort() throws RemoteException {
        return port;
    }

    @Override
    public Integer getN() throws RemoteException {
        return n;
    }

    @Override
    public Integer getK() throws RemoteException {
        return k;
    }

    public Tracker(Integer port, Integer n, Integer k) throws RemoteException, NotBoundException {
        this.port = port;
        this.n = n;
        this.k = k;

        this.serverList = new ArrayList<>();
    }

    @Override
    public List<IGame> getServerList() throws RemoteException {
//        serverList = validateGameList(serverList);
        return serverList;
    }

    @Override
    public GameStatus test(GameStatus gameStatus) throws RemoteException {
        return gameStatus;
    }

    @Override
    public synchronized List<IGame> joinGame(String host, int port, String playerId) throws RemoteException, MalformedURLException, NotBoundException {
        // The 1st player joining the game is the Master.
        // The 2nd player joining the game is the Slave.

        Logging.printInfo("ASK start to joining game, playerId:" + playerId);

        String url = new String("//" + host + ":" + port + "/" + playerId);
        Logging.printDebug("player lookup url = " + url.toString());

        IGame game = (IGame) Naming.lookup(url);

        serverList.add(game);

        if (serverList.size() == 1) {
            game.setMaster(true);
//            masterId = playerId;
        } else if (serverList.size() == 2) {
            game.setSlave(true);
        }
//        this.serverList = validateGameList(serverList);

        printCurrentServerStatus();
        return serverList;
    }

    private void printCurrentServerStatus() throws RemoteException {

 /*       boolean hasMaster = false;
        boolean hasSlave = false;
        Logging.printInfo("Current Gamer Status!!!! GameList Size = " + serverList.size());
        try {
            int i = 0;
            for (IGame iGame : serverList) {
                if (iGame.getIsMaster()) {
                    hasMaster = true;
                }
                if (iGame.getIsSlave()) {
                    hasSlave = true;
                }
                i++;
                Logging.printInfo("Player " + i + ". playerId = " + iGame.getId() + "; isMaster = " + iGame.getIsMaster() + "; isSlave = " + iGame.getIsSlave());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logging.printException(e);
        }


        //TODO should be removed if thread works fine.
        if (hasMaster == false && serverList.size() > 0) {
            serverList.get(0).setMaster(true);
        }

        if (hasSlave == false && serverList.size() > 1) {
            serverList.get(1).setSlave(true);
        }*/

        Logging.printInfo("Current Gamer Status!!!! GameList Size = " + serverList.size());
        int i = 0;
        for (IGame iGame : serverList) {
            i++;
            try {
                Logging.printInfo("Player " + i + ". playerId = " + iGame.getId() +
                        "; isMaster = " + iGame.getIsMaster() + "; isSlave = " + iGame.getIsSlave());
            } catch (Exception e){
                e.printStackTrace();
                Logging.printException(e);
            }
        }
    }

    @Override
    public void initGame(int n, int k) throws RemoteException {
        this.n = n;
        this.k = k;
    }

    @Override
    public synchronized List<IGame> setServerList(List<IGame> inputServerList) throws RemoteException {
//        List<IGame> newList = validateGameList(inputServerList);
/*        List<IGame> newList = inputServerList;

        if (!masterId.equals(newList.get(0).getId()))
        {
            // Master has changed
            Logging.printDebug("old Master = " + masterId + " new Master = " + newList.get(0).getId());

            String url = new String("//localhost:" + port + "/" + masterId);
            Logging.printDebug("old Master's lookup url = " + url.toString());

            try {
                Naming.unbind(url);
            } catch (Exception e) {
                e.printStackTrace();
                Logging.printException(e);
            }
        }

        this.serverList = newList;*/

//        this.serverList = validateGameList(inputServerList);
        this.serverList = inputServerList;
        printCurrentServerStatus();
        return serverList;
    }


/*    private List<IGame> validateGameList(List<IGame> serverList) throws RemoteException {

        return new ArrayList<>(serverList.parallelStream().filter(game -> {
            try {
                game.ping();
                return true;
            } catch (Exception e) {
                return false;
            }
        }).collect(Collectors.toList()));
    }*/

    public static void main(String[] args) throws RemoteException, NotBoundException, AlreadyBoundException, MalformedURLException {

        // Get port, n, k (if given). Use defaults otherwise.
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 1099;
        int n = args.length > 1 ? Integer.parseInt(args[1]) : 15;
        int k = args.length > 2 ? Integer.parseInt(args[2]) : 10;
        ITracker tracker = new Tracker(port, n, k);
        createTracker(tracker);
        tracker.initGame(n, k);
    }

    private static void createTracker(ITracker tracker) throws RemoteException, NotBoundException, AlreadyBoundException, MalformedURLException {

        // Start Tracker at localhost
        String url = new String("//localhost:" + tracker.getPort() + "/tracker");
        Logging.printDebug("tracker binding url = " + url.toString());

        //try and retry for 3 times
        try {
            Naming.rebind(url, tracker);
        } catch (Exception e) {
            try {
                Thread.sleep (200);
                Naming.rebind(url, tracker);
            } catch (Exception e2) {
                try {
                    Thread.sleep (200);
                    Naming.rebind(url, tracker);
                } catch (Exception e3) {
                    e3.printStackTrace();
                    Logging.printException(e3);
                    return;
                }
            }
        }

        Logging.printInfo("Tracker is Created");
    }
}

