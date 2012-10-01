package network;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import network.exceptions.NetworkException;

import events.EventHandler;

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
	void connect(InetSocketAddress adress);
	
	/**
	 * Connects the client to the supplied address and port.
	 * @param address the address to connect to.
	 * @param port the port to connect to.
	 * @throw NetworkException is the client cannot connect.
	 */
	void connect(InetAddress address, int port);
	
	/**
	 * Connects the client to the supplied host and port.
	 * @param host the host to connect to.
	 * @param port the port to connect to.
	 * @throw NetworkException is the client cannot connect.
	 */
	void connect(String host, int port);
	
	/**
	 * Reconnects to the last used connection.
	 * If we are already connected this is a no-op.
	 * @param retries the number of times a reconnect will be attempted.
	 * @throws NetworkException if the client cannot reconnect.
	 */
	void reconnect(int retries);
	
	/**
	 * Disconnects the currently active connection.
	 * If no connection is active this is a no-op.
	 */
	void disconnect();
	
	/**
	 * Sends a message using the UDP-protocol.
	 * @throws NetworkException if the client is not connected. 
	 * @param toSend the message to send.
	 */
	void sendUDP(byte[] toSend);
	
	/**
	 * Sends a message using the TCP-protocol.
	 * @throws NetworkException if the client is not connected. 
	 * @param toSend the message to send.
	 */
	void sendTCP(byte[] toSend);
	
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
	void addRevicedListener(EventHandler<IClient, byte[]> listener);
	
	/**
	 * Removes a listener forcing it to stop listening to received messages.
	 * @param listener the listener.
	 */
	void removeRevicedListener(EventHandler<IClient, byte[]> listener);
		
	/**
	 * TODO: Temporary fix
	 */
	IConnection getConnection();
}