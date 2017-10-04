/**
 * Utility class to present the game to the user.
 */

import java.util.*;

public class GameView {
    /**
     * Prints a summary of the game so far.
     *
     * @param gameStatus
     * @param playerId   The player viewing the summary.
     */
    public static void printGameSummary(GameStatus gameStatus, String playerId, String masterId, String slaveId) {
//        System.out.println("Status for player Id : " + playerId + " Master ID " + masterId );
//        System.out.println();

        System.out.println("\n\nWelcome to the game, Player " + playerId + "!");
        GameView.getGraphicalMaze(gameStatus);
        System.out.println();
        GameView.printPlayerInformation(gameStatus, masterId, slaveId);
    }

    static String printRow(GameStatus gameStatus, int gridRow) {
        StringBuilder sb_1 = new StringBuilder();
        StringBuilder sb_2 = new StringBuilder();

        // print a row like the following and display player id and treasure as *
        // +----+----+----+----+----+
        // | ab |    |    |    |    |
        // +----+----+----+----+----+
        for (int gridCol = 0; gridCol < gameStatus.getGridSize(); gridCol++) {
            sb_1.append("+----");

            if (gameStatus.getPlayerAt(gridRow, gridCol) != null) {
                sb_2.append("| " + gameStatus.getPlayerAt(gridRow, gridCol) + " ");
            } else if (gameStatus.getTreasureAt(gridRow, gridCol) == 1) {
                sb_2.append("| *  ");
            } else {
                sb_2.append("|    ");
            }
        }
        sb_1.append("+\n");
        sb_2.append("|\n");

        return sb_1.append(sb_2).toString();
    }

    static void getGraphicalMaze(GameStatus gameStatus) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < gameStatus.getGridSize(); i++) {
            sb.append(printRow(gameStatus, i));
        }

        sb.append('+');
        for (int k = 0; k < gameStatus.getGridSize(); k++) {
            sb.append("----+");
        }

        System.out.print(sb.toString() + "\n");
    }

    static String placeScoreInMiddle(String score, int space) {
        int prefix = (space - score.length()) / 2;
        int postfix = space - score.length() - prefix;
        String result = "";
        result += (prefix == 0 ? "" : String.format("%" + prefix + "s", " "));
        result += score;
        result += (postfix == 0 ? "" : String.format("%" + postfix + "s", " "));
        return result;
    }

    static String printPlayerInformation(GameStatus gameStatus, String masterId, String slaveId) {
        StringBuilder sb = new StringBuilder();
        // print top
        sb.append("+----+-------+-------+\n" +
                  "| ID | Score | Label |\n" +
                  "+----+-------+-------+\n");

        // print master
        sb.append("| " + masterId + " |");
        sb.append(placeScoreInMiddle(gameStatus.getPlayerTreasureMap().get(masterId).toString(), 7));
        sb.append("|   M   |\n");
        sb.append("+----+-------+-------+\n");


        // print slave if exists
        if (slaveId != "") {
            sb.append("| " + slaveId + " |");
            sb.append(placeScoreInMiddle(gameStatus.getPlayerTreasureMap().get(slaveId).toString(), 7));
            sb.append("|   S   |\n");
            sb.append("+----+-------+-------+\n");
        }

        // print the rest players
        Map<String, Integer> playTreasureMap = gameStatus.getPlayerTreasureMap();
        for (Map.Entry<String, Integer> entry : playTreasureMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(masterId) || entry.getKey().equalsIgnoreCase(slaveId))
                continue;
            sb.append("| " + entry.getKey() + " |");
            sb.append(placeScoreInMiddle(entry.getValue().toString(), 7));
            sb.append("|       |\n");
            sb.append("+----+-------+-------+\n");
        }
        System.out.print(sb.toString() + "\n");
        return sb.toString();

    }
}
