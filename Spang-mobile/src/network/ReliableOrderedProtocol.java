package network;

import network.exceptions.NotImplementedException;

public class ReliableOrderedProtocol implements IProtocolHelper {

	public byte[] processRecivedMessage(byte[] message) {
		throw new NotImplementedException();
	}

	public byte[] processSentMessage(byte[] message) {
		throw new NotImplementedException();
	}

	public byte[] nextMessage() {
		throw new NotImplementedException();
	}

	public Protocol getProtocol() {
		throw new NotImplementedException();
	}

}
