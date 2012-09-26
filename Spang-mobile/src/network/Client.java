package network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import events.Action;
import events.Action1;
import events.EventHandler;
import events.EventHandlerDelegate;

public class Client implements IClient {

	//The connection used to send and receive messages.
	private IConnection connection;
	//Worker used to receive udp messages asynchronously.
	private UdpWorker udpWorker;	
	//Worker used to receive tcp messages asynchronously.
	private TcpWorker tcpWorker;
	
	//Event handler that raises the connected event.
	private EventHandlerDelegate<IClient, Boolean> connectionEvent;
	
	//Event handler that raises the received event.
	private EventHandlerDelegate<IClient, byte[]> recivedEvent;
	
	//Event handler that raises the disconnected event.
	private EventHandlerDelegate<IClient, DisconnectionCause> disconnectedEvent;
	
	//Stores the connectionTimeout.
	private int connectionTimout;
	
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
		return this.connectionTimout;
	}


	/**
	 * {@inheritDoc}
	 */
	public void setConnectionTimeout(int value) {
		this.connectionTimout = value;
		if(this.connection != null)
			this.connection.setTimeout(value);
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect(InetSocketAddress address) {
		this.connect(address, false);
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
			throw new NetworkException("Host name could not be resolved. name =" + host);			
		}		
		
	}
	
	private void connect(InetSocketAddress address, boolean reconnecting) {
		if(this.connection != null)
			throw new IllegalArgumentException("We are already connected. Can't connect to a new connecting.");
		
		this.connection = Connection.connectTO(address);
		
		this.onConnected(reconnecting);
	}

	private void onConnected(boolean reconnecting) {
		this.connectionEvent.invokeActions(this, reconnecting);
		this.startReciving();
	}

	/**
	 * {@inheritDoc}
	 */
	public void reconnect(int retries) {
		if(this.connection == null) {
			throw new IllegalArgumentException("Never been connected to anything so cannot reconnect");
		} else if(this.connection.isConnected()) {
			return; //Nothing to do if we are already connected.
		} else {
			this.reconnectInternal(retries);
		}
	}

	private void reconnectInternal(int retries) {
		for (int i = 0; i < retries; i++) {
			try {
				System.out.println("Trying to reconnect to " + this.connection.getRemoteEndPoint());	
				this.connect(this.connection.getRemoteEndPoint(), true);
				
			} catch(NetworkException e) {
				System.out.println("Failed to reconnect " + i + " retrying...");
			}
			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void disconnect() {
		this.onDisconnect(DisconnectionCause.LocalShutdown);
	}

	
	private void onDisconnect(DisconnectionCause cause) {
		this.stopReciving();
		this.connection.close();
		
		this.disconnectedEvent.invokeActions(this, cause);
	}


	private void startReciving() {
		
		this.tcpWorker = new TcpWorker(this.connection);
		this.udpWorker = new UdpWorker(this.connection);
		
		this.tcpWorker.addRecivedAction(new Action1<byte[]>() {
			public void onAction(byte[] obj) {
				onRecived(obj);
			}
		});
		
		this.tcpWorker.addTimeoutAction(new Action() {			
			public void onAction() {
				onTimeout();
			}
		});
		
		new Thread(tcpWorker).start();
		new Thread(udpWorker).start();
	}

	private void stopReciving() {
		this.tcpWorker.StopWorking();
		this.udpWorker.StopWorking();
		
		this.tcpWorker = null;
		this.udpWorker = null;
	}
	
	private void onTimeout() {
		this.onDisconnect(DisconnectionCause.TCPTimeout);
	}

	private void onRecived(byte[] message) {
		if(isHeartbeat(message)) { 
			this.sendHeartBeatResponse();
		} else if(isSystemMessage(message)) {
			this.handleSystemMessage(message);		
		} else {
			this.recivedEvent.invokeActions(this, message);
		}	
	}
	
	
	private void sendHeartBeatResponse() {
		this.sendTCP(new byte[0]);
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
	public void sendUDP(byte[] toSend) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void sendTCP(byte[] toSend) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void addConnectedListener(EventHandler<IClient, Boolean> listener) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeConnectedListener(EventHandler<IClient, Boolean> listener) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void addDisconnectedListener(
			EventHandler<IClient, DisconnectionCause> listener) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeDisconnectedListener(
			EventHandler<IClient, DisconnectionCause> listener) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void addRevicedListener(EventHandler<IClient, byte[]> listener) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeRevicedListener(EventHandler<IClient, byte[]> listener) {
		// TODO Auto-generated method stub
		
	}	
}