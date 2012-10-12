package network;

import network.exceptions.NetworkException;
import network.exceptions.RemoteCrashException;
import network.exceptions.RemoteShutdownException;
import network.exceptions.TimeoutException;
import utils.Logger;
import events.Action1;
import events.Action1Delegate;

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
	private Action1Delegate<DCCause> readFailedEvent;
		
	/**
	 * Creates a new UdpWorker.
	 * @param connection the connection used.
	 */
	public UdpWorker(IConnection connection) {
		this.connection = connection;
		this.recivedEvent = new Action1Delegate<byte[]>();
		this.readFailedEvent = new Action1Delegate<DCCause>();
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
	public void addReciveFailedListener(Action1<DCCause> action) {
		this.readFailedEvent.addListener(action);
	}	
	
	/**
	 * Removes a listener so that it will no longer be notified when a message is received.
	 * @param action the listener to remove.
	 */
	public void removeTimeoutAction(Action1<DCCause> action) {
		this.readFailedEvent.removeListener(action);
	}
	
		
	@Override
	protected void DoWork() {
		try {
			byte[] bytes = this.connection.recive();
			this.recivedEvent.invoke(bytes);				
		} catch(TimeoutException exe) {
			Logger.logInfo("Connection timed out.");
			this.StopWorking();			
			
			this.readFailedEvent.invoke(DCCause.Timeout);
		} catch(RemoteCrashException exe) {
			Logger.logInfo("Remote connection crashed.");
			this.StopWorking();
			
			this.readFailedEvent.invoke(DCCause.RemoteNetworkCrash);
		} catch(RemoteShutdownException exe) {
			Logger.logInfo("Remote endpoint shutdown.");
			this.StopWorking();		
			
			this.readFailedEvent.invoke(DCCause.EndpointShutdown);
		} catch(NetworkException exe) {
			Logger.logException(exe);		
			
			if(!this.stopWorking)
				this.readFailedEvent.invoke(DCCause.LocalNetworkCrash);
			
			this.StopWorking();
		}
	}

	/**
	 * Clears the events. Removing any listener that listens to received or timeout events.
	 */
	public void clearEventListeners() {
		this.recivedEvent.clear();
		this.readFailedEvent.clear();
	}
}