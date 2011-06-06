package it.wallgren.game.engine;

import it.wallgren.game.GameApplication;
import it.wallgren.game.engine.items.AbstractItem;
import it.wallgren.game.engine.items.Cell;
import it.wallgren.game.engine.items.Collision;
import it.wallgren.game.engine.items.DynamicItem;
import it.wallgren.game.engine.items.Grid;
import it.wallgren.game.engine.items.HeadsUpDisplay;
import it.wallgren.game.engine.items.Item;
import it.wallgren.game.engine.items.Player;
import it.wallgren.game.engine.items.event.GameEvent;
import it.wallgren.game.engine.items.event.GameEvent.Type;
import it.wallgren.game.engine.items.event.GameListener;
import it.wallgren.game.engine.items.event.ItemEvent;
import it.wallgren.game.engine.items.gem.GemItem;
import it.wallgren.game.util.ItemCollection;
import it.wallgren.game.util.Logger;
import it.wallgren.game.util.PositionUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;

public class GameModel {

	public enum State {
		INIT, SELECTION_FASE, ANIMATION_FASE;
	}

	private HandlerThread worker;

	public static final int Z_INDEX_BACKDROP = 0;
	public static final int Z_INDEX_GEM = Z_INDEX_BACKDROP + 1;
	public static final int Z_INDEX_PLAYER = Z_INDEX_GEM + 1;
	public static final int Z_INDEX_HUD = Z_INDEX_PLAYER + 1;

	private static final long ROUND_LENGTH = 5000;
	private static final int ROWS = 7;
	private static final int COLS = 5;

	private StateMachine stateMachine;
	private Handler workerHandler;
	private ReentrantReadWriteLock listenerLock;
	private ReentrantReadWriteLock modelLock;
	private Set<GameListener> gameListeners;
	private GemGenerator gemGenerator;
	private Grid grid;
	private ItemCollection items;
	private Player playerOne;
	private Player playerTwo;
	private Round currentRound;
	private State state = State.INIT;
	private Rect bounds;
	private Random random = new Random();

	private HeadsUpDisplay hud;

	private AtomicBoolean gameOver = new AtomicBoolean(false);
	private AtomicBoolean paused = new AtomicBoolean(false);

	public GameModel(GameApplication context) {
		worker = new HandlerThread("GameModel-worker");
		worker.start();
		workerHandler = new Handler(worker.getLooper());
		listenerLock = new ReentrantReadWriteLock();
		modelLock = new ReentrantReadWriteLock();
		gameListeners = new LinkedHashSet<GameListener>();
		stateMachine = new StateMachine(this);
		addGameListener(stateMachine);
		gemGenerator = new GemGenerator(this);
		Rect screen = context.getScreen();
		items = new ItemCollection();
		bounds = new Rect(0, 0, (screen.right / COLS) * COLS, (screen.bottom / ROWS) * ROWS);
		createHud();
		Rect gridBounds = new Rect(bounds.left, bounds.top, bounds.right, hud.getY() - 1);
		grid = new Grid(COLS, ROWS, gridBounds);
		grid.setMessageHandler(workerHandler);
	}

	private void createHud() {
		if (hud == null) {
			hud = new HeadsUpDisplay(this);
			hud.setWidth(bounds.width());
			hud.setHeight(bounds.height() / ROWS);
			hud.setX(0);
			hud.setY(bounds.height() - hud.getHeight());
			hud.setZ(Z_INDEX_HUD);
		}
		addItem(hud);
		if (playerOne != null) {
			playerOne.addItemListener(hud.getPlayerListener());
		}
		if (playerTwo != null) {
			playerTwo.addItemListener(hud.getPlayerListener());
		}
	}

	public void addGameListener(GameListener gameListener) {
		try {
			listenerLock.writeLock().lock();
			gameListeners.add(gameListener);
		} finally {
			listenerLock.writeLock().unlock();
		}
	}

	public void removeGameListener(GameListener gameListener) {
		try {
			listenerLock.writeLock().lock();
			gameListeners.remove(gameListener);
		} finally {
			listenerLock.writeLock().unlock();
		}
	}

	public Grid getGrid() {
		return grid;
	}

	public Item getBackgroundItem() {
		return grid;
	}

	public List<DynamicItem<? extends ItemEvent>> getItems() {
		try {
			modelLock.readLock().lock();
			return items.getItems();
		} finally {
			modelLock.readLock().unlock();
		}
	}

	public void addItem(DynamicItem<? extends ItemEvent> item) {
		item.setMessageHandler(workerHandler);
		try {
			modelLock.writeLock().lock();
			items.add(item);
		} finally {
			modelLock.writeLock().unlock();
		}

		dispatchEvent(new GameEvent(Type.ITEM_ADDED, item));
	}

