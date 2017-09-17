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


    //Player List Info
    private List<Player> playerList;

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
    }

    public int getMazeSize() {
        return this.mazeSize;
    }

    public void start() {
        if (this.started) {
            return;
        }

        // Scatter treasures.
        for (int i = 0; i < this.totalTreasures; i++) {
            Pair<Integer, Integer> randomPosition = this.getVacantRandomPosition();
            this.coordinatesToNumberOfTreasure[randomPosition.x][randomPosition.y]++;
        }
        this.started = true;
    }

    public boolean hasStarted() {
        return this.started;
    }

    public boolean isOver() {
        return this.started && this.remainingTreasures == 0;
    }

    public Player getPlayer(String id) {
        return this.playerIdToPlayerHashMap.get(id);
    }

    public Player getPlayerAt(int x, int y) {
        return this.coordinatesToPlayer[x][y];
    }

    public int getNumTreasuresAt(int x, int y) {
        return this.coordinatesToNumberOfTreasure[x][y];
    }

    public int getPlayerTreasureCount(String id) {
        return this.playerIdToTreasures.get(id);
    }

    public Collection<Player> getPlayers() {
        return this.playerIdToPlayerHashMap.values();
    }

}
