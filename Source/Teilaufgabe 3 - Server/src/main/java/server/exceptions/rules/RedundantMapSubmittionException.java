package server.exceptions.rules;

public class RedundantMapSubmittionException extends RuleBreakException {

	private static final long serialVersionUID = 1L;

	public RedundantMapSubmittionException(String playerID) {
		super("RedundantMapSubmittionException", "Player \"" + playerID + "\" already submitted a map!");
	}

}
