import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by ydai on 16/9/17.
 */
public interface TrackerInterface {

    public int joinGame(GameInterface server) throws RemoteException;

    List<Game> getGameList();
}
