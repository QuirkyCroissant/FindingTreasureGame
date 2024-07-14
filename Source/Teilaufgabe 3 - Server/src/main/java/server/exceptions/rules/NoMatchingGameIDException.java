package server.exceptions.rules;

import server.exceptions.GenericExampleException;

public class NoMatchingGameIDException extends GenericExampleException {

	private static final long serialVersionUID = 1L;

	public NoMatchingGameIDException(String gameID) {
		super("NoMatchingGameIDException", "GameID(\"" + gameID + "\") that was provided does not exist!");
	}

}
