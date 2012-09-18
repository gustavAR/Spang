package network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

/**
 * A utility class implementing IConnection making it easier to send and receive data through a connection
 * @author Lukas Kurtyan & Joakim Johansson
 *
 */
public class Connection implements IConnection {

	//The maximum size of a data packet;
	private static final int DATA_CAPACITY = 1024;

	private DatagramSocket udpSocket;
	private Socket tcpSocket;
	
	/**Constructor for Connection.
	 * 
	 */
	public Connection(Socket tcpSocket, DatagramSocket udpSocket)  
	{
		this.tcpSocket = tcpSocket;
		this.udpSocket = udpSocket;
	}

	/**
	 * {@inheritDoc}
	 */	
	public void reconnect() {
		throw new NotImplementedException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void sendUDP(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			this.udpSocket.send(packet);
		} catch (IOException e) {
			throw new IllegalArgumentException("The client could not send the data");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void sendTCP(byte[] data) {
		try {
			OutputStream stream = this.tcpSocket.getOutputStream();
			stream.write(data);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] reciveUDP() {
		DatagramPacket packet = new DatagramPacket(new byte[DATA_CAPACITY], DATA_CAPACITY);
		this.udpSocket.receive(packet);
		return packet.getData();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] reciveTCP() {
		try {
			java.io.InputStream stream = this.tcpSocket.getInputStream();
			int available = stream.available();
			byte[] data = new byte[available];
			stream.read(data);
			return data;
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
}
