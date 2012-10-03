package network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import network.exceptions.InvalidEndpointException;
import network.exceptions.NetworkException;
import utils.Logger;
import events.Action;
import events.Action1;
import events.EventHandler;
import events.EventHandlerDelegate;

public class Client implements IClient {

	//Default time it takes for the connection to time out.
	private static final int DEF_TIMEOUT = 5000;
	
	//Last connection connected to or null if we never connected.
	private InetSocketAddress lastConnectedAddress;
	
	//Connector used to connect with.
	private final IConnector connector;
	
	//The connection used to send and receive messages.
	private IConnection connection;
	
	//Worker used to receive udp messages asynchronously.
	private UdpWorker udpWorker;	
	
	//Event handler that raises the connected event.
	private EventHandlerDelegate<IClient, Boolean> connectionEvent;
	
	//Event handler that raises the received event.
	private EventHandlerDelegate<IClient, byte[]> recivedEvent;
	
	//Event handler that raises the disconnected event.
	private EventHandlerDelegate<IClient, DCCause> disconnectedEvent;
	
	//Stores the connectionTimeout.
	private int connectionTimeout;
	
	
	/**
	 * Creates a new client.
	 */
	public Client(IConnector connector) {
		this.connector = connector;
		this.connectionEvent = new EventHandlerDelegate<IClient, Boolean>();
		this.recivedEvent = new EventHandlerDelegate<IClient, byte[]>();
		this.disconnectedEvent = new EventHandlerDelegate<IClient, DCCause>();
		this.connectionTimeout = DEF_TIMEOUT;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isConnected() {
		return this.connection != null &&
			   this.connection.isConnected();
	}


	/**
	 * {@inheritDoc}
	 */
	public int getConnectionTimeout() {
		return this.connectionTimeout;
	}


	/**
	 * {@inheritDoc}
	 */
	public void setConnectionTimeout(int value) {
		this.connectionTimeout = value;
		if(this.connection != null)
			this.connection.setTimeout(value);
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect(InetSocketAddress address) {
		this.connect(address, this.connectionTimeout, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect(InetAddress address, int port) {
		this.connect(new InetSocketAddress(address, port));
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect(String host, int port) {
		try {
			InetAddress address = InetAddress.getByName(host);
			this.connect(address, port);
		} catch (UnknownHostException e) {
			String message = "Host name invalid name =" + host;
			if(port < 0 || port > 0xFFFF) {
				message += " Port invalid port=" + port;
			}
			throw new InvalidEndpointException(message);			
		}				
	}
	
	private void connect(InetSocketAddress address, int timeout, boolean reconnecting) {
		//If we are connected it makes no since to connect again.
		if(this.connection != null)
			throw new NetworkException("We are already connected. Can't connect to a new connecting.");
		
		this.connection = this.connector.connect(address, timeout);
		this.lastConnectedAddress = address;
		this.onConnected(reconnecting);
	}

	private void onConnected(boolean reconnecting) {
		this.connection.setTimeout(this.connectionTimeout);				
		this.connectionEvent.invoke(this, reconnecting);	
		
		//Starts a receiver thread to start receiving new messages.
		this.startReciving();
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void reconnect(int retries, int timeout) {
		if(this.lastConnectedAddress == null) {
			throw new IllegalArgumentException("Never been connected to anything so cannot reconnect");
		} else {
			this.reconnectInternal(retries, timeout, this.lastConnectedAddress);
		}
	}

	private void reconnectInternal(int retries, int timeout, InetSocketAddress endpoint) {
		for (int i = 0; i < retries; i++) {
			try {
				System.out.println("Trying to reconnect to " + endpoint);	
				this.connect(endpoint, timeout, true);
				System.out.println("Success!");	
				return;
				
			} catch(NetworkException e) {
				System.out.println("Failed to reconnect " + i + " retrying...");
			}
			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void disconnect() {
		this.onDisconnect(DCCause.LocalShutdown);
	}

	
	private void onDisconnect(DCCause cause) {
		this.stopReciving();
		this.connection.close();
		this.connection = null;
		
		this.disconnectedEvent.invoke(this, cause);
	}


	private void startReciving() {
		this.udpWorker = new UdpWorker(this.connection);
		
		this.udpWorker.addRecivedAction(new Action1<byte[]>() {
			
			public void onAction(byte[] obj) {
				onRecived(obj);
			}
		});
		
		this.udpWorker.addTimeoutAction(new Action() {			
			public void onAction() {
				onTimeout();
			}
		});
		
		this.udpWorker.addReadCrashAction(new Action() {
			
			public void onAction() {
				onReadCrash();
			}
		});
		
		new Thread(udpWorker).start();
	}

	private void stopReciving() {
		if(this.udpWorker == null)
			return;
		
		this.udpWorker.StopWorking();
		this.udpWorker.clearEventListeners();
		
		this.udpWorker = null;
	}
	
	private void onTimeout() {
		this.onDisconnect(DCCause.TCPTimeout);
	}
	
	protected void onReadCrash() {
		this.onDisconnect(DCCause.LocalNetworkCrash);
	}


	private void onRecived(byte[] message) {
		if(isHeartbeat(message)) { 
			this.sendHeartBeatResponse();
		} else if(isSystemMessage(message)) {
			this.handleSystemMessage(message);		
		} else {
			this.recivedEvent.invoke(this, message);
		}	
	}
	
	
	private void sendHeartBeatResponse() {
		this.send(new byte[0]);
	}

	private boolean isHeartbeat(byte[] message) {
		return message.length == 0;
	}
	
	private boolean isSystemMessage(byte[] message) {
		return message[0] == 0;
	}
	
	private void handleSystemMessage(byte[] message) {		
		//TODO implement this.		
		System.out.println("Just recived a system message.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void send(byte[] toSend) {
		try {
			this.connection.send(toSend);	
		} catch(NetworkException e) {
			Logger.logException(e);		
			this.onDisconnect(DCCause.LocalNetworkCrash);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void send(byte[] toSend, Protocol protocol) {
		try {
			this.connection.send(toSend, protocol);
		} catch(NetworkException e) {
			Logger.logException(e);
			this.onDisconnect(DCCause.LocalNetworkCrash);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addConnectedListener(EventHandler<IClient, Boolean> listener) {
		this.connectionEvent.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeConnectedListener(EventHandler<IClient, Boolean> listener) {
		this.connectionEvent.removeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addDisconnectedListener(
			EventHandler<IClient, DCCause> listener) {
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

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		this.connection.close();
	}	
}