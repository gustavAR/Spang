package events;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base class for all delegates.
 * This class is equivalent to a c# Multicast delegate. 
 * This class is thread safe.
 * @author Lukas Kurtyan.
 *
 * @param <T> the listener this delegate listens to.
 */
public abstract class Delegate<T> {
	//Thread safe collection.
	protected CopyOnWriteArrayList<T> listeners;
	
	public Delegate() {
		this.listeners = new CopyOnWriteArrayList<T>();
	}
	
	/**
	 * Adds a listener to this delegate.
 	 * @param action the listener to add.
	 */
	public final void addListener(T listener) {
			this.listeners.add(listener);		
	}
	
	/**
	 * Removes a listener from this delegate.
	 * @param action the listener to remove.
	 */
	public final void removeListener(T listener) {
			this.listeners.remove(listener);
	}
	
	/**
	 * Clears all listeners form this delegate.
	 */
	public final void clear() {
		this.listeners.clear();
	}
}