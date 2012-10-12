package network.exceptions;

@SuppressWarnings("serial")
public class InvalidEndpointException extends NetworkException {

	public InvalidEndpointException() {
		super();
	}

	public InvalidEndpointException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidEndpointException(String message) {
		super(message);
	}

	public InvalidEndpointException(Throwable cause) {
		super(cause);
	}

}
