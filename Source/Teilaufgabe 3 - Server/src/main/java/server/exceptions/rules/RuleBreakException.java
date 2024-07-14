package server.exceptions.rules;

import server.exceptions.GenericExampleException;

public class RuleBreakException extends GenericExampleException {

	private static final long serialVersionUID = 1L;

	public RuleBreakException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

}
