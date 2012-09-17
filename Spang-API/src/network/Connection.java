package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * A utility class implementing IConnection making it easier to send and receive data through a connection
 * @author Lukas Kurtyan & Joakim Johansson
 *
 */
public class Connection implements IConnection {

	//The lowest number a port can take without using reserved port numbers.
	private static final int LOW_PORT = 1024;
	//The largest port number a port can take.
	private static final int HIGH_PORT = 65536;
	//The time after an application times out. 
	private static final int CONNECTION_TIMEOUT = 15000;
	//The maximum size of a data packet;
	private static final int DATA_CAPACITY = 1024;

	private DatagramSocket socket;
	private InetAddress address;
	private int port;
	
	/**Constructor for Connection.
	 * 
	 */
	public Connection()  
	{
		this.address = null;
		this.socket = null;
		this.port = -1;
	}
	
	private InetAddress getAddress(String hostName) {
		try {
			return InetAddress.getByName(hostName);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("The hostName was not found to be a valid IP adress");
		}

	}

	//Makes sure that the InetAdress is valid
	private void validateAddress() {
		try {
			if(address == null) {
				throw new IllegalArgumentException("The address cannot be null.");
			}
			address.isReachable(CONNECTION_TIMEOUT);
		} catch (IOException e) {
			throw new IllegalArgumentException("An IO connection could not be essablished with the provided address.");
		}
	}
	
	//Makes sure that the port is inside the allowed range
	private void validatePort() {
		if(port < LOW_PORT || port > HIGH_PORT) {
			throw new IllegalArgumentException(String.format("The port must be in the range 1024- but was %d", port));
		}
	}

	private void validateConnectionInfo() {
		this.validatePort();
		this.validateAddress();
	}
	
	private boolean canConnect() {
		return this.socket == null;	
	}

	private void createConnection() {
		try {
			this.socket = new DatagramSocket();
			this.socket.connect(this.address, this.port);
		} catch(SocketException e) {
			throw new RuntimeException(e);
		}
	}	
	
	/**
	 * {@inheritDoc}
	 */	
	public void reconnect() {
		this.socket.connect(this.address, port);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void connect(String hostName, int port) {
		if(!canConnect()) {
			throw new IllegalArgumentException("The connection is already connected.");
		}
			
		this.address = this.getAddress(hostName);
		this.port = port;
		this.validateConnectionInfo();
		
		this.createConnection();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void sendUDP(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			throw new IllegalArgumentException("The client could not send the data");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void sendTCP(byte[] data) {
		// TODO implement
		throw new NotImplementedException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] reciveUDP() {
		DatagramPacket packet = new DatagramPacket(new byte[DATA_CAPACITY], DATA_CAPACITY);
		return packet.getData();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] reciveTCP() {
		// TODO implement
		throw new NotImplementedException();
	}
}
