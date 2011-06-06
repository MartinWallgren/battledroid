package it.wallgren.game.engine.items;

import it.wallgren.game.engine.Velocity;
import it.wallgren.game.engine.items.event.ItemEvent;

public abstract class DynamicItem<T extends ItemEvent> extends AbstractItem<T> {
	protected Velocity xVelocity;
	protected Velocity yVelocity;
	protected Velocity zVelocity;

	protected int destinationX;
	protected int destinationY;
	protected int destinationZ;

	public void onTick(long timestamp) {
		if (xVelocity != null && xVelocity.getSpeed() != 0) {
			// super.setX to avoid resetting the velocity position
			super.setX((int) xVelocity.getPosition(timestamp));
			if (destinationX - getX() >= 0 != xVelocity.getDirection() > 0) {
				// We have passed the intended destination, stop movement
				setX(destinationX);
				setXVelocity(null);
			}
		}
		if (yVelocity != null && yVelocity.getSpeed() != 0) {
			// super.setY to avoid resetting the velocity position
			super.setY((int) yVelocity.getPosition(timestamp));
			if (destinationY - getY() >= 0 != yVelocity.getDirection() > 0) {
				// We have passed the intended destination, stop movement
				setY(destinationY);
				setYVelocity(null);
			}
		}
		if (zVelocity != null && zVelocity.getSpeed() != 0) {
			// super.setZ to avoid resetting the velocity position
			super.setZ((int) zVelocity.getPosition(timestamp));
			if (destinationZ - z >= 0 != zVelocity.getDirection() > 0) {
				// We have passed the intended destination, stop movement
				z = destinationZ;
				setZVelocity(null);
			}
		}
	}

	public Velocity getXVelocity() {
		return xVelocity;
	}

	public void setXVelocity(Velocity xVelocity) {
		this.xVelocity = xVelocity;
		if (xVelocity != null) {
			xVelocity.setPosition(getX());
		}
	}

	public Velocity getYVelocity() {
		return yVelocity;
	}

	public void setYVelocity(Velocity yVelocity) {
		this.yVelocity = yVelocity;
		if (yVelocity != null) {
			yVelocity.setPosition(getY());
		}
	}

	public Velocity getZVelocity() {
		return zVelocity;
	}

	public void setZVelocity(Velocity zVelocity) {
		this.zVelocity = zVelocity;
		if (zVelocity != null) {
			zVelocity.setPosition(z);
		}
	}

	public void setVelocity(Velocity x, Velocity y, Velocity z, int destinationX, int destinationY,
			int destinationZ) {
		xVelocity = x;
		yVelocity = y;
		zVelocity = z;
		this.destinationX = destinationX;
		this.destinationY = destinationY;
		this.destinationZ = destinationZ;
		if (xVelocity != null) {
			xVelocity.setPosition(getX());
		}
		if (yVelocity != null) {
			yVelocity.setPosition(getY());
		}
		if (zVelocity != null) {
			zVelocity.setPosition(getZ());
		}
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		if (xVelocity != null) {
			xVelocity.setPosition(x);
		}
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		if (yVelocity != null) {
			yVelocity.setPosition(y);
		}
	}

	@Override
	public void setZ(int z) {
		super.setZ(z);
		if (zVelocity != null) {
			zVelocity.setPosition(z);
		}
	}

	public boolean isMoving() {
		return (xVelocity != null && xVelocity.getSpeed() != 0)
				|| (yVelocity != null && yVelocity.getSpeed() != 0)
				|| (zVelocity != null && zVelocity.getSpeed() != 0);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " (" + getX() + ", " + getY() + ")";
	}

	public abstract void onCollision(DynamicItem<? extends ItemEvent> item);
}
