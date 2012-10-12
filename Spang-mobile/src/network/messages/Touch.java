package network.messages;

public class Touch {
	
	public final float X;
	public final float Y;
	public final float Pressure;

	public Touch(float x, float y, float pressure) {
		this.X = x;
		this.Y = y;
		this.Pressure = pressure;
	}
}
