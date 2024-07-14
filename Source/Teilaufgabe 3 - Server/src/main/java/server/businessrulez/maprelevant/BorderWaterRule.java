package server.businessrulez.maprelevant;

import java.util.Map;

import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import server.businessrulez.IRule;
import server.exceptions.rules.MapValidationException;
import server.gamedata.GameStatus;

public class BorderWaterRule implements IRule {

	private static final int MAX_LATITUDE_WATER_TILES = 4;
	private static final int MAX_LONGITUDE_WATER_TILES = 2;

	private static final int MAX_WIDTH = 9;
	private static final int MAX_HEIGHT = 4;
	private static final int ZERO = 0;

	/**
	 * This implementation of the Border Businessrule checks, if only the allowed
	 * quantity of water tiles are on the long and short vertige are in the halfmap
	 */
	@Override
	public void validateSubmittedMap(final Map<String, GameStatus> gameHistory, final String gameID,
			final PlayerHalfMap halfMap) {
		int topCnt = 0;
		int btmCnt = 0;
		int leftCnt = 0;
		int rightCnt = 0;

		for (var entry : halfMap.getMapNodes()) {

			// top count: (?,0) coordinates
			if (Integer.compare(entry.getY(), ZERO) == 0 && (entry.getTerrain().equals(ETerrain.Water)))
				topCnt++;

			// bottom count: (?,4) coordinates
			if (Integer.compare(entry.getY(), MAX_HEIGHT) == 0 && (entry.getTerrain().equals(ETerrain.Water)))
				btmCnt++;

			// left count: (0,?) coordinates
			if (Integer.compare(entry.getX(), ZERO) == 0 && (entry.getTerrain().equals(ETerrain.Water)))
				leftCnt++;

			// right count: (9,?) coordinates
			if (Integer.compare(entry.getX(), MAX_WIDTH) == 0 && (entry.getTerrain().equals(ETerrain.Water)))
				rightCnt++;

		}

		if (topCnt > MAX_LATITUDE_WATER_TILES || btmCnt > MAX_LATITUDE_WATER_TILES)
			throw new MapValidationException(
					"Submitted HalfMap has too much water on the long edges of the map, which is not allowed!");
		else if (leftCnt > MAX_LONGITUDE_WATER_TILES || rightCnt > MAX_LONGITUDE_WATER_TILES)
			throw new MapValidationException(
					"Submitted HalfMap has too much water on the short edges of the map, which is not allowed!");
	}
}
