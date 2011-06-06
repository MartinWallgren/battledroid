package it.wallgren.game.view;

import it.wallgren.game.engine.GameClock;

/**
 * A list that will return the same element for a certain period of time.
 * 
 * This class is not synchronised!
 */
public class TimeList<E> {
	private Node root;
	private Node last;
	private Node currentNode;

	private boolean loop;
	private boolean running;
	private long timestamp;

	public void add(E element, int duration) {
		Node node = new Node(element, duration);
		if (root == null) {
			root = node;
			last = node;
			currentNode = root;
		} else {
			last.next = node;
			last = node;
		}

		if (loop) {
			last.next = root;
		}
	}

	/**
	 * Get the current element
	 * 
	 * @return
	 */
	public E get() {
		if (currentNode == null) {
			return null;
		}

		if (running && GameClock.getTime() >= timestamp + currentNode.duration) {
			timestamp += currentNode.duration;
			if (currentNode.next != null) {
				currentNode = currentNode.next;
			}
		}
		return currentNode.frame;
	}

	public void start() {
		running = true;
		timestamp = GameClock.getTime();
	}

	public void reset() {
		running = false;
		currentNode = root;
	}

	public boolean isLoop() {
		return loop;
	}

	/**
	 * Set to true if this list should loop for infinity If set to false, get
	 * will return the last frame when the time is up
	 * 
	 * @param loop
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
		if(loop) {
			last.next = root;
		}
	}

	private class Node {
		E frame;
		int duration;
		Node next;

		public Node(E element, int duration) {
			super();
			this.frame = element;
			this.duration = duration;
		}
	}
}
