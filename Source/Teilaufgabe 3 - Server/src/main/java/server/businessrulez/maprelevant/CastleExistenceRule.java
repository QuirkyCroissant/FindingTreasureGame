package server.businessrulez.maprelevant;

import java.util.Map;

import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.businessrulez.IRule;
import server.exceptions.rules.MapValidationException;
import server.gamedata.GameStatus;

public class CastleExistenceRule implements IRule {
	// MapValidationException

	@Override
	public void validateSubmittedMap(final Map<String, GameStatus> gameHistory, final String gameID,
			final PlayerHalfMap halfMap) {
		boolean castleDetected = false;
		for (PlayerHalfMapNode entry : halfMap.getMapNodes()) {
			if (entry.isFortPresent()) {
				if (!entry.getTerrain().equals(ETerrain.Grass))
					throw new MapValidationException("Castle is placed on a " + entry.getTerrain().toString()
							+ "-Tile on (" + entry.getX() + "/" + entry.getY() + ") Coordinate!");
				else if (castleDetected)
					throw new MapValidationException("Multiple Castles on submitted map detected!");
				castleDetected = true;
			}
		}

		if (!castleDetected)
			throw new MapValidationException("No Castle was detected on submitted map!");
	}
}
