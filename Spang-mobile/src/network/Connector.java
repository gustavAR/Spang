package network;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import network.exceptions.NetworkException;

public class Connector implements IConnector {

	public IConnection connect(InetSocketAddress address) {
		try {
			Socket tcpSocket = new Socket(address.getAddress(), address.getPort());						
			int localPort = tcpSocket.getLocalPort();
			
			DatagramSocket udpSocket = new DatagramSocket(localPort);
		
			int udpPort = readOutgoingUdpPort(tcpSocket);
			//Connects the udpSocket to the correct receiving UDP-connection.
			udpSocket.connect(tcpSocket.getInetAddress(), udpPort);		
			
			return new Connection(tcpSocket, udpSocket);
			
		} catch(Exception e) {
			throw new NetworkException("Could not connect to " + address.getAddress() + " at port" + address.getPort(), e);
		}		
	}
	
	private static int readOutgoingUdpPort(Socket tcpSocket) throws IOException {
		InputStream stream = new DataInputStream(tcpSocket.getInputStream());
		//The format of the sent integer is little-endian so we convert it using a ByteBuffer.
		ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
		stream.read(buffer.array(),0, 4);
		return buffer.getInt();
	}
}