	public void removeItem(DynamicItem<? extends ItemEvent> item) {
		try {
			modelLock.writeLock().lock();
			items.remove(item);
		} finally {
			modelLock.writeLock().unlock();
		}

		dispatchEvent(new GameEvent(Type.ITEM_REMOVED, item));
	}

	public Player getPlayerOne() {
		return playerOne;
	}

	public Player getPlayerTwo() {
		return playerTwo;
	}

	public void setPlayerOne(Player player) {
		try {
			modelLock.writeLock().lock();
			if (playerOne != null) {
				removePlayer(playerOne);
			}
			this.playerOne = player;
			addPlayer(player);
		} finally {
			modelLock.writeLock().unlock();
		}
	}

	public void setPlayerTwo(Player player) {
		try {
			modelLock.writeLock().lock();
			if (playerTwo != null) {
				removePlayer(playerTwo);
			}
			this.playerTwo = player;
			addPlayer(player);
		} finally {
			modelLock.writeLock().unlock();
		}
	}

	private void addPlayer(Player player) {
		player.setZ(Z_INDEX_PLAYER);
		player.addItemListener(stateMachine.getPlayerListener());
		player.addItemListener(hud.getPlayerListener());
		addItem(player);
	}

	private void removePlayer(Player player) {
		items.remove(player);
		player.removeItemListener(hud.getPlayerListener());
	}

	/**
	 * Center the item at the position in the grid
	 * 
	 * @param column
	 * @param row
	 * @param item
	 */
	private void setPosition(int column, int row, AbstractItem<? extends ItemEvent> item) {
		Cell cell = grid.getCell(column, row);
		Point p = PositionUtil.centerItemOverItem(item, cell);
		item.setX(p.x);
		item.setY(p.y);
	}

	public void start() {
		// Setup player positions
		setPosition(2, ROWS - 1, playerOne);
		setPosition(2, 0, playerTwo);
		gemGenerator.generateNewGems(grid.getCells(), 3);
		startNewRound();
	}

	void startNewRound() {
		currentRound = new Round(playerOne, playerTwo);
		state = State.SELECTION_FASE;
		currentRound.start(ROUND_LENGTH);
		playerOne.makeMove(getAvailableMoves(playerOne));
		playerTwo.makeMove(getAvailableMoves(playerTwo));
	}

	void animateRound() {
		state = State.ANIMATION_FASE;
		moveGems();
	}

	void handleCollision() {
		List<Collision> collisions = new LinkedList<Collision>();
		List<Player> playerOrder = currentRound.getPlayerOrder();

		if (!Rect.intersects(playerOne.getBounds(), playerTwo.getBounds())) { // XXX: Workaround for two players on the same gem
			for (Player player : playerOrder) {
				Cell playerCell = grid.getCellByPosition(player.getX(), player.getY());

				for (GemItem gemItem : getGems()) {
					Cell gemCell = grid.getCellByPosition(gemItem.getX(), gemItem.getY());
					if (gemCell.equals(playerCell)) {
						collisions.add(new Collision(gemItem, player, gemCell));
						break;
					}
				}
			}
		}

		dispatchEvent(new GameEvent(Type.GEM_COLLISIONS, collisions));

	}

	private void moveGems() {
		int cellHeight = grid.getCell(0, 0).getHeight();
		List<GemItem> gems = getGems();
		dispatchEvent(new GameEvent(Type.GEM_MOVEMENT_STARTED, gems));
		for (GemItem gem : gems) {
			gem.addItemListener(stateMachine.getGemItemListener());
			Cell cell = grid.getCellByPosition(gem.getCenterX(), gem.getCenterY());

			int direction;
			if (cell.getRow() < grid.getRows() / 2) {
				direction = -1;
			} else if (cell.getRow() > grid.getRows() / 2) {
				direction = 1;
			} else {
				if (grid.getRows() % 2 != 0) {
					// Uneven amount of rows, center row gems can move in any
					// direction
					direction = random.nextBoolean() ? 1 : -1;
				} else {
					direction = 1;
				}
			}

			gem.setVelocity(null, new Velocity(GameClock.getTickTime(), direction * 0.03f), null,
					gem.getX(), gem.getY() + (cellHeight * direction), gem.getZ());
		}

	}

	void generateNewGems() {
		gemGenerator.generateNewGems(gemGenerator.getCenterCells());
		dispatchEvent(new GameEvent(Type.NEW_GEMS_GENERATED));
	}

	void movePlayers() {
		Cell playerOneMove = currentRound.getPlayerOneMove();
		if (playerOneMove != null) {
			Point p = PositionUtil.centerItemOverItem(playerOne, playerOneMove);
			playerOne.walkTo(p.x, p.y);
		}
		Cell playerTwoMove = currentRound.getPlayerTwoMove();
		if (playerTwoMove != null) {
			Point p = PositionUtil.centerItemOverItem(playerTwo, playerTwoMove);
			playerTwo.walkTo(p.x, p.y);
		}
	}

