package server.businessrulez;

import java.util.Map;

import messagesbase.messagesfromclient.PlayerHalfMap;
import server.exceptions.rules.ForbiddenPlayerActionException;
import server.gamedata.GameStatus;
import server.gamedata.PlayerInfoHandler;

public class PlayerActionRule implements IRule {

	@Override
	public void validateSubmittedMap(final Map<String, GameStatus> gameHistory, final String gameID,
			final PlayerHalfMap halfMap) {

		PlayerInfoHandler players = gameHistory.get(gameID).getManagePlayers();
		String playerId = halfMap.getUniquePlayerID();

		if (!players.getPlayerThatHasToAct().equals(playerId)) {
			players.settleGameScore(playerId, false);
			throw new ForbiddenPlayerActionException(playerId);
		}

	}

}
