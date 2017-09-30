import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ydai on 9/9/17.
 */
public class Tracker extends UnicastRemoteObject implements ITracker {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<IGame> serverList;

    private Integer n;
    private Integer k;

    @Override
    public Integer getN() throws RemoteException {
        return n;
    }


    @Override
    public Integer getK() throws RemoteException {
        return k;
    }

    public Tracker(Integer n, Integer k) throws RemoteException, NotBoundException {
        this.n = n;
        this.k = k;

        this.serverList = new ArrayList<>();
    }


    @Override
    public List<IGame> getServerList() throws RemoteException {
        return serverList;
    }

    @Override
    public void joinGame(IGame game) throws RemoteException {
        // TODO: Should we move this logic to Game class and let master call Tracker.setServerList to update Tracker's serverList? Prof wants Tracker to be as light as possible
        // first join in, the gamer join in is master
        Logging.printInfo("ASK start to joining game");
        if (serverList.size() == 0) {
            Logging.printInfo("Master to add new Player~~~");
            serverList = game.addNewPlayer(game);
        } else {
            for (int i = 0; i < serverList.size(); i++) {
                IGame master = serverList.get(i);
                if (serverList.get(i).getIsMaster())
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
    public void setServerList(List<IGame> serverList) throws RemoteException {
        this.serverList = serverList;
    }
}

