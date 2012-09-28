package spang.mobile;

import network.Client;
import network.IClient;
import network.exceptions.HostException;
import network.exceptions.NetworkException;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

/**
 * This is a service which provides applications with the 
 * ability to connect and send messages over the network.
 * @author Gustav Alm Rosenblad & Lukas Kurtyan
 */
public class NetworkService extends Service {

	/**
	 * The IP-address to connect to
	 */
	public static final String CONNECTION_ADDRESS = "spang_connection_address";

	/**
	 * The port to connect to
	 */
	public static final String CONNECTION_PORT = "spang_connection_port";

	/**
	 * The id of messages to be sent over the network
	 */
	public static final String NETWORK_MESSAGE = "spang_network_message";

	/**
	 * Indicates that the message is a TCP network message
	 */
	public static final int TCP_NETWORK_MESSAGE = 0;

	/**
	 * Indicates that the message is a UDP network message
	 */
	public static final int UDP_NETWORK_MESSAGE = 1;


	/**
	 * Indicates that the host is invalid
	 */
	public static final int CONNECTION_FAIL_ADDRESS = 2;

	/**
	 * Indicates that the connection has timed out
	 */
	public static final int CONNECTION_FAIL_TIMEOUT = 3;

	/**
	 * Indicates that the connection has successfully connected
	 */
	public static final int CONNECTION_SUCCESS = 4;

	/**
	 * Indicates a callback message when the binder binds
	 */
	public static final int CALLBACK_MESSAGE = 5;

	//The client used to send messages over the network
	private IClient client;
	//The messenger which communicates with activities
	private final Messenger messenger = new Messenger(new NetworkHandler());
	private Messenger sender = null;
	private int connectionResult;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate() {
		client = new Client();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBinder onBind(Intent intent) {
		if(!this.client.isConnected())
			setupConnection(intent);
		return this.messenger.getBinder();
	}

	private void setupConnection(Intent intent) {

		String hostAddress = intent.getStringExtra(CONNECTION_ADDRESS);
		int port = intent.getIntExtra(CONNECTION_PORT, -1);

		try{
			this.client.connect(hostAddress, port);
			this.connectionResult = CONNECTION_SUCCESS;
		} catch (HostException exe){
			this.connectionResult = CONNECTION_FAIL_ADDRESS;
		} catch (NetworkException exe){
			this.connectionResult = CONNECTION_FAIL_TIMEOUT;
		}
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show(); 
	}

	private final class NetworkHandler extends Handler {
		public NetworkHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {   
			switch (msg.what) {   
			case UDP_NETWORK_MESSAGE:
				byte[] udpMessage = msg.getData().getByteArray(NETWORK_MESSAGE);
				NetworkService.this.client.sendUDP(udpMessage);
				break;
			case TCP_NETWORK_MESSAGE:
				byte[] tcpMessage = msg.getData().getByteArray(NETWORK_MESSAGE);
				NetworkService.this.client.sendTCP(tcpMessage);
				break;
			case CALLBACK_MESSAGE:
				NetworkService.this.sender = msg.replyTo;
				Message message = Message.obtain(null, connectionResult);
				
				try {
					NetworkService.this.sender.send(message);
				} catch (RemoteException e) {
					//WAHT
				}
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}

	public class NetworkBinder extends Binder {
		public NetworkService getService()
		{
			return NetworkService.this;
		}
	}
}