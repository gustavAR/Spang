package network;

import java.nio.ByteBuffer;

public class UnorderedProtocol implements IProtocolHelper {
	public byte[] processRecivedMessage(byte[] message) {
		return message; //Nothing to do.
	}

	public Protocol getProtocol() {
		return Protocol.Unordered;
	}

	public byte[] processSentMessage(byte[] message) {
		byte[] correctedMessage = new byte[message.length + 1];
		ByteBuffer buffer = ByteBuffer.wrap(correctedMessage);
		buffer.put(this.getProtocol().getID());
		buffer.put(message);
		return correctedMessage;
	}
}
