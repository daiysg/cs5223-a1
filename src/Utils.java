import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by ydai on 23/9/17.
 */
public class Utils {

    /**
     * Static method to connect to other gamer
     *
     * @param host
     * @param port
     * @param playerId
     * @return
     */
    public static IGame connectToGame(String host, int port, String playerId) throws RemoteException, NotBoundException, InterruptedException, MalformedURLException {

        String url = new String("rmi://localhost:" + port + "/" + playerId);

        IGame game = (IGame) Naming.lookup(url);
        return game;
    }

    public static ITracker connectToGame(String host, int port) throws RemoteException, NotBoundException, InterruptedException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        ITracker tracker = (ITracker) registry.lookup("tracker");
        return tracker;
    }
}
