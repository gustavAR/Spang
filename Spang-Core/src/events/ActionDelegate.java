package events;

/**
 *  A class that makes event-handling of Action simpler.
 *  This class is thread safe.
 * @author Lukas
 */
public class ActionDelegate extends Delegate<Action>{
	
	/**
	 * Creates the action delegate.
	 */
	public ActionDelegate() {
		super();
	}
		
	/**
	 * Invokes the delegate making it notify all of it's listeners.
	 */
	public void invoke() {
		for (Action listener : this.listeners) {
			listener.onAction();
		}
	}
}
