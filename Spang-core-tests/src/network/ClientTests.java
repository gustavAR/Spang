package network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import network.exceptions.InvalidEndpointException;

import org.junit.Before;
import org.junit.Test;

import serialization.SerializeManager;
import serialization.StringSerializer;
import spang.events.EventHandler;
import utils.Packer;
import utils.UnPacker;

/**
 * Tests the client class.
 * @author Lukas Kurtyan
 *
 */
public class ClientTests {

	private IClient client; 
	private IConnector mockedConnector;
	private IConnection mockedConnection;
	private SerializeManager mockedManager;
	
	@Before
	public void setup() {
		mockedConnector = mock(IConnector.class);
		mockedConnection = mock(IConnection.class);
		mockedManager = mock(SerializeManager.class);
		when(mockedConnector.connect(any(InetSocketAddress.class), anyInt())).thenReturn(mockedConnection);
			
		client = new Client(mockedConnector, mockedManager);
	}
	
	
	@Test (expected = InvalidEndpointException.class)
	public void testIfExceptionIsThrownOnInvalidPort() {
		client.connect("localhost", -2);
	}
	
	@Test (expected = InvalidEndpointException.class)
	public void testIfExceptionIsThrownOnInvalidHost() {
		client.connect("Trololo", 23545);
	}
	
	@Test
	public void testRegisterSerializer(){
		StringSerializer serializer = new StringSerializer();
		client.registerSerializer(serializer);
		verify(this.mockedManager).registerSerilizer(serializer);
	}
	
	@Test
	public void testConnectionGetSetTimeoutBeforeConnected(){
		int expected = 1000;
		client.setConnectionTimeout(expected);
		int result = client.getConnectionTimeout();
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testConnectionGetSetTimeoutAfterConnected(){
		client.connect("localhost", 21352);
		int expected = 1000;
		client.setConnectionTimeout(expected);
		verify(this.mockedConnection).setTimeout(expected);
		
		int result = client.getConnectionTimeout();
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testIfCanConnect() throws UnknownHostException{
		InetSocketAddress adress = new InetSocketAddress(InetAddress.getByName("localhost"),2622);
		client.connect(adress);
		verify(this.mockedConnector).connect(adress, client.getConnectionTimeout());
	}
	
	@Test
	public void testIfConnectionClosedOnDisconnect(){
		client.connect("localhost", 2161);
		client.disconnect();
		verify(this.mockedConnection).close();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testIfConnectionIsThrownIfCannotReconnect(){
		client.reconnect(5, 4000);
	}
	
	@Test 
	public void testIfCanReconnect(){
		client.connect("localhost", 2161);
		client.disconnect();
		client.reconnect(5, 4000);
	}
	
	@Test
	public void testIfReconnectDoesNothingWhenAlreadyConnected(){
		client.connect("localhost", 2161);
		client.reconnect(5, 4000);
	}
	
	@Test
	public void testIfDisconnectDoesNothingIfNotConnected(){
		client.disconnect();
	}
	
	@Test
	public void testIfConnectedEventIsTriggeredWhenConnected() {
		final boolean[] flag = {false}; //Java sucks. 
		this.client.addConnectedListener(new EventHandler<IClient, Boolean>() {
			@Override
			public void onAction(IClient sender, Boolean eventArgs) {
				flag[0] = true;
			}
		});
		
		this.client.connect("localhost", 1234);
		assertTrue(flag[0]);		
	}
	
	@Test
	public void testIfDisconnectedEventIsTriggeredWhenDisconnected() {
		final boolean[] flag = {false};
		this.client.addDisconnectedListener(new EventHandler<IClient, DCCause>() {
			
			@Override
			public void onAction(IClient sender, DCCause eventArgs) {
				flag[0] = true;
			}
		});
		this.client.connect("localhost", 1234);
		this.client.disconnect();
		assertTrue(flag[0]);		
	}
	
	@Test
	public void testIfReciveEventIsTriggeredWhenDataIsRecived() throws InterruptedException {
		byte[] array = { 1,2,3,4 };
		final String string = "HellO";
		when(this.mockedConnection.recive()).thenReturn(array);
		when(this.mockedManager.deserialize(any(UnPacker.class))).thenReturn(string);
		
		final boolean[] flag = {false};
		this.client.addRevicedListener(new EventHandler<IClient, Object>() {	
			@Override
			public void onAction(IClient sender, Object eventArgs) {
				if(eventArgs.equals(string))
					flag[0] = true;
			}
		});
		this.client.connect("localhost",1324);	
		Thread.sleep(100);
		
		assertTrue(flag[0]);
	}
	
	@Test
	public void testIfCanSendMessages() {
		String message = "Hello";
		this.client.connect("localhost", 2142);
		this.client.send(message);
		
		verify(this.mockedManager).serialize(any(Packer.class), any(String.class));
		verify(this.mockedConnection).send(any(byte[].class), any(Protocol.class));
	}
	
	@Test
	public void testIfCanSendMessagesWithProtocols() {
		String message = "Hello";
		this.client.connect("localhost", 2142);
		this.client.send(message, Protocol.Ordered);
		
		verify(this.mockedManager).serialize(any(Packer.class), any(String.class));
		verify(this.mockedConnection).send(new byte[0],Protocol.Ordered);
	}
	
	
	
}