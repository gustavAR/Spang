package network;

import java.nio.ByteBuffer;

public class OrderedProtocol implements IProtocolHelper {
	private int lastSequenceNumber;
	private int nextSequenceNumber;
	

	public byte[] processSentMessage(byte[] message) {
		byte[] correctedMessage = new byte[message.length + 5];
		ByteBuffer buffer = ByteBuffer.wrap(correctedMessage);		
		buffer.put(this.getProtocol().getID());
		buffer.putInt(getNextSecuenceNum());		
		buffer.put(message);
		return correctedMessage;		
	}
	
	private int getNextSecuenceNum() {
		return this.nextSequenceNumber++;
	}
	
	public byte[] processRecivedMessage(byte[] message) {
		ByteBuffer buffer = ByteBuffer.wrap(message);
		int sequenceNumber = buffer.getInt();
		
		if(sequenceNumber > lastSequenceNumber) { 
			return message;
		}			
		
		//Discard this message.
		return null;
	}

	public Protocol getProtocol() {
		return Protocol.Ordered;
	}
}
