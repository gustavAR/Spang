package spang.mobile;

import java.util.List;

import network.Client;
import network.DCCause;
import network.IClient;
import network.Protocol;
import network.Connector;
import network.exceptions.InvalidEndpointException;
import network.exceptions.NetworkException;
import utils.Logger;
import utils.MessageBuffer;
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
	
	//Interval in wich to send buffered messages.
	private long MESSAGE_SEND_INTERVALL = 1000 / 10;
	
	//The client used to send messages over the network
	private IClient client;
	
	//Binder used by Activities to bind to this instance of NetworkService
	private final IBinder binder = new NetworkBinder();
	
	//Buffer that help buffer network messages.
	private MessageBuffer sendBuffer;
	
	//Thread used to send buffered messages.
	private Thread senderThread;
	
	//The default protocol to use when sending messages.
	private volatile Protocol defaultProtcol;
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate() {
		client = new Client(new Connector());
		this.defaultProtcol = Protocol.Unordered; 
				
		client.addDisconnectedListener(new EventHandler<IClient, DCCause>() {
			public void onAction(IClient sender, DCCause cause) {
				Logger.logInfo("Dced: " + cause);
				//Interrupts the sender-thread so it understands that we have disconnected.
				senderThread.interrupt();
		
				//If we timed out or crashed we reconnect.
				if(cause == DCCause.TCPTimeout || cause == DCCause.LocalNetworkCrash || cause == DCCause.RemoteNetworkCrash) {				
					sender.reconnect(5, 1000);	
					//TODO add exception handling if we cannot reconnect.
					startSendThread();
				} 
			}
		});		

		this.sendBuffer = new MessageBuffer(1024);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String host = intent.getStringExtra("HOST");
		int port = intent.getIntExtra("PORT", -1);

		this.client.connect(host, port);
		this.startSendThread();
		
		return START_STICKY;
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
		this.client.disconnect();

		super.onDestroy();
	}


	/**
	 * Class used to bind to the active network service.
	 * @author Lukas Kurtyan.
	 *
	 */
	public class NetworkBinder extends Binder {
		/**
		 * Retrives the active networkservice.
		 * @return the service to get.
		 */
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

	private void startSendThread() {
		senderThread = new Thread(new Runnable() {

			public void run() {
				try {
					while(true) {
						//Gets the messages since the last time messages were sent and sends them.						
						sendMessages();
						//We send messages in a given interval to lower network traffic.
						Thread.sleep(MESSAGE_SEND_INTERVALL);
					}
					
				} catch(NetworkException e) {
					return; //We cannot fix what ever caused the connection problem in this thread so we exit.
				} catch (InterruptedException e) {
					return;
				}
			}
		});
		
		senderThread.start();
	}
	
	private void sendMessages() {
		List<byte[]> toSend = sendBuffer.getNewMessages();
		for (byte[] bs : toSend) {
			client.send(bs, defaultProtcol);
		}				
	}

	/**
	 * Sets the default protocol that will be used when messages are sent.
	 * @param protocol the protocol.
	 * @throws NullPointerException if protocol is null.
	 */
	public void setDefaultMessageProtocol(Protocol protocol) throws NullPointerException{
		if(protocol == null)
			throw new NullPointerException("protocol");
			
		this.defaultProtcol = protocol;
	}
	
	/**
	 * Sends message over the network.
	 * @param message the message to send.
	 */
	public void send(byte[] message) {
		//Buffer the message.
		this.sendBuffer.addMessage(message);
	}
}