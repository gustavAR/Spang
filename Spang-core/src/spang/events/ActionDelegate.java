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
