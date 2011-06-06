package it.wallgren.game.engine.items.event;

public interface EventListener<T extends Event> {
	/**
	 * An event has occurred. Events will always arrive on an
	 * dedicated message thread. 
	 * 
	 * Don't lock the message thread, and don't manipulate any gui objects here!
	 * 
	 * @param event
	 */
	void onEvent(T event);
}
