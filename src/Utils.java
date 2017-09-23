import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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


    public static Map<String, Player> convertGameListToPlayerHashMap(List<IGame> gameList) {
        return gameList.stream().collect(Collectors.toMap(IGame::getId, IGame::getPlayer));
    }
}
