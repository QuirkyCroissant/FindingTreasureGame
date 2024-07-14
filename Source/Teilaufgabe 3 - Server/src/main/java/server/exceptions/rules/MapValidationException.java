package server.exceptions.rules;

public class MapValidationException extends RuleBreakException {

	private static final long serialVersionUID = 1L;

	public MapValidationException(String errorMessage) {
		super("MapValidationException", errorMessage);
	}

}
