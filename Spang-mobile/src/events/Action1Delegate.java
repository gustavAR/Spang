package events;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread safe delegate. 
 * @author Lukas
 * @param <T>
 */
public class Action1Delegate<T> {
	private CopyOnWriteArrayList<Action1<T>> listeners;
			
	public Action1Delegate(){
		listeners = new CopyOnWriteArrayList<Action1<T>>();
	}
	
	public void addAction(Action1<T> action) {
			this.listeners.add(action);		
	}
	
	public void removeAction(Action1<T> action) {
			this.listeners.remove(action);
	}
	
	public void invokeActions(T args) {
		for (Action1<T> listener : this.listeners) {
			listener.onAction(args);
		}
	}
}
