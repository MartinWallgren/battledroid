package it.wallgren.game.view.sprites;

import it.wallgren.game.engine.items.Cell;
import it.wallgren.game.engine.items.event.CellEvent;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

public class CellSprite extends Sprite<CellEvent, Cell> {

	private Rect bounds;
	private Drawable standardDrawable;
	private Paint paint;

	public CellSprite(Context context, Cell item, Drawable standardDrawable) {
		super(item);
		this.bounds = new Rect(item.getX(), item.getY(), item.getX() + item.getWidth(), item.getY() + item.getHeight());
		this.standardDrawable = standardDrawable;
		paint = new Paint();
	}

	@Override
	public void onDraw(Canvas canvas, Canvas glassPane) {
		if (getItem().isActive()) {
			paint.setARGB(100, 255, 0, 0);
			canvas.drawRect(bounds, paint);
		} else if (getItem().isSelectable()) {
			paint.setARGB(100, 0, 255, 0);
			canvas.drawRect(bounds, paint);
		}
	}

	/**
	 * Draw the appearance of a non active cell to canvas
	 * 
	 * @param canvas
	 */
	public void drawBG(Canvas canvas) {
		standardDrawable.setBounds(bounds);
		standardDrawable.draw(canvas);
	}

	@Override
	public int getWidth() {
		return bounds.width();
	}

	@Override
	public int getHeight() {
		return bounds.height();
	}

	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	public void onEvent(CellEvent event) {
	}
}
