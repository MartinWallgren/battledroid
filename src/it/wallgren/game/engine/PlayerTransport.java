package it.wallgren.game.engine;

import it.wallgren.game.GameApplication;
import it.wallgren.game.engine.items.Player;
import it.wallgren.game.engine.items.event.ItemListener;
import it.wallgren.game.engine.items.event.PlayerEvent;
import it.wallgren.game.util.Logger;

public class PlayerTransport implements ItemListener<PlayerEvent> {
	private static final float WALK_SPEED = 0.04f;
	private Player player;
	private boolean walking;

	int destinationX;
	int destinationY;

	public PlayerTransport(Player player) {
		this.player = player;
	}

	public void walkTo(int x, int y) {
		this.destinationX = x;
		this.destinationY = y;
		walking = true;
	}

	public void onTick(long timeStamp) {
		if (!player.isMoving() && walking) {
			float speed = WALK_SPEED;
			int diffX = destinationX - player.getX();
			int diffY = destinationY - player.getY();
			if (diffX != 0 && diffX * diffX > diffY * diffY) {
				if (diffX < 0) {
					speed *= -1;
				}
				player.setVelocity(new Velocity(timeStamp, speed), null, null, destinationX, destinationY, player.getZ());
			} else if (diffY != 0) {
				if (diffY < 0) {
					speed *= -1;
				}
				player.setVelocity(null, new Velocity(timeStamp, speed), null, destinationX, destinationY, player.getZ());
			}
		}
	}

	public void onEvent(PlayerEvent event) {
		switch (event.getType()) {
		case MOVE_COMPLETED:
			walking = false;
			break;
		default:
			break;
		}
	}

	/**
	 * Returns true if the player is walking and has reach the destination
	 * 
	 * @return
	 */
	public boolean hasArrived() {
		if(GameApplication.DEBUG) {
			Logger.d(this, "hasArrived " + player + "[" + player.getX() + ", " + player.getY() + "] destination=[" + destinationX + ", " + destinationY + "]");
		}
		return walking && player.getX() == destinationX && player.getY() == destinationY;
	}
}
