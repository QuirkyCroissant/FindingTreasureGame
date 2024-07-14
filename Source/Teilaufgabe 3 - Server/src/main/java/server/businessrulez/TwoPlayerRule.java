package server.businessrulez;

import java.util.Map;

import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.PlayerRegistrationException;
import server.gamedata.GameStatus;

public class TwoPlayerRule implements IRule {

	private static final int MAX_PLAYER_CAP = 2;

	@Override
	public void validatePlayerRegistration(final Map<String, GameStatus> gamesMap, final String gameID,
			final PlayerRegistration playerRegistration) {
		if (gamesMap.get(gameID).getManagePlayers().getPlayerbase().size() == MAX_PLAYER_CAP)
			throw new PlayerRegistrationException(playerRegistration.getStudentFirstName() + " "
					+ playerRegistration.getStudentLastName() + " can not be added to game " + gameID
					+ " because the maximum capacity is already reached!");
	}
}
