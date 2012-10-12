package network.messages;


public class SensorEvent implements IPhoneMessage{
	public final int SensorID;
	public final float[] SensorData;
	
    public SensorEvent(int id, float[] data)
    {
    	this.SensorID = id;
        this.SensorData = data;
    }
}
