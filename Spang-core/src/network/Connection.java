/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
 *  
 *  This file is part of Spang.

    Spang is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spang is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spang.  If not, see <http://www.gnu.org/licenses/>.
 */
package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import network.exceptions.NetworkException;
import network.exceptions.RemoteCrashException;
import network.exceptions.RemoteShutdownException;
import network.exceptions.TimeoutException;
import utils.Packer;
import utils.UnPacker;

/**
 * Standard implementation of the connection interface.
 * This class represents a connection between two endpoints.
 * The implementation is based on {@link java.net.DatagramSocket;}
 * 
 * This class should never be instantiated using the new keyword. Instead
 * the Connector class should be used {@link network.Connector}.
 * @author Lukas Kurtyan
 *
 */
public class Connection implements IConnection {
	
	//Bit symbolizing message acknowledgment used by Reliable and OrderedReliable protocols.
	private final static int ACK_BIT = 0x10;		
	
	//Bit symbolizing that the remote endpoint closed the connection.
	private final static int SHUTDOWN_BIT = 0x20;
	
	//The maximum size of incomming packages.
	private static final int DATA_CAPACITY = 1024;
	
	//Are we connected?
	private volatile boolean connected = false;
	
	//The socket used for any actual networking.
	private final DatagramSocket socket;	
	
	//Map between protocols and their protocol manager.
	private final Map<Protocol, ProtocolManager> protocols;
	
	//Timer used to resend messages that has yet to be acknowledged by the remote endpoint.
	private final MessageResender resender;
	
	
	/**
	 * Creates a connection. 
	 * @param socket a connected socket. 
	 * @throws IllegalArgumentException if the socket is not connected.
	 */
	protected Connection(DatagramSocket socket) {
		if(!socket.isConnected())
			throw new IllegalArgumentException("Socket not connected!");
			
		this.socket = socket;		
		this.connected = true;		
		this.resender = new MessageResender();
		this.protocols = new HashMap<Protocol, ProtocolManager>();
		
		populateProtocolMap();
		startResendMessageThread();
	}
	
	private void startResendMessageThread() {
		Thread thread = new Thread(this.resender);
		thread.setDaemon(true);
		thread.start();
	}

	private void populateProtocolMap() {
		this.protocols.put(Protocol.Ordered, new OrderedProtocol());
		this.protocols.put(Protocol.Unordered, new UnorderedProtocol());
		this.protocols.put(Protocol.Reliable, new ReliableProtocol());
		this.protocols.put(Protocol.OrderedReliable, new OrderedReliableProtocol());
	}

