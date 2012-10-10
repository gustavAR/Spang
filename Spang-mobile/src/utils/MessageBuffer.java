package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import network.Protocol;

public class MessageBuffer {
	private final int messageLength;
	private Map<Protocol, List<byte[]>> messages;
	private final Map<Protocol, Packer> messagePackers;
	
	private final Object lock = new Object();
	
	
	public MessageBuffer(int messageLength) {
		this.messageLength = messageLength;		
		this.messages = new HashMap<Protocol, List<byte[]>>();
		this.messagePackers = new HashMap<Protocol, Packer>();		
		this.fillMaps();
	}
	
	
	private void fillMaps() {
		for (Protocol protcol : Protocol.values()) {
			this.messagePackers.put(protcol, new Packer(this.messageLength));
			this.messages.put(protcol, new ArrayList<byte[]>());
		}
	
	}
	
	public void addMessage(byte[] message, Protocol protocol) {		
		synchronized (lock) {
			if(message.length > this.messageLength) {
				splitMessage(message,protocol);
				return;
			} 
			
			Packer packer = this.messagePackers.get(protocol);
			if(packer.getPackedSize() + message.length < this.messageLength) {
				packer.packByteArray(message);
			} else {
				addPackedMessage(protocol);
				packer.packByteArray(message);
			}	
		}
	}
	
	private void splitMessage(byte[] message, Protocol protocol) {
		for (int i = 0; i < message.length; i++) {
			byte[] copy = new byte[this.messageLength];
			for (int j = 0; i < message.length && j < copy.length; i++, j++) {
				copy[j] = message[i];
			}
			
			this.addMessage(copy,protocol);			
		}		
	}

	private void addPackedMessage(Protocol protocol) {
		Packer packer = this.messagePackers.get(protocol);
		List<byte[]> protocolMessages = this.messages.get(protocol);
		protocolMessages.add(packer.getPackedData());
		packer.clear();
	}

	public List<byte[]> getNewMessages(Protocol protocol) {	
		synchronized (lock) {
			Packer packer = this.messagePackers.get(protocol);
			List<byte[]> protocolMessages = this.messages.get(protocol);
			
			//Pack the final message.
			if(packer.getPackedSize() > 0) {
				this.addPackedMessage(protocol);
			}
	
			List<byte[]> list = new ArrayList<byte[]>();
			list.addAll(protocolMessages);
			
			protocolMessages.clear();
			return list;	
		}
	}
}
