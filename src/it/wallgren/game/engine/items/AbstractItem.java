package it.wallgren.game.engine.items;

import it.wallgren.game.engine.items.event.ItemEvent;
import it.wallgren.game.engine.items.event.ItemListener;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.graphics.Rect;
import android.os.Handler;

public abstract class AbstractItem<T extends ItemEvent> implements Item {
	protected final String TAG = this.getClass().getSimpleName();
	protected int z;
	protected final Rect bounds = new Rect();

	private Handler messageHandler;

	private ReentrantReadWriteLock listenerLock = new ReentrantReadWriteLock();

	private Set<ItemListener<T>> itemListeners = new LinkedHashSet<ItemListener<T>>();

	public Rect getBounds() {
		return bounds;
	}
	
	public int getX() {
		return bounds.left;
	}

	public void setX(int x) {
		/*
		 * We need to reset the width since the 4 corners of the Rect is treated
		 * separately
		 */
		int width = getWidth();
		bounds.left = x;
		setWidth(width);
	}

	public int getY() {
		return bounds.top;
	}

	public void setY(int y) {
		/*
		 * We need to reset the height since the 4 corners of the Rect is
		 * treated separately
		 */
		int height = getHeight();
		bounds.top = y;
		setHeight(height);
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getWidth() {
		return bounds.width();
	}

	public void setWidth(int width) {
		bounds.right = bounds.left + width;
	}

	public int getHeight() {
		return bounds.height();
	}

	public void setHeight(int height) {
		bounds.bottom = bounds.top + height;
	}

	public int getCenterX() {
		return getX() + getWidth() / 2;
	}

	public int getCenterY() {
		return getY() + getHeight() / 2;
	}

	public void addItemListener(ItemListener<T> itemListener) {
		try {
			listenerLock.writeLock().lock();
			itemListeners.add(itemListener);
		} finally {
			listenerLock.writeLock().unlock();
		}
	}

	public void removeItemListener(ItemListener<T> itemListener) {
		try {
			listenerLock.writeLock().lock();
			itemListeners.remove(itemListener);
		} finally {
			listenerLock.writeLock().unlock();
		}
	}

	public void setMessageHandler(Handler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public Handler getMessageHandler() {
		return messageHandler;
	}

	/**
	 * Dispatch an event on the message thread to all ItemListeners
	 * 
	 * @param event
	 */
	protected final void dispatchEvent(final T event) {
		if (messageHandler == null) {
			return;
		}
		messageHandler.post(new Runnable() {
			public void run() {
				LinkedList<ItemListener<T>> listeners;
				try {
					listenerLock.readLock().lock();
					listeners = new LinkedList<ItemListener<T>>(itemListeners);
				} finally {
					listenerLock.readLock().unlock();
				}
				for (ItemListener<T> listener : listeners) {
					listener.onEvent(event);
				}
			}
		});

	}
}
