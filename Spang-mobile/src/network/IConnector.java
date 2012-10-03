package network;
import java.net.InetSocketAddress;

/**
 * Interface used to connect IConnections.
 * @author LukasFiddle
 *
 */
public interface IConnector {
	
	/**
	 * Connects to a remote endpoint.
	 * @param address the remote endpoint to connect to.
	 * @param timeout the time the connector will try to connect.
	 * @return a new IConnection object that connects the two remote points.
	 */
	IConnection connect(InetSocketAddress address, int timeout);
}
