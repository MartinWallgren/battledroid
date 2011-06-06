package it.wallgren.game.engine.items.event;

import it.wallgren.game.engine.items.Item;
import it.wallgren.game.engine.items.gem.GemItem;

public class GemEvent implements ItemEvent{
	public enum Type {
		MOVEMENT_COMPLETE, ACTION_STARTED, ACTION_COMPLETE, ACTION_COLLISION;
	}
	
	private final GemItem gem;
	private final Type type;
	private final Item item;
	
	public GemEvent(GemItem gem, Type type) {
		this(gem, type, null);
	}
	
	public GemEvent(GemItem gem, Type type, Item item) {
		this.gem = gem;
		this.type = type;
		this.item = item;
	}
	
	public GemItem getGem() {
		return gem;
	}
	
	public Type getType() {
		return type;
	}
	
	public Item getItem() {
		return item;
	}
	
	@Override
	public String toString() {
	    return getClass().getSimpleName() + type.name();
	}
}
