package it.wallgren.game.engine.items;

import it.wallgren.game.GameApplication;
import it.wallgren.game.engine.GameModel;
import it.wallgren.game.engine.items.gem.GemItem;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class AiPlayer extends Player {
	private Random random = new Random();
	private GameApplication context;

	/** Keep the ai player from moving for debug purposes. */
	private static final boolean FROZEN = false;

	public AiPlayer(String name, GameApplication context) {
		super(name);
		this.context = context;
	}

	@Override
	public void makeMove(List<Cell> availableMoves) {

		if (!FROZEN) {
			Cell cell;
			List<Cell> cellsWithGem = getCellsThatWillHaveGem(availableMoves);
			if (cellsWithGem.size() > 0) {
				int selectedIndex = random.nextInt(cellsWithGem.size());
				cell = cellsWithGem.get(selectedIndex);
			} else {
				int selectedIndex = random.nextInt(availableMoves.size());
				cell = availableMoves.get(selectedIndex);
			}

			moveSelected(cell);
		} else {
			// Don't move
			moveSelected(context.getModel().getGrid().getCellByPosition(getX(), getY()));
		}
	}

	private List<Cell> getCellsThatWillHaveGem(List<Cell> availableMoves) {
		List<Cell> cells = new LinkedList<Cell>();
		GameModel model = context.getModel();
		Grid grid = model.getGrid();
		List<GemItem> gems = model.getGems();
		int centerRow = grid.getRows() / 2;
		for (Cell cell : availableMoves) {
			Cell next;
			if (cell.getRow() < centerRow) {
				next = grid.getCell(cell.getColumn(), cell.getRow() + 1);
			} else {
				next = grid.getCell(cell.getColumn(), cell.getRow() - 1);
			}
			if (cellContainsGem(next, model, gems)) {
				cells.add(cell);
			}
		}
		return cells;
	}

	private boolean cellContainsGem(Cell cell, GameModel model, List<GemItem> gems) {
		List<AbstractItem<?>> items = model.getItems(cell.getCenterX(), cell.getCenterY());
		for (AbstractItem<?> item : items) {
			if (item instanceof GemItem) {
				return true;
			}
		}
		return false;
	}

}
