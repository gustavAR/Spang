package events;

import java.util.HashSet;
import java.util.Set;

/**
 * Thread safe delegate. 
 * @author Lukas
 * @param <T>
 */
public class Action1Delegate<T> {
	private Set<Action1<T>> listeners;
	private Object lock = new Object();
			
	public Action1Delegate(){
		listeners = new HashSet<Action1<T>>();
	}
	
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
