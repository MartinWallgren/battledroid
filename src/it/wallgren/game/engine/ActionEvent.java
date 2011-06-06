package it.wallgren.game.engine;

public class ActionEvent {
	private Action action;
	public enum Action {
		ACTIVATE, DEACTIVATE;
	}
	public ActionEvent(Action action) {
		this.action = action;
	}
	
	public Action getAction() {
		return action;
	}
}
