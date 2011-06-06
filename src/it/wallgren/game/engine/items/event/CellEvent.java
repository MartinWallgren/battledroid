package it.wallgren.game.engine.items.event;

import it.wallgren.game.engine.items.Cell;


public class CellEvent implements ItemEvent {
	public enum Type {
		SELECTED
	}
	
	private final Cell cell;
	private final Type type;
	
	
	public CellEvent(Cell cell, Type type) {
		this.cell = cell;
		this.type = type;
	}


	public Type getType() {
		return type;
	}
	
	public Cell getCell() {
		return cell;
	}
	
	@Override
	public String toString() {
		return cell + " " + type;
	}
}