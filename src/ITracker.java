import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by ydai on 16/9/17.
 */
public interface ITracker extends Remote {

    Integer getPort() throws RemoteException;

    Integer getN() throws RemoteException;

    Integer getK() throws RemoteException;

    List<IGame> getServerList() throws RemoteException;

    GameStatus test(GameStatus gameStatus) throws RemoteException;

    /**
     * Player call tracker to join game
     *
     * @param game
     * @return
     * @throws RemoteException
     */
    //List<IGame> joinGame(IGame game) throws RemoteException;

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

    List<IGame> joinGame(String playerId) throws RemoteException, MalformedURLException, NotBoundException;
}
