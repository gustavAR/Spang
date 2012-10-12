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