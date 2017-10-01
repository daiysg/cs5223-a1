import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

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

        String url = new String("//localhost:" + port + "/" + playerId);

        IGame game = (IGame) Naming.lookup(url);
        return game;
    }
}
