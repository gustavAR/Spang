package events;

/**
 * A simple action listener that listens to events that have no event parameters.
 * @author Lukas
 *
 */
public interface Action {
	
	/**
	 * Invoked when the event calls the listener.
	 */
	public void onAction();
}
