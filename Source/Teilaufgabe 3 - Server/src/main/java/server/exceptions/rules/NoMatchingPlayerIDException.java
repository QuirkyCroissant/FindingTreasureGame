package server.exceptions.rules;

import server.exceptions.GenericExampleException;

public class NoMatchingPlayerIDException extends GenericExampleException {

	private static final long serialVersionUID = 1L;

	public NoMatchingPlayerIDException(String gameID, String playerID) {
		super("NoMatchingPlayerIDException",
				"Submitted Player(\"" + playerID + "\") is not registered in provided Game (\"" + gameID + "\")!");
	}

}
