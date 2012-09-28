package events;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 *  A class that makes event-handling of Action1 simpler.
 *  This class is thread safe.
 * @author Lukas
 * @param <T> the type of argument the listener uses.
 */
public class Action1Delegate<T> {
	//Thread safe collection.
	private CopyOnWriteArrayList<Action1<T>> listeners;
			
	/**
	 * Creates the Action1Delegate.
	 */
	public Action1Delegate(){
		listeners = new CopyOnWriteArrayList<Action1<T>>();
	}
	
	/**
	 * Adds a listener to this delegate.
 	 * @param action the listener to add.
	 */
	public void addAction(Action1<T> action) {
			this.listeners.add(action);		
	}
	
	/**
	 * Removes a listener from this delegate.
	 * @param action the listener to remove.
	 */
	public void removeAction(Action1<T> action) {
			this.listeners.remove(action);
	}
	
	/**
	 * Invokes the delegate making it notify all of it's listeners.
	 * @param args argument invoked with.
	 */
	public void invoke(T args) {
		for (Action1<T> listener : this.listeners) {
			listener.onAction(args);
		}
	}
}