package it.wallgren.game.view.sprites;

import it.wallgren.game.GameApplication;
import it.wallgren.game.R;
import it.wallgren.game.engine.GameModel;
import it.wallgren.game.engine.items.Cell;
import it.wallgren.game.engine.items.Grid;
import it.wallgren.game.engine.items.Player;
import it.wallgren.game.engine.items.event.ItemListener;
import it.wallgren.game.engine.items.event.PlayerEvent;
import it.wallgren.game.util.PositionUtil;
import it.wallgren.game.view.TimeList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class PlayerSprite extends Sprite<PlayerEvent, Player> implements ItemListener<PlayerEvent> {
	private static final int X_COUNT = 4;
	private static final int Y_COUNT = 4;
	private Rect bounds = new Rect();
	private Bitmap bm;
	private int width;
	private int height;

	private TimeList<Frame> walkEast;
	private TimeList<Frame> walkNorth;
	private TimeList<Frame> walkSouth;
	private TimeList<Frame> walkWest;
	private TimeList<Frame> standStill;

	private TimeList<Frame> currentFrame;
	private boolean draw = true;

	public PlayerSprite(Context context, Player item) {
		super(item);
		item.addItemListener(this);
		if (item.toString().equals("Player-1")) {
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.testplayer);
		} else {
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.testplayer2);
		}
		width = bm.getWidth() / X_COUNT;
		height = bm.getHeight() / Y_COUNT;
		setupAnimations();

		/***
		 * Size hack, remove when size of player item can be set without
		 * knowledge of the Bitmap size
		 **/
		item.setWidth(width);
		item.setHeight(height);
		GameModel model = ((GameApplication) context.getApplicationContext()).getModel();
		Grid grid = model.getGrid();
		Cell cell = grid.getCellByPosition(item.getCenterX(), item.getCenterY());
		Point p = PositionUtil.centerItemOverItem(item, cell);
		item.setX(p.x);
		item.setY(p.y);
		/***************** Size hack end ********************************/
	}

	private void setupAnimations() {
		int frameLength = 175;
		walkEast = new TimeList<Frame>();
		walkEast.add(new Frame(bm, new Rect(0, 2 * height, width, 2 * height + height)), frameLength);
		walkEast.add(new Frame(bm, new Rect(1 * width, 2 * height, 1 * width + width, 2 * height + height)), frameLength);
		walkEast.add(new Frame(bm, new Rect(2 * width, 2 * height, 2 * width + width, 2 * height + height)), frameLength);
		walkEast.add(new Frame(bm, new Rect(3 * width, 2 * height, 3 * width + width, 2 * height + height)), frameLength);
		walkEast.setLoop(true);

		walkSouth = new TimeList<Frame>();
		walkSouth.add(new Frame(bm, new Rect(0, 0 * height, width, 0 * height + height)), frameLength);
		walkSouth.add(new Frame(bm, new Rect(1 * width, 0 * height, 1 * width + width, 0 * height + height)), frameLength);
		walkSouth.add(new Frame(bm, new Rect(2 * width, 0 * height, 2 * width + width, 0 * height + height)), frameLength);
		walkSouth.add(new Frame(bm, new Rect(3 * width, 0 * height, 3 * width + width, 0 * height + height)), frameLength);
		walkSouth.setLoop(true);

		walkWest = new TimeList<Frame>();
		walkWest.add(new Frame(bm, new Rect(0, 1 * height, width, 1 * height + height)), frameLength);
		walkWest.add(new Frame(bm, new Rect(1 * width, 1 * height, 1 * width + width, 1 * height + height)), frameLength);
		walkWest.add(new Frame(bm, new Rect(2 * width, 1 * height, 2 * width + width, 1 * height + height)), frameLength);
		walkWest.add(new Frame(bm, new Rect(3 * width, 1 * height, 3 * width + width, 1 * height + height)), frameLength);
		walkWest.setLoop(true);

		walkNorth = new TimeList<Frame>();
		walkNorth.add(new Frame(bm, new Rect(0, 3 * height, width, 3 * height + height)), frameLength);
		walkNorth.add(new Frame(bm, new Rect(1 * width, 3 * height, 1 * width + width, 3 * height + height)), frameLength);
		walkNorth.add(new Frame(bm, new Rect(2 * width, 3 * height, 2 * width + width, 3 * height + height)), frameLength);
		walkNorth.add(new Frame(bm, new Rect(3 * width, 3 * height, 3 * width + width, 3 * height + height)), frameLength);
		walkNorth.setLoop(true);

		standStill = new TimeList<Frame>();
		standStill.add(new Frame(bm, new Rect(2 * width, 0 * height, 2 * width + width, 0 * height + height)), 0);
		currentFrame = standStill;
	}

	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	public void onDraw(Canvas canvas, Canvas glassPane) {
		if (!draw) {
			return;
		}
		Player item = getItem();
		bounds.set(item.getX(), item.getY(), item.getX() + width, item.getY() + height);
		Frame frame = getCurrentFrame();
		canvas.drawBitmap(frame.getBitmap(), frame.getBounds(), bounds, null);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	private Frame getCurrentFrame() {
		return currentFrame.get();

	}

	public void onEvent(PlayerEvent event) {
		PlayerEvent.Type type = event.getType();
		switch (type) {
		case WALK_EAST:
			currentFrame = walkEast;
			break;
		case WALK_WEST:
			currentFrame = walkWest;
			break;
		case WALK_NORTH:
			currentFrame = walkNorth;
			break;
		case WALK_SOUTH:
			currentFrame = walkSouth;
			break;
		case GEM_ACTIVATED:
			draw = false; // The gem is responsible for drawing action sequences
			break;
		case GEM_COMPLETE:
			draw = true;
			break;
		case COLLISION:
			break;
		case PLAYER_HURT:

			break;
		case STOP:
		default:
			currentFrame = standStill;
		}
		currentFrame.reset();
		currentFrame.start();
	}
}
