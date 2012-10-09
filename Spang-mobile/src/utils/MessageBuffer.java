package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import network.Protocol;

public class MessageBuffer {
	private final int messageLength;
<<<<<<< HEAD
	private List<byte[]> messages;
	private final Packer messagePacker;

=======
	private Map<Protocol, List<byte[]>> messages;
	private final Map<Protocol, Packer> messagePackers;
	
>>>>>>> origin/master
	private final Object lock = new Object();


	public MessageBuffer(int messageLength) {
<<<<<<< HEAD
		this.messageLength = messageLength;	
		this.messagePacker = new Packer(messageLength);
		this.messages = new ArrayList<byte[]>();
	}

	public void addMessage(byte[] message) {	
=======
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
>>>>>>> origin/master
		synchronized (lock) {
			if(message.length > this.messageLength) {
				splitMessage(message,protocol);
				return;
<<<<<<< HEAD
			}

			if(this.messagePacker.getPackedSize() + message.length < this.messageLength) {
				this.packMessage(message);
=======
			} 
			
			Packer packer = this.messagePackers.get(protocol);
			if(packer.getPackedSize() + message.length < this.messageLength) {
				packer.packByteArray(message);
>>>>>>> origin/master
			} else {
				addPackedMessage(protocol);
				packer.packByteArray(message);
			}	
		}
	}
<<<<<<< HEAD

	private void splitMessage(byte[] message) {
=======
	
	private void splitMessage(byte[] message, Protocol protocol) {
>>>>>>> origin/master
		for (int i = 0; i < message.length; i++) {
			byte[] copy = new byte[this.messageLength];
			for (int j = 0; i < message.length && j < copy.length; i++, j++) {
				copy[j] = message[i];
			}
<<<<<<< HEAD
			this.addMessage(copy);	
		}	
	}

	private void packMessage(byte[] message) {
		this.messagePacker.packByteArray(message);
	}

	private void addPackedMessage() {
		this.messages.add(this.messagePacker.getPackedData());
		this.messagePacker.clear();

=======
			
			this.addMessage(copy,protocol);			
		}		
	}

	private void addPackedMessage(Protocol protocol) {
		Packer packer = this.messagePackers.get(protocol);
		List<byte[]> protocolMessages = this.messages.get(protocol);
		protocolMessages.add(packer.getPackedData());
		packer.clear();
>>>>>>> origin/master
	}

	public List<byte[]> getNewMessages(Protocol protocol) {	
		synchronized (lock) {
			Packer packer = this.messagePackers.get(protocol);
			List<byte[]> protocolMessages = this.messages.get(protocol);
			
			//Pack the final message.
<<<<<<< HEAD
			if(this.messagePacker.getPackedSize() > 0) {
				this.addPackedMessage();
=======
			if(packer.getPackedSize() > 0) {
				this.addPackedMessage(protocol);
>>>>>>> origin/master
			}

			List<byte[]> list = new ArrayList<byte[]>();
<<<<<<< HEAD
			list.addAll(messages);

			messages.clear();
=======
			list.addAll(protocolMessages);
			
			protocolMessages.clear();
>>>>>>> origin/master
			return list;	
		}
	}
}
