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
    public Map<String, Integer> playerLastMoveMap;

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

    public void prepareForNewPlayer(Player player) {

        //1. assign random position for new Player
        Position position = getAvailRandomPosition();
        player.setPosition(position);
        playerPosition[position.getX()][position.getY()] = player;

        //2. add new player into playerMap and player last move map, player treasuremap
        playerHashMap.put(player.getId(), player);
        playerLastMoveMap.put(player.getId(), 0);
        playerTreasureMap.put(player.getId(), 0);
    }



    private Position getRandomPosition() {
        Random random = new Random();
        int randomX = random.nextInt(gridSize);
        int randomY = random.nextInt(gridSize);
        return new Position(randomX, randomY);
    }

    private Position getAvailRandomPosition() {
        Position candidatePosition;
        do {
            candidatePosition = getRandomPosition();
        } while (!isVacantCell(candidatePosition.getX(), candidatePosition.getY()));
        return candidatePosition;
    }

    private boolean isVacantCell(int x, int y) {
        return this.playerPosition[x][y] == null;
    }

    public void movePlayer(String playerId, Direction direction, int numOfStep) {
        Player player = this.playerHashMap.get(playerId);
        Position position = player.getPosition();

    }
}
