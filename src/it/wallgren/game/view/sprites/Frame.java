package it.wallgren.game.view.sprites;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class Frame {
	private Bitmap bitmap;
	private Rect bounds;

	public Frame(Bitmap bitmap, Rect bounds) {
		this.bitmap = bitmap;
		this.bounds = bounds;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Rect getBounds() {
		return bounds;
	}

	public void setBounds(Rect bounds) {
		this.bounds = bounds;
	}
}
