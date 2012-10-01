package spang.mobile;

import network.Client;
import network.DCCause;
import network.IClient;
import network.Connector;
import network.IConnection;
import network.exceptions.InvalidEndpointException;
import network.exceptions.NetworkException;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;
import events.EventHandler;

/**
 * This is a service which provides applications with the 
 * ability to connect and send messages over the network.
 * @author Gustav Alm Rosenblad & Lukas Kurtyan
 */
public class NetworkService extends Service {
	//The client used to send messages over the network
	private IClient client;
	private final IBinder binder = new NetworkBinder();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate() {
		client = new Client(new Connector());
		
		client.addDisconnectedListener(new EventHandler<IClient, DCCause>() {
			public void onAction(IClient sender, DCCause cause) {
				if(cause == DCCause.TCPTimeout) {				
					sender.reconnect(5);							
				}
			}
		});		
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(this, "Just bound!", Toast.LENGTH_SHORT).show();
		return this.binder;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void onDestroy() {
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show(); 
	}

	
	public class NetworkBinder extends Binder {
		public NetworkService getService()
		{
			return NetworkService.this;
		}
	}

	/**
	 * Connects to the specified host and port.
	 * @param host the host to connect to.
	 * @param port the port to connect to.
	 * @throws InvalidEndpointException thrown if the port or host are invalid.
	 * @throws NetworkException if the connection could not be made.
	 */
	public void connectTo(String host, int port) {
		this.client.connect(host, port);
	}
	
	/**
	 * Sends a udp message over the network.
	 * @param message the message to send.
	 */
	public void sendUDP(byte[] message) {
		this.client.sendUDP(message);		
	}

	/**
	 * Sends a udp message over the network.
	 * @param message the message to send.
	 */
	public void sendTCP(byte[] message) {
		this.client.sendTCP(message);
	}

	public IConnection getConnection() {
		return this.client.getConnection();
	}

	public boolean isConnected() {
		return this.client.isConnected();
	}
}