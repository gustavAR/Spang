/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
 *  
 *  This file is part of Spang.

    Spang is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spang is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spang.  If not, see <http://www.gnu.org/licenses/>.
 */
package spang.events;

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