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
 * Class used to determinie how messages should be sent accross the Network.
 * @author Lukas Kuryan
 *
 */
public enum Protocol {
	
	/**	
	 * Normal UDP protocol.
	 * Very fast. (Should not be used for important messages!)
	 */
	Unordered((byte)0x01),
	
	/**
	 * Ordered UDP protocol. (Out of data messages are discarded)
	 */
	Ordered((byte)0x02), 	
	
	/**
	 * Guaranteed to arrive.
	 * NOTE: Only use for very important messages.
	 */
	Reliable((byte)0x04),
	
	/**
	 * Guaranteed to arrive in order.
	 * NOTE: Only use if important this type of messages can be very slow and 
	 * put high pressure on the network.
	 */
	OrderedReliable((byte)0x08);
	
	
	private final byte orderID;
	
	private Protocol(byte orderID) {
		this.orderID = orderID;
	}
	
	public byte getBit() {
		return this.orderID;
	}

	public static Protocol fromID(byte b) {
		for (Protocol p : Protocol.values()) {
			if((b & p.orderID) == p.orderID) {
				return p;
			}
		}		
		throw new IllegalArgumentException("The id " + b + " is not a valid protocol id");	
	}
} 