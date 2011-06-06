package it.wallgren.game.engine;

import it.wallgren.game.util.Logger;



public class Velocity {
	private long timestamp;
	private float speed;
	private float position;

	public Velocity(long timestamp, float speed) {
		this.timestamp = timestamp;
		this.speed = speed;
	}

	public float getPosition(long now) {
		long timespan = now - timestamp;
		timestamp = now;
		position += speed * timespan;
		return position;
	}

	/**
	 * Get the speed in units per millisecond
	 * 
	 * @return
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * set the speed in units per millisecond
	 * 
	 * @param speed
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setPosition(float position) {
		this.position = position;
	}

	public int getDirection() {
		if (speed == 0) {
			return 0;
		}
		return speed > 0 ? 1 : -1;
	}
	
}
