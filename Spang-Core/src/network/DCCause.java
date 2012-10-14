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
package network;

/**
 * Enum used to signal the cause of a disconnection.
 * @author Lukas
 *
 */
public enum DCCause {
	
	/**
	 * Connection disconnected from not receiving a message during the timeout period.
	 */
	Timeout,
	
	/**
	 * Connection disconnected because the remote endpoint requested it.
	 */
	EndpointShutdown,
	
	/**
	 * Connection was disconnected locally.
	 */
	LocalShutdown,
	
	/**
	 * Connection was disconnected because the local connection crashed.
	 */
	LocalNetworkCrash,
	
	/**
	 * Connection was disconnection because the remote endpoint crashed.
	 * Note: This almost always shows itself as a Timeout cause.
	 */
	RemoteNetworkCrash
}
