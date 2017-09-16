import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * Created by ydai on 9/9/17.
 */
public class Player implements Runnable{

    private String id;
    private GameStatus gameStatus;
    private Scanner in;

    public Player(String id, GameStatus gameStatus, Scanner in) {
        this.id = id;
        this.gameStatus = gameStatus;
        this.in = in;
    }

    @Override
    public void run() {
        System.out.println("GameServer initializing...");
        System.out.println("GameServer starts...");

        do {
            try {
                synchronized (this) {
                    this.move();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } while (this.gameStatus != null
                && this.gameStatus.numTreasuresLeft > 0);

    }

    private void move() throws RemoteException {
        System.out.println("Enter move direction (0, 1, 2, 3, 4, 9): ");
        Direction direction = Direction.getDirection(Integer.valueOf(in.nextLine()));
        if (direction.equals(Direction.INVALID)) {
            System.out.println("Invalid move direction. ");
            return;
        }


        System.out.println("Receive directly " + direction.getDirecton());
        try {
            this.gameStatus = this.gameStatus.masterServer.move(id,
                    direction);
        } catch (RemoteException e) {
            try {
                if (this.gameStatus.slaveServer == null) {
                    //update server from tracker
                    return;
                }
                this.gameStatus = this.gameStatus.slaveServer.callSlave(
                        id, direction);
            } catch (RemoteException ee) {
                return;
            }
        }
    }

}
