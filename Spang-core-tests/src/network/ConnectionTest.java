/*  Copyright 2012 Joakim Johansson, Lukas Kurtyan, Gustav Alm Rosenblad and Pontus Pall
 *  
 *  This file is part of Spang.

    Spang is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spang is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spang.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	public void testCanReceiveMessage() throws Exception {
		DatagramSocket sendSocket = new DatagramSocket(new InetSocketAddress(InetAddress.getByName("localhost"), 5123));
		DatagramSocket receiveSocket = new DatagramSocket(new InetSocketAddress(InetAddress.getByName("localhost"), 34255));
		receiveSocket.connect(sendSocket.getLocalSocketAddress());
		//sendSocket.connect(receiveSocket.getLocalSocketAddress());
		
		this.connection = new Connection(receiveSocket);
		
		byte[] array = { 1,2,3,4 };
		
		Packer packer = new Packer();
		packer.packByte((byte)0x01);
		packer.packByteArray(array);
		sendSocket.send(new DatagramPacket(packer.getPackedData(), packer.getPackedSize(), receiveSocket.getLocalSocketAddress()));
	
		byte[] received = this.connection.receive();
		
		for (int i = 0; i < received.length; i++) {
			assertEquals(received[i], array[i]);
		}
		
		sendSocket.close();
		receiveSocket.close();
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