package it.wallgren.game.util;

import it.wallgren.game.engine.items.Grid;
import it.wallgren.game.engine.items.Item;
import android.graphics.Point;

public class PositionUtil {

	/**
	 * Get the point where Item i is centered over Item i2
	 * 
	 * @param i
	 * @param i2
	 * @return
	 */
	public static Point centerItemOverItem(Item i, Item i2) {
		int x = i2.getX() + (i2.getWidth() - i.getWidth()) / 2;
		int y = i2.getY() + (i2.getHeight() - i.getHeight()) / 2;
		return new Point(x, y);
	}

	public static boolean isOutsideGrid(Grid grid, Item item) {
		return 	item.getX() < 0 || // item left of grid
				item.getY() < 0 || // item above grid
				item.getX() > grid.getWidth() - item.getWidth() || // item right of grid
				item.getY() > grid.getHeight() - item.getHeight(); // item below grid
	}
}
