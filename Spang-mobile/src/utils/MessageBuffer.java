package utils;

import java.util.ArrayList;
import java.util.List;

public class MessageBuffer {
	private final int messageLength;
	private List<byte[]> messages;
	private final Packer messagePacker;
	
	private final Object lock = new Object();
	
	
	public MessageBuffer(int messageLength) {
		this.messageLength = messageLength;		
		this.messagePacker = new Packer(messageLength);
		this.messages = new ArrayList<byte[]>();
	}
	
	public void addMessage(byte[] message) {		
		synchronized (lock) {
			if(message.length > this.messageLength) {
				splitMessage(message);
				return;
			}
			
			if(this.messagePacker.getPackedSize() + message.length < this.messageLength) {
				this.packMessage(message);
			} else {
				addPackedMessage();
				this.packMessage(message);
			}	
		}
	}
	
	private void splitMessage(byte[] message) {
		for (int i = 0; i < message.length; i++) {
			byte[] copy = new byte[this.messageLength];
			for (int j = 0; i < message.length && j < copy.length; i++, j++) {
				copy[j] = message[i];
			}
			this.addMessage(copy);			
		}		
	}

	private void packMessage(byte[] message) {
		this.messagePacker.packByteArray(message);
	}
	
	private void addPackedMessage() {
		this.messages.add(this.messagePacker.getPackedData());
		this.messagePacker.clear();
		
	}

	public List<byte[]> getNewMessages() {	
		synchronized (lock) {
			//Pack the final message.
			if(this.messagePacker.getPackedSize() > 0) {
				this.addPackedMessage();
			}
	
			List<byte[]> list = new ArrayList<byte[]>();
			list.addAll(messages);
			
			messages.clear();
			return list;	
		}
	}
}
