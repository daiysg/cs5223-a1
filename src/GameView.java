package cs5223;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Utility class to present the game to the user.
 * @author michael
 *
 */
public class GameView {
	/**
	 * Prints a summary of the game so far.
	 * @param game
	 * @param playerId The player viewing the summary.
	 */
	static void printGameSummary(Game game, String playerId) {
		System.out.println(GameView.getGraphicalMaze(game, playerId));
		GameView.printPlayerInformation(game, playerId);
	}

	/**
	 * Prints the final game state.
	 * @param game
	 * @param playerId
	 */
	static void printGameOverSummary(Game game, String playerId) {
		GameView.printGameSummary(game, playerId);
		GameView.printGameOutcome(game, playerId);
	}

	static void printGameOutcome(Game game, String playerId) {
		Collection<Player> players = game.getPlayers();
		ArrayList<String> playerIdsWithMaxScore = new ArrayList<String>();
		int maxScore = 0;
		for (Player player : players) {
			int score = game.getPlayerTreasureCount(player.id);
			if (score > maxScore) {
				playerIdsWithMaxScore.clear();
				playerIdsWithMaxScore.add(player.id);
				maxScore = score;
			} else if (score == maxScore) {
				playerIdsWithMaxScore.add(player.id);
			}
		}

		if (playerIdsWithMaxScore.contains(playerId)) {
			System.out.println("YOU WON!");
		} else {
			System.out.println("YOU LOST!");
		}
	}

	static String getGraphicalMaze(Game game, String playerId) {
		StringBuilder mazeRepresentationStringBuilder = new StringBuilder();
		// Top of the maze.
		mazeRepresentationStringBuilder.append('+');
		for (int k = 0; k < game.getMazeSize(); k++) {
			mazeRepresentationStringBuilder.append("--------+");
		}

		// Each row.
		for (int i = game.getMazeSize() - 1; i >= 0; i--) {
			// Padding top.
			mazeRepresentationStringBuilder.append("\n+");
			for (int j = 0; j < game.getMazeSize(); j++) {
				mazeRepresentationStringBuilder.append("        +");
			}

			mazeRepresentationStringBuilder.append("\n+");
			for (int j = 0; j < game.getMazeSize(); j++) {
				// Figure out what is in the cell (If any).
				// NOTE: The coordinates here are flipped.
				Player playerAtCell = game.getPlayerAt(j, i);
				int numTreasuresAtCell = game.getNumTreasuresAt(j, i);
				String contents;
				if (playerAtCell != null) {
					String playerIdentifier = "P" + playerAtCell.id;
					playerIdentifier += ",T" + game.getPlayerTreasureCount(playerAtCell.id);
					if (playerAtCell.id.equals(playerId)) {
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
				mazeRepresentationStringBuilder.append(contents);
			}

			// Padding bottom.
			mazeRepresentationStringBuilder.append("\n+");
			for (int j = 0; j < game.getMazeSize(); j++) {
				mazeRepresentationStringBuilder.append("        +");
			}
			// Close the cell.
			mazeRepresentationStringBuilder.append("\n+");
			for (int j = 0; j < game.getMazeSize(); j++) {
				mazeRepresentationStringBuilder.append("--------+");
			}
		}
		return mazeRepresentationStringBuilder.toString();
	}

	static void printPlayerInformation(Game game, String playerId) {
		Player player = game.getPlayer(playerId);
		System.out.println(
			String.format(
				"You are currently at %d, %d and have %d treasures.",
				player.getXCoordinate(),
				player.getYCoordinate(),
				game.getPlayerTreasureCount(playerId)));
	}
}
