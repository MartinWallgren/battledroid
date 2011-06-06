package it.wallgren.game.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BoardView extends SurfaceView {

	private AnimatorThread animator;

	public BoardView(Context context, Scene scene) {
		super(context);
		
		animator = new AnimatorThread(this.getHolder(), scene);
		getHolder().addCallback(animator);
	}

	public void setInputHandler(InputHandler inputHandler){
		this.setOnTouchListener(inputHandler);
	}
	
	private class AnimatorThread extends Thread implements SurfaceHolder.Callback {
		private SurfaceHolder surfaceHolder;
		private Bitmap glassPane;
		private Canvas glassPaneCanvas;
		private Scene scene;
		private boolean run;
		private Paint glassPanePaint;

		public AnimatorThread(SurfaceHolder surfaceHolder, Scene scene) {
			this.surfaceHolder = surfaceHolder;
			this.scene = scene;
			glassPaneCanvas = new Canvas();
			glassPanePaint = new Paint();
			glassPanePaint.setColor(Color.TRANSPARENT);
			glassPanePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR)); 
		}

		public void surfaceCreated(SurfaceHolder holder) {
			start();
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			glassPane = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			glassPaneCanvas = new Canvas(glassPane);
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			boolean retry = true;
			run = false;
			while (retry) {
				try {
					join();
					retry = false;
				} catch (InterruptedException e) {
				}
			}
		}

		@Override
		public void run() {
			run = true;
			Canvas mainCanvas = null;
			while (run) {
				try {
					mainCanvas = surfaceHolder.lockCanvas();
					scene.onDraw(mainCanvas, glassPaneCanvas);
					if (glassPane != null) {
						mainCanvas.drawBitmap(glassPane, 0, 0, null);
						glassPaneCanvas.drawPaint(glassPanePaint);
					}
				} finally {
					if (mainCanvas != null) {
						surfaceHolder.unlockCanvasAndPost(mainCanvas);
					}
				}
			}
		}
	}
}
