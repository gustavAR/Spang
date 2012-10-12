package tests;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

import java.net.InetSocketAddress;

import network.Client;
import network.DCCause;
import network.IClient;
import network.IConnection;
import network.IConnector;
import network.exceptions.InvalidEndpointException;
import network.exceptions.NetworkException;

import org.junit.Test;

import events.EventHandler;

/**
 * Tests the client class.
 * @author Lukas Kurtyan
 *
 */
public class ClientTests {
	
/*	private InetSocketAddress sampleAddress() {
		return new InetSocketAddress("localhost", 1337);
	}
	
	
	@Test(expected = InvalidEndpointException.class) 
	public void connectThrowsExceptionIfInvalidHost() {
		Client client = new Client(mock(IConnector.class));		
		client.connect("blargarg", 12313);
		
		fail("Should throw exception.");
	}
	
	@Test(expected = InvalidEndpointException.class) 
	public void connectThrowsExceptionIfInvalidPort() {
		Client client = new Client(mock(IConnector.class));
		client.connect("localhost", -123);
		
		fail("Should throw exception");
	}
	
	@Test 
	public void canReconect() {
		IConnector connector = mock(IConnector.class);
		when(connector.connect(this.sampleAddress(), 5000)).thenReturn(mock(IConnection.class));
				
		Client client = new Client(connector);		
		client.setConnectionTimeout(5000);
		
		client.connect(this.sampleAddress());
		client.disconnect();
		
		assertFalse(client.isConnected());
		
		client.reconnect(1, 1000);
		
		assertTrue(client.isConnected());
	}
	
	@Test 
	public void canDisconnect() {
		IConnector connector = mock(IConnector.class);
		when(connector.connect(this.sampleAddress(), 5000)).thenReturn(mock(IConnection.class));
				
		Client client = new Client(connector);		
		client.setConnectionTimeout(5000);
		
		client.connect(this.sampleAddress());
		client.disconnect();
		
		assertFalse(client.isConnected());
	}
	
	@Test
	public void canConnect() {
		IConnector connector = mock(IConnector.class);
		when(connector.connect(this.sampleAddress(), 5000)).thenReturn(mock(IConnection.class));
				
		Client client = new Client(connector);		
		client.setConnectionTimeout(5000);
		
		client.connect(this.sampleAddress());		
		assertTrue(client.isConnected());		
	}
	
	@Test(expected = NetworkException.class)
	public void connectThrowsExceptionIfAlreadyConnected() {
		IConnector connector = mock(IConnector.class);
		when(connector.connect(this.sampleAddress(), 5000)).thenReturn(mock(IConnection.class));
		
		Client client = new Client(connector);		
		client.setConnectionTimeout(5000);
		
		client.connect(this.sampleAddress());
		//Should get here.
		client.connect(this.sampleAddress());
		
		fail("Should not be able to connect twice");
	}
	
	@Test
	public void onConnectedCalledWhenNewConnectionConnects() {
		IConnector connector = mock(IConnector.class);
		when(connector.connect(this.sampleAddress(), 5000)).thenReturn(mock(IConnection.class));
		Client client = new Client(connector);		
		client.setConnectionTimeout(5000);
		
		//Doing this since java does not support real lambadas. So we need to fake it.
		final boolean[] helper = new boolean[1];
		helper[0] = false;
		
		client.addConnectedListener(new EventHandler<IClient, Boolean>() {
			public void onAction(IClient sender, Boolean eventArgs) {
				helper[0] = true;
			}
		});
		
		client.connect(this.sampleAddress());
		client.disconnect();
		
		assertTrue("Connected not called on connect", helper[0]);					
	}
	
	
	@Test
	public void disconnectedCallbackCalledWhenConnectionTeminates() {		
		IConnector connector = mock(IConnector.class);
		when(connector.connect(this.sampleAddress(), 5000)).thenReturn(mock(IConnection.class));
		Client client = new Client(connector);		
		client.setConnectionTimeout(5000);
		
		//Doing this since java does not support real lambadas. So we need to fake it.
		final boolean[] helper = new boolean[1];
		helper[0] = false;
		
		client.addDisconnectedListener(new EventHandler<IClient, DCCause>() {
			public void onAction(IClient sender, DCCause eventArgs) {
				helper[0] = true;
			}
		});

		client.connect(this.sampleAddress());
		client.disconnect();
		
		assertTrue("Disconnected not called on disconnect", helper[0]);						
	}
	
	@Test
	public void recivedCallbackCalledWhenMessageIsRecived() throws InterruptedException {
		IConnector connector = mock(IConnector.class);
		IConnection connection = mock(IConnection.class);
		when(connector.connect(this.sampleAddress(), 5000)).thenReturn(connection);
		byte[] reciveMessage = new byte[] {1, 2};
		when(connection.recive()).thenReturn(reciveMessage);
		
		Client client = new Client(connector);		
		client.setConnectionTimeout(5000);
		
		//Doing this since java does not support real lambadas. So we need to fake it.
		final boolean[] helper = new boolean[1];
		helper[0] = false;
		
		client.addRevicedListener(new EventHandler<IClient, byte[]>() {
			
			public void onAction(IClient sender, byte[] eventArgs) {
				helper[0] = true;
			}
		});
		
		client.connect(this.sampleAddress());
		
		//Received is called on another thread so we give it some time to process the message.
		Thread.sleep(10);
		
		client.disconnect();
		
		assertTrue(helper[0]);	
	}*/
}