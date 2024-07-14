package server.businessrulez;

import java.util.Map;

import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.rules.NoMatchingGameIDException;
import server.exceptions.rules.NoMatchingPlayerIDException;
import server.exceptions.rules.RuleBreakException;
import server.gamedata.GameStatus;

public interface IRule {

	default void confirmPlayerID(final GameStatus game, final String gameID, final String playerID)
			throws NoMatchingPlayerIDException {
	}

	default void confirmGameIDExistance(final Map<String, GameStatus> gamesMap, final String gameID)
			throws NoMatchingGameIDException {
	}

	default void validatePlayerRegistration(final Map<String, GameStatus> gamesMap, final String gameID,
			final PlayerRegistration playerRegistration) throws RuleBreakException {
	}

	default void validateSubmittedMap(final Map<String, GameStatus> gamesMap, final String gameID,
			final PlayerHalfMap halfMap) throws RuleBreakException {
	}

	default void validateGameStatusState(final Map<String, GameStatus> gamesMap, final String gameID,
			final String playerID) throws RuleBreakException {
	}
}
