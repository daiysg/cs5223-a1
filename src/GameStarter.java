import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class GameStarter {
    /**
     * Main entry to create game.
     * @param args
     * @throws RemoteException
     * @throws NotBoundException
     * @throws AlreadyBoundException
     * @throws InterruptedException
     */
    public static void main(String[] args)
            throws RemoteException, NotBoundException, AlreadyBoundException, InterruptedException, MalformedURLException {
        // Get host and port
        String host = args.length > 0 ? args[0] : null;
        String playerId = args.length > 1 ? args[1] : "test";
        createAndConnectToTracker(host, playerId);
    }

    private static void createAndConnectToTracker(String host, String playerId)
            throws RemoteException, NotBoundException, InterruptedException, AlreadyBoundException, MalformedURLException {
     /*   Registry registry = LocateRegistry.getRegistry(host);

        Logging.printInfo("Ready for finding tracker!!");
        Tracker tracker = (Tracker) registry.lookup("tracker");

        Logging.printInfo("Found tracker!!");*/
        Logging.printInfo("Ready for finding tracker!!");
        String url = new String("rmi://localhost/tracker");
        ITracker tracker = (ITracker) Naming.lookup(url);
        Logging.printInfo("Found tracker!!");
        Game game = new Game(tracker, playerId);
    }

}
