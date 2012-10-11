package sensors;

@SuppressWarnings("serial")
public class NoSensorException extends RuntimeException {
	public NoSensorException(String arg) {
		super(arg);
	}
}
