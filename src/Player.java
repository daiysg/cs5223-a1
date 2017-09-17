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

    public Player(String id, Position position, Integer acquiredTresure, Integer numOfSteps) {
        this.id = id;
        this.position = position;
        this.acquiredTresure = acquiredTresure;
        this.numOfSteps = numOfSteps;
    }
}
