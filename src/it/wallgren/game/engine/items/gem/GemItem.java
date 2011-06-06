package it.wallgren.game.engine.items.gem;

import it.wallgren.game.engine.Velocity;
import it.wallgren.game.engine.items.DynamicItem;
import it.wallgren.game.engine.items.Player;
import it.wallgren.game.engine.items.event.GemEvent;
import it.wallgren.game.engine.items.event.GemEvent.Type;

public abstract class GemItem extends DynamicItem<GemEvent> {
	private boolean isMoving;
	private Player player;
	
	abstract void onPerformAction(Player player);

	@Override
	public void onTick(long timestamp) {
		super.onTick(timestamp);
		if (isMoving && getYVelocity() == null && getXVelocity() == null && getZVelocity() == null) {
			// We have stopped
			isMoving = false;
			dispatchEvent(new GemEvent(this, Type.MOVEMENT_COMPLETE));
		}
	}
	
	public Player getPlayer() {
		return player;
	}

	@Override
	public void setVelocity(Velocity x, Velocity y, Velocity z, int destinationX, int destinationY, int destinationZ) {
		super.setVelocity(x, y, z, destinationX, destinationY, destinationZ);
		isMoving = x != null || y != null || z != null;
	}
	
	public final void performAction(Player player) {
		this.player = player;
	    dispatchEvent(new GemEvent(this, Type.ACTION_STARTED));
	    onPerformAction(player);
	}
}
