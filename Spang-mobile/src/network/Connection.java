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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
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
	private final AccTimer timer;
	
	
	/**
	 * Creates a connection.
	 * @param socket a connected socket. 
	 * @throws IllegalArgumentException
	 */
	public Connection(DatagramSocket socket) {
		if(!socket.isConnected())
			throw new IllegalArgumentException("Socket not connected!");
			
		this.socket = socket;		
		this.connected = true;		
		this.timer = new AccTimer();
		this.protocols = new HashMap<Protocol, ProtocolManager>();
		
		populateProtocolMap();
		startResendMessageThread();
	}
	
	private void startResendMessageThread() {
		Thread thread = new Thread(this.timer);
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
	
	
	private void sendInternal(byte[] toSend) {		
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
		this.sendInternal(packer.getPackedData());
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
			recived = this.protocols.get(protocol).processRecivedMessage(copy);
			if(recived != null)
				break;	
		}
		
		return recived;
	}

	private boolean isShutDownMessage(byte[] copy) {
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
		this.connected = false;
		this.timer.StopWorking();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isConnected() {
		return this.connected;
	}


	/**
	 * {@inheritDoc}
	 */
	public int getTimeout() {
		try {
			return this.socket.getSoTimeout();
		} catch (SocketException e) {
			return 0; //Can never happen.
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTimeout(int value) {
		try {
			this.socket.setSoTimeout(value);
		} catch (SocketException e) {
			//Can never happen.
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
		if(this.connected) {
			this.sendShutdownMessage();
			this.connected = false;
		}
		this.socket.close();		
	}
	
	
	
	private class MessageResendTimer {
		private static final long TIMER_INTERVAL = 100000; //Resends after 100 milisec. 1000 00 nanoseconds
		long targetTime;
		int accnumber;
		byte[] message;
		
		public void resend() {
			Connection.this.sendInternal(message);
		}
	}
	
	public class AccTimer extends AsyncWorker {
		private Map<Integer, MessageResendTimer> resenders;
		
		public AccTimer() {
			this.resenders = new ConcurrentHashMap<Integer, MessageResendTimer>();
		}
		
		public void addResender(MessageResendTimer resender) {
				resenders.put(resender.accnumber, resender);
		}
		
		public void removeResender(int accNum) {
				resenders.remove(accNum);
		}
		
		private void updateAndSend() {
    		for (MessageResendTimer resender : this.resenders.values()) {
				long time = System.nanoTime();
				if(resender.targetTime <= time) {
					resender.targetTime += MessageResendTimer.TIMER_INTERVAL;
					resender.resend();
				}
			}		
		}			
		
		@Override
		protected void DoWork() {
			try {
				Thread.sleep(1000);
				this.updateAndSend();
			} catch (InterruptedException e) {
				this.StopWorking();
			} catch(Exception exe) {
				this.StopWorking();
			}
		} 
	}
	
	
	private abstract class ProtocolManager {
		protected abstract int getHeaderLength();
		protected abstract void fixMessage(Packer packer, byte[] message);
		protected abstract byte[] processMessage(UnPacker unPacker);
		
		private void sendMessage(byte[] message) {
			Packer packer = new Packer(message.length + getHeaderLength());
			fixMessage(packer, message);
			Connection.this.sendInternal(packer.getPackedData());			
		}
		
		private byte[] processRecivedMessage(byte[] recivedMessage) {
			return this.processMessage(new UnPacker(recivedMessage));
		}
	}
	
	private class UnorderedProtocol extends ProtocolManager {

		@Override
		protected int getHeaderLength() {
			return 1;
		}

		@Override
		protected void fixMessage(Packer packer, byte[] message) {
			packer.packByte(Protocol.Unordered.getBit());
			packer.packByteArray(message);
		}

		@Override
		protected byte[] processMessage(UnPacker unPacker) {
			unPacker.unpackByte(); //Remove id byte from message.
			return unPacker.unpackByteArray(unPacker.remaining());
		}
	}
	
	private class OrderedProtocol extends ProtocolManager {
		volatile int lastRecivedSequenceNumber;
		volatile int sendReciveSequenceNumber;
		
		@Override
		protected int getHeaderLength() {
			return 5;
		}

		@Override
		protected void fixMessage(Packer packer, byte[] message) {
			packer.packByte(Protocol.Ordered.getBit());
			packer.packInt(sendReciveSequenceNumber++);
			packer.packByteArray(message);
		}

		@Override
		protected byte[] processMessage(UnPacker unPacker) {
			unPacker.unpackByte();
			int seqNum = unPacker.unpackInt();
			if(seqNum < lastRecivedSequenceNumber) {
				return null; //Discard the message if it is old. 
			} else {
				lastRecivedSequenceNumber = seqNum;
				return unPacker.unpackByteArray(unPacker.remaining());
			}
		}	
	}
	
	private class ReliableProtocol extends ProtocolManager {
		volatile int sendAccNum;
		volatile int lastRecivedMessageAck;
		List<Integer> missingMessages = new CopyOnWriteArrayList<Integer>(); 
		
		@Override
		protected int getHeaderLength() {
			return 5;
		}

		@Override
		protected void fixMessage(Packer packer, byte[] message) {
			int toSendAccNum = this.sendAccNum++;
			packer.packByte(Protocol.Reliable.getBit());
			packer.packInt(toSendAccNum);
			packer.packByteArray(message);
			
			MessageResendTimer timer = new MessageResendTimer();
			timer.accnumber = toSendAccNum;
			timer.message = packer.getPackedData();
			timer.targetTime = System.nanoTime() + MessageResendTimer.TIMER_INTERVAL;		
			
			Connection.this.timer.addResender(timer);
		}

		@Override
		protected byte[] processMessage(UnPacker unPacker) {
			int flags = unPacker.unpackByte();		
			int accnum = unPacker.unpackInt();
			
			if((flags & ACK_BIT) == ACK_BIT) {
				Connection.this.timer.removeResender(accnum);
				return null;
			} else {		
				sendAckMessage(accnum);				
				return processRecivedMessage(unPacker, accnum);
			}
		}

		private byte[] processRecivedMessage(UnPacker unPacker, int accnum) {
		    if (accnum > this.lastRecivedMessageAck)
            {
                addMissingMessages(accnum);
                this.lastRecivedMessageAck = accnum;

                return unPacker.unpackByteArray(unPacker.remaining());
            }
            else
            {
                if (this.missingMessages.contains(accnum))
                {
                    this.missingMessages.remove(accnum);
                    return unPacker.unpackByteArray(unPacker.remaining());
                }

                return null;
            }
		}

		private void addMissingMessages(int accnum) {
            for (int i = this.lastRecivedMessageAck; i < this.lastRecivedMessageAck - accnum; i++)
            {
                this.missingMessages.add(i);
            }
		}

		private void sendAckMessage(int accnum) {
			Packer packer = new Packer(5);
			packer.packByte((byte)(Protocol.Reliable.getBit() | ACK_BIT));
			packer.packInt(accnum);
			Connection.this.sendInternal(packer.getPackedData());
		}
	}
	
	private class OrderedReliableProtocol extends ProtocolManager {
        volatile int sendAccNum;
        volatile int lastOrderedMessage;
        private SortedMap<Integer, byte[]> outOfOrderMessages = new ConcurrentSkipListMap<Integer, byte[]>();

        protected int getHeaderLength()
        {
            return 5;
        }

        protected void fixMessage(Packer packer, byte[] message)
        {

			int toSendAccNum = this.sendAccNum++;
			packer.packByte(Protocol.OrderedReliable.getBit());
			packer.packInt(toSendAccNum);
			packer.packByteArray(message);
			
			MessageResendTimer timer = new MessageResendTimer();
			timer.accnumber = toSendAccNum;
			timer.message = packer.getPackedData();
			timer.targetTime = System.nanoTime() + MessageResendTimer.TIMER_INTERVAL;		
			
			Connection.this.timer.addResender(timer);
        }

        protected byte[] processMessage(UnPacker unPacker)
        {
            int flags = unPacker.unpackByte();
            int accnum = unPacker.unpackInt();

            if ((flags & ACK_BIT) == ACK_BIT)
            {
				Connection.this.timer.removeResender(accnum);
                return null;
            }
            else
            {
                sendAckMessage(accnum);
                return processRecivedMessage(unPacker, accnum);
            }
        }

        private byte[] processRecivedMessage(UnPacker unPacker, int accnum)
        {
            if (this.lastOrderedMessage + 1 == accnum)
            {
                this.lastOrderedMessage++;
                if (this.outOfOrderMessages.isEmpty())
                    return unPacker.unpackByteArray(unPacker.remaining());
                
                return this.buildOrderedMessages(unPacker);
            }
            else if (this.lastOrderedMessage >= accnum)
            {
                return null;
            } 
            else
            {
                if (!this.outOfOrderMessages.containsKey(accnum))
                {
                    this.outOfOrderMessages.put(accnum, unPacker.unpackByteArray(unPacker.remaining()));
                }
                return null;
            }
        }

        private synchronized byte[] buildOrderedMessages(UnPacker unPacker)
        {
            List<Integer> keysToRemove = new ArrayList<Integer>();
            Packer packer = new Packer(unPacker.remaining());
            packer.packByteArray(unPacker.unpackByteArray(unPacker.remaining()));

            for(Integer item : this.outOfOrderMessages.keySet())
            {
                if (this.lastOrderedMessage + 1 == item)
                {
                    keysToRemove.add(item);
                    this.lastOrderedMessage++;
                    packer.packByteArray(this.outOfOrderMessages.get(item));
                }
                else
                {
                    break;
                }
            }

            for (Integer integer : keysToRemove) {
            	this.outOfOrderMessages.remove(integer);
			}    

            return packer.getPackedData();
        }


		private void sendAckMessage(int accnum) {
			Packer packer = new Packer(5);
			packer.packByte((byte)(Protocol.Reliable.getBit() | ACK_BIT));
			packer.packInt(accnum);
			Connection.this.sendInternal(packer.getPackedData());
		}
    }
}