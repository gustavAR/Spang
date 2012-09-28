package network;

/**
 * Enum used to signal the cause of a disconnection.
 * @author Lukas
 *
 */
public enum DCCause {
	
	/**
	 * Connection disconnected from a tcp timeout.
	 */
	TCPTimeout,
	
	/**
	 * Connection disconnected because the remote endpoint requested it.
	 */
	EndpointShutdown,
	
	/**
	 * Connection was disconnected locally.
	 */
	LocalShutdown
}
