package it.wallgren.game.engine.items;

import android.graphics.Rect;


public interface Item {

	/**
	 * Returns the x coordinate of this item
	 * 
	 * @return
	 */
	public int getX();

	/**
	 * Returns the y coordinate of this item
	 * 
	 * @return
	 */
	public int getY();

	/**
	 * Returns the z coordinate of this item
	 * 
	 * @return
	 */
	public int getZ();

	public int getWidth();

	public int getHeight();
	
	public Rect getBounds();

	public void setWidth(int width);

	public void setHeight(int height);

	/**
	 * Some time has passed, take appropriate action
	 * 
	 * @param timestamp
	 */
	public void onTick(long timestamp);
}