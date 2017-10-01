import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface ITracker extends Remote {

    Integer getPort() throws RemoteException;

    Integer getN() throws RemoteException;

    Integer getK() throws RemoteException;

    List<IGame> getServerList() throws RemoteException;

    GameStatus test(GameStatus gameStatus) throws RemoteException;

    /**
     * Master (1st player) call tracker to join game
     *
     * @param host, port, playerId
     * @return
     * @throws RemoteException
     */
    List<IGame> joinGame(String host, int port, String playerId) throws RemoteException, MalformedURLException, NotBoundException;

    /**
     *
     */
    void initGame(int n, int k) throws RemoteException;

    /**
     * Master update Tracker with new Game List
     *
     * @param serverList
     */
    void setServerList(List<IGame> serverList) throws RemoteException;
}
