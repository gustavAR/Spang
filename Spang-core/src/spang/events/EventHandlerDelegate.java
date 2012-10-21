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
 *  A class that makes event-handling of EventHandler simpler.
 *  This class is thread safe.
 * @author Lukas
 *
 * @param <S> The sender of the event.
 * @param <A> The argument sent with.
 */
public class EventHandlerDelegate<S,A> extends Delegate<EventHandler<S, A>>{
	/**
	 * Creates the event-handler delegate.
	 */
	public EventHandlerDelegate(){
		super();
	}
	/**
	 * Invokes the event notifying all registered listeners.
	 * @param sender the sender the invokee of the event.
	 * @param args the argument passed with the event.
	 */
	public void invoke(S sender, A args) {
		for (EventHandler<S,A> listener : this.listeners) {
			listener.onAction(sender, args);
		}
	}
}
