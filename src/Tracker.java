import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

/**
 * Created by ydai on 9/9/17.
 */
public class Tracker implements ITracker{
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
