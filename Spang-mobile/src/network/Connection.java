package network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Arrays;

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
			DataOutputStream stream = new DataOutputStream(this.tcpSocket.getOutputStream());
			stream.writeShort(data.length);
			stream.write(data);
			stream.flush();
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] reciveUDP() {
		DatagramPacket packet = new DatagramPacket(new byte[DATA_CAPACITY], DATA_CAPACITY);
		try {
			this.udpSocket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		packet.getLength();
		
		
		byte[] copy = new byte[packet.getLength()];
		for (int i = 0; i < copy.length; i++) {
			copy[i] = packet.getData()[i];
		}
		
		return copy;
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
