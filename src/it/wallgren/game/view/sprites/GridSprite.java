package it.wallgren.game.view.sprites;

import it.wallgren.game.GameApplication;
import it.wallgren.game.R;
import it.wallgren.game.engine.items.Grid;
import it.wallgren.game.engine.items.event.ItemEvent;
import it.wallgren.game.util.Logger;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

public class GridSprite extends Sprite<ItemEvent, Grid> {
	private CellSprite[][] cells;
	private GameApplication context;
	private int width;
	private int height;
	private CellSprite prevCell;
	private Bitmap bm;
	private Paint paint;
	private Matrix matrix;

	public GridSprite(GameApplication context, Grid item) {
		super(item);
		this.context = context;
		paint = new Paint();
		matrix = new Matrix();
		if (cells == null || bm == null || bm.isRecycled()) {
			createGrid();
		}
	}

	@Override
	public void onDraw(Canvas canvas, Canvas glassPane) {
		canvas.drawBitmap(bm, matrix, paint);
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				cells[x][y].onDraw(canvas, glassPane);
			}
		}
	}

	private void createGrid() {
		Grid grid = getItem();
		width = grid.getWidth();
		height = grid.getHeight();

		cells = new CellSprite[grid.getColumns()][grid.getRows()];
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				Drawable tile = getTile(x, y);
				cells[x][y] = new CellSprite(context, grid.getCell(x, y), tile);
			}
		}

		bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas bmCanvas = new Canvas(bm);
		bmCanvas.drawColor(Color.BLACK);
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				cells[x][y].drawBG(bmCanvas);
			}
		}
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	private CellSprite getCell(float x, float y) {
		int column = ((int) x) / (width / getItem().getColumns());
		int row = ((int) y) / (height / getItem().getRows());
		return getCell(column, row);
	}

	private CellSprite getCell(int column, int row) {
		return cells[column][row];
	}

	public boolean onTouch(View v, MotionEvent event) {
		CellSprite cell = getCell(event.getX(), event.getY());
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			activateCell(cell);
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			deactivateCell(cell);
			break;
		case MotionEvent.ACTION_MOVE:
			if (prevCell != null && cell != prevCell) {
				deactivateCell(cell);
			}
			activateCell(cell);
			break;
		default:
			if (prevCell != null) {
				deactivateCell(prevCell);
			}
		}
		prevCell = cell;
		return false;
	}

	private void activateCell(CellSprite cell) {
		if (cell.getItem().isSelectable()) {
			cell.getItem().setActive(true);
		}
	}

	private void deactivateCell(CellSprite cell) {
		cell.getItem().setActive(false);
	}

	private Drawable getTile(int x, int y) {
		int sprite = new Random().nextInt(14); // High upper limit to get an
												// overweight of tile1
		switch (sprite) {
		case 0:
			return context.getResources().getDrawable(R.drawable.tile1);
		case 1:
			return context.getResources().getDrawable(R.drawable.tile2);
		case 2:
			return context.getResources().getDrawable(R.drawable.tile3);
		case 3:
			return context.getResources().getDrawable(R.drawable.tile4);
		case 4:
			return context.getResources().getDrawable(R.drawable.tile5);
		case 5:
			return context.getResources().getDrawable(R.drawable.tile6);
		case 6:
			return context.getResources().getDrawable(R.drawable.tile7);
		default:
			return context.getResources().getDrawable(R.drawable.tile1);
		}
	}

	public void onEvent(ItemEvent event) {
	}
}
