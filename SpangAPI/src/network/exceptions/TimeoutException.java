package network.exceptions;

/**
 * An exception raised when a connection times out.
 * @author Lukas Kurtyan
 *
 */
@SuppressWarnings("serial")
public class TimeoutException extends NetworkException{

	public TimeoutException() {
		super();
	}

	public TimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public TimeoutException(String message) {
		super(message);
	}

	public TimeoutException(Throwable cause) {
		super(cause);
	}
}
