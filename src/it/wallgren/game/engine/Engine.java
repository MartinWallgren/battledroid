package it.wallgren.game.engine;

public class Engine {
	private GameTimerThread gameThread;
	private GameModel model;
	private CollisionSupervisor collisionSupervisor;
	private final Object GAME_LOCK = new Object();

	public Engine(GameModel model) {
		this.model = model;
		this.collisionSupervisor = new CollisionSupervisor(model);
		gameThread = new GameTimerThread();
	}

	private class GameTimerThread extends Thread {

		public GameTimerThread() {
			super("GameTimerThread");
		}

		/**
		 * The refresh rate of the game.
		 */
		private int tickLength = 1000 / 20;

		public void run() {
			Engine.this.model.start();
			while (true) {
				try {
					GameClock.tick();
					model.onTick(GameClock.getTickTime());
					collisionSupervisor.onTick(GameClock.getTickTime());
					waitForNextTick(GameClock.getTickTime());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		private void waitForNextTick(long startTimeOfCurrentTick) {
			if (model.isPaused()) {
				synchronized (GAME_LOCK) {
					while (model.isPaused()) {
						try {
							GAME_LOCK.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}

			long sleep;
			try {
				sleep = startTimeOfCurrentTick + tickLength - GameClock.getTime();
				if (sleep > 0) {
					Thread.sleep(sleep);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void startGame() {
		gameThread.start();
	};

	public void pauseGame(boolean pause) {
		model.pause(pause);
		synchronized (GAME_LOCK) {
			GameClock.pause(pause);
			GAME_LOCK.notifyAll();
		}
	}
}
