package network;

import java.net.InetSocketAddress;

/**
 * Interface to handle the connection at the most basic level.
 * 
 * @author Lukas Kurtyan & Joakim Johansson
 *
 */
public interface IConnection {
		
	/**
	 * Sends the supplied data using the UDP-protocol. 
	 * @param data the data to be sent.
	 */
	void sendUDP(byte[] data);
	
	/**
	 * Sends the supplied data using the TCP-protocol.
	 * @param data
	 */
	void sendTCP(byte[] data);
	
	/**
	 * Receives an array of byte data from the UDP-protocol.
	 * @return the array of byte that was sent over the network.
	 */
	byte[] reciveUDP();
	
	
	/**
	 * Receives an array of byte data from the TCP-protocol.
	 * @return the array of byte that was sent over the network.
	 */
	byte[] reciveTCP();

	/**
	 * Is the connected connected.
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