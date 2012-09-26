package events;

import java.util.Set;

/**
 * Thread safe delegate. 
 * @author Lukas
 * @param <T>
 */
public class Action1Delegate<T> {
	private Set<Action1<T>> listeners;
	private Object lock = new Object();
			
	public void addAction(Action1<T> action) {
		synchronized (lock) {
			this.listeners.add(action);		
		}
	}
	
	public void removeAction(Action1<T> action) {
		synchronized (lock) {
			this.listeners.remove(action);
		}
		
	}
	
	public void invokeActions(T args) {
		synchronized (lock) {
			for (Action1<T> listener : this.listeners) {
				listener.onAction(args);
			}
		}	
	}
}
