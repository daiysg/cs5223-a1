import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ydai on 9/9/17.
 */
public class Tracker extends UnicastRemoteObject implements ITracker, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<IGame> serverList;

    private Integer port;
    private Integer n;
    private Integer k;

    @Override
    public Integer getPort() throws RemoteException {
        return port;
    }

    @Override
    public Integer getN() throws RemoteException {
        return n;
    }

    @Override
    public Integer getK() throws RemoteException {
        return k;
    }

    public Tracker(Integer port, Integer n, Integer k) throws RemoteException, NotBoundException {
        this.port = port;
        this.n = n;
        this.k = k;

        this.serverList = new ArrayList<>();
    }

    @Override
    public List<IGame> getServerList() throws RemoteException {
        return serverList;
    }

    @Override
    public GameStatus test(GameStatus gameStatus) throws RemoteException{
        return gameStatus;
    }

    @Override
    public synchronized List<IGame> joinGame(String playerId) throws RemoteException, MalformedURLException, NotBoundException {
        // TODO: Should we move this logic to Game class and let master call Tracker.setServerList to update Tracker's serverList? Prof wants Tracker to be as light as possible
        // The 1st gamer joining in is the Master; the 2nd gamer joining in is the Slave
        Logging.printInfo("ASK start to joining game, playerid:" + playerId);

        String url = new String("rmi://localhost:" + port + "/"+ playerId);
        Logging.printDebug("lookup url = " + url.toString());

        IGame game = (IGame) Naming.lookup(url);

        serverList.add(game);

        if (serverList.size() == 1) {
            game.setMaster(true);
        } else if (serverList.size() == 2) {
            game.setSlave(true);
        }
        printCurrentServerStatus();
        return serverList;
    }

    private void printCurrentServerStatus() throws RemoteException {

        Logging.printInfo("Current Gamer Status!!!!");
        int i = 0;
        for (IGame iGame : serverList) {
            i++;
            Logging.printInfo("Player " + i + ". -- Player id: " + iGame.getId() + " is Master " + iGame.getIsMaster());
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

