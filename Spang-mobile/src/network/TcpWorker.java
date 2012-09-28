package network;

import utils.Logger;
import network.exceptions.NetworkException;
import network.exceptions.TimeoutException;
import events.Action;
import events.Action1;
import events.Action1Delegate;
import events.ActionDelegate;

/**
 * Worker class that listens to incoming tcp messages on a connection.
 * 
 * @see ContinuousWorker
 * @author Lukas Kurtyan
 */
public class TcpWorker extends ContinuousWorker {
	
	//Connection used.
	private final IConnection connection;
	
	//Event raised when a message is recived.
	private Action1Delegate<byte[]> recivedEvent;
	
	//Event raised when the connection times out.
	private ActionDelegate timeoutEvent;
	
	/**
	 * Creates a new tcp worker.
	 * @param connection the connection used.
	 */
	public TcpWorker(IConnection connection) {
		this.connection = connection;
		
		this.recivedEvent = new Action1Delegate<byte[]>();
		this.timeoutEvent = new ActionDelegate();
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
	
	/**
	 * Adds a listener that will be notified when a timeout occurs.
	 * @param action the listener to add.
	 */
	public void addTimeoutAction(Action action) {
		this.timeoutEvent.addListener(action);
	}
	
	/**
	 * Removes a listener so that it will no longer be notified when a timeout occurs.
	 * @param action the listener to remove.
	 */
	public void removeTimeoutAction(Action action) {
		this.timeoutEvent.addListener(action);
	}
		
	@Override
	protected void DoWork() {
		try {
			//Receives a message and raises an event notifying the listeners.						
			byte[] bytes = this.connection.reciveTCP();
			this.recivedEvent.invoke(bytes);		
		} catch(TimeoutException te) {
			//On a timeout the network connection was lost so we stop working.
			this.StopWorking();
			this.timeoutEvent.invoke();			
		} catch(NetworkException ne) {
			//If the system failed to read for a reason that was not timeout
			//the connection is no longer valid so we stop reading from it.
			Logger.logException(ne);
			this.StopWorking();
		}
	}

	public void clearEventListeners() {
		this.timeoutEvent.clear();
		this.recivedEvent.clear();
	}
}