package events;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 *  A class that makes event-handling of Action simpler.
 *  This class is thread safe.
 * @author Lukas
 */
public class ActionDelegate {
	//Thread safe collection.
	private CopyOnWriteArrayList<Action> listeners;
			
	/**
	 * Creates the action delegate.
	 */
	public ActionDelegate() {
		this.listeners = new CopyOnWriteArrayList<Action>();		
	}
	
	/**
	 * Adds a listener to this delegate.
 	 * @param action the listener to add.
	 */
	public void addAction(Action action) {
		this.listeners.add(action);		
	}
	
	/**
	 * Removes a listener from this delegate.
	 * @param action the listener to remove.
	 */
	public void removeAction(Action action) {
		this.listeners.remove(action);		
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
