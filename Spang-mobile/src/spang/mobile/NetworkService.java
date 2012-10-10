package spang.mobile;

import java.net.InetSocketAddress;
import java.util.List;

import network.Client;
import network.Connector;
import network.DCCause;
import network.IClient;
import network.Protocol;
import network.exceptions.NetworkException;
import utils.MessageBuffer;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;
import events.Action;
import events.Action1;
import events.Action1Delegate;
import events.ActionDelegate;
import events.EventHandler;

/**
 * This service enables the android application to 
 * connect, send messages and receive messages through the
 * network API. It acts as a client and can only connect to 
 * remote connections not host connections itself. 
 * 
 * The service is designed in a way that makes all callbacks 
 * and events happen on the UI thread. This makes it safe 
 * to change any UI elements in response to any event that 
 * can happen in the service.
 * 
 * IMPORTANT: The service must be started on the UI thread 
 * using Activity.startService(intent); if this is not done 
 * the service will fail to start.
 * 
 * @author Gustav Alm Rosenblad & Lukas Kurtyan
 */
public class NetworkService extends Service {
	
	//Default Interval in which to send buffered messages. (in milliseconds)
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
	private ActionDelegate connectedEvent;
	
	//Event invoked when the service disconnects form a remote location.
	private Action1Delegate<DCCause> disconnectedEvent;
	
	//Event invoked when the service receives data on the incoming connection.
	private Action1Delegate<byte[]> recivedEvent;
	
