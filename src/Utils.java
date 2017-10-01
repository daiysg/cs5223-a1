import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

//TODO: Is this class in use at all?
public class Utils {

    /**
     * Static method to connect to other gamer
     *
     * @param host
     * @param port
     * @param playerId
     * @return
     */
    public static IGame connectToGame(String host, int port, String playerId) throws RemoteException, NotBoundException, InterruptedException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        IGame game = (IGame) registry.lookup(playerId);
        return game;
    }

    public static ITracker connectToGame(String host, int port) throws RemoteException, NotBoundException, InterruptedException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        ITracker tracker = (ITracker) registry.lookup("tracker");
        return tracker;
    }
}
