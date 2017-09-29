import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
            throws RemoteException, NotBoundException,AlreadyBoundException, InterruptedException {
        // Get host and port
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 1099;
        String playerId = args.length > 2 ? args[2] : "test";
        createAndConnectToTracker(host, port, playerId);
    }

    private static void createAndConnectToTracker(String host, int port, String playerId)
            throws RemoteException, NotBoundException, InterruptedException, AlreadyBoundException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        ITracker tracker = (ITracker) registry.lookup("tracker");
        Game game = new Game(tracker, playerId);
        game.askTrackerJoinGame();
    }

}
