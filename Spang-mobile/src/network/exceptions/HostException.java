package network.exceptions;

@SuppressWarnings("serial")
public class HostException extends NetworkException {

	public HostException() {
		super();
	}

	public HostException(String message, Throwable cause) {
		super(message, cause);
	}

	public HostException(String message) {
		super(message);
	}

	public HostException(Throwable cause) {
		super(cause);
	}

}