	private List<Cell> getAvailableMoves(Player player) {
		int moveLength = 1;
		List<Cell> cells;
		Cell currentCell = grid.getCellByPosition(player.getX(), player.getY());
		int left = currentCell.getColumn() - moveLength;
		if (left < 0) {
			left = 0;
		}
		int right = currentCell.getColumn() + moveLength;
		if (right > grid.getColumns() - 1) {
			right = grid.getColumns() - 1;
		}
		int top = currentCell.getRow() - moveLength;
		if (top < 0) {
			top = 0;
		}
		int bottom = currentCell.getRow() + moveLength;
		if (bottom > grid.getRows() - 1) {
			bottom = grid.getRows() - 1;
		}
		cells = new ArrayList<Cell>((right - left + 1) * (bottom - top + 1));

		for (int column = left; column <= right; column++) {
			for (int row = top; row <= bottom; row++) {
				cells.add(grid.getCell(column, row));
			}
		}

		return cells;
		
		// Move anywhere.
//		return grid.getCells();
	}

	protected final void dispatchEvent(final GameEvent event) {
		workerHandler.post(new Runnable() {
			public void run() {
				if (GameApplication.DEBUG) {
					Logger.d(this, event.toString());
				}
				LinkedList<GameListener> listeners;
				try {
					listenerLock.readLock().lock();
					listeners = new LinkedList<GameListener>(gameListeners);
				} finally {
					listenerLock.readLock().unlock();
				}
				for (GameListener listener : listeners) {
					listener.onEvent(event);
				}
			}
		});
	}

	public List<GemItem> getGems() {
		List<GemItem> gems = new LinkedList<GemItem>();

		// Get items copy to avoid the need to synchronise the whole loop
		List<DynamicItem<? extends ItemEvent>> allItems = getItems();
		for (Item item : allItems) {
			if (item instanceof GemItem) {
				gems.add((GemItem) item);
			}
		}
		return gems;
	}

	public void onTick(long timestamp) {
		if (state == State.SELECTION_FASE && currentRound.isTimeUp()) {
			state = State.ANIMATION_FASE;
			List<Cell> cells = getAvailableMoves(playerOne);
			for (Cell cell : cells) {
				cell.setSelectable(false);
			}
			if (currentRound.getPlayerOneMove() == null) {

				currentRound.setPlayerOneMove(grid.getCellByPosition(playerOne.getX(),
						playerOne.getY()));
			}
			if (currentRound.getPlayerTwoMove() == null) {
				currentRound.setPlayerTwoMove(grid.getCellByPosition(playerTwo.getX(),
						playerTwo.getY()));
			}
			animateRound();
		}

		List<DynamicItem<? extends ItemEvent>> currentItems;
		try {
			modelLock.readLock().lock();
			currentItems = items.getItems();
		} finally {
			modelLock.readLock().unlock();
		}

		for (DynamicItem<? extends ItemEvent> item : currentItems) {
			item.onTick(timestamp);
		}
	}

	public State getState() {
		return state;
	}

	public Round getCurrentRound() {
		return currentRound;
	}

	public StateMachine getStateMachine() {
		return stateMachine;
	}

	/**
	 * Get the bounds of this model. Nothing can exist outside the bounds
	 * 
	 * @return the bounds of the game
	 */
	public Rect getBounds() {
		return bounds;
	}

	/**
	 * Get items that occupy the position
	 * 
	 * Expensive if there are a large amount of items
	 * 
	 * @param x
	 * @param y
	 */
	public List<AbstractItem<?>> getItems(int x, int y) {

		try {
			modelLock.readLock().lock();
			return items.get(x, y);
		} finally {
			modelLock.readLock().unlock();
		}
	}

	public void setGameOver() {
		if (!gameOver.getAndSet(true)) {
			dispatchEvent(new GameEvent(Type.GAME_OVER));
		}
	}

	public boolean isGameOver() {
		return gameOver.get();
	}

	/**
	 * Reset the game to prepare for a new game.
	 */
	public void reset() {
		try {
			modelLock.writeLock().lock();
			Player p1 = getPlayerOne();
			Player p2 = getPlayerTwo();
			List<DynamicItem<? extends ItemEvent>> allItems = items.getItems();
			for (DynamicItem<?> item : allItems) {
				removeItem(item);
			}
			grid.reset();
			p1.reset();
			p2.reset();
			setPlayerOne(p1);
			setPlayerTwo(p2);
			createHud();
			gameOver.set(false);
			start();
		} finally {
			modelLock.writeLock().unlock();
		}
	}

	public void pause(boolean pause) {
		if (paused.getAndSet(pause) != pause) {
			dispatchEvent(new GameEvent(Type.PAUSE_STATE_CHANGED));
		}
	}

	public boolean isPaused() {
		return paused.get();
	}
}
