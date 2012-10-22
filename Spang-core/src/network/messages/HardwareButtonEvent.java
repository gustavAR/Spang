package network.messages;

public class HardwareButtonEvent {
	public final static int VOLUME_UP = 0x01;
	public final static int VOLUME_DOWN = 0x02;
	public final static int SEARCH = 0x04;
	
	public final int ID;
	
	public HardwareButtonEvent(int id) {
		this.ID = id;
	}
	
	public boolean equals(Object object) {
		if(object instanceof HardwareButtonEvent){
			return ((HardwareButtonEvent)object).ID == this.ID;
		}
		return false;
	}
}