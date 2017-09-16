

import java.io.Serializable;
import java.util.Vector;

/**
 * The object that is sent back to clients updating them about the game state
 */
public class GameStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    //grid size
    public int n;

    //Number of Treasures
    public int k;

    //k left
    public int numTreasuresLeft;

    public Vector<Player> players;


    public GameInterface masterServer;

    public GameInterface slaveServer;

    public boolean isGameStarted = false;

}
