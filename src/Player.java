import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * Created by ydai on 9/9/17.
 */
public class Player {

    private String id;

    private Position position;

    public Player(String id, Position position) {
        this.id = id;
        this.position = position;
    }
}
