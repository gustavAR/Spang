package spang.mobile;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import network.Client;
import network.Connector;
import network.DCCause;
import network.IClient;
import network.Protocol;
import network.exceptions.NetworkException;
import utils.Logger;
import utils.MessageBuffer;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import events.Action1;
import events.EventHandler;
import events.EventHandlerDelegate;

/**
 * This is a service which provides applications with the 
 * ability to connect and send messages over the network.
 * @author Gustav Alm Rosenblad & Lukas Kurtyan
 */
public class NetworkService extends Service implements IClient {
	
	//Default Interval in which to send buffered messages.
	private int DEF_MESSAGE_SEND_INTERVALL = 1000 / 20;
	
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
	
	//Interval in which to send buffered messages.
	private volatile int messageSendInterval;
	
	//Event invoked when the service connects to a remote location.
	private EventHandlerDelegate<IClient, Boolean> connectedEvent;
	
	//Event invoked when the service disconnects form a remote location.
	private EventHandlerDelegate<IClient, DCCause> disconnectedEvent;
	
	//Event invoked when the service receives data on the incoming connection.
	private EventHandlerDelegate<IClient, byte[]> recivedEvent;
	
	private Handler handler;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate() {
		client = new Client(new Connector());
		this.defaultProtcol = Protocol.Unordered; 
		this.messageSendInterval = DEF_MESSAGE_SEND_INTERVALL;
		this.sendBuffer = new MessageBuffer(1024);
		this.connectedEvent = new EventHandlerDelegate<IClient, Boolean>();
		this.disconnectedEvent = new EventHandlerDelegate<IClient, DCCause>();
		this.recivedEvent = new EventHandlerDelegate<IClient, byte[]>();		
		this.handler = new Handler();
		
		addListeners();
			
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
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
		this.disconnect();

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

	private void stopSenderThread() {
		if(this.senderThread != null)
			this.senderThread.interrupt();
	}

	private void startSendThread() {
		senderThread = new Thread(new Runnable() {

			public void run() {
				try {
					while(true) {
						//Gets the messages since the last time messages were sent and sends them.						
						sendMessages();
						//We send messages in a given interval to lower network traffic.
						Thread.sleep(NetworkService.this.messageSendInterval);
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
		//Sends all the buffered messages using the protocol they were buffered with.
		for (Protocol protocol : Protocol.values()) {
			List<byte[]> toSend = sendBuffer.getNewMessages(protocol);
			for (byte[] bs : toSend) {
				client.send(bs, protocol);
			}	
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
	 * Sets the interval between messages.
	 * This value is in milliseconds.
	 * @param milisec the message intervals.
	 */
	public void setSendInterval(int milisec) {
		this.messageSendInterval = milisec;
	}
	
	/**
	 * Gets the interval between messages.
	 * This value is in milliseconds.
	 * @return  interval between messages.
	 */
	public int getSendInterval() {
		return this.messageSendInterval;
	}
	
	private void addListeners() {
		client.addDisconnectedListener(new EventHandler<IClient, DCCause>() {
			public void onAction(IClient sender, DCCause cause) {
				Logger.logInfo("Dced: " + cause);
				//Interrupts the sender-thread so it understands that we have disconnected.
				senderThread.interrupt();
		
				//If we timed out or crashed we reconnect.
				if(cause == DCCause.Timeout || cause == DCCause.LocalNetworkCrash || cause == DCCause.RemoteNetworkCrash) {		
					try {
						sender.reconnect(5, 1000);	
						startSendThread();
					} catch(NetworkException e) {
						//If we could not reconnect we notify any listeners about this.
						NetworkService.this.onDisconnected(cause);
					}
				} 
			}
		});		
		
		client.addConnectedListener(new EventHandler<IClient, Boolean>() {
			public void onAction(IClient sender, Boolean eventArgs) {
				NetworkService.this.onConnected(eventArgs);
			}
		});
		client.addRevicedListener(new EventHandler<IClient, byte[]>() {
			
			public void onAction(IClient sender, byte[] eventArgs) {
				NetworkService.this.onRecived(eventArgs);
			}
		});
	}
	
	private void onRecived(final byte[] eventArgs) {
		this.handler.post(new Runnable() {
			
			public void run() {
				NetworkService.this.recivedEvent.invoke(NetworkService.this, eventArgs);	
			}
		});
	}

	private void onDisconnected(final DCCause eventArgs) {
		this.handler.post(new Runnable() {
			
			public void run() {
				NetworkService.this.disconnectedEvent.invoke(NetworkService.this, eventArgs);	
			}
		});
	}

	private void onConnected(final Boolean eventArgs) {
		this.handler.post(new Runnable() {
			
			public void run() {
				NetworkService.this.connectedEvent.invoke(NetworkService.this, eventArgs);	
			}
		});		
	}

	/**
	 * Sends message over the network.The message is buffered and sent
	 * at appropriate intervals.Set the interval through setSendInterval
	 * to change the interval.
	 * @param message the message to send.
	 */
	public void send(byte[] message) {
		//Buffer the message.
		this.sendBuffer.addMessage(message, this.defaultProtcol);
	}
	
	/**
	 * Sends message over the network. The message is buffered and sent
	 * at a fixed interval. Set the interval through setSendInterval
	 * to change the interval.
	 * @param message the message to send.
	 * @param protocol the protocol to be used when sending.
	 */
	public void send(byte[] message, Protocol protocol) {
		//Buffer the message.
		this.sendBuffer.addMessage(message, protocol);
	}
	
	/**
	 * Sends the message directly and does not buffer it.
	 * @param message the message to send.
	 * @param protocol the protocol used when sending.
	 */
	public void sendDirect(byte[] message, Protocol protocol) {
		try {		
			this.client.send(message, protocol);		
		} catch(Exception e) {
			//The connection is not valid. but there is nothing we can do about it here
			//so we silently ignore the exception.
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isConnected() {
		return this.client.isConnected();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getConnectionTimeout() {
		return this.client.getConnectionTimeout();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setConnectionTimeout(int value) {
		this.client.setConnectionTimeout(value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect(InetSocketAddress address) {
		this.connect(address.getHostName(), address.getPort());
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect(InetAddress address, int port) throws NetworkException {
		this.connect(address.getHostAddress(), port);
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect(String host, int port) throws NetworkException {
		if(this.client.isConnected()) {
			this.stopSenderThread();
			this.client.disconnect();
		}
		
		this.client.connect(host, port);
		this.startSendThread();
	}
	
	public void connectAsync(final String host, final int port, final Action1<Boolean> callback) {
		new Thread(new Runnable() {
			boolean success;					
			public void run() {
				try {
					NetworkService.this.connect(host, port);
					success = true;
				} catch(NetworkException exe) {
					success = false;
				} finally {
					NetworkService.this.handler.post(new Runnable() {
						public void run() {
							callback.onAction(success);
						}
					});			
				}
			}
		}).start();
	}
	
	
	public void reconnectAsync(final int retries, final int timeout, final Action1<Boolean> callback) {
		new Thread(new Runnable() {
			boolean success;					
			public void run() {
				try {
					NetworkService.this.client.reconnect(retries, timeout);
					success = true;
				} catch(NetworkException exe) {
					success = false;
				} finally {
					NetworkService.this.handler.post(new Runnable() {
						public void run() {
							callback.onAction(success);
						}
					});			
				}
			}
		}).start();	
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void reconnect(int retries, int timeout) throws NetworkException {
		this.client.reconnect(retries, timeout);
	}

	/**
	 * {@inheritDoc}
	 */
	public void disconnect() {
		this.client.disconnect();
	}

	/**
	 * {@inheritDoc}
	 */
	public void addConnectedListener(EventHandler<IClient, Boolean> listener) {
		this.connectedEvent.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeConnectedListener(EventHandler<IClient, Boolean> listener) {
		this.connectedEvent.removeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addDisconnectedListener(EventHandler<IClient, DCCause> listener) {
		this.disconnectedEvent.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeDisconnectedListener(
			EventHandler<IClient, DCCause> listener) {
		this.disconnectedEvent.removeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addRevicedListener(EventHandler<IClient, byte[]> listener) {
		this.recivedEvent.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeRevicedListener(EventHandler<IClient, byte[]> listener) {
		this.recivedEvent.removeListener(listener);
	}
}