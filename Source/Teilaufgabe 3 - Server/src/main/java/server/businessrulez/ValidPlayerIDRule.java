package server.businessrulez;

import java.util.Map;

import messagesbase.messagesfromclient.PlayerHalfMap;
import server.exceptions.rules.NoMatchingPlayerIDException;
import server.gamedata.GameStatus;

public class ValidPlayerIDRule implements IRule {

	@Override
	public void confirmPlayerID(final GameStatus game, final String gameID, final String player) {
		if (!game.getManagePlayers().isPlayerInDatabase(player)) {
			throw new NoMatchingPlayerIDException(gameID, player);
		}
	}

	@Override
	public void validateSubmittedMap(final Map<String, GameStatus> gamesMap, final String gameID,
			final PlayerHalfMap halfMap) {
		confirmPlayerID(gamesMap.get(gameID), gameID, halfMap.getUniquePlayerID());
	}

	@Override
	public void validateGameStatusState(final Map<String, GameStatus> gamesMap, final String gameID,
			final String playerID) {
		confirmPlayerID(gamesMap.get(gameID), gameID, playerID);
	}
}
