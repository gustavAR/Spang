package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import network.exceptions.NetworkException;
import utils.UnPacker;

public class Connector implements IConnector {

	/**
	 * {@inheritDoc}
	 */
	public IConnection connect(InetSocketAddress address, int timeout) {
		try {
			DatagramSocket socket = new DatagramSocket();
			// It should take no longer then 500 miliseconds to connect if it does we try again.
			//socket.setSoTimeout(20000);
			
			sendConnectionRequest(address, socket);				
			reciveConnectionInformation(address, socket);		
			
			sendConnectionAcknowledgement(socket);		
		
			
			return new Connection(socket);
		} catch (IOException e) {
			throw new NetworkException("Could not connect");
		}
	}

	private void reciveConnectionInformation(InetSocketAddress address,
			DatagramSocket socket) throws IOException, SocketException {
		//Receives a callback message giving endpoint specific information.
		int remotePort = this.readOutgoingUdpPort(socket);
		//Uses the received information to connect.
		socket.connect(new InetSocketAddress(address.getAddress(), remotePort));
	}

	private void sendConnectionRequest(InetSocketAddress address,
			DatagramSocket socket) throws SocketException, IOException {
		
		//Sends a empty message signaling that we want to connect.
		DatagramPacket dpacket = new DatagramPacket(new byte[0], 0, address);
		socket.send(dpacket);
	}

	private int readOutgoingUdpPort(DatagramSocket socket) throws IOException {
		//Recives connection information package.
		DatagramPacket dpacket = new DatagramPacket(new byte[4], 4);
		socket.receive(dpacket);
		//Unpack the contents of the package.
		UnPacker unPacker = new UnPacker(dpacket.getData());	
		return unPacker.unpackInt();
	}

	private void sendConnectionAcknowledgement(DatagramSocket socket) throws IOException {
		//Send an empty message to let the endpoint know that we are ready to send and recive data.
		DatagramPacket dpacket = new DatagramPacket(new byte[0], 0);
		socket.send(dpacket);
	}
}