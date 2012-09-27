package events;

import java.util.concurrent.CopyOnWriteArrayList;

public class ActionDelegate {
	private CopyOnWriteArrayList<Action> listeners;
			
	public ActionDelegate() {
		this.listeners = new CopyOnWriteArrayList<Action>();		
	}
	
	public void addAction(Action action) {
		this.listeners.add(action);		
	}
	
	public void removeAction(Action action) {
		this.listeners.remove(action);		
	}
	
	public void invokeActions() {
		for (Action listener : this.listeners) {
			listener.onAction();
		}
	}
}
