package events;

import java.util.concurrent.CopyOnWriteArrayList;

public class EventHandlerDelegate<S,A> {
	private CopyOnWriteArrayList<EventHandler<S,A>> listeners;
	
	public EventHandlerDelegate(){
		listeners = new CopyOnWriteArrayList<EventHandler<S,A>>();
	}
			
	public void addAction(EventHandler<S,A> action) {
		this.listeners.add(action);		
	}
	
	public void removeAction(EventHandler<S,A> action) {
		this.listeners.remove(action);
	}
	
	public void invokeActions(S sender, A args) {
		for (EventHandler<S,A> listener : this.listeners) {
			listener.onAction(sender, args);
		}
	}
}
