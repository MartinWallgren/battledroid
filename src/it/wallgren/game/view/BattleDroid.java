package it.wallgren.game.view;

import it.wallgren.game.GameApplication;
import it.wallgren.game.R;
import it.wallgren.game.engine.GameModel;
import it.wallgren.game.engine.items.DynamicItem;
import it.wallgren.game.engine.items.Grid;
import it.wallgren.game.engine.items.event.GameEvent;
import it.wallgren.game.engine.items.event.GameListener;
import it.wallgren.game.engine.items.event.ItemEvent;
import it.wallgren.game.view.sprites.GridSprite;
import it.wallgren.game.view.sprites.SpriteFactory;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class BattleDroid extends Activity implements GameListener {
	@SuppressWarnings("unused")
	private static final String TAG = BattleDroid.class.getSimpleName();
	private BoardView board;
	private Scene scene;
	private GameModel model;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	protected void onResume() {
		GameApplication gameApplication = (GameApplication) getApplication();
		gameApplication.init();
		scene = new Scene(gameApplication);
		model = gameApplication.getModel();

		scene.addSprite(new GridSprite(gameApplication, (Grid) model.getBackgroundItem()));

		List<DynamicItem<? extends ItemEvent>> items = model.getItems();

		for (DynamicItem<? extends ItemEvent> item : items) {
			scene.addSprite(SpriteFactory.get(gameApplication, item));
		}

		model.addGameListener(scene);
		model.addGameListener(this);
		setContentView(R.layout.game);
		FrameLayout gameLayout = (FrameLayout) findViewById(R.id.gameLayout);
		board = new BoardView(this, scene);
		gameLayout.addView(board);
		board.setOnTouchListener(scene);
		super.onResume();
	}

	@Override
	protected void onPause() {
		GameApplication gameApplication = (GameApplication) getApplication();
		GameModel model = gameApplication.getModel();
		model.removeGameListener(scene);
		model.removeGameListener(this);
		model = null;
		scene = null;
		board = null;
		super.onPause();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Pause");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		GameApplication gameApplication = (GameApplication) getApplication();
		gameApplication.getEngine().pauseGame(!model.isPaused());
		return true;
	}
	
	public void onEvent(GameEvent event) {
		switch (event.getType()) {
		case GAME_OVER:
			GameApplication gameApplication = (GameApplication) getApplication();
			gameApplication.getEngine().pauseGame(true);
			runOnUiThread(new Runnable() {
				public void run() {
					Builder builder = new AlertDialog.Builder(BattleDroid.this);
					builder.setTitle("GAME OVER");
					String winner;
					if(model.getPlayerOne().getHp() > model.getPlayerTwo().getHp()) {
						winner = model.getPlayerOne().toString();
					} else {
						winner = model.getPlayerTwo().toString();
					}
					builder.setMessage("Winner is\n" + winner);
					builder.setOnCancelListener(new OnCancelListener() {
						public void onCancel(DialogInterface dialog) {
							GameApplication gameApplication = (GameApplication) getApplication();
							model.reset();
							gameApplication.getEngine().pauseGame(false);
						}
					});
					builder.show();
				}
			});
			break;
		}
	}
}