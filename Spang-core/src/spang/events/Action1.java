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
