package events;

/**
 * A simple action listener that listens to events that have one event parameters.
 * @author Lukas
 * @param <T> the type of the argument that is received when the onAction is invoked.
 */
public interface Action1<T> {
	
	/**
	 * Invoked when the event calls the listener.
	 * @param obj the parameter invoked with.
	 */
	public void onAction(T obj);
}
