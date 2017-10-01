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

        Game game = new Game(playerId);
/*
        String url2 = new String("//localhost:" + port + "/" + playerId);
        Game game = new Game("localhost", Integer.valueOf(port), playerId);
        String url2 = new String("rmi://localhost:" + port + "/" + playerId);
        Logging.printDebug("player binding url2 = " + url2.toString());
        Naming.rebind(url2, game);*/

        Naming.rebind(playerId, game);

        // DEBUG: to print out all names on rmiregistry
        int i = 0;
        for (String name : Naming.list(playerId))
        {
            i++;
            Logging.printDebug("rmiregistry entry " + i + ": " + name.toString());
        }

        game.connectToTracker(tracker);
    }

}
