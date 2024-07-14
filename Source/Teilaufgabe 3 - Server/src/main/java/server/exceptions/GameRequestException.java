package server.exceptions;

public class GameRequestException extends GenericExampleException {

	private static final long serialVersionUID = 1L;

	public GameRequestException(String errorName, String errorMessage) {
		super(errorName, errorMessage);

	}

}
