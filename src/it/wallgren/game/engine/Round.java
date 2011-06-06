package it.wallgren.game.engine;

import it.wallgren.game.engine.items.Cell;
import it.wallgren.game.engine.items.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents one round of moves
 * 
 */
public class Round {
	private Cell playerOneMove;
	private Cell playerTwoMove;
	private long startTime;
	private long endTime;
	private long roundLength;

	private boolean playerOneMovementComplete;
	private boolean playerTwoMovementComplete;
	private List<Player> players;
	private Random random;

	public Round(Player playerOne, Player playerTwo) {
		players = new ArrayList<Player>(2);

		random = new Random();
		if (random.nextBoolean()) {
			players.add(playerOne);
			players.add(playerTwo);
		} else {
			players.add(playerTwo);
			players.add(playerOne);
		}
	}

	public void start(long roundLength) {
		this.roundLength = roundLength;
		startTime = GameClock.getTime();
		endTime = startTime + roundLength;
	}
	
	public long getRoundLength() {
		return roundLength;
	}

	public Cell getPlayerOneMove() {
		return playerOneMove;
	}

	public void setPlayerOneMove(Cell playerOneMove) {
		this.playerOneMove = playerOneMove;
	}

	public Cell getPlayerTwoMove() {
		return playerTwoMove;
	}

	public void setPlayerTwoMove(Cell playerTwoMove) {
		this.playerTwoMove = playerTwoMove;
	}

	public List<Player> getPlayerOrder() {
		return players;
	}

	/**
	 * Determine if all players has made their move
	 * 
	 * @return true if the players have made their choices
	 */
	public boolean isPlayersReady() {
		return (playerOneMove != null && playerTwoMove != null);
	}

	public void setPlayerOneMovementComplete(boolean movementComplete) {
		this.playerOneMovementComplete = movementComplete;
	}

	public void setPlayerTwoMovementComplete(boolean movementComplete) {
		this.playerTwoMovementComplete = movementComplete;
	}

	public boolean isComplete() {
		return (playerOneMovementComplete || playerOneMove == null) && (playerTwoMove == null || playerTwoMovementComplete);
	}

	public boolean isTimeUp() {
		return endTime <= GameClock.getTime();
	}

	public long getSelectionStartTime() {
		return startTime;
	}

	public long getSelectionEndTime() {
		return endTime;
	}
}
