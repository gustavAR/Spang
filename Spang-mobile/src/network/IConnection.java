package network;
/**
 * Interface to handle the connection at the most basic level.
 * 
 * @author Lukas Kurtyan & Joakim Johansson
 *
 */
public interface IConnection {
	
	/**
	 * Connects a user to the default address and port. 
	 */
	void reconnect();
	
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
}