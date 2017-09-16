import java.rmi.RemoteException;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

/**
 * Created by ydai on 16/9/17.
 */
public class Game implements GameInterface {

    public static final int PING_TIMER_IN_SECONDS = 2; // ping between master to all players

    private Boolean isMaster = false;
    private String masterId;
    protected String slaveId;
    protected Vector<GameInterface> games;

    //shared info
    protected GameStatus serverGameStatus;


    protected Timer pingTimer;
    public Player game;
    private TrackerInterface trackerInterface;


    public Game() throws RemoteException {
        this.games = new Vector<GameInterface>();
        this.serverGameStatus = new GameStatus();

        trackerInterface.joinGame(this);
        //
        List<Game> gameList = trackerInterface.getGameList();


    }

    @Override
    public void startGame(GameStatus initGameState) throws RemoteException {
        this.game.setGameState(initGameState);
        Thread t = new Thread(this.game);
        t.start();
    }

    @Override
    public GameStatus move(String id, Direction direction) throws RemoteException {
        return null;
    }

    @Override
    public boolean createSlave(GameStatus gameState, Vector<ClientInterface> clients) throws RemoteException {
        return false;
    }

    @Override
    public GameStatus callSlave(String id, Direction moveDirection) throws RemoteException {
        return null;
    }

    @Override
    public void pindAllPlayers(Vector<ClientInterface> players) throws RemoteException {

    }

    @Override
    public GameStatus findSlave(String id, Direction direction) throws RemoteException {
        return null;
    }

    @Override
    public String getId() {
        return this.masterId;
    }

}
