package it.wallgren.game.view.sprites.weapons;

import it.wallgren.game.R;
import it.wallgren.game.engine.items.Player;
import it.wallgren.game.engine.items.event.GemEvent;
import it.wallgren.game.engine.items.gem.Laser;
import it.wallgren.game.view.TimeList;
import it.wallgren.game.view.sprites.Frame;
import it.wallgren.game.view.sprites.Sprite;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class LaserSprite extends Sprite<GemEvent, Laser> {
	private static Bitmap laserBm;
	private static Bitmap blueBm;
	private Laser laser;
	private Paint paint = new Paint();
	private Context context;

	private TimeList<Frame> playerAnimation;
	private Rect bounds = new Rect();
	private static Bitmap playerBm;

	public LaserSprite(Context context, Laser laser) {
		super(laser);
		this.context = context;
		// TODO: Improve image handling. (cache?)
		if (laserBm == null || laserBm.isRecycled()) {
			laserBm = BitmapFactory.decodeResource(context.getResources(), R.drawable.laser);
		}
		if (blueBm == null || blueBm.isRecycled()) {
			blueBm = BitmapFactory.decodeResource(context.getResources(), R.drawable.circle_blue);
		}
		
		laser.setWidth(blueBm.getWidth());
		laser.setHeight(blueBm.getHeight());
		this.laser = laser;
	}

	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	public void onDraw(Canvas canvas, Canvas glassPane) {
		if (laser.isActive()) {
			paint.setColor(Color.WHITE);
			paint.setStrokeWidth(3);
			glassPane.drawLine(laser.getCenterX(), laser.getCenterY(), laser.getCenterX(), laser.getCenterY() - laser.getLengthNorth(), paint);
			glassPane.drawLine(laser.getCenterX(), laser.getCenterY(), laser.getCenterX(), laser.getCenterY() + laser.getLengthSouth(), paint);
			glassPane.drawLine(laser.getCenterX(), laser.getCenterY(), laser.getCenterX() - laser.getLengthWest(), laser.getCenterY(), paint);
			glassPane.drawLine(laser.getCenterX(), laser.getCenterY(), laser.getCenterX() + laser.getLengthEast(), laser.getCenterY(), paint);
			paint.setColor(Color.RED);
			paint.setStrokeWidth(1);
			glassPane.drawLine(laser.getCenterX(), laser.getCenterY(), laser.getCenterX(), laser.getCenterY() - laser.getLengthNorth(), paint);
			glassPane.drawLine(laser.getCenterX(), laser.getCenterY(), laser.getCenterX(), laser.getCenterY() + laser.getLengthSouth(), paint);
			glassPane.drawLine(laser.getCenterX(), laser.getCenterY(), laser.getCenterX() - laser.getLengthWest(), laser.getCenterY(), paint);
			glassPane.drawLine(laser.getCenterX(), laser.getCenterY(), laser.getCenterX() + laser.getLengthEast(), laser.getCenterY(), paint);
			glassPane.drawBitmap(laserBm, laser.getX(), laser.getY(), paint);
		} else {
			canvas.drawBitmap(blueBm, laser.getX(), laser.getY(), paint);
			
		}
		if (playerAnimation != null) {
			Frame frame = playerAnimation.get();
			glassPane.drawBitmap(frame.getBitmap(), frame.getBounds(), bounds, null);
		}
	}

	@Override
	public int getWidth() {
		return blueBm.getWidth();
	}

	@Override
	public int getHeight() {
		return blueBm.getHeight();
	}

	public void onEvent(GemEvent event) {
		switch (event.getType()) {
		case ACTION_STARTED:
			break;
		case ACTION_COLLISION:
			if (playerAnimation == null) {
				setupPlayerAnimation();
			}
			break;
		}
	}

	private void setupPlayerAnimation() {
		Laser item = getItem();
		Player player = item.getCollidedPlayer();
		bounds.set(player.getX(), player.getY(), player.getX() + player.getWidth(), player.getY() + player.getHeight());
		int xCount = 4;
		int yCount = 1;
		if (playerBm == null || playerBm.isRecycled()) {
			playerBm = BitmapFactory.decodeResource(context.getResources(), R.drawable.laser_player);
		}
		int width = playerBm.getWidth() / xCount;
		int height = playerBm.getHeight() / yCount;
		int frameLength = 175;
		playerAnimation = new TimeList<Frame>();
		playerAnimation.add(new Frame(playerBm, new Rect(0, 0, width, height)), frameLength);
		playerAnimation.add(new Frame(playerBm, new Rect(1 * width, 0, 1 * width + width, height)), frameLength);
		playerAnimation.add(new Frame(playerBm, new Rect(2 * width, 0, 2 * width + width, height)), frameLength);
		playerAnimation.add(new Frame(playerBm, new Rect(3 * width, 0, 3 * width + width, height)), frameLength);
		playerAnimation.setLoop(true);
		playerAnimation.start();
	}
}
