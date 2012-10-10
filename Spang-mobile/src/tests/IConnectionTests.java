package tests;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;

import network.Connector;
import network.IConnection;
import network.IConnector;
import network.Protocol;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class IConnectionTests {

	private InetSocketAddress localServerAddr;
	
	private IConnection connection;
	private IConnector connector;
	
	@BeforeClass
	public void setup() {
		connection = connector.connect(localServerAddr, 1000);
	}
	
	@AfterClass
	public void tearDown() {
		connection.close();
	}
	
	public IConnectionTests(IConnector connector) {
		this.connector = connector;
		
	}
	

	@Parameters
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] { { new Connector() } };		
		return Arrays.asList(data);
	}

	@Test
	public void canSendData() {
		connection.send(new byte[] { 1 }, Protocol.Unordered);
		connection.send(new byte[] { 1 }, Protocol.Ordered);
		connection.send(new byte[] { 1 }, Protocol.Reliable);
		connection.send(new byte[] { 1 }, Protocol.OrderedReliable);
	}
	
	@Test
	public void canReciveData() {
		connection.recive();
	}
	
	@Test
	public void isConnectedUseful() {
		assertTrue(this.connection.isConnected());
		this.connection.close();
		assertFalse(this.connection.isConnected());
	}
	
	@Test
	public void timeoutMethodsWork() {
		this.connection.setTimeout(12455);
		assertSame(this.connection.getTimeout(), 12455);
	}
	
	@Test
	public void canClose() {
		this.connection.close();		
	}
}