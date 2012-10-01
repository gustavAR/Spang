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
	 * @param the remote endpoint to connect to.
	 * @return a new IConnection object that connects the two remote points.
	 */
	IConnection connect(InetSocketAddress address);
}
