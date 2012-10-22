package network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import network.exceptions.NetworkException;
import org.junit.Test;

public class ConnectorTests {

	@Test(expected = NetworkException.class)
	public void testIfExceptionIsThrownIfCannotConnect() throws UnknownHostException {
		IConnector connector = new Connector();
		connector.connect(new InetSocketAddress(InetAddress.getByName("localhost"), 1245), 1000);		
	}
}