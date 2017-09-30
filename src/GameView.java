/**
 * Utility class to present the game to the user.
 */
public class GameView {
    /**
     * Prints a summary of the game so far.
     *
     * @param game
     * @param playerId The player viewing the summary.
     */
    public static void printGameSummary(GameStatus game, String playerId, String masterId) {
        System.out.println("Status for player Id : " + playerId + " Master ID " + masterId );
        System.out.println();
        System.out.println(GameView.getGraphicalMaze(game, playerId));
        GameView.printPlayerInformation(game, playerId);
    }

    static String getGraphicalMaze(GameStatus gameStatus, String playerId) {
        StringBuilder sb = new StringBuilder();
        // Top of the maze.
        sb.append('+');
        for (int k = 0; k < gameStatus.getGridSize(); k++) {
            sb.append("--------+");
        }

        // Each row.
        for (int i = gameStatus.getGridSize() - 1; i >= 0; i--) {
            // Padding top.
            sb.append("\n+");
            for (int j = 0; j < gameStatus.getGridSize(); j++) {
                sb.append("        +");
            }

            sb.append("\n+");
            for (int j = 0; j < gameStatus.getGridSize(); j++) {
                // Figure out what is in the cell (If any).
                // NOTE: The coordinates here are flipped.
                String playerIdAtCell = gameStatus.getPlayerAt(j, i);
                int numTreasuresAtCell = gameStatus.getTreasureAt(j, i);
                String contents;
                if (playerIdAtCell != null) {
                    String playerIdentifier = "P" + playerIdAtCell;
                    playerIdentifier += ",T" + gameStatus.getPlayerTreasureMap().get(playerIdAtCell);
                    if (playerIdAtCell.equals(playerId)) {
                        playerIdentifier += "*";
                    }
                    if (playerIdentifier.length() < 8) {
                        playerIdentifier = " " + playerIdentifier;
                    }
                    contents = String.format("%1$-8s+", playerIdentifier);
                } else if (numTreasuresAtCell > 0) {
                    contents = String.format(" T:%1$-4d +", numTreasuresAtCell);
                } else {
                    contents = "        +";
                }
                sb.append(contents);
            }

            // Padding bottom.
            sb.append("\n+");
            for (int j = 0; j < gameStatus.getGridSize(); j++) {
                sb.append("        +");
            }
            // Close the cell.
            sb.append("\n+");
            for (int j = 0; j < gameStatus.getGridSize(); j++) {
                sb.append("--------+");
            }
        }
        return sb.toString();
    }

    static void printPlayerInformation(GameStatus gameStatus, String playerId) {
        Integer treasureCount = gameStatus.getPlayerTreasureMap().get(playerId);
        Position position = gameStatus.getPlayerPositionMap().get(playerId);
        System.out.println(
                String.format(
                        "You are currently at %d, %d and have %d treasures.",
                        position.getX(),
                        position.getY(),
                        treasureCount));
    }
}
