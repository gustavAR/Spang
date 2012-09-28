package network;

import network.exceptions.NetworkException;
import utils.Logger;
import events.Action1;
import events.Action1Delegate;

/**
 * Worker class that listens to incoming udp messages on a connection.
 * 
 * see ContinuousWorker
 * @author Lukas Kurtyan
 */
public class UdpWorker extends ContinuousWorker{
	
	//The connection used.
	private final IConnection connection;

	//Event raised when a message is recived.
	private Action1Delegate<byte[]> recivedEvent;
	
	/**
	 * Creates a new UdpWorker.
	 * @param connection the connection used.
	 */
	public UdpWorker(IConnection connection) {
		this.connection = connection;
		
		this.recivedEvent = new Action1Delegate<byte[]>();
	}
	
	/**
	 * Adds a listener that will be notified when a new message was received.
	 * @param action the listener to add.
	 */
	public void addRecivedAction(Action1<byte[]> action) {
		this.recivedEvent.addListener(action);		
	}
	
	/**
	 * Removes a listener so that it will no longer be notified when a message is received.
	 * @param action the listener to remove.
	 */
	public void removeRecivedAction(Action1<byte[]> action) {
		this.recivedEvent.removeListener(action);
	}	
		
	@Override
	protected void DoWork() {
		try {
			byte[] bytes = this.connection.reciveTCP();
			this.recivedEvent.invoke(bytes);				
		} catch(NetworkException exe) {
			Logger.logException(exe);			
			this.StopWorking();
		}
	}

	public void clearEventListeners() {
		this.recivedEvent.clear();
	}	
}