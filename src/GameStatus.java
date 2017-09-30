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
    private String[][] playerPositionList;

    /**
     * player position Map
     */
    private Map<String, Position> playerPositionMap;

    /**
     * Treasure Position
     */
    public int[][] treasurePostion;

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
        this.treasurePostion = new int[gridSize][gridSize];

        //assign treasure
        for (int i = 0; i < totalTreasures; i++) {
            randomAssignTreasure(1);
        }
    }

    public synchronized void prepareForNewPlayer(String playerId) {

        //1. assign random position for new Player
        Position position = getAvailRandomPosition();
        playerPositionMap.put(playerId, position);
        playerPositionList[position.getX()][position.getY()] = playerId;

        playerLastMoveMap.put(playerId, 0);
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
        } while (!isVacantCell(candidatePosition.getX(), candidatePosition.getY()));
        return candidatePosition;
    }

    private boolean isVacantCell(int x, int y) {
        return this.playerPositionList[x][y] == null;
    }

    public void movePlayer(String playerId, Direction direction, int numOfStep) {
        Position position = this.playerPositionMap.get(playerId);

        if (position == null) {
            return;
        }

        //check if this move is been executed
        if (playerLastMoveMap.get(playerId) >= numOfStep) {
            Logging.printError("THis move is exectued already!!!");
            return;
        }

        Position newPosition = getNewPosition(position, direction);

        boolean isValidPosition = checkValidPosition(newPosition);
        if (!isValidPosition) {
            Logging.printError("Invalid postion after moving!! Player " + playerId + " new X:" + position.getX() + " new Y:" + position.getY());
            return;
        }

        // Update player's position.
        playerPositionList[position.getX()][position.getY()] = null;
        playerPositionMap.put(playerId, newPosition);
        playerPositionList[newPosition.getX()][newPosition.getY()] = playerId;

        // Add player Last Move
        playerLastMoveMap.put(playerId, numOfStep);

        // Collect treasures at new position, if any.
        int numTreasures = this.treasurePostion[newPosition.getX()][newPosition.getY()];

        if (numTreasures > 0) {
            this.treasurePostion[newPosition.getX()][newPosition.getY()] = 0;
            randomAssignTreasure(numTreasures);
            this.playerTreasureMap.put(playerId, playerTreasureMap.get(playerId) + numTreasures);

            Logging.printInfo("Treasure Aquired!!!! PlayerID:" + playerId + " at X:" + newPosition.getX() + " Y: " + newPosition.getY());
        }
    }

    private void randomAssignTreasure(int numTreasures) {
        Position position = getAvailRandomPosition();
        treasurePostion[position.getX()][position.getY()] = numTreasures;
    }

    private boolean checkValidPosition(Position newPosition) {
        return isValidCell(newPosition.getX(), newPosition.getY()) &&
                isVacantCell(newPosition.getX(), newPosition.getY());
    }

    private boolean isValidCell(Integer x, Integer y) {
        return 0 <= x && x < gridSize && 0 <= y && y < gridSize;
    }

    private Position getNewPosition(Position position, Direction direction) {
        switch (direction) {
            case N:
                return new Position(position.getX(), position.getY() + 1);
            case E:
                return new Position(position.getX() + 1, position.getY());
            case S:
                return new Position(position.getX(), position.getY() - 1);
            case W:
                return new Position(position.getX() - 1, position.getY());
            default:
                return new Position(position.getX(), position.getY());
        }
    }

    public void playerQuit(String playerId) {
        // remove player from playerPosition;
        Position position = this.playerPositionMap.get(playerId);
        playerPositionList[position.getX()][position.getY()] = null;

        // remove player from playerLastMoveMap
        playerLastMoveMap.remove(playerId);
        playerTreasureMap.remove(playerId);
    }


    // Method for update player list by removing the unexists player List
    public void updatePlayerList(List<String> existPlayerList) {
        Set<String> playerIdSet = playerPositionMap.keySet();
        playerIdSet.removeAll(existPlayerList);
        playerIdSet.parallelStream().forEach(playerId -> playerQuit(playerId));
    }
}
