package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import network.exceptions.NetworkException;
import network.exceptions.TimeoutException;
import utils.Logger;

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
	public void sendUDP(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			this.udpSocket.send(packet);
		} catch (IOException e) {
			throw new NetworkException("The connection could not send the data", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void sendTCP(byte[] data) {
		try {
			OutputStream stream = this.tcpSocket.getOutputStream();
			short length = (short)data.length;
			
			stream.write((byte)((length >> 8) & 0xFF));
			stream.write((byte)(length & 0xFF));
			
			stream.write(data);
			stream.flush();
		} catch (IOException e) {
			throw new NetworkException("The connection could not send the data", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] reciveUDP() {
		DatagramPacket packet = new DatagramPacket(new byte[DATA_CAPACITY], DATA_CAPACITY);
		this.reciveUdpPackage(packet);		
		
		byte[] copy = new byte[packet.getLength()];
		for (int i = 0; i < copy.length; i++) {
			copy[i] = packet.getData()[i];
		}
		
		return copy;
	}
	
	private void reciveUdpPackage(DatagramPacket packet) {
		while(true) {		
			try {
				this.udpSocket.receive(packet);
				return;
			}catch(SocketTimeoutException ste) {
				//Since udp is a connection-less protocol it should never timeout.
				//But this particular java implementation does for some reason.
				//We ignore it and simply try to receive the package again.
			} catch (IOException e) {
				throw new NetworkException("UDP read failed.", e);
			}
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] reciveTCP() {
		try {
			InputStream stream = this.tcpSocket.getInputStream();			
			int b1 = stream.read();
			int b2 = stream.read();
			int length = (b1 << 8) | b2;			
			byte[] data = new byte[length];
						
			stream.read(data);
			return data;
		}catch(SocketTimeoutException ste) {
			throw new TimeoutException("Tcp read timed out.", ste);		
		}catch(NegativeArraySizeException neg) {
			//Sometimes when the socket times out the stream continues to read.
			throw new TimeoutException("Tcp read timed out.", neg);		
		} catch (IOException e) {
			throw new NetworkException("Tcp read failed", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isConnected() {
		return this.tcpSocket.isConnected();
	}


	/**
	 * {@inheritDoc}
	 */
	public void setTimeout(int value) {
		try {
			this.tcpSocket.setSoLinger(true, value);
		} catch (SocketException e) {
			Logger.logException(e);			
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getTimeout() {
		try {
			return this.tcpSocket.getSoTimeout();
		} catch (SocketException e) {
			Logger.logException(e);
			return 0;
		}	
	}
	
	/**
	 * {@inheritDoc}
	 */
	public InetSocketAddress getRemoteEndPoint() {
		return (InetSocketAddress) this.tcpSocket.getRemoteSocketAddress();
	}

	/**
	 * {@inheritDoc}
	 */
	public InetSocketAddress getLocalEndPoint() {
		return (InetSocketAddress) this.tcpSocket.getLocalSocketAddress();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void close() {
		try {
			this.tcpSocket.close();
			this.udpSocket.close();
		} catch (IOException e) {
			throw new NetworkException("Failed to close the connection!");			
		}
	}
}