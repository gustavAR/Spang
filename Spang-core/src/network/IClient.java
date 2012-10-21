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

import java.net.InetAddress;
import java.net.InetSocketAddress;

import serialization.ISerializer;
import spang.events.EventHandler;

import network.exceptions.NetworkException;


public interface IClient {
	
	/**
	 * Gets the connection status of the client.
	 * @return connection status.
	 */
	boolean isConnected();
	
	/**
	 * Gets the time a connection will wait for a message without disconnecting. 
	 * @return time in milliseconds.
	 */
	int getConnectionTimeout();
	
	/**
	 * Sets the time a connection will wait for a message without disconnecting.
	 * @param value the time in milliseconds. Note: (0 is wait indefinite)
	 */
	void setConnectionTimeout(int value);
	
	/**
	 * Connects the client to the supplied address.
	 * @param adress the address to connect to.
	 * @throw NetworkException is the client cannot connect.
	 */
	void connect(InetSocketAddress adress) throws NetworkException;
	
	/**
	 * Connects the client to the supplied address and port.
	 * @param address the address to connect to.
	 * @param port the port to connect to.
	 * @throw NetworkException is the client cannot connect.
	 */
	void connect(InetAddress address, int port) throws NetworkException;
	
	/**
	 * Connects the client to the supplied host and port.
	 * @param host the host to connect to.
	 * @param port the port to connect to.
	 * @throw NetworkException is the client cannot connect.
	 */
	void connect(String host, int port) throws NetworkException;
	
	/**
	 * Reconnects to the last used connection.
	 * @param retries the number of times a reconnect will be attempted. In milliseconds.
	 * @param time that the connection will try to connect. in milliseconds.
	 * @throws NetworkException if the client cannot reconnect.
	 */
	void reconnect(int retries, int timeout) throws NetworkException;
	
	/**
	 * Disconnects the currently active connection.
	 * If no connection is active this is a no-op.
	 */
	void disconnect();
	
	/**
	 * Sends a message using the UDP-protocol.
	 * @param toSend the message to send.
	 * @throws NetworkException if the client is not connected. 
	 */
	void send(Object toSend) throws NetworkException;
	
	/**
	 * Sends a message using the TCP-protocol.
	 * @param toSend the message to send.
	 * @throws NetworkException if the client is not connected. 
	 */
	void send(Object toSend, Protocol protocol) throws NetworkException;
	
	
	/**
	 * Registers a new type to be sent over the network.
	 * @param serializer the serializer
	 */
	void registerSerializer(ISerializer serializer);
	
	/**
	 * Adds a listener that will be notified when the client is connected.
	 * @param listener the listener.
	 */
	void addConnectedListener(EventHandler<IClient, Boolean> listener);

	/**
	 * Removes a listener forcing it to stop listening to connected events.
	 * @param listener the listener.
	 */
	void removeConnectedListener(EventHandler<IClient, Boolean> listener);
	
	/**
	 * Adds a listener that will be notified when the client is disconnected.
	 * @param listener the listener.
	 */
	void addDisconnectedListener(EventHandler<IClient, DCCause> listener);

	/**
	 * Remove a listener forcing it to stop listening to disconnected events.
	 * @param listener the listener.
	 */
	void removeDisconnectedListener(EventHandler<IClient, DCCause> listener);
	
	/**
	 * Adds a listener that will be notified when the client receives messages.
	 * @param listener the listener.
	 */
	void addRevicedListener(EventHandler<IClient, Object> listener);
	
	/**
	 * Removes a listener forcing it to stop listening to received messages.
	 * @param listener the listener.
	 */
	void removeRevicedListener(EventHandler<IClient, Object> listener);
}