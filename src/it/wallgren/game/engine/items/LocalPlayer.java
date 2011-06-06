package it.wallgren.game.engine.items;

import static it.wallgren.game.util.CollectionUtil.isNullOrEmpty;

import java.util.List;

public class LocalPlayer extends Player {
	private List<Cell> availableMoves;
	public LocalPlayer(String name) {
		super(name);
	}

	@Override
	public void makeMove(List<Cell> availableMoves) {
		this.availableMoves = availableMoves;
		for (Cell cell : availableMoves) {
			cell.setSelectable(true);
			cell.addItemListener(this);
		}
	}
	
	@Override
	protected void moveSelected(Cell selectedCell) {
		if (!isNullOrEmpty(availableMoves)) {
			for (Cell cell : availableMoves) {
				cell.setSelectable(false);
				cell.removeItemListener(this);
			}
		}
		super.moveSelected(selectedCell);
	}
}
