package it.wallgren.game.engine;

import it.wallgren.game.engine.items.Cell;
import it.wallgren.game.engine.items.DynamicItem;
import it.wallgren.game.engine.items.Grid;
import it.wallgren.game.engine.items.Item;
import it.wallgren.game.engine.items.event.GemEvent;
import it.wallgren.game.engine.items.gem.Arrow;
import it.wallgren.game.engine.items.gem.Laser;
import it.wallgren.game.util.PositionUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Point;

/**
 * class for generating new and moving existing gems
 * 
 * @author martin
 * 
 */
public class GemGenerator {
	private GameModel model;
	private Random random = new Random();
	private Context context;
	private static final int GENERATION_FACTOR = 2;

	public GemGenerator(GameModel model) {
		this.model = model;
	}

	public void generateNewGems(List<Cell> cells) {
		generateNewGems(cells, GENERATION_FACTOR);
	}

	public void generateNewGems(List<Cell> cells, int factor) {
		for (Cell cell : cells) {
			if (random.nextInt(factor) == 0) {
				generateGem(cell);
			}
		}
	}

	public void generateGem(Cell cell) {
		DynamicItem<GemEvent> gem;
		if (random.nextBoolean()) {
			gem = new Laser(model.getGrid().getBounds());
		} else {
			gem = new Arrow(model.getGrid().getBounds(), model);
		}
		gem.setZ(GameModel.Z_INDEX_GEM);
		Point p = PositionUtil.centerItemOverItem(gem, cell);
		gem.setX(p.x);
		gem.setY(p.y);
		model.addItem(gem);
	}

	public List<Cell> getCenterCells() {
		List<Cell> cells = new LinkedList<Cell>();
		Grid grid = model.getGrid();
		int width = grid.getColumns();
		int height = grid.getRows();

		// There can be one or two center rows
		for (int i = 0; i < (height % 2 == 0 ? 2 : 1); i++) {
			int row = height / 2 - i;
			for (int col = 0; col < width; col++) {
				cells.add(grid.getCell(col, row));
			}
		}
		return cells;
	}

}
