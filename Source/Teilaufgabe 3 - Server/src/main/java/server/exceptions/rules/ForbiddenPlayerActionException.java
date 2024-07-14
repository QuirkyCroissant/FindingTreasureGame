package server.exceptions.rules;

public class ForbiddenPlayerActionException extends RuleBreakException {

	private static final long serialVersionUID = 1L;

	public ForbiddenPlayerActionException(String playerID) {
		super("ForbiddenPlayerActionException", "Player \"" + playerID
				+ "\" sent a command to the server, despite not being permitted to ACT at the moment!");
	}

}
