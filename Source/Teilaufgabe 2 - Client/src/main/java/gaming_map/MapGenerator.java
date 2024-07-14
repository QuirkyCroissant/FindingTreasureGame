package gaming_map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapGenerator {

	private static final int VERTICAL_TOP = 5;
	private static final int HORIZONTAL_TOP = 10;

	private static final int HALFMAP_SIZE = 50;

	private static final int GRASS_PERCENTAGE = 66;
	private static final int MOUNTAIN_PERCENTAGE = 85;

	private static Logger mapGenLogger = LoggerFactory.getLogger(MapGenerator.class);

	private Map<Coordinate, Field> builtMap = new HashMap<>();

	public MapGenerator() {
		this.builtMap = buildHalfMap();
	}

	public Map<Coordinate, Field> buildHalfMap() {

		HashMap<Coordinate, Field> res = new HashMap<>();

		List<Coordinate> grassList = new ArrayList<>();
		int generateCnt = 1;

		while (true) {

			// Generates 50 random fields to fill the map
			// if results fails validation it discards the map
			// and starts anew.
			while (res.size() < HALFMAP_SIZE) {

				Random rand = new Random();
				// rand coordinates
				int x = rand.nextInt(HORIZONTAL_TOP - 0) + 0;
				int y = rand.nextInt(VERTICAL_TOP - 0) + 0;
				Coordinate tempCoord = new Coordinate(x, y);

				// min 5 Bergfelder, 24 Wiesenfelder, 7 Wasserfelder => 36 total
				ETerrain tempTerrain;
				int terrainPercentage = rand.nextInt(100);

				if (terrainPercentage < GRASS_PERCENTAGE) // 66% Grass
					tempTerrain = ETerrain.GRASS;
				else if (terrainPercentage < MOUNTAIN_PERCENTAGE) // ~19.44% Mountain
					tempTerrain = ETerrain.MOUNTAIN;
				else
					tempTerrain = ETerrain.WATER; // ~13.88% Water

				Field tempField = new Field(tempTerrain, ESpecial.NONE);

				if (!res.containsKey(tempCoord)) {
					res.put(tempCoord, tempField);
					if (tempField.getTiletype().equals(ETerrain.GRASS))
						grassList.add(tempCoord);
				}

			}

			Random rand = new Random();
			// places the castle on random grass tile
			Coordinate castleCoord = grassList.get(rand.nextInt(grassList.size()));
			res.get(castleCoord).setTileinfo(ESpecial.BASE_CASTLE_IS_HERE);

			this.builtMap = res;
			mapGenLogger.debug("{}. Attempt: ", generateCnt++);

			// has to be validated, if false discard it!
			if (!MapValidator.validateTests(res)) {
				res.clear();
				grassList.clear();
			}

			// if every validation has been passed successfully
			// we break out of the loop
			if (!res.isEmpty())
				break;

		}

		mapGenLogger.debug("{}", this);

		return res;
	}

	public Map<Coordinate, Field> getBuiltMap() {
		return builtMap;
	}

	@Override
	public String toString() {

		StringBuilder res = new StringBuilder();

		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				Coordinate searchCoords = new Coordinate(x, y);
				Field curField = this.builtMap.get(searchCoords);
				ESpecial curSpecial = curField.getTileinfo();
				ETerrain curTerrain = curField.getTiletype();

				if (curSpecial.equals(ESpecial.BASE_CASTLE_IS_HERE)) {
					res.append("\u001B[51;101m|>\u001B[0m");
				} else {

					switch (curTerrain) {
					case GRASS:
						res.append("\u001B[51;102m,,\u001B[0m");
						break;
					case MOUNTAIN:
						res.append("\u001B[51;215m/\\\u001B[0m");
						break;
					default:
						res.append("\u001B[51;106m~~\u001B[0m");
						break;
					}
				}
			}
			res.append("\n");
		}

		return res.toString();
	}

}
