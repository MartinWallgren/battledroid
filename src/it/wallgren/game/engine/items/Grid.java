package it.wallgren.game.engine.items;

import it.wallgren.game.engine.GameModel;
import it.wallgren.game.engine.items.event.ItemEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Rect;
import android.os.Handler;

public class Grid extends AbstractItem<ItemEvent> {

	private Cell[][] items;
	private Rect bounds;

	public Grid(int columns, int rows, Rect bounds) {
		this.bounds = bounds;
		setX(bounds.left);
		setY(bounds.top);
		setWidth(bounds.width());
		setHeight(bounds.height());
		setZ(GameModel.Z_INDEX_BACKDROP);
		items = new Cell[columns][rows];

		int cellWidth = getWidth() / columns;
		int cellHeight = getHeight() / rows;

		for (int column = 0; column < items.length; column++) {
			for (int row = 0; row < items[column].length; row++) {
				Rect cellBounds = new Rect(column * cellWidth, row * cellHeight, (column + 1) * cellWidth - 1, (row + 1) * cellHeight - 1);
				items[column][row] = new Cell(column, row);
				items[column][row].setWidth(cellBounds.width());
				items[column][row].setHeight(cellBounds.height());
				items[column][row].setX(cellBounds.left);
				items[column][row].setY(cellBounds.top);
				items[column][row].setMessageHandler(getMessageHandler());
			}
		}
	}

	@Override
	public void setMessageHandler(Handler messageHandler) {
		super.setMessageHandler(messageHandler);
		for (int column = 0; column < items.length; column++) {
			for (int row = 0; row < items[column].length; row++) {
				items[column][row].setMessageHandler(getMessageHandler());
			}
		}
	}

	public int getRows() {
		return items[0].length;
	}

	public int getColumns() {
		return items.length;
	}

	public void onTick(long timestamp) {

	}

	/**
	 * Get the cell at the gridposition column, row
	 * 
	 * @param column
	 * @param row
	 * @return the cell
	 */
	public Cell getCell(int column, int row) {
		return items[column][row];
	}

	/**
	 * Get the cell at the position on actual pixel position x, y
	 * 
	 * @param x
	 * @param y
	 * @return the cell
	 */
	public Cell getCellByPosition(int x, int y) {
		int column = x / (getWidth() / getColumns());
		int row = y / (getHeight() / getRows());
		return items[column][row];
	}
	
	public List<Cell> getCells(){
		LinkedList<Cell> cells = new LinkedList<Cell>();
		for (int column = 0; column < items.length; column++) {
			for (int row = 0; row < items[column].length; row++) {
				cells.add(items[column][row]);
			}
		}
		return cells;
	}

	public void reset() {
		for (int column = 0; column < items.length; column++) {
			for (int row = 0; row < items[column].length; row++) {
				items[column][row].setActive(false);
				items[column][row].setSelectable(false);
			}
		}
	}

	public Rect getBounds() {
		return bounds;
	}
}
