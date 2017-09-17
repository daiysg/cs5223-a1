

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
     * slave call master
     */
    void pingMaster() throws RemoteException;

    /**
     *
     * Master will call slave to update game status
     *
     */
    void updateGameStatus(GameStatus gameStatus) throws RemoteException;

    /**
     * Master assign new slave if slave down
     *
     */
    void assignNewSlave(GameStatus gameStatus) throws RemoteException;

    /**
     * Slave Become Master
     * @throws RemoteException
     */
    void slaveBecomeMaster() throws RemoteException;

    /**
     * Tracker call master for adding new player
     * @param game
     * @throws RemoteException
     */
    List<Player> addNewPlayer(Game game) throws RemoteException;

    /**
     * Master call player game start
     *
     * should be inside addNewPlayer
     *
     * @param gameStatus
     * @throws RemoteException
     */
    void startGame(GameStatus gameStatus) throws RemoteException;


    /**
     *  move request by all players
     *
     *  Master need to update game status;
     *  and call game status to slave
     *
     *
     *  need handle exit game
     */

    GameStatus move(String playerId, Direction direction, int numOfStep) throws RemoteException;

    String getId();
}
