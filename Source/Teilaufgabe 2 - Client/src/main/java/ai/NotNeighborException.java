package ai;

//unchecked Exception
public class NotNeighborException extends RuntimeException {

	public NotNeighborException() {
		super();
	}

	public NotNeighborException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	public NotNeighborException(String message, Throwable cause) {
		super(message, cause);

	}

	public NotNeighborException(String message) {
		super(message);

	}

	public NotNeighborException(Throwable cause) {
		super(cause);

	}

}
