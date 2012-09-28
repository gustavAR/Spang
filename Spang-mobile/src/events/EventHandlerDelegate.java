package events;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 *  A class that makes event-handling of EventHandler simpler.
 *  This class is thread safe.
 * @author Lukas
 *
 * @param <S> The sender of the event.
 * @param <A> The argument sent with.
 */
public class EventHandlerDelegate<S,A> {
	private CopyOnWriteArrayList<EventHandler<S,A>> listeners;
	
	/**
	 * Creates the event-handler delegate.
	 */
	public EventHandlerDelegate(){
		listeners = new CopyOnWriteArrayList<EventHandler<S,A>>();
	}
		
	/**
	 * Adds a listener to this delegate.
 	 * @param action the listener to add.
	 */
	public void addHandler(EventHandler<S,A> action) {
		this.listeners.add(action);		
	}
	
	/**
	 * Removes a listener from this delegate.
	 * @param action the listener to remove.
	 */
	public void removeHandler(EventHandler<S,A> action) {
		this.listeners.remove(action);
	}
	
	/**
	 * Invokes the event notifying all registered listeners.
	 * @param sender the sender the invokee of the event.
	 * @param args the argument passed with the event.
	 */
	public void invoke(S sender, A args) {
		for (EventHandler<S,A> listener : this.listeners) {
			listener.onAction(sender, args);
		}
	}
}
