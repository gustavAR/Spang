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
package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Lukas Kurtyan
 *
 * @param <T> The state for the StateMachine to handle.
 */
public class StateMachine<T extends State> {
	protected T activeState;

	private Map<String, T> stateMap;

	public StateMachine() {
		this.activeState = null;
		this.stateMap = new HashMap<String, T>();
	}

	public void registerState(String stateKey, T state) {
		this.stateMap.put(stateKey, state);
	}

	public void changeState(String stateKey) {
		if(this.activeState != null) {
			this.activeState.exit();
		}

		T state = stateMap.get(stateKey);

		if(state == null) {
			throw new NullPointerException("The state " + stateKey + "does not exist");
		}

		this.activeState = state;
		this.activeState.enter();
	}

	public String getActiveState() {
		Set<String> keys = this.stateMap.keySet();

		for (String string : keys) {
			if(this.stateMap.get(string).equals(activeState))
				return string;
		}
		throw new RuntimeException("ActiveState not found");
	}
}