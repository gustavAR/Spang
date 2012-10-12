package network.exceptions;

@SuppressWarnings("serial")
public class NetworkException extends RuntimeException {

	public NetworkException() {
		super();
	}

	public NetworkException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetworkException(String message) {
		super(message);
	}

	public NetworkException(Throwable cause) {
		super(cause);
	}

}
