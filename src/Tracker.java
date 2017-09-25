import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ydai on 9/9/17.
 */
public class Tracker extends UnicastRemoteObject implements ITracker{
    private List<IGame> serverList;

    private Integer n;
    private Integer k;


    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Integer getK() {
        return k;
    }

    public void setK(Integer k) {
        this.k = k;
    }

    public Tracker(Integer n, Integer k) throws RemoteException, NotBoundException {
        this.n = n;
        this.k = k;

        this.serverList = new ArrayList<>();
    }


    @Override
    public List<IGame> getServerList() {
        return serverList;
    }

    @Override
    public int joinGame(IGame game) throws RemoteException {
        return 0;
    }

    @Override
    public void initGame(int n, int k) throws RemoteException {

    }

    @Override
    public void setServerList(List<IGame> serverList) {
        this.serverList = serverList;
    }
}

