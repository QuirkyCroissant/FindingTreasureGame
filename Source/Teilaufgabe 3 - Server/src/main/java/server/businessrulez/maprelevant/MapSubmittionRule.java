package server.businessrulez.maprelevant;

import java.util.Map;

import messagesbase.messagesfromclient.PlayerHalfMap;
import server.businessrulez.IRule;
import server.exceptions.rules.RedundantMapSubmittionException;
import server.gamedata.GameStatus;
import server.gamedata.PlayerInfoHandler;

public class MapSubmittionRule implements IRule {

	/**
	 * Method which tests if Player already submitted a halfmap and if he did than
	 * it throws en Exception
	 */
	@Override
	public void validateSubmittedMap(final Map<String, GameStatus> gameHistory, final String gameID,
			final PlayerHalfMap halfMap) {

		PlayerInfoHandler players = gameHistory.get(gameID).getManagePlayers();
		String playerId = halfMap.getUniquePlayerID();

		if (players.getSpecificPlayer(playerId).hasAlreadyDeliveredMap()) {
			players.settleGameScore(playerId, false);

			throw new RedundantMapSubmittionException(playerId);

		}
	}
}
