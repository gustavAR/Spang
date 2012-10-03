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

import network.exceptions.NetworkException;
import network.exceptions.TimeoutException;

public class Connection implements IConnection {
	
	//The maximum size of incomming packages.
	private static final int DATA_CAPACITY = 1024;
	
	//Are we connected?
	private volatile boolean connected = false;
	
	//The socket used for any actual networking.
	private final DatagramSocket socket;	
	
	
	private final Map<Protocol, IProtocolHelper> protocols;
	
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
		
		this.protocols = new HashMap<Protocol, IProtocolHelper>();
		this.protocols.put(Protocol.Ordered, new OrderedProtocol());
		this.protocols.put(Protocol.Unordered, new UnorderedProtocol());
		this.protocols.put(Protocol.Reliable, new Reliable());
		this.protocols.put(Protocol.ReliableOrdered, new ReliableOrderedProtocol());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void send(byte[] data) {
		this.send(data, Protocol.Unordered);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void send(byte[] toSend, Protocol protocol) {
		toSend = this.protocols.get(protocol).processSentMessage(toSend);
		
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
		DatagramPacket packet = new DatagramPacket(new byte[DATA_CAPACITY], DATA_CAPACITY);
		this.reciveUdpPackage(packet);		
		
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
				throw new TimeoutException("UDP timeout");
			} catch(SocketTimeoutException ste) {
				//We timed out.
				throw new TimeoutException("UDP timeout");
			} catch (IOException e) {
				//Some other error occurred.
				throw new NetworkException("UDP read failed.", e);
			} finally {
				this.connected = false;
			}
		}
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
}