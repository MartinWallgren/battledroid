package it.wallgren.game.engine.items.event;

import it.wallgren.game.engine.items.Cell;
import it.wallgren.game.engine.items.Player;

public class PlayerEvent implements ItemEvent {
	public enum Type {
		WALK_EAST, WALK_SOUTH, WALK_WEST, WALK_NORTH, STOP, SELECTED_MOVE, MOVE_COMPLETED, GEM_ACTIVATED, GEM_COMPLETE, COLLISION, PLAYER_HURT;
	}

	private final Player player;
	private final Type type;
	private final Cell cell;

	public PlayerEvent(Player player, Type type) {
		this(player, type, null);
	}

	public PlayerEvent(Player player, Type type, Cell cell) {
		this.type = type;
		this.player = player;
		this.cell = cell;
	}

	public Type getType() {
		return type;
	}

	public Player getPlayer() {
		return player;
	}

	public Cell getCell() {
		return cell;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + type.name();
	}
}
