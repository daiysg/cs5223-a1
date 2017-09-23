import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * Created by ydai on 9/9/17.
 */
public class Player {

    private String id;

    private Position position;

    private Integer acquiredTresure;

    private Integer numOfSteps;

    private PlayerStatus playerStatus;

    public Player(String id, Position position, Integer acquiredTresure, Integer numOfSteps, PlayerStatus playerStatus) {
        this.id = id;
        this.position = position;
        this.acquiredTresure = acquiredTresure;
        this.numOfSteps = numOfSteps;
        this.playerStatus = playerStatus;
    }

    public String getId() {
        return id;
    }
}
