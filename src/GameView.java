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
        System.out.println("\n\nWelcome to the game, Player " + playerId + "!");
        GameView.getGraphicalMaze(gameStatus, masterId, slaveId);
        System.out.println();
    }

    static List<String> printRow(GameStatus gameStatus, int gridRow) {
        List<String> mazeRows = new ArrayList<String>();
        String s1 = "";
        String s2 = "";

        // print a row like the following and display player id and treasure as *
        // +----+----+----+----+----+
        // | ab |    |    |    |    |
        // +----+----+----+----+----+
        for (int gridCol = 0; gridCol < gameStatus.getGridSize(); gridCol++) {
            s1 += "+----";

            if (gameStatus.getPlayerAt(gridRow, gridCol) != null) {
                s2 += "| " + gameStatus.getPlayerAt(gridRow, gridCol) + " ";
            } else if (gameStatus.getTreasureAt(gridRow, gridCol) == 1) {
                s2 += "| *  ";
            } else {
                s2 += "|    ";
            }
        }
        mazeRows.add(s1 + "+");
        mazeRows.add(s2 + "|");

        return mazeRows;
    }

    static void getGraphicalMaze(GameStatus gameStatus, String masterId, String slaveId) {
        List<String> gameView  = new ArrayList<String>();
        List<String> playersInfo = printPlayerInformation(gameStatus, masterId, slaveId);
        Integer count = 0;
        String space_prefix = "                   ";
        for (int i = 0; i < gameStatus.getGridSize(); i++) {
            String s1 = "";
            if (count < playersInfo.size()) {
                s1 += playersInfo.get(count) + "  ";
                count++;
            } else {
                s1 += space_prefix;
            }

            String s2 = "";
            if (count < playersInfo.size()) {
                s2 += playersInfo.get(count) + "  ";
                count++;
            } else {
                s2 += space_prefix;
            }

            for (int gridCol = 0; gridCol < gameStatus.getGridSize(); gridCol++) {
                s1 += "+----";

                if (gameStatus.getPlayerAt(i, gridCol) != null) {
                    s2 += "| " + gameStatus.getPlayerAt(i, gridCol) + " ";
                } else if (gameStatus.getTreasureAt(i, gridCol) == 1) {
                    s2 += "| *  ";
                } else {
                    s2 += "|    ";
                }
            }
            gameView.add(s1 + "+");
            gameView.add(s2 + "|");
        }

        String s1 = "";
        if (count < playersInfo.size()) {
            s1 += playersInfo.get(count) + "  ";
            count++;
        } else {
            s1 += space_prefix;
        }

        s1 += '+';
        for (int k = 0; k < gameStatus.getGridSize(); k++) {
            s1 += "----+";
        }
        gameView.add(s1);

        while (count < playersInfo.size()) {
            gameView.add(playersInfo.get(count));
            count++;
        }

        for (String s: gameView)
            System.out.print(s + "\n");
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

    static List<String> printPlayerInformation(GameStatus gameStatus, String masterId, String slaveId) {
        List<String> playersInfo = new ArrayList<String>();
        // print top
        playersInfo.add("+-------+-------+");
        playersInfo.add("|  ID   | Score |");
        playersInfo.add("+-------+-------+");

        // print master
        playersInfo.add(
                "| " + masterId + "(M) |" +
                placeScoreInMiddle(gameStatus.getPlayerTreasureMap().get(masterId).toString(), 7) +
                "|"
        );
        playersInfo.add("+-------+-------+");


        // print slave if exists
        if (slaveId != null && slaveId != "") {
            try {
                playersInfo.add(
                        "| " + slaveId + "(S) |" +
                                placeScoreInMiddle(gameStatus.getPlayerTreasureMap().get(slaveId).toString(), 7) +
                                "|"
                );
                playersInfo.add("+-------+-------+");
            } catch (Exception e)
            {
                e.printStackTrace();
                Logging.printException(e);
            }

        }

        // print the rest players
        Map<String, Integer> playTreasureMap = gameStatus.getPlayerTreasureMap();
        for (Map.Entry<String, Integer> entry : playTreasureMap.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(masterId) && !entry.getKey().equalsIgnoreCase(slaveId))
            {
                playersInfo.add(
                        "|  " + entry.getKey() + "   |" +
                        placeScoreInMiddle(entry.getValue().toString(), 7) +
                        "|"
                );
                playersInfo.add("+-------+-------+");
            }
        }
        return playersInfo;
    }
}
