import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ydai on 9/9/17.
 */
public class Tracker extends UnicastRemoteObject implements ITracker {
    private List<IGame> serverList;

    private Integer n;
    private Integer k;

    @Override
    public Integer getN() {
        return n;
    }


    @Override
    public Integer getK() {
        return k;
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
    public void joinGame(IGame game) throws RemoteException {
        // TODO: Should we move this logic to Game class and let master call Tracker.setServerList to update Tracker's serverList? Prof wants Tracker to be as light as possible
        // first join in
        if (serverList.size() == 0) {
            serverList = game.addNewPlayer(game);
        } else {
            for (int i = 0; i < serverList.size(); i++) {
                IGame master = serverList.get(i);
                if (serverList.get(i).isMaster())
                    serverList = master.addNewPlayer(game);
            }
        }
    }

    @Override
    public void initGame(int n, int k) throws RemoteException {
        this.n = n;
        this.k = k;
    }

    @Override
    public void setServerList(List<IGame> serverList) {
        this.serverList = serverList;
    }
}

