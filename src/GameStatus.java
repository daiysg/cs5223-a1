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
    private Map<String, Player> playerHashMap;

    /**
     * A mapping of player id to the number of treasures collected.
     */
    private Map<String, Integer> playerTreasureMap;

    public boolean isGameStarted = false;


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

    public synchronized Player newPlayer() {
        Player newPlayer = Player.createPlayer();
        this.playerIdToPlayerHashMap.put(newPlayer.id, newPlayer);
        this.playerIdToTreasures.put(newPlayer.id, 0);

        // Assign the new player a starting position.
        Pair<Integer, Integer> startingPosition = this.getVacantRandomPosition();
        newPlayer.setXCoordinate(startingPosition.x);
        newPlayer.setYCoordinate(startingPosition.y);
        this.coordinatesToPlayer[startingPosition.x][startingPosition.y] = newPlayer;
        return newPlayer;
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

    /**
     * Moves player in the given direction if the move results in a valid board state:
     * - Only one player in a cell.
     * - Player remains in the maze.
     * @param playerId
     * @param direction
     */
    public synchronized void movePlayer(String playerId, Direction direction) {
        Player player = this.playerIdToPlayerHashMap.get(playerId);

        // Get new position.
        Pair<Integer, Integer> newPosition = null;
        int xCoordinate = player.getXCoordinate();
        int yCoordinate = player.getYCoordinate();
        if (direction == Direction.N) {
            newPosition = new Pair<Integer, Integer>(xCoordinate, yCoordinate + 1);
        } else if (direction == Direction.S) {
            newPosition = new Pair<Integer, Integer>(xCoordinate, yCoordinate - 1);
        } else if (direction == Direction.E){
            newPosition = new Pair<Integer, Integer>(xCoordinate + 1, yCoordinate);
        } else if (direction == Direction.W) {
            newPosition = new Pair<Integer, Integer>(xCoordinate - 1, yCoordinate);
        }

        // Only update the player's position if the new position is valid and is empty.
        if (newPosition != null &&
                this.isValidCell(newPosition.x, newPosition.y) &&
                this.isVacantCell(newPosition.x, newPosition.y)) {

            // Update the player's position.
            this.coordinatesToPlayer[player.getXCoordinate()][player.getYCoordinate()] = null;
            player.setXCoordinate(newPosition.x);
            player.setYCoordinate(newPosition.y);
            this.coordinatesToPlayer[player.getXCoordinate()][player.getYCoordinate()] = player;

            // Collect treasures at new position, if any.
            if (this.coordinatesToNumberOfTreasure[newPosition.x][newPosition.y] > 0) {
                int numTreasures = this.coordinatesToNumberOfTreasure[newPosition.x][newPosition.y];
                this.playerIdToTreasures.put(playerId, this.getPlayerTreasureCount(playerId) + numTreasures);
                this.remainingTreasures -= numTreasures;
                this.coordinatesToNumberOfTreasure[newPosition.x][newPosition.y] = 0;
            }
        }
    }

    private Pair<Integer, Integer> getRandomPosition() {
        Random random = new Random();
        int randomX = random.nextInt(mazeSize);
        int randomY = random.nextInt(mazeSize);
        return new Pair<Integer, Integer>(randomX, randomY);
    }

    private Pair<Integer, Integer> getVacantRandomPosition() {
        Pair<Integer, Integer> candidatePair;
        do {
            candidatePair = getRandomPosition();
        } while (!this.isVacantCell(candidatePair.x, candidatePair.y));
        return candidatePair;
    }

    /**
     * Returns whether the position given is a valid cell position.
     * @param x
     * @param y
     * @return
     */
    private boolean isValidCell(int x, int y) {
        return 0 <= x && x < mazeSize && 0 <= y && y < mazeSize;
    }

    /**
     * Returns whether the cell at the given position is not being occupied by another player.
     * @param x
     * @param y
     * @return
     */
    private boolean isVacantCell(int x, int y) {
        return this.coordinatesToPlayer[x][y] == null;
    }

}
