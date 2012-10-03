package network;

import network.exceptions.NetworkException;
import network.exceptions.TimeoutException;
import utils.Logger;
import events.Action;
import events.Action1;
import events.Action1Delegate;
import events.ActionDelegate;

/**
 * Worker class that listens to incoming udp messages on a connection.
 * 
 * see ContinuousWorker
 * @author Lukas Kurtyan
 */
public class UdpWorker extends AsyncWorker{
	
	//The connection used.
	private final IConnection connection;

	//Event raised when a message is recived.
	private Action1Delegate<byte[]> recivedEvent;
	
	//Event raised when the connection timesout.
	private ActionDelegate timeoutEvent;
	
	private ActionDelegate readCrash;
	
	/**
	 * Creates a new UdpWorker.
	 * @param connection the connection used.
	 */
	public UdpWorker(IConnection connection) {
		this.connection = connection;
		this.timeoutEvent = new ActionDelegate();
		this.readCrash = new ActionDelegate();
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
	

	/**
	 * Adds a listener that will be notified when the connection times out.
	 * @param action the listener to add.
	 */
	public void addTimeoutAction(Action action) {
		this.timeoutEvent.addListener(action);
	}	
	
	/**
	 * Removes a listener so that it will no longer be notified when a message is received.
	 * @param action the listener to remove.
	 */
	public void removeTimeoutAction(Action action) {
		this.timeoutEvent.removeListener(action);
	}
	
	/**
	 * Adds a listener that will be notified when a connection crashes for a reason that is not timeout.
	 * @param action the listener to add.
	 */
	public void addReadCrashAction(Action action) {
		this.timeoutEvent.addListener(action);
	}	
	
	/**
	 * Removes a listener so that it will no longer be notified when a  connection crashes for a reason that is not timeout.
	 * @param action the listener to remove.
	 */
	public void removeReadCrashAction(Action action) {
		this.timeoutEvent.removeListener(action);
	}
		
	@Override
	protected void DoWork() {
		try {
			byte[] bytes = this.connection.recive();
			this.recivedEvent.invoke(bytes);				
		} catch(TimeoutException exe) {
			Logger.logInfo("Connection timed out!");
			this.StopWorking();
			this.timeoutEvent.invoke();			
		} catch(NetworkException exe) {
			Logger.logException(exe);			
			this.StopWorking();
			this.readCrash.invoke();
		}
	}

	/**
	 * Clears the events. Removing any listener that listens to received or timeout events.
	 */
	public void clearEventListeners() {
		this.recivedEvent.clear();
		this.timeoutEvent.clear();
	}
}