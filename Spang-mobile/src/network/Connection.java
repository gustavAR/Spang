package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import network.exceptions.NetworkException;
import network.exceptions.TimeoutException;
import utils.Packer;
import utils.UnPacker;

public class Connection implements IConnection {
	
	//The maximum size of incomming packages.
	private static final int DATA_CAPACITY = 1024;
	
	//Are we connected?
	private volatile boolean connected = false;
	
	//The socket used for any actual networking.
	private final DatagramSocket socket;	
	
	
	private final Map<Protocol, ProtocolManager> protocols;
	
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
		
		//TODO Implement Reliable Ordered Protocol.
		this.protocols.put(Protocol.ReliableOrdered, new ReliableProtocol());
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
	
	private void sendProtocol(byte[] toSend) {		
		DatagramPacket packet = new DatagramPacket(toSend, toSend.length);		
		try {
			this.socket.send(packet);			
		} catch(IOException exe) {
			throw new NetworkException("The connection could not send the data", exe);
		}	
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] recive() {
		byte[] recived;
		while(true) {
			DatagramPacket packet = new DatagramPacket(new byte[DATA_CAPACITY], DATA_CAPACITY);
			this.reciveUdpPackage(packet);		
			
			byte[] copy = new byte[packet.getLength()];
			for (int i = 0; i < copy.length; i++) {
				copy[i] = packet.getData()[i];
			}
			
			//If copy is heartbeat.
			if(copy.length == 0) {
				recived = copy;
				break;
			}
			
			
			//What protocol was the message sent with?
			Protocol protocol = Protocol.fromID(copy[0]);
			
			recived = this.protocols.get(protocol).processRecivedMessage(copy);
			if(recived.length > 0)
				break;	
		}
		
		return recived;
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
				throw new TimeoutException("UDP timeout");
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
		this.socket.close();		
	}
	
	
	
	private class MessageResendTimer {
		private static final long TIMER_INTERVAL = 100000; //Resends after 100 milisec. 1000 00 nanoseconds
		long targetTime;
		int accnumber;
		byte[] message;
		
		public void resend() {
			Connection.this.sendProtocol(message);
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
			Connection.this.sendProtocol(packer.getPackedData());			
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
			packer.packByte(Protocol.Unordered.getID());
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
			packer.packByte(Protocol.Ordered.getID());
			packer.packInt(sendReciveSequenceNumber++);
			packer.packByteArray(message);
		}

		@Override
		protected byte[] processMessage(UnPacker unPacker) {
			unPacker.unpackByte();
			int seqNum = unPacker.unpackInt();
			if(seqNum < lastRecivedSequenceNumber) {
				return new byte[0]; //Discard the message if it is old. 
			} else {
				lastRecivedSequenceNumber = seqNum;
				return unPacker.unpackByteArray(unPacker.remaining());
			}
		}	
	}
	
	private class ReliableProtocol extends ProtocolManager {
		private final static int ACK_MESSAGE = 0x10;		
		volatile int sendAccNum;
		
		
		@Override
		protected int getHeaderLength() {
			return 5;
		}

		@Override
		protected void fixMessage(Packer packer, byte[] message) {
			int toSendAccNum = this.sendAccNum++;
			packer.packByte(Protocol.Reliable.getID());
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
			
			if((flags & ACK_MESSAGE) == ACK_MESSAGE) {
				Connection.this.timer.removeResender(accnum);
				return new byte[0];
			} else {		
				sendAckMessage(accnum);				
				return unPacker.unpackByteArray(unPacker.remaining());
			}
		}

		private void sendAckMessage(int accnum) {
			Packer packer = new Packer(5);
			packer.packByte((byte)(Protocol.Reliable.getID() | ACK_MESSAGE));
			packer.packInt(accnum);
			Connection.this.sendProtocol(packer.getPackedData());
		}
	}
}