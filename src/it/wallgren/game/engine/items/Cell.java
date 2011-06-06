package it.wallgren.game.engine.items;

import it.wallgren.game.engine.items.event.CellEvent;
import it.wallgren.game.engine.items.event.CellEvent.Type;

public class Cell extends AbstractItem<CellEvent> {
	private boolean active;
	private boolean selectable;
	
	private int column;
	
	private int row;
	
	public Cell(int column, int row) {
		this.column = column;
		this.row = row;
	}

	public void onTick(long timestamp) {
		
	}

	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
		if(active) {
			dispatchEvent(new CellEvent(this, Type.SELECTED));
		}
	}
	
	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	@Override
	public String toString() {
		return Cell.class.getSimpleName() + "[" + column + ", " + row + "]";
	}
}
