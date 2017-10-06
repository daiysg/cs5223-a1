import java.io.Serializable;
import java.util.*;

/**
 * The object that is sent back to clients updating them about the game state
 */
public class GameStatus implements Serializable {

    private static final long serialVersionUID = 4L;

    //grid size
    private int gridSize;

    //Number of Treasures
    private int totalTreasures;

    /**
     * players position List.
     */
    private String[][] playerPositionList;

    /**
     * player position Map
     */
    private Map<String, Position> playerPositionMap;

    /**
     * Treasure Position
     */
    public int[][] treasurePosition;

    /**
     * Number of moves processed for players
     */
    public Map<String, Integer> playerLastMoveMap;


    /**
     * A mapping of player id to the number of treasures collected.
     */
    private Map<String, Integer> playerTreasureMap;

    public GameStatus(int n, int k) {
        this.gridSize = n;
        this.totalTreasures = k;
        this.playerTreasureMap = new HashMap<>();
        this.playerPositionList = new String[gridSize][gridSize];
        this.playerLastMoveMap = new HashMap<>();
        this.playerPositionMap = new HashMap<>();
        this.treasurePosition = new int[gridSize][gridSize];

        //assign treasure
        for (int i = 0; i < totalTreasures; i++) {
            randomAssignTreasure();
        }
    }

    public synchronized void prepareForNewPlayer(String playerId) {

        //1. assign random position for new Player
        Position position = getAvailRandomPosition();
        playerPositionMap.put(playerId, position);
        playerPositionList[position.getX()][position.getY()] = playerId;

        playerLastMoveMap.put(playerId, -1);
        playerTreasureMap.put(playerId, 0);
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
        } while (!isPlayerVacantCell(candidatePosition.getX(), candidatePosition.getY()));
        return candidatePosition;
    }

    private boolean isPlayerVacantCell(int x, int y) {
        return this.playerPositionList[x][y] == null;
    }

    private boolean isTreasureVacantCell(int x, int y) {
        return this.treasurePosition[x][y] != 1;
    }

    public void movePlayer(String playerId, Direction direction, int numOfStep) {
        Position position = this.playerPositionMap.get(playerId);

        if (position == null) {
            return;
        }

        //check if this move is been executed
        if (playerLastMoveMap.get(playerId) >= numOfStep) {
            Logging.printError("THis move is executed already!!!");
            return;
        }

        Position newPosition = getNewPosition(position, direction);

        boolean isValidPosition = checkValidPosition(newPosition);
        if (!isValidPosition) {
            Logging.printError("Invalid position after moving!! Player " + playerId + " new X:" + newPosition.getX() + " new Y:" + newPosition.getY());
            newPosition = position;
        }

        // Update player's position.
        playerPositionList[position.getX()][position.getY()] = null;
        playerPositionMap.put(playerId, newPosition);
        playerPositionList[newPosition.getX()][newPosition.getY()] = playerId;

        // Add player Last Move
        playerLastMoveMap.put(playerId, numOfStep);

        // Collect treasures at new position, if any.
        int numTreasures = this.treasurePosition[newPosition.getX()][newPosition.getY()];

        if (numTreasures > 0) {
            this.treasurePosition[newPosition.getX()][newPosition.getY()] = 0;
            randomAssignTreasure();
            this.playerTreasureMap.put(playerId, playerTreasureMap.get(playerId) + numTreasures);

            Logging.printInfo("Treasure Acquired!!!! PlayerID:" + playerId + " at X:" + newPosition.getX() + " Y: " + newPosition.getY());
        }

    }

    private void randomAssignTreasure() {
        Position position = getAvailRandomPosition();
        while (!isTreasureVacantCell(position.getX(), position.getY())){
            position = getAvailRandomPosition();
        }
        treasurePosition[position.getX()][position.getY()] = 1;
    }

    private boolean checkValidPosition(Position newPosition) {
        return isValidCell(newPosition.getX(), newPosition.getY()) &&
                isPlayerVacantCell(newPosition.getX(), newPosition.getY());
    }

    private boolean isValidCell(Integer x, Integer y) {
        return 0 <= x && x < gridSize && 0 <= y && y < gridSize;
    }

    private Position getNewPosition(Position position, Direction direction) {
        switch (direction) {
            case N:
                return new Position(position.getX() - 1, position.getY());
            case E:
                return new Position(position.getX(), position.getY() + 1);
            case S:
                return new Position(position.getX() + 1, position.getY());
            case W:
                return new Position(position.getX(), position.getY() - 1);
            default:
                return new Position(position.getX(), position.getY());
        }
    }

    public void playerQuit(String playerId) {
        // remove player from playerPosition;
        Position position = this.playerPositionMap.get(playerId);
        if (position != null) {
            playerPositionList[position.getX()][position.getY()] = null;
        }
        // remove player from playerLastMoveMap
        playerLastMoveMap.remove(playerId);
        playerTreasureMap.remove(playerId);
        playerPositionMap.remove(playerId);
    }

    public int getGridSize() {
        return gridSize;
    }

    public int getTotalTreasures() {
        return totalTreasures;
    }

    public Map<String, Integer> getPlayerTreasureMap() {
        return playerTreasureMap;
    }

    public Map<String, Position> getPlayerPositionMap() {
        return playerPositionMap;
    }


    // Method for update player list by removing the non-existing player from the List
    public void updatePlayerList(List<String> existPlayerList) {
        Set<String> wholePlayerSet = new HashSet<>(playerPositionMap.keySet());
        wholePlayerSet.removeAll(existPlayerList);
        wholePlayerSet.parallelStream().forEach(playerId -> playerQuit(playerId));
    }

    public String getPlayerAt(int j, int i) {
        return playerPositionList[j][i];
    }

    public int getTreasureAt(int j, int i) {
        return treasurePosition[j][i];
    }
}
