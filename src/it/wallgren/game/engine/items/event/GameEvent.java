package it.wallgren.game.engine.items.event;

public class GameEvent implements Event {
	public enum Type {
		ITEM_ADDED, ITEM_REMOVED, SELECTION_TIME_UP, GEM_MOVEMENT_STARTED, GEM_MOVEMENT_COMPLETE, NEW_GEMS_GENERATED, GEM_COLLISIONS, GAME_OVER, PAUSE_STATE_CHANGED;
	}

	private final Type type;
	private final Object data;

	public GameEvent(Type type) {
		this(type, null);
	}

	public GameEvent(Type type, Object data) {
		this.type = type;
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public Type getType() {
		return type;
	}
	
	@Override
    public String toString() {
        return getClass().getSimpleName() + type.name();
    }
}
