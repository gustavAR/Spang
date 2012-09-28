package network;

import events.Action;
import events.Action1;
import events.Action1Delegate;
import events.ActionDelegate;

/**
 * Worker class that listens to incomming tcp messages on a connection.
 * NOTE: This class is designed to be excecuted by a background worker thread
 * since the run method uses blocking IO.
 * 
 *  Correct Creation and usage is:
 *  {@code 
 *  	TcpWorker worker = new TcpWorker();
 *  	Thread thread = new Thread(worker);
 *  	thread.start();
 *  }
 *  
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
	 * Adds a listener that will be notified when a new message was recived.
	 * @param action the listener to add.
	 */
	public void addRecivedAction(Action1<byte[]> action) {
		this.recivedEvent.addAction(action);		
	}

	/**
	 * Removes a listener so that it will no longer be notified when a message is recived.
	 * @param action the listener to remove.
	 */
	public void removeRecivedAction(Action1<byte[]> action) {
		this.recivedEvent.removeAction(action);
	}
	
	/**
	 * Adds a listener that will be notified when a timeout occurs.
	 * @param action the listener to add.
	 */
	public void addTimeoutAction(Action action) {
		this.timeoutEvent.addAction(action);
	}
	
	/**
	 * Removes a listener so that it will no longer be notified when a message is recived.
	 * @param action the listener to remove.
	 */
	public void removeTimeoutAction(Action action) {
		this.timeoutEvent.addAction(action);
	}
		
	@Override
	protected void DoWork() {
		try {
			byte[] bytes = this.connection.reciveTCP();
			this.recivedEvent.invokeActions(bytes);			
		} catch(NetworkException exe) {
			if(this.stopWorking)
				return;
			
			//TODO remove tmp syso.
			System.out.println("Tcp recive timed out.");
			System.out.println(exe.getMessage());
			
			this.StopWorking();
			this.timeoutEvent.invokeActions();
		}
	}
}