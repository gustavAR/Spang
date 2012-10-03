package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import network.exceptions.NetworkException;

public class Connector implements IConnector {

	/**
	 * {@inheritDoc}
	 */
	public IConnection connect(InetSocketAddress address, int timeout) {
		try {
			DatagramSocket socket = new DatagramSocket();
			//Sends a empty message signaling that we want to connect.
			DatagramPacket dpacket = new DatagramPacket(new byte[0], 0, address);
			socket.send(dpacket);
							
			//Receives a callback message giving endpoint specific information.
			int remotePort = this.readOutgoingUdpPort(socket);
			//Uses the received information to connect.
			socket.connect(new InetSocketAddress(address.getAddress(), remotePort));		
		
			return new Connection(socket);
		} catch (IOException e) {
			throw new NetworkException("Could not connect");
		}
	}

	private int readOutgoingUdpPort(DatagramSocket socket) throws IOException {
		//Recives connection information package.
		DatagramPacket dpacket = new DatagramPacket(new byte[4], 4);
		socket.receive(dpacket);
		
		//Parses the package using LittleEndian byte order.
		ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(dpacket.getData());
		buffer.rewind();
		
		
		return buffer.getInt();
	}
}