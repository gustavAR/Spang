package network;

import events.Action1;
import events.Action1Delegate;

public class UdpWorker extends ContinuousWorker{
private final IConnection connection;
	
	private Action1Delegate<byte[]> recivedEvent;
	
	public UdpWorker(IConnection connection) {
		this.connection = connection;
		
		this.recivedEvent = new Action1Delegate<byte[]>();
	}
	
	public void addRecivedAction(Action1<byte[]> action) {
		this.recivedEvent.addAction(action);		
	}
	
	public void removeRecivedAction(Action1<byte[]> action) {
		this.recivedEvent.removeAction(action);
	}	
		
	@Override
	protected void DoWork() {
		try {
			byte[] bytes = this.connection.reciveTCP();
			this.recivedEvent.invokeActions(bytes);			
		} catch(NetworkException exe) {
			
			//TODO remove tmp syso.
			System.out.println("Udp read failed.");
			System.out.println(exe.getMessage());
			this.StopWorking();
		}
	}	
}