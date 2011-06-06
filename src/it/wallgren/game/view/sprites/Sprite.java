package it.wallgren.game.view.sprites;

import it.wallgren.game.engine.items.AbstractItem;
import it.wallgren.game.engine.items.event.ItemEvent;
import it.wallgren.game.engine.items.event.ItemListener;
import it.wallgren.game.view.InputHandler;
import android.graphics.Canvas;

public abstract class Sprite<T extends ItemEvent, E extends AbstractItem<T>> implements InputHandler, ItemListener<T> {
	private E item;

	public Sprite(E item) {
		this.item = item;
		item.addItemListener(this);
	}

	public abstract void onDraw(Canvas canvas, Canvas glassPane);

	public int getX() {
		return item.getX();
	}

	public int getY() {
		return item.getY();
	}

	public int getZ() {
		return item.getZ();
	}
	
	public abstract int getWidth();
	
	public abstract int getHeight();

	public E getItem() {
		return item;
	}
}
