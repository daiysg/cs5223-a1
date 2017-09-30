import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;

public class GameStarter {
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
        String playerId = args.length > 2 ? args[2] : "zz" + (new Random().nextInt() % 100);
        createAndConnectToTracker(host, port, playerId);
    }

    private static void createAndConnectToTracker(String host, String port, String playerId)
            throws RemoteException, NotBoundException, InterruptedException, AlreadyBoundException, MalformedURLException, WrongGameException {
     /*   Registry registry = LocateRegistry.getRegistry(host);

        Logging.printInfo("Ready for finding tracker!!");
        Tracker tracker = (Tracker) registry.lookup("tracker");

        Logging.printInfo("Found tracker!!");*/
        Logging.printInfo("Ready to look for tracker!!");
        String url = new String("rmi://" + host + ":" + port + "/tracker");
        Logging.printDebug("lookup url = " + url.toString());

        ITracker tracker = (ITracker) Naming.lookup(url);
        Logging.printInfo("Found tracker!!");
//        Naming.rebind("tracker", tracker);

        Game game = new Game(playerId);
        Naming.rebind(playerId, game);

        // DEBUG: to print out all names on rmiregistry
        for (String name : Naming.list(playerId))
        {
            Logging.printDebug("rmiregistry entry: " + name.toString());
        }

        game.connectToTracker(tracker);
    }

}
