package it.wallgren.game.view;

import it.wallgren.game.engine.items.Item;
import it.wallgren.game.engine.items.event.GameEvent;
import it.wallgren.game.engine.items.event.GameListener;
import it.wallgren.game.util.Logger;
import it.wallgren.game.view.sprites.Sprite;
import it.wallgren.game.view.sprites.SpriteCollection;
import it.wallgren.game.view.sprites.SpriteFactory;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

public class Scene implements InputHandler, GameListener {

	@SuppressWarnings("unused")
	private static final String TAG = Scene.class.getSimpleName();
	private SpriteCollection sprites;
	private Object SPRITE_LOCK = new Object();
	private Context context;

	public Scene(Context context) {
		this.context = context;
		sprites = new SpriteCollection();
	}

	public void addSprite(Sprite<?, ?> sprite) {
		synchronized (SPRITE_LOCK) {
			sprites.add(sprite);
		}
	}

	public void removeSprite(Sprite<?, ?> sprite) {
		synchronized (SPRITE_LOCK) {
			sprites.remove(sprite);
		}
	}

	public void onDraw(Canvas canvas, Canvas glassPane) {
		synchronized (SPRITE_LOCK) {
			canvas.drawColor(Color.BLACK);
			for (Sprite<?, ?> sprite : sprites) {
				sprite.onDraw(canvas, glassPane);
			}
		}
	}

	/**
	 * Get the sprites that occupy the given position
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private List<Sprite<?, ?>> getSprites(int x, int y) {
		LinkedList<Sprite<?, ?>> matchingSprites = new LinkedList<Sprite<?, ?>>();
		synchronized (SPRITE_LOCK) {
			for (Sprite<?, ?> sprite : sprites) {
				if (coversXY(sprite, x, y)) {
					matchingSprites.add(sprite);
				}
			}
		}
		return matchingSprites;
	}

	private boolean coversXY(Sprite<?, ?> sprite, int x, int y) {
		return x >= sprite.getX() && x < sprite.getX() + sprite.getWidth() && y >= sprite.getY() && y < sprite.getY() + sprite.getHeight();
	}

	public boolean onTouch(View v, MotionEvent event) {
		List<Sprite<?, ?>> touchedSprites = getSprites((int) event.getX(), (int) event.getY());
		if (touchedSprites.size() > 0) {
			for (Sprite<?, ?> sprite : touchedSprites) {
				if (sprite.onTouch(v, event)) {
					break;
				}
			}
		}
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onEvent(GameEvent event) {
		switch (event.getType()) {
		case ITEM_ADDED:
			addSprite(SpriteFactory.get(context, (Item) event.getData()));
			break;
		case ITEM_REMOVED:
			synchronized (SPRITE_LOCK) {
				for (Sprite sprite : sprites) {
					if (sprite.getItem().equals(event.getData())) {
						removeSprite(sprite);
						break;
					}
				}
			}
			break;
		case SELECTION_TIME_UP:
			synchronized (SPRITE_LOCK) {
				for (Sprite sprite : sprites) {
					if (sprite.getItem().equals(event.getData())) {
						removeSprite(sprite);
					}
				}
			}
			break;
		}
	}
}
