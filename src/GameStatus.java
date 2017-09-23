/**
 * Refer to Game
 */
import java.io.Serializable;
import java.util.*;

/**
 * The object that is sent back to clients updating them about the game state
 */
public class GameStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    //grid size
    private int gridSize;

    //Number of Treasures
    private int totalTreasures;

    /**
     * players position List.
     */
    private Player[][] playerPosition;

    /**
     * Treasure Position
     */
    public int[][] treasurePostion;

    /**
     * Number of moves processed for players
     */
    public Map<Player, Integer> playerLastMoveMap;

    /**
     * A mapping of player id to the player object.
     */
    private Map<String, Player> playerHashMap;

    /**
     * A mapping of player id to the number of treasures collected.
     */
    private Map<String, Integer> playerTreasureMap;

    public GameStatus(int n, int k) {
        this.gridSize = n;
        this.totalTreasures = k;
        this.playerHashMap = new HashMap<>();
        this.playerTreasureMap = new HashMap<>();
        this.playerPosition = new Player[gridSize][gridSize];
        this.playerLastMoveMap = new HashMap<>();
    }

    public Map<String, Player> getPlayerHashMap() {
        return playerHashMap;
    }

    public void setPlayerHashMap(Map<String, Player> playerHashMap) {
        this.playerHashMap = playerHashMap;
    }
}
