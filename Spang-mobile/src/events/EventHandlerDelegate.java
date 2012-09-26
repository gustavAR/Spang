package events;

import java.util.Set;

public class EventHandlerDelegate<S,A> {
	private Set<EventHandler<S,A>> listeners;
	private Object lock = new Object();
			
	public void addAction(EventHandler<S,A> action) {
		synchronized (lock) {
			this.listeners.add(action);		
		}
	}
	
	public void removeAction(EventHandler<S,A> action) {
		synchronized (lock) {
			this.listeners.remove(action);
		}
		
	}
	
	public void invokeActions(S sender, A args) {
		synchronized (lock) {
			for (EventHandler<S,A> listener : this.listeners) {
				listener.onAction(sender, args);
			}
		}	
	}
}
