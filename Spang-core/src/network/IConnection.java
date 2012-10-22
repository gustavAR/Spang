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

import java.net.InetSocketAddress;

import network.exceptions.NetworkException;
import network.exceptions.TimeoutException;

/**
 * Interface to handle the connection at the most basic level.
 * 
 * @author Lukas Kurtyan & Joakim Johansson
 *
 */
public interface IConnection {
		
	/**
	 * Sends the supplied data using the default Protocol.
	 * @param data the data to be sent.
	 * @throws NetworkException if the connection could not send the message.
	 */
	void send(byte[] data) throws NetworkException;
	
	/**
	 * Sends the supplied data with the specified protocol.
	 * @param toSend data to send.
	 * @param protocol the protocol to use.
	 * @throws NetworkException if the connection could not send the message.
	 */
	void send(byte[] toSend, Protocol protocol) throws NetworkException;
	
	/**
	 * Receives an array of byte data from the UDP-protocol.
	 * @return the array of byte that was sent over the network.
	 * @throws TimeoutException if the connection timed out.
	 * @throws NetworkException if the connection is unable to send messages.
	 */
	byte[] receive() throws TimeoutException , NetworkException;
		
	/**
	 * Is the connected connected.
	 * Note: This is not reliable in all implementations. Since it might depend on old state.
	 * @return connection status.
	 */
	boolean isConnected();
	
	/**
	 * Gets the time a connection will wait for a message without disconnecting. 
	 * @return time in milliseconds.
	 */
	int getTimeout();
	
	/**
	 * Sets the time a connection will wait for a message without disconnecting.
	 * @param value the time in milliseconds. Note: (0 is wait indefinite)
	 */
	void setTimeout(int value);

	/**
	 * Gets the remote endpoint this connection is connected to.
	 * @return the remote endpoint as a InetSocketAddress.
	 */
	InetSocketAddress getRemoteEndPoint();
	
	/**
	 * Gets the local endpoint this connection is connected on.
	 * @return the local endpoint as a InetSocketAddress.
	 */
	InetSocketAddress getLocalEndPoint();

	/**
	 * Closes the connection disposing any used resources.
	 */
	void close();
}