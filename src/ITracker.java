import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by ydai on 16/9/17.
 */
public interface ITracker {

    public int joinGame(IGame server) throws RemoteException;

    List<Game> getGameList();
}
