package events;

/**
 * A listener class that can be used to listen to event that have a sender and a event argument parameter.
 * @author Lukas
 *
 * @param <T1> The sender object.
 * @param <T2> The event argument parameter.
 */
public interface EventHandler<S,A> {
	public void onAction(S sender, A eventArgs);
}
