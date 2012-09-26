package network;

import events.Action;
import events.Action1;
import events.Action1Delegate;
import events.ActionDelegate;

public class TcpWorker extends ContinuousWorker {
	
	private final IConnection connection;
	
	private Action1Delegate<byte[]> recivedEvent;
	
	private ActionDelegate timeoutEvent;
	
	public TcpWorker(IConnection connection) {
		this.connection = connection;
		
		this.recivedEvent = new Action1Delegate<byte[]>();
		this.timeoutEvent = new ActionDelegate();
	}
	
	public void addRecivedAction(Action1<byte[]> action) {
		this.recivedEvent.addAction(action);		
	}
	
	public void removeRecivedAction(Action1<byte[]> action) {
		this.recivedEvent.removeAction(action);
	}
	
	public void addTimeoutAction(Action action) {
		this.timeoutEvent.addAction(action);
	}
	
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