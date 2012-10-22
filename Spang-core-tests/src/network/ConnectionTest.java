package network;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.Packer;

public class ConnectionTest {

	private DatagramSocket mockedSocket;
	private Connection connection;

	@Before
	public void setup() {
		this.mockedSocket = mock(DatagramSocket.class);
		when(this.mockedSocket.isConnected()).thenReturn(true);
		connection = new Connection(mockedSocket);
	}

	@After
	public void teardown() {
		this.connection.close();
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsExceptionOnInvalidUDPConnection() {
		DatagramSocket socket = mock(DatagramSocket.class);	
		new Connection(socket);
		fail("Should throw exception on invalid udp socket!");
	}

	@Test
	public void canSendUnorderedMessage() throws IOException {
		byte[] message = { 1, 2, 3, 4 };
		this.connection.send(message, Protocol.Unordered);

		verify(this.mockedSocket).send(any(DatagramPacket.class));
	}
	
	@Test
	public void canSendOrderedMessage() throws IOException {
		byte[] message = { 1, 2, 3, 4 };
		this.connection.send(message, Protocol.Ordered);

		verify(this.mockedSocket).send(any(DatagramPacket.class));
	}
	
	@Test
	public void canSendReliableMessage() throws IOException {
		byte[] message = { 1, 2, 3, 4 };
		this.connection.send(message, Protocol.Reliable);

		verify(this.mockedSocket).send(any(DatagramPacket.class));
	}
	
	@Test
	public void canSendOrderedReliableMessage() throws IOException {
		byte[] message = { 1, 2, 3, 4 };
		this.connection.send(message, Protocol.OrderedReliable);

		verify(this.mockedSocket).send(any(DatagramPacket.class));
	}

	@Test 
	public void testCanReciveMessage() throws Exception {
		DatagramSocket sendSocket = new DatagramSocket(new InetSocketAddress(InetAddress.getByName("localhost"), 5123));
		DatagramSocket reciveSocket = new DatagramSocket(new InetSocketAddress(InetAddress.getByName("localhost"), 34255));
		reciveSocket.connect(sendSocket.getLocalSocketAddress());
		//sendSocket.connect(reciveSocket.getLocalSocketAddress());
		
		this.connection = new Connection(reciveSocket);
		
		byte[] array = { 1,2,3,4 };
		
		Packer packer = new Packer();
		packer.packByte((byte)0x01);
		packer.packByteArray(array);
		sendSocket.send(new DatagramPacket(packer.getPackedData(), packer.getPackedSize(), reciveSocket.getLocalSocketAddress()));
	
		byte[] recived = this.connection.recive();
		
		for (int i = 0; i < recived.length; i++) {
			assertEquals(recived[i], array[i]);
		}
		
		sendSocket.close();
		reciveSocket.close();
	}
	
	@Test
	public void testTimeout() throws SocketException {
		int expected = 3000;
		this.connection.setTimeout(expected);
		verify(this.mockedSocket).setSoTimeout(expected);
		
		when(this.mockedSocket.getSoTimeout()).thenReturn(expected);
		
		int result = this.connection.getTimeout();
		verify(this.mockedSocket).getSoTimeout();
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testClose() {
		this.connection.close();
		verify(this.mockedSocket).close();
	}
	
	@Test
	public void testIsConnected() {
		assertTrue(this.connection.isConnected());
		this.connection.close();
		assertFalse(this.connection.isConnected());
	}
}