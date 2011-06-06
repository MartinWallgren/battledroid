package it.wallgren.game.engine.items.gem;

import it.wallgren.game.engine.GameClock;
import it.wallgren.game.engine.items.DynamicItem;
import it.wallgren.game.engine.items.Player;
import it.wallgren.game.engine.items.event.GemEvent;
import it.wallgren.game.engine.items.event.GemEvent.Type;
import it.wallgren.game.engine.items.event.ItemEvent;
import it.wallgren.game.util.MathUtil;

import java.util.Random;

import android.graphics.Rect;

public class Laser extends GemItem {
	private static final Random RANDOM = new Random();
	private static Double SPEED = 0.9; // Speed of the fired laser beam
	private long actionStarted;
	private boolean active;
	private Rect bounds;

	/* Rectangle containing the fired laser */
	private Rect shotBounds;

	private Player collidedPlayer;
	private static final int DAMAGE_MAX = 25;
	private static final int DAMAGE_MIN = 10;

	/**
	 * 
	 * @param bounds
	 *            the bounds confining how far the laser will reach
	 */
	public Laser(Rect bounds) {
		// TODO: Fix the size hack
		setWidth(48);
		setHeight(48);
		this.bounds = bounds;
	}

	@Override
	void onPerformAction(Player player) {
		active = true;
		int centerY = getCenterY();
		int centerX = getCenterX();
		shotBounds = new Rect(centerX, centerY, centerX, centerY);
		actionStarted = GameClock.getTime();
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public void onTick(long timestamp) {
		super.onTick(timestamp);
		if (active && isComplete()) {
			active = false;
			dispatchEvent(new GemEvent(this, Type.ACTION_COMPLETE));
		}
		if (active) {
			int length = (int) ((timestamp - actionStarted) * SPEED);
			shotBounds.top = getCenterY() - length;
			if (shotBounds.top < bounds.top) {
				shotBounds.top = bounds.top;
			}

			shotBounds.right = getCenterX() + length;
			if (shotBounds.right > bounds.right) {
				shotBounds.right = bounds.right;
			}

			shotBounds.bottom = getCenterY() + length;
			if (shotBounds.bottom > bounds.bottom) {
				shotBounds.bottom = bounds.bottom;
			}

			shotBounds.left = getCenterX() - length;
			if (shotBounds.left < bounds.left) {
				shotBounds.left = bounds.left;
			}
		}
	}

	private boolean isComplete() {
		return (shotBounds.left <= bounds.left && shotBounds.top <= bounds.top
				&& shotBounds.right >= bounds.right && shotBounds.bottom >= bounds.bottom)
				&& GameClock.getTime() > actionStarted + 2000;
	}

	public long getActionStarted() {
		return actionStarted;
	}

	public int getLengthNorth() {
		return getCenterY() - shotBounds.top + 1;
	}

	public int getLengthSouth() {
		return shotBounds.bottom - getCenterY() + 1;
	}

	public int getLengthEast() {
		return shotBounds.right - getCenterX() + 1;
	}

	public int getLengthWest() {
		return getCenterX() - shotBounds.left + 1;
	}

	public boolean isColliding(Player player) {
		if (!active || player.equals(getPlayer())) {
			// we don't want to shoot our self
			return false;
		}

		Rect playerBounds = player.getBounds();
		if (Rect.intersects(playerBounds, shotBounds)) {
			/*
			 * The rectangle surrounding the shot has reached the other player,
			 * let's see if we are on colliding course
			 */
			boolean verticalIntersection = MathUtil.rangesIntersect(playerBounds.left,
					playerBounds.right, this.getCenterX() - 1, this.getCenterX() + 1);
			boolean horizontalIntersection = MathUtil.rangesIntersect(playerBounds.top,
					playerBounds.bottom, this.getCenterY() - 1, this.getCenterY() + 1);
			return (verticalIntersection || horizontalIntersection);
		}
		return false;
	}

	@Override
	public void onCollision(DynamicItem<? extends ItemEvent> item) {
		if (item instanceof Player && active) {
			Player player = (Player) item;
			if (!this.equals(player.getActiveGem()) && collidedPlayer == null) {
				collidedPlayer = player;

				collidedPlayer.inflictDamage(RANDOM.nextInt(DAMAGE_MAX - DAMAGE_MIN) + DAMAGE_MIN);
				dispatchEvent(new GemEvent(this, Type.ACTION_COLLISION, item));
			}
		}
	}

	public Player getCollidedPlayer() {
		return collidedPlayer;
	}
}
