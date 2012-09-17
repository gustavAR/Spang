package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * An utility class implementing IConnection making it easier to send and receive data through a connection
 * @author Lukas Kurtyan & Joakim Johansson
 *
 */
public class ClientConnection implements IConnection {

	//The lowest number a port can take without using reserved port numbers.
	private static final int LOW_PORT = 1024;
	//The largest port number a port can take.
	private static final int HIGH_PORT = 65536;
	//The time after an application times out. 
	private static final int CONNECTION_TIMEOUT = 15000;
	//The maximum receive size of a datagram packet;
	private static final int RECEIVE_CAPACITY = 1024;


	private InetAddress address;
	private int port;

	private final DatagramSocket socket;

	/**
	 * 
	 * @param address This has to be a valid adress and cannot be null
	 * @param port This has to be a port not in use
	 * @throws IllegalArgumentException
	 */
	public ClientConnection(InetAddress address, int port) {
		this.validatePort(port);
		this.validateAddress(address);

		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			throw new IllegalArgumentException(e);
		}
		this.address = address;
		this.port = port;

	}
	//Makes sure that the InetAdress is valid
	private void validateAddress(InetAddress address) {
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
	private void validatePort(int port) {
		if(port < LOW_PORT || port > HIGH_PORT) {
			throw new IllegalArgumentException(String.format("The port must be in the range 1024- but was %d", port));
		}
	}

	/**
	 * {@inheritDoc}
	 */	
	public void connect() {
		this.socket.connect(this.address, port);
	}
	/**
	 * {@inheritDoc}
	 */
	public void connect(String hostName, int port) {
		this.updateAddress(hostName);
		this.socket.connect(address, port);
	}

	private void updateAddress(String hostName) {

		try {
			this.address = InetAddress.getByName(hostName);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("The hostName was not found to be a valid IP adress");
		}

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
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}
	/**
	 * {@inheritDoc}
	 */
	public byte[] reciveUDP() {
		DatagramPacket packet = new DatagramPacket(new byte[RECEIVE_CAPACITY], RECEIVE_CAPACITY);
		return packet.getData();
	}
	/**
	 * {@inheritDoc}
	 */
	public byte[] reciveTCP() {
		throw new NotImplementedException();
	}

}