	/**
	 * {@inheritDoc}
	 */
	public void send(byte[] data) {
		this.send(data, Protocol.Ordered);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void send(byte[] toSend, Protocol protocol) {
		this.protocols.get(protocol).sendMessage(toSend);	
	}
	
	
	private void sendRaw(byte[] toSend) {		
		//Create a new packet to send.
		DatagramPacket packet = new DatagramPacket(toSend, toSend.length);		
		try {
			//Send the packet.
			this.socket.send(packet);			
		} catch(IOException exe) {
			throw new NetworkException("The connection could not send the data", exe);
		}	
	}
	
	private void sendShutdownMessage() {
		//Pack a shutdown message.
		Packer packer = new Packer(1);
		packer.packByte((byte)(SHUTDOWN_BIT));	
		
		//Send it.
		this.sendRaw(packer.getPackedData());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] recive() {
		byte[] recived;
		while(true) {
			//Create a new packet for the next incomming message.
			DatagramPacket packet = new DatagramPacket(new byte[DATA_CAPACITY], DATA_CAPACITY);
			//Receive the message.
			this.reciveUdpPackage(packet);		
						
			//Copy the received data. 
			byte[] copy = copyPacketData(packet);
			
			//If the message is a shutdown message we can no longer be receiving so we throw an 
			//Exception notifying the calling code that the connection is no longer valid.
			if(isShutDownMessage(copy)) {
				throw new RemoteShutdownException("The remote host shut down the connection.");
			}
			
			//Extract the protocol that the data was sent with.
			Protocol protocol = extractProtocol(copy);
	
			//If the protocol is null something is wrong with the message so we simply discard it.
			if(protocol == null)
				continue;
			
			//Process the received data by protocol specific processing.
			recived = this.protocols.get(protocol).processMessage(new UnPacker(copy));
			if(recived != null)
				break;	
		}
		
		return recived;
	}

	private boolean isShutDownMessage(byte[] copy) {
		//The message is a shutdown message if the shutdown-bit is active.
		return copy.length == 1 &&
			   (copy[0] & SHUTDOWN_BIT) == SHUTDOWN_BIT;
	}

	private Protocol extractProtocol(byte[] copy) {
		if(copy.length == 0) {
			//If a message is sent without a protocol we return null to symbolize that the message is invalid.
			return null;
		} else {
			try {
				//Try to extract a protocol form the id byte.
				return Protocol.fromID(copy[0]);
			} catch(IllegalArgumentException e) {
				//If the protocol byte is invalid we return null to symbolize that the message is invalid.
				return null;
			}
		}
	}
	
	private byte[] copyPacketData(DatagramPacket packet) {
		byte[] copy = new byte[packet.getLength()];
		for (int i = 0; i < copy.length; i++) {
			copy[i] = packet.getData()[i];
		}
		return copy;
	}
	
	//Helper that exception checks the receive.
	private void reciveUdpPackage(DatagramPacket packet) {
		while(true) {		
			try {
				//Receives the packet.
				this.socket.receive(packet);
				return;
			} catch(PortUnreachableException ppe) {
				//Host timed out.				
				this.disconnect();
				throw new RemoteCrashException("UDP timeout");
			} catch(SocketTimeoutException ste) {
				//We timed out.	
				this.disconnect();
				throw new TimeoutException("UDP timeout");
			} catch (IOException e) {
				//Some other error occurred.	
				this.disconnect();
				throw new NetworkException("UDP read failed.", e);
			}
		}
	}
	

	private void disconnect() {
		//We are no longer connected so we stop working.
		this.connected = false;
		this.resender.StopWorking();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isConnected() {
		return this.connected  && this.socket.isConnected() && !this.socket.isClosed();
	}


	/**
	 * {@inheritDoc}
	 */
	public int getTimeout() {
		try {
			return this.socket.getSoTimeout();
		} catch (SocketException e) {
			return 0; //Can never happen. If this happens we are in a very bad place and it does not matter.
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTimeout(int value) {
		try {
			this.socket.setSoTimeout(value);
		} catch (SocketException e) {
			//Can never happen. If this happens we are in a very bad place and it does not matter.
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public InetSocketAddress getRemoteEndPoint() {
		return (InetSocketAddress) this.socket.getRemoteSocketAddress();
	}

	/**
	 * {@inheritDoc}
	 */
	public InetSocketAddress getLocalEndPoint() {
		return (InetSocketAddress) this.socket.getLocalSocketAddress();
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {	
		if(this.connected && this.socket.isConnected() && !this.socket.isClosed()) {
			this.sendShutdownMessage();
			this.connected = false;
		}
		this.socket.close();		
	}
	
	
	
	
	//Helper class used to store data needed to resend messages.
	private class MessageInfo {
		//Resends after 100 milisec. 100000 nanoseconds
		private static final long TIMER_INTERVAL = 100000;
		//The time that this message should be resent.
		long targetTime; 
		//The acknowledgment number associated to this message.
		int accnumber;
		//The message that may be resent.
		byte[] message; 
		
		//Resends the message.
		public void resend() {
			Connection.this.sendRaw(message);
		}
	}
	
	
	
	//Implementation of asyncworker that is used to resend messages when Reliable or OrderedReliable protocol is used. 
	private class MessageResender extends AsyncWorker {
		private Map<Integer, MessageInfo> resenders; //The messages to resend.
		
		public MessageResender() {
			//Since multiple threads may access this class we use a ConcurrentCollection.
			this.resenders = new ConcurrentHashMap<Integer, MessageInfo>();
		}
		
		//Adds a message to be resent at an appropriate time.
		public void addResender(MessageInfo messageToResend) {
				resenders.put(messageToResend.accnumber, messageToResend);		
		}
		//Removes resent
		public void removeResender(int accNum) {
				resenders.remove(accNum);
		}
		
		//Resends any messages that needs to be resent.
		private void resendMessages() {
    		for (MessageInfo resender : this.resenders.values()) {
				long time = System.nanoTime();
				if(resender.targetTime <= time) {
					resender.targetTime += MessageInfo.TIMER_INTERVAL;
					resender.resend();
				}
			}		
		}			
		
		@Override
		protected void DoWork() {
			try {
				//Sleep to save CPU cycles.
				Thread.sleep(10);
				this.resendMessages();
			} catch(Exception exe) {
				//Stop working if something went wrong while we were sleeping or sending data.
				this.StopWorking();
			}
		} 
	}
	
	//Superclass for all protocols.
	private abstract class ProtocolManager {
		//To save space different protocols have differently sized headers.
		protected abstract int getHeaderLength();
		
		//Configures the message to include the header.		
		protected abstract void addHeader(Packer packer, byte[] message);
		
		//Decodes the header and processes the messages according to the protocol used.
		protected abstract byte[] processMessage(UnPacker unPacker);
		
		//Sends a message with an appropriate header.
		private void sendMessage(byte[] message) {
			Packer packer = new Packer(message.length + getHeaderLength());
			//Add a protocol header to the message.
			addHeader(packer, message);
			Connection.this.sendRaw(packer.getPackedData());			
		}
		
	}
	
	//Basic udp protocol implementation.
	private class UnorderedProtocol extends ProtocolManager {

		protected int getHeaderLength() {
			//1 byte is used to identify the protocol.
			return 1;
		}

		protected void addHeader(Packer packer, byte[] message) {
			//Add the protocol id bit
			packer.packByte(Protocol.Unordered.getBit());
			//Pack the message.
			packer.packByteArray(message);
		}

		protected byte[] processMessage(UnPacker unPacker) {
			//Remove the id byte from the message.
			unPacker.unpackByte();
			return unPacker.unpackByteArray(unPacker.remaining());
		}
	}
	
	//This protocol discards messages that arrive out of order.
	private class OrderedProtocol extends ProtocolManager {
		//The largest sequence number received so far.
		volatile int lastRecivedSequenceNumber;
		//The largest sequence number sent so far.
		volatile int sendReciveSequenceNumber;
		
		protected int getHeaderLength() {
			//ID byte + sequence-number Integer 1 + 4 = 5
			return 5;
		}

		protected void addHeader(Packer packer, byte[] message) {
			//Pack id bit.
			packer.packByte(Protocol.Ordered.getBit());
			//Pack sequence number.
			packer.packInt(sendReciveSequenceNumber++);
			//Pack message.
			packer.packByteArray(message);
		}

		@Override
		protected byte[] processMessage(UnPacker unPacker) {
			//Discard the ID byte.
			unPacker.unpackByte();
			//Retrieve the sequence number.
			int seqNum = unPacker.unpackInt();
			if(seqNum < lastRecivedSequenceNumber) {
				return null; //Discard the message if it is old. 
			} else {
				//This is the largest sequence number so far so we remember it as such.
				lastRecivedSequenceNumber = seqNum;
				//Unpacks the message without header.
				return unPacker.unpackByteArray(unPacker.remaining());
			}
		}	
	}
	
	//Protocol that guarantees that messages are received. This is done
	//by sending callbacks when a message is received. 
	private class ReliableProtocol extends ProtocolManager {
		//The next acknowledgment number to send with a message.
		volatile int lastAccNumSent;
		
		//The largest acknowledgment number received so far.
		volatile int lastRecivedMessageAck;
		
		//All messages that are missing. Messages that should have made it here 
		//based on different acknowledgment number received.
		List<Integer> missingMessages = new CopyOnWriteArrayList<Integer>(); 
		
		@Override
		protected int getHeaderLength() {
			//ID byte + acknowledgment Integer 1 + 4 = 5.
			return 5;
		}

		@Override
		protected void addHeader(Packer packer, byte[] message) {
			//The number to send.			
			int toSendAccNum = this.lastAccNumSent++;
			//Pack bit ID.
			packer.packByte(Protocol.Reliable.getBit());
			//Pack acknowledgment number.
			packer.packInt(toSendAccNum);
			//Pack the message.
			packer.packByteArray(message);
			
			//Create a message info so that the message can be resent if needed.
			MessageInfo info = new MessageInfo();
			info.accnumber = toSendAccNum;
			info.message = packer.getPackedData();
			info.targetTime = System.nanoTime() + MessageInfo.TIMER_INTERVAL;		
			
			//Add the message to the resender so that it can be resent.
			Connection.this.resender.addResender(info);
		}

		@Override
		protected byte[] processMessage(UnPacker unPacker) {
			//Unpack the flag bit (ID bit is included in this)
			int flags = unPacker.unpackByte();		
			//The acknowledgment number received.
			int accnum = unPacker.unpackInt();
			
			//Check if the message is a callback acknowledgment message.
			if((flags & ACK_BIT) == ACK_BIT) {
				Connection.this.resender.removeResender(accnum);
				return null;
			} else {		
				//If the message is not a callback we send our own callback message
				//to notify the other endpoint that we recived the message.
				sendAckMessage(accnum);				
				
				//Do additional processing on the message.
				return processRecivedMessage(unPacker, accnum);
			}
		}

		private byte[] processRecivedMessage(UnPacker unPacker, int accnum) {
		    //If the received message has an acknowledgment number larger than 
			//any we have received before we can be certain that we receive this message 
			//for the first time.
			if (accnum > this.lastRecivedMessageAck)
            {   	
				//If the accnum is out of order add any missing messages.
                addMissingMessages(accnum);
                this.lastRecivedMessageAck = accnum;
                
                //Return the actual message.
                return unPacker.unpackByteArray(unPacker.remaining());
            }
            else
            {
            	//If the message is a missing message we receive it for the first time.
            	//So we should let it pass through to the caller.
                if (this.missingMessages.contains(accnum))
                {
                    this.missingMessages.remove(accnum);
                    return unPacker.unpackByteArray(unPacker.remaining());
                }
                
                //If we were not missing the message we discard it.
                return null;
            }
		}

		private void addMissingMessages(int accnum) {
			//Adds any missing messages in the interval (lastRecivedMessageAck, accnum)
            for (int i = this.lastRecivedMessageAck; i < this.lastRecivedMessageAck - accnum; i++)
            {
                this.missingMessages.add(i);
            }
		}

		//Sends an acknowledgment message.
		private void sendAckMessage(int accnum) {
			Packer packer = new Packer(5);
			//The acknowledgment is specified using the ACK_BIT so we add it to the ID byte. and pack it.
			packer.packByte((byte)(Protocol.Reliable.getBit() | ACK_BIT));
			//Packs the acknowledgment number.
			packer.packInt(accnum);
			//Send the acknowledgment message.
			Connection.this.sendRaw(packer.getPackedData());
		}
	}
	
	//Like the reliable protocol but this also makes the packages arrive in order.
	private class OrderedReliableProtocol extends ProtocolManager {
		//The next acknowledgment number to send with a message.
		volatile int lastAccNumSent;
				
		//The last message that arrived in order.
		volatile int lastOrderedMessage;
		
		//Stores all the messages that arrived out of order.
        private SortedMap<Integer, byte[]> outOfOrderMessages = new TreeMap<Integer, byte[]>();

        protected synchronized int getHeaderLength()
        {
			//ID byte + acknowledgment Integer 1 + 4 = 5.
            return 5;
        }

        protected synchronized void addHeader(Packer packer, byte[] message)
        {
			//The number to send.			
			int toSendAccNum = this.lastAccNumSent++;
			//Pack bit ID.
			packer.packByte(Protocol.OrderedReliable.getBit());
			//Pack acknowledgment number.
			packer.packInt(toSendAccNum);
			//Pack the message.
			packer.packByteArray(message);
			
			//Create a message info so that the message can be resent if needed.
			MessageInfo info = new MessageInfo();
			info.accnumber = toSendAccNum;
			info.message = packer.getPackedData();
			info.targetTime = System.nanoTime() + MessageInfo.TIMER_INTERVAL;		
			
			//Add the message to the resender so that it can be resent.
			Connection.this.resender.addResender(info);
        }

        protected synchronized byte[] processMessage(UnPacker unPacker)
        {      	
			//Unpack the flag bit (ID bit is included in this)
			int flags = unPacker.unpackByte();		
			//The acknowledgment number received.
			int accnum = unPacker.unpackInt();
			
			//Check if the message is a callback acknowledgment message.
			if((flags & ACK_BIT) == ACK_BIT) {
				Connection.this.resender.removeResender(accnum);
				return null;
			} else {		
				//If the message is not a callback we send our own callback message
				//to notify the other endpoint that we received the message.
				sendAckMessage(accnum);				
				
				//Do additional processing on the message.
				return processRecivedMessage(unPacker, accnum);
			}
        }

        private synchronized byte[] processRecivedMessage(UnPacker unPacker, int accnum)
        {
        	//If the message is the next message in order it's acknowledgment number
        	//should be one higher then the previous one to arrive.
            if (this.lastOrderedMessage + 1 == accnum)
            {
                this.lastOrderedMessage++;
                
                //If we have no out of order messages we simply returns this message.
                if (this.outOfOrderMessages.isEmpty())
                    return unPacker.unpackByteArray(unPacker.remaining());
                
                //Build a new message containing all ordered messages we can pack into it.
                return this.buildOrderedMessages(unPacker);
            }
            //If the message is less then the largest ordered processed its an old message so we discard it.
            else if (this.lastOrderedMessage >= accnum)
            {
                return null;
            } 
            else
            {
            	//Add out of order messages to the collection.
                if (!this.outOfOrderMessages.containsKey(accnum))
                {
                    this.outOfOrderMessages.put(accnum, unPacker.unpackByteArray(unPacker.remaining()));
                }
                
                //Since the message is out of order we discard it.
                return null;
            }
        }

        
        private synchronized byte[] buildOrderedMessages(UnPacker unPacker)
        {
        	//Stores all the messages processed that can be removed.
            List<Integer> keysToRemove = new ArrayList<Integer>();
            
            //Create a packer to pack ordered messages.
            Packer packer = new Packer(unPacker.remaining());
            packer.packByteArray(unPacker.unpackByteArray(unPacker.remaining()));

            for(Integer item : this.outOfOrderMessages.keySet())
            {
            	//If the next out of order message is in fact in order pack it to the new message.
                if (this.lastOrderedMessage + 1 == item)
                {
                    keysToRemove.add(item);
                    this.lastOrderedMessage++;
                    packer.packByteArray(this.outOfOrderMessages.get(item));
                }
                else
                {
                	//Any subsequent messages are also out of order so we break out of the loop.
                    break;
                }
            }

            //Remove the messages that were packed.
            for (Integer integer : keysToRemove) {
            	this.outOfOrderMessages.remove(integer);
			}    

            //Return the new packed message.
            return packer.getPackedData();
        }
      
		//Sends an acknowledgment message.
		private synchronized void sendAckMessage(int accnum) {
			Packer packer = new Packer(5);
			//The acknowledgment is specified using the ACK_BIT so we add it to the ID byte. and pack it.
			packer.packByte((byte)(Protocol.Reliable.getBit() | ACK_BIT));
			//Packs the acknowledgment number.
			packer.packInt(accnum);
			//Send the acknowledgment message.
			Connection.this.sendRaw(packer.getPackedData());
		}
    }
}