

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

public interface IGame extends Remote {

    /**
     * Message will send to all player
     *
     */
    /**
     * health check
     */
    void ping() throws RemoteException;

    /**
     * update game state
     */
    void updateGameState(Game game) throws RemoteException;

    /**
     * Signals to the peer that the game has started.
     * @param game
     * @throws RemoteException
     */
    void startGame(Game game) throws RemoteException;

    /**
     * Get the player list
     * @param playerList
     */
    void getAllPlayer(List<Player> playerList);

    /**
     * Message will send to master
     *
     */
    /**
     *  call by new Player to join the game
     */

    Player joinGame(IGame game) throws RemoteException;


    /**
     *  move request by all players
     */

    GameStatus move(String playerId, Direction direction, int counter) throws RemoteException;

    String getId();
}
