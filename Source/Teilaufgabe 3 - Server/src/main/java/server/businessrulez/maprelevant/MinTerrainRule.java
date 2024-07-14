package server.businessrulez.maprelevant;

import java.util.Map;

import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import server.businessrulez.IRule;
import server.exceptions.rules.MapValidationException;
import server.gamedata.GameStatus;

public class MinTerrainRule implements IRule {

	private static final int MIN_GRASS_TILES = 24;
	private static final int MIN_MOUNTAIN_TILES = 5;
	private static final int MIN_WATER_TILES = 7;

	@Override
	public void validateSubmittedMap(final Map<String, GameStatus> gameHistory, final String gameID,
			final PlayerHalfMap halfMap) {

		for (int i = 0; i < ETerrain.values().length; i++) {

			int cnt = 0;
			ETerrain curType = ETerrain.values()[i];

			for (var entry : halfMap.getMapNodes()) {
				if (entry.getTerrain().equals(curType))
					cnt++;
			}

			if (curType.equals(ETerrain.Grass) && cnt < MIN_GRASS_TILES) {
				throw new MapValidationException("Submitted Halfmap has insufficient Grass Fields! Needs "
						+ MIN_GRASS_TILES + " Fields, but only " + cnt + " were found!");
			} else if (curType.equals(ETerrain.Mountain) && cnt < MIN_MOUNTAIN_TILES) {
				throw new MapValidationException("Submitted Halfmap has insufficient Mountain Fields! Needs "
						+ MIN_MOUNTAIN_TILES + " Fields, but only " + cnt + " were found!");
			} else if (curType.equals(ETerrain.Water) && cnt < MIN_WATER_TILES) {
				throw new MapValidationException("Submitted Halfmap has insufficient Water Fields! Needs "
						+ MIN_WATER_TILES + " Fields, but only " + cnt + " were found!");
			}

		}

	}
}
