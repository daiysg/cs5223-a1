

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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

    /**
     * 2D map of players List.
     */
    private Player[][] coordinatesToPlayer;

    /**
     * Coordinates of all Treasure
     */
    public int[][] treasureGrid;

    public Map<Player, Integer> playerLastMoveMap;

    /**
     * A mapping of player id to the player object.
     */
    private HashMap<String, Player> playerHashMap;

    /**
     * A mapping of player id to the number of treasures collected.
     */
    private HashMap<String, Integer> playerTreasureCollect;

    public boolean isGameStarted = false;

}
