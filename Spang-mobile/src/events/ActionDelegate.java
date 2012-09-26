package events;

import java.util.Set;

public class ActionDelegate {
	private Set<Action> listeners;
	private Object lock = new Object();
			
	public void addAction(Action action) {
		synchronized (lock) {
			this.listeners.add(action);		
		}
	}
	
	public void removeAction(Action action) {
		synchronized (lock) {
			this.listeners.remove(action);
		}
		
	}
	
	public void invokeActions() {
		synchronized (lock) {
			for (Action listener : this.listeners) {
				listener.onAction();
			}
		}	
	}
}
