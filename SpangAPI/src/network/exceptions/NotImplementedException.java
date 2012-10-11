package network.exceptions;

/**
 * Exception that should be used on methods that are not implemented. 
 * This should be done instead of return default values. Such as null
 * since that can cause errors in the wrong places.
 * @author Lukas
 */
@SuppressWarnings("serial")
public class NotImplementedException extends RuntimeException {

	public NotImplementedException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NotImplementedException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	public NotImplementedException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public NotImplementedException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}


}
