package server.businessrulez;

import java.util.Map;

import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.rules.NoMatchingGameIDException;
import server.gamedata.GameStatus;

public class GameIDAvaibleRule implements IRule {

	@Override
	public void confirmGameIDExistance(final Map<String, GameStatus> gameHistory, final String gameID) {
		if (!gameHistory.containsKey(gameID)) {
			throw new NoMatchingGameIDException(gameID);
		}
	}

	@Override
	public void validatePlayerRegistration(final Map<String, GameStatus> gameHistory, final String gameID,
			final PlayerRegistration playerRegistration) {
		confirmGameIDExistance(gameHistory, gameID);
	}

	@Override
	public void validateSubmittedMap(final Map<String, GameStatus> gameHistory, final String gameID,
			final PlayerHalfMap halfMap) {
		confirmGameIDExistance(gameHistory, gameID);
	}

	@Override
	public void validateGameStatusState(final Map<String, GameStatus> gameHistory, final String gameID,
			final String playerID) {
		confirmGameIDExistance(gameHistory, gameID);
	}

}
