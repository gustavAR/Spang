package network;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Client {


	public Client() { }
	
	public IConnection connectTo(InetAddress address, int port) {
		try {
			Socket tcpSocket = new Socket(address, port);
			DatagramSocket udpSocket = new DatagramSocket(tcpSocket.getLocalPort());
			udpSocket.connect(tcpSocket.getRemoteSocketAddress());
			return new Connection(tcpSocket,udpSocket);

		} catch(Exception e) {
			throw new NetworkException("Could not connect to " + address + " at port" + port, e);
		}


	}

}
