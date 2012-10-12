package utils;

@SuppressWarnings("serial")
public class MissingSensorException extends RuntimeException {
	public MissingSensorException() {
		super();
	}

	public MissingSensorException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingSensorException(String message) {
		super(message);
	}

	public MissingSensorException(Throwable cause) {
		super(cause);
	}
}
