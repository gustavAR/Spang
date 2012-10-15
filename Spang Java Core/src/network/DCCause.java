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
