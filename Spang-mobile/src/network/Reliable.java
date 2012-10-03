package network;

import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import network.exceptions.NotImplementedException;

public class Reliable implements IProtocolHelper{
	//TODO implement.
	
	
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