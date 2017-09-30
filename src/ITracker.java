import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by ydai on 16/9/17.
 */
public interface ITracker extends Remote {

    Integer getN() throws RemoteException;

    Integer getK() throws RemoteException;

    List<IGame> getServerList() throws RemoteException;

    /**
     * Player call tracker to join game
     *
     * @param game
     * @return
     * @throws RemoteException
     */
    void joinGame(IGame game) throws RemoteException;

    /**
     *
     */
    void initGame(int n, int k) throws RemoteException;

    /**
     * master update Tracker with new Game List
     * Get the player list
     *
     * @param serverList
     */
    void setServerList(List<IGame> serverList) throws RemoteException;

}
