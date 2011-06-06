package it.wallgren.game.engine;

import it.wallgren.game.engine.GameModel.State;
import it.wallgren.game.engine.items.Collision;
import it.wallgren.game.engine.items.Player;
import it.wallgren.game.engine.items.event.GameEvent;
import it.wallgren.game.engine.items.event.GameEvent.Type;
import it.wallgren.game.engine.items.event.GameListener;
import it.wallgren.game.engine.items.event.GemEvent;
import it.wallgren.game.engine.items.event.ItemListener;
import it.wallgren.game.engine.items.event.PlayerEvent;
import it.wallgren.game.engine.items.gem.GemItem;
import it.wallgren.game.util.PositionUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class StateMachine implements GameListener {
	private int gemCount;
	private GameModel model;
	private ItemListener<PlayerEvent> playerListener;

	private ItemListener<GemEvent> gemItemListener;
	private Queue<Collision> collisions;

	public StateMachine(GameModel model) {
		this.model = model;
		playerListener = new PlayerEventListener();
		gemItemListener = new GemEventListener();
	}

	private class PlayerEventListener implements ItemListener<PlayerEvent> {
		public void onEvent(PlayerEvent event) {
			Round currentRound = model.getCurrentRound();
			Player playerOne = model.getPlayerOne();
			Player playerTwo = model.getPlayerTwo();

			switch (event.getType()) {
			case SELECTED_MOVE:
				if (model.getState() == State.SELECTION_FASE) {
					if (currentRound != null && !currentRound.isPlayersReady()) {
						if (event.getPlayer() == playerOne) {
							currentRound.setPlayerOneMove(event.getCell());
						} else {
							if (event.getPlayer() == playerTwo) {
								currentRound.setPlayerTwoMove(event.getCell());
							}
						}
						if (currentRound.isPlayersReady()) {
							model.animateRound();
						}
					}
				}
				break;
			case MOVE_COMPLETED:
				if (currentRound != null && !currentRound.isComplete()) {
					if (event.getPlayer() == playerOne) {
						currentRound.setPlayerOneMovementComplete(true);
					} else if (event.getPlayer() == playerTwo) {
						currentRound.setPlayerTwoMovementComplete(true);
					}
					if (currentRound.isComplete()) {
						model.handleCollision();
					}
				}
				break;
			case PLAYER_HURT:
				if (event.getPlayer().getHp() <= 0) {
					model.setGameOver();
				}
			default:
				break;
			}
		}
	}

	private class GemEventListener implements ItemListener<GemEvent> {
		public void onEvent(GemEvent event) {
			switch (event.getType()) {
			case MOVEMENT_COMPLETE:
				GemItem gem = event.getGem();
				gem.removeItemListener(this);
				if (PositionUtil.isOutsideGrid(model.getGrid(), gem)) {
					model.removeItem(gem);
				}
				if (--gemCount == 0) {
					model.dispatchEvent(new GameEvent(Type.GEM_MOVEMENT_COMPLETE));
				}
				break;
			case ACTION_STARTED:
				break;
			case ACTION_COMPLETE:
				model.removeItem(event.getGem());
				if (collisions.size() > 0) {
					Collision collision = collisions.poll();
					gemAction(collision.getPlayer(), collision.getGem());
				} else {
					model.startNewRound();
				}
				break;
			}
		}
	}

	public ItemListener<PlayerEvent> getPlayerListener() {
		return playerListener;
	}

	public ItemListener<GemEvent> getGemItemListener() {
		return gemItemListener;
	}

	@SuppressWarnings("unchecked")
	public void onEvent(GameEvent event) {
		switch (event.getType()) {
		case GEM_MOVEMENT_STARTED:
			List<GemItem> gems = (List<GemItem>) event.getData();
			gemCount = gems.size();
			if (gemCount == 0) {
				model.dispatchEvent(new GameEvent(Type.GEM_MOVEMENT_COMPLETE));
			}
			break;
		case GEM_MOVEMENT_COMPLETE:
			model.generateNewGems();
			break;
		case NEW_GEMS_GENERATED:
			model.movePlayers();
			break;
		case GEM_COLLISIONS:
			collisions = new LinkedList<Collision>((LinkedList<Collision>) event.getData());
			if (collisions.size() > 0) {
				Collision collision = collisions.poll();
				gemAction(collision.getPlayer(), collision.getGem());
			} else {
				model.startNewRound();
			}
			break;
		default:
			break;
		}
	}

	void gemAction(Player player, GemItem gemItem) {
		gemItem.addItemListener(getGemItemListener());
		gemItem.performAction(player);
		player.performAction(gemItem);
	}
}
