import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ydai on 16/9/17.
 */
public interface ITracker {

    List<IGame> getServerList();

    /**
     *
     * Player call tracker to join game
     * @param game
     * @return
     * @throws RemoteException
     */
    int joinGame(IGame game) throws RemoteException;

    /**
     *
     */
    void initGame(int n, int k) throws RemoteException;

    /**
     *
     * master update Tracker with new Game List
     * Get the player list
     * @param serverList
     */ void setServerList(List<IGame> serverList);

}
