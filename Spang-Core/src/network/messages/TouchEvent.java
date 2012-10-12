package network.messages;


public class TouchEvent implements IPhoneMessage {
	
	public final Touch[] data;
	public TouchEvent(Touch[] data) {
		this.data = data;		
	}
}
