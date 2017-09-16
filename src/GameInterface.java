

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface GameInterface extends Remote {


    /**
     * All Players Work
     */
    void startGame(GameStatus initGameState)  throws RemoteException;



    /**
     * Master Work
     *
     */
    //receive move from client
    GameStatus move(String id, Direction direction) throws RemoteException;

    //createslave
    boolean createSlave(GameStatus gameState) throws RemoteException;

    //pin all slave
    void pinAllPlayers() throws RemoteException;



    GameStatus callSlave(String id, Direction moveDirection) throws RemoteException;


    /**
     * Slave Work
     * @return
     */

    String getId();
}
