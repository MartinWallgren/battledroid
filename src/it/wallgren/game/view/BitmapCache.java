package it.wallgren.game.view;

import it.wallgren.game.util.Cache;
import android.graphics.Bitmap;

public class BitmapCache extends Cache<String, Bitmap> {
	private static final int LIMIT = 50;

	public BitmapCache() {
		super(LIMIT);
	}

	@Override
	public Bitmap get(String key) {
		// treat recycled bitmaps as non existing
		Bitmap bm = super.get(key);
		if (bm != null && bm.isRecycled()) {
			super.delete(key);
			bm = null;
		}
		return bm;
	}
}
