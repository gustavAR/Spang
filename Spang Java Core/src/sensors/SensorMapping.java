package sensors;

public enum SensorMapping {
	TOUCH (0), 
	TEXT(1),
	ACCELEROMETER(2),
	GYROSCOPE(3),
	LUMINANCE(4),
	MAGNETICFIELD(5),
	PROXIMITY(6),
	HUMIDITY(7),
	AIRPRESSURE(8),
	GRAVITY(9),
	ORIENTATION(10);
	
	private int code;
 
	private SensorMapping(int c) {
		code = c;
	}
 
	public int code() {
		return code;
	}
}
