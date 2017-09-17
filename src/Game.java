import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

/**
 * Created by ydai on 16/9/17.
 */
public class Game implements IGame {

    public static final int PING_TIMER_IN_SECONDS = 2; // ping between master to all players
    public static final int MASTER_SERVER_INDEX = 0;
    public static final int SLAVE_SERVER_INDEX = 1;

    private Boolean isMaster = false;

    /**
     * An ordered list of all gamer in the game
     * Please note that first is primary and second is backup
     */
    protected List<IGame> games;
    //shared info
    protected GameStatus serverGameStatus;


    protected Timer pingTimer;

    /**
     * Associated Player
     */
    public Player game;

    /**
     * Connected tracker
     */
    private ITracker tracker;

    /**
     * The thread responsible for prompting the user for input.
     * (This is run separately so as not to block other operations.)
     */
    private Thread gameInputThread;


    @Override
    public void ping() throws RemoteException {

    }

    @Override
    public void updateGameState(Game game) throws RemoteException {

    }

    @Override
    public void startGame(Game game) throws RemoteException {

    }

    @Override
    public void getAllPlayer(List<Player> playerList) {

    }

    @Override
    public Player joinGame(IGame game) throws RemoteException {
        return null;
    }

    @Override
    public GameStatus move(String playerId, Direction direction, int counter) throws RemoteException {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

}
