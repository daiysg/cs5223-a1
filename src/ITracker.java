import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by ydai on 16/9/17.
 */
public interface ITracker {

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
     * master update Tracker with new Player List
     * Get the player list
     * @param playerList
     */
    void updatePlayerList(List<Player> playerList);

    /**
     *
     */
    List<Player> getPlayerList();
}
