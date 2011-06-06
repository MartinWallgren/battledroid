package it.wallgren.game.engine.items;

import it.wallgren.game.engine.PlayerTransport;
import it.wallgren.game.engine.Velocity;
import it.wallgren.game.engine.items.event.CellEvent;
import it.wallgren.game.engine.items.event.GemEvent;
import it.wallgren.game.engine.items.event.ItemEvent;
import it.wallgren.game.engine.items.event.ItemListener;
import it.wallgren.game.engine.items.event.PlayerEvent;
import it.wallgren.game.engine.items.event.PlayerEvent.Type;
import it.wallgren.game.engine.items.gem.GemItem;

import java.util.List;

public abstract class Player extends DynamicItem<PlayerEvent> implements ItemListener<CellEvent> {
	public static final int FULL_HP = 100;
	private PlayerTransport playerTransport;
	private String name;
	private int hp = FULL_HP;
	private GemItem activeGem;
	private DynamicItem<? extends ItemEvent> collidingItem;

	public Player(String name) {
		this.name = name;
		playerTransport = new PlayerTransport(this);
		addItemListener(playerTransport);
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public void inflictDamage(int hp) {
		this.hp -= hp;
		if (this.hp <= 0) {
			this.hp = 0;
		}
		dispatchEvent(new PlayerEvent(this, Type.PLAYER_HURT));
	}

	@Override
	public void onTick(long timestamp) {
		super.onTick(timestamp);
		if (playerTransport != null) {
			playerTransport.onTick(timestamp);
		}
	}

	public void walkTo(int x, int y) {
		playerTransport.walkTo(x, y);
		if (x == getX() && y == getY()) {
			// We're already at the destination
			dispatchEvent(new PlayerEvent(this, Type.MOVE_COMPLETED));
		}
	}

	@Override
	public void setXVelocity(Velocity xVelocity) {
		super.setXVelocity(xVelocity);
		velocityChanged();
	}

	@Override
	public void setYVelocity(Velocity yVelocity) {
		super.setYVelocity(yVelocity);
		velocityChanged();
	}

	@Override
	public void setVelocity(Velocity x, Velocity y, Velocity z, int destinationX, int destinationY, int destinationZ) {
		super.setVelocity(x, y, z, destinationX, destinationY, destinationZ);
		velocityChanged();
	}

	private void velocityChanged() {
		PlayerEvent event = null;
		if (xVelocity != null && xVelocity.getSpeed() != 0) {
			if (xVelocity.getSpeed() > 0) {
				event = new PlayerEvent(this, PlayerEvent.Type.WALK_EAST);
			} else {
				event = new PlayerEvent(this, PlayerEvent.Type.WALK_WEST);
			}
		}
		if (yVelocity != null && yVelocity.getSpeed() != 0) {
			if (yVelocity.getSpeed() > 0) {
				event = new PlayerEvent(this, PlayerEvent.Type.WALK_SOUTH);
			} else {
				event = new PlayerEvent(this, PlayerEvent.Type.WALK_NORTH);
			}
		}
		if (event == null) {
			if (playerTransport.hasArrived()) {
				event = new PlayerEvent(this, PlayerEvent.Type.MOVE_COMPLETED);
			} else {
				event = new PlayerEvent(this, PlayerEvent.Type.STOP);
			}
		}
		dispatchEvent(event);
	}

	public abstract void makeMove(List<Cell> availableMoves);

	public void onEvent(CellEvent event) {
		switch (event.getType()) {
		case SELECTED:
			moveSelected(event.getCell());
			break;
		}
	}

	protected void moveSelected(Cell selectedCell) {
		dispatchEvent(new PlayerEvent(this, Type.SELECTED_MOVE, selectedCell));
	}

	@Override
	public String toString() {
		return "Player-" + name;
	}

	/**
	 * Called when the player has activated a gem. (for example, started to fire
	 * a weapon)
	 * 
	 * @param gemItem
	 */
	public void performAction(GemItem gemItem) {
		this.activeGem = gemItem;
		gemItem.addItemListener(new ItemListener<GemEvent>() {
			public void onEvent(GemEvent event) {
				switch(event.getType()) {
				case ACTION_COMPLETE:
					dispatchEvent(new PlayerEvent(Player.this, Type.GEM_COMPLETE));
					break;
				}
			}
		});
		dispatchEvent(new PlayerEvent(this, Type.GEM_ACTIVATED));
	}
	
	public GemItem getActiveGem() {
		return activeGem;
	}

	public DynamicItem<? extends ItemEvent> getCollidingItem() {
		return collidingItem;
	}

	@Override
	public void onCollision(DynamicItem<? extends ItemEvent> item) {
		if (collidingItem != item) {
			this.collidingItem = item;
			dispatchEvent(new PlayerEvent(this, Type.COLLISION));
		}
	}

	public void reset() {
		setHp(FULL_HP);
		activeGem = null;
	}

	@Override
	public void setWidth(int width) {
		super.setWidth(width);
	}

	@Override
	public void setHeight(int height) {
		super.setHeight(height);
	}
}