	//Handler used to offload work to the UI thread. This is done so that listeners
	//of the network can change UI without upsetting the android API.
	private Handler handler;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate() {
		if(Looper.myLooper() != Looper.getMainLooper()) 
			throw new IllegalStateException("The thread that starts the NetworkService must be the UI thread.");
		
		client = new Client(new Connector());
		this.defaultProtcol = Protocol.Unordered; 
		this.messageSendInterval = DEF_MESSAGE_SEND_INTERVALL;
		this.sendBuffer = new MessageBuffer(1024);
		this.connectedEvent = new ActionDelegate();
		this.disconnectedEvent = new Action1Delegate<DCCause>();
		this.recivedEvent = new Action1Delegate<byte[]>();		
		this.handler = new Handler();
		
		addListeners();
			
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Start sticky so that the service will not randomly be GCed.
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
		 * Retrieves the active networkservice.
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
					return; //We were interupted we should exit.
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
	 * Sets the default protocol that will be used when messages are sent,
	 * @see send(byte[])
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
				//Interrupts the sender-thread so it does not try to send messages over the bad connection.
				senderThread.interrupt();
		
				//If we timed out or crashed we reconnect.
				if(cause == DCCause.Timeout || cause == DCCause.LocalNetworkCrash || cause == DCCause.RemoteNetworkCrash) {		
					try {
						sender.reconnect(3, 1000);	
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
		//Make the event get invoked on the UI thread.
		this.handler.post(new Runnable() {
			
			public void run() {
				NetworkService.this.recivedEvent.invoke(eventArgs);	
			}
		});
	}

	private void onDisconnected(final DCCause eventArgs) {
		//Make the event get invoked on the UI thread.
		this.handler.post(new Runnable() {
			
			public void run() {
				NetworkService.this.disconnectedEvent.invoke(eventArgs);	
			}
		});
	}

	private void onConnected(final Boolean eventArgs) {
		//Make the event get invoked on the UI thread.
		this.handler.post(new Runnable() {
			public void run() {
				NetworkService.this.connectedEvent.invoke();	
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
	 * Gets the connection status of the service.
	 * @return connection status.
	 */
	public boolean isConnected() {
		return this.client.isConnected();
	}

	/**
	 * Gets the time a connection will wait for a message without disconnecting. 
	 * @return time in milliseconds.
	 */
	public int getConnectionTimeout() {
		return this.client.getConnectionTimeout();
	}
	
	/**
	 * Sets the time a connection will wait for a message without disconnecting.
	 * @param value the time in milliseconds. Note: (0 is wait indefinite)
	 */
	public void setConnectionTimeout(int value) {
		this.client.setConnectionTimeout(value);
	}

	
	private void connect(InetSocketAddress address) throws NetworkException {
		//Stops the current connection and the sender thread if we are already connected.
		if(this.client.isConnected()) {
			this.stopSenderThread();
			this.client.disconnect();
		}
		
		//Connects to the specified address.
		this.client.connect(address);
		//Start sending messages. 
		this.startSendThread();
	}
	
	
	
	/**
	 * This is a convenience method for connectAsync(InetSocketAddress, Action1(Boolean)
	 * @param host the host to connect to.
	 * @param port the port to connect to.
	 * @param callback the callback to be called when the connection is complete,
	 */
	public void connectAsync(final String host, final int port, final Action1<Boolean> callback) {
		InetSocketAddress address = new InetSocketAddress(host, port);			
		this.connectAsync(address, callback);
	}
	
	/**
	 * Connects to a new connection specified by the socketAddress.
	 * This is done asynchronously so the caller can't make any 
	 * assumptions about the state of the connection before the callback is called.
	 * The callback will always be invoked on the UI thread so it is safe to modify
	 * any UI elements from within the callback. 
	 * 
	 * @param host the host to connect to.
	 * @param port the port to connect to.
	 * @param callback the callback to be invoked when the connection is complete. The value sent
	 * to the callback is the success status of the connection attempt. true = success, false = failure
	 */
	public void connectAsync(final InetSocketAddress socketAddress, final Action1<Boolean> callback) {
		new Thread(new Runnable() {
			volatile boolean success;					
			public void run() {
				try {
					NetworkService.this.connect(socketAddress);
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
	 * Reconnects to the last open connection.
	 * This is done asynchronously so the caller can't make any 
	 * assumptions about the state of the connection before the callback is called.
	 * The callback will always be invoked on the UI thread so it is safe to modify
	 * any UI elements from within the callback. 
	 * 
	 * @param retries number of reconnection attempts.
	 * @param timeout timeout for each reconnection attempt. (in miliseconds)
	 * @param callback the callback to be invoked when the reconnection is complete. The value sent
	 * to the callback is the success status of the reconnection attempt. true = success, false = failure
	 */
	public void reconnectAsync(final int retries, final int timeout, final Action1<Boolean> callback) {
		new Thread(new Runnable() {
			volatile boolean success;					
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
	 * Disconnects the NetworkService from it's current connection.
	 * After this is done messages will no longer be sent over the network.
	 */
	public void disconnect() {
		this.client.disconnect();
	}
	
	/**
	 * Add a listener to start listening for received events.
	 * The connection event is called when a connection is made.
	 * @param listener the listener to add.
	 */
	public void addConnectedListener(Action listener) {
		this.connectedEvent.addListener(listener);
	}

	/**
	 * Removes a listener listening for disconnection events.
	 * @param listener the listener to remove.
	 */
	public void removeConnectedListener(Action listener) {
		this.connectedEvent.removeListener(listener);
	}

	/**
	 * Add a listener to start listening for received events.
	 * The disconnection event is triggered when the connection is closed or if it fails.
	 * The cause of the disconnection is sent to the listener as a DCCause.
	 * @param listener the listener to add.
	 */
	public void addDisconnectedListener(Action1<DCCause> listener) {
		this.disconnectedEvent.addListener(listener);
	}

	/**
	 * Removes a listener listening for disconnection events.
	 * @param listener the listener to remove.
	 */
	public void removeDisconnectedListener(Action1<DCCause> listener) {
		this.disconnectedEvent.removeListener(listener);
	}

	/**
	 * Add a listener to start listening for received events.
	 * The received event is triggered when a message is received from the network.
	 * The data received from onAction is the message recived.
	 * @param listener the listener to add.
	 */
	public void addRevicedListener(Action1<byte[]> listener) {
		this.recivedEvent.addListener(listener);
	}

	/**
	 * Removes a listener listening for received events.
	 * @param listener the listener to remove.
	 */
	public void removeRevicedListener(Action1<byte[]> listener) {
		this.recivedEvent.removeListener(listener);
	}
}