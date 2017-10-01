import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class Tracker extends UnicastRemoteObject implements ITracker, Serializable {

    private static final long serialVersionUID = 1L;

    private List<IGame> serverList;

    private Integer port;
    private Integer n;
    private Integer k;

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

        Logging.printInfo("ASK start to joining game, playerid:" + playerId);

        String url = new String("//" + host + ":" + +port + "/" + playerId);
        Logging.printDebug("player lookup url = " + url.toString());

        IGame game = (IGame) Naming.lookup(url);

        serverList.add(game);

        if (serverList.size() == 1) {
            game.setMaster(true);
        } else if (serverList.size() == 2) {
            game.setSlave(true);
        }

        printCurrentServerStatus();
        return serverList;
    }

    private void printCurrentServerStatus() throws RemoteException {

        Logging.printInfo("Current Gamer Status!!!! GameList Size = " + serverList.size());
        try {
            int i = 0;
            for (IGame iGame : serverList) {
                i++;
                Logging.printInfo("Player " + i + ". playerId = " + iGame.getId() + "; isMaster = " + iGame.getIsMaster() + "; isSlave = " + iGame.getIsSlave());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void initGame(int n, int k) throws RemoteException {
        this.n = n;
        this.k = k;
    }

    @Override
    public void setServerList(List<IGame> serverList) throws RemoteException {
        this.serverList = serverList;
        printCurrentServerStatus();
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, AlreadyBoundException, MalformedURLException {

        // Get port, n, k (if given). Use defaults otherwise.
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 1099;
        int n = args.length > 1 ? Integer.parseInt(args[0]) : 5;
        int k = args.length > 2 ? Integer.parseInt(args[1]) : 5;
        ITracker tracker = new Tracker(port, n, k);
        createTracker(tracker);
        tracker.initGame(n, k);
    }

    private static void createTracker(ITracker tracker) throws RemoteException, NotBoundException, AlreadyBoundException, MalformedURLException {

        // Start Tracker at localhost
        String url = new String("//localhost:" + tracker.getPort() + "/tracker");
        Logging.printDebug("tracker binding url = " + url.toString());

        Naming.rebind(url, tracker);
        Logging.printInfo("Tracker is Created");
    }
}

