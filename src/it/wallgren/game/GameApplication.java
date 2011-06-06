package it.wallgren.game;

import it.wallgren.game.engine.Engine;
import it.wallgren.game.engine.GameModel;
import it.wallgren.game.engine.items.AiPlayer;
import it.wallgren.game.engine.items.LocalPlayer;
import it.wallgren.game.view.BitmapCache;
import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class GameApplication extends Application {
	public static final boolean DEBUG = false;
	private Rect screen;
	private Engine engine;
	private GameModel model;
	private BitmapCache bitmapCache;

	@Override
	public void onCreate() {
		super.onCreate();
		bitmapCache = new BitmapCache();
	}

	public synchronized void init() {
		if (model == null) {
			model = new GameModel(this);
			engine = new Engine(model);
			model.setPlayerOne(new LocalPlayer("1"));
			model.setPlayerTwo(new AiPlayer("2", this));
			engine.startGame();
		}
	}

	public GameModel getModel() {
		return model;
	}

	/**
	 * Get a Rect representing the screen. Taking dpi in to account
	 * 
	 * @return
	 */
	public Rect getScreen() {
		if (screen == null) {
			synchronized (this) {
				if (screen == null) {
					Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
					DisplayMetrics metrics = new DisplayMetrics();
					display.getMetrics(metrics);
					screen = new Rect();
					screen.set(0, 0, (int) (display.getWidth() / metrics.density), (int) (display.getHeight() / metrics.density));
				}
			}
		}
		return screen;
	}

	public Engine getEngine() {
		return engine;
	}

	public BitmapCache getBitmapCache() {
		return bitmapCache;
	}
}
