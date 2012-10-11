package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import network.Connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ConnectionTest {

	private DatagramSocket mockedSocket;
	private Connection connection;


	@Before
	public void setup() {
		this.mockedSocket = mock(DatagramSocket.class);
		when(this.mockedSocket.isConnected()).thenReturn(true);
//		connection = new Connection(mockedSocket);
	}

	@After
	public void teardown() {
		this.connection.close();
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsExceptionOnInvalidUDPConnection() {
		DatagramSocket socket = mock(DatagramSocket.class);	
//		new Connection(socket);
		fail("Should throw exception on invalid udp socket!");
	}

	@Test
	public void canSendMessage() throws IOException {
		byte[] message = { 1, 2, 3, 4 };
		this.connection.send(message);

		verify(this.mockedSocket).send(any(DatagramPacket.class));
	}

	@Test
	public void canReciveMessage() throws IOException {
		this.connection.recive();	
		verify(this.mockedSocket).receive(any(DatagramPacket.class));
	}	
}