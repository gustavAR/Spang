package events;


/**
 *  A class that makes event-handling of EventHandler simpler.
 *  This class is thread safe.
 * @author Lukas
 *
 * @param <S> The sender of the event.
 * @param <A> The argument sent with.
 */
public class EventHandlerDelegate<S,A> extends Delegate<EventHandler<S, A>>{
	/**
	 * Creates the event-handler delegate.
	 */
	public EventHandlerDelegate(){
		super();
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
