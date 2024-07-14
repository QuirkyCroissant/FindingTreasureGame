package gaming_map;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapValidator {

	private static Logger mapValLogger = LoggerFactory.getLogger(MapValidator.class);

	private static final int MAPSIZE = 50;
	private static final int MAX_WIDTH = 9;
	private static final int MAX_HEIGHT = 4;
	private static final int ZERO = 0;

	private static final int MIN_GRASS_TILES = 24;
	private static final int MIN_MOUNTAIN_TILES = 5;
	private static final int MIN_WATER_TILES = 7;

	private static final int MAX_HORIZONTAL_WATERBORDER_NUMBER = 4;
	private static final int MAX_VERTICAL_WATERBORDER_NUMBER = 2;

	public static boolean validateTests(Map<Coordinate, Field> map) {

		if (!checkMapSize(map)) {
			return false;
		}
		if (!checkForCastle(map)) {
			return false;
		}
		if (!checkMinTileCounts(map)) {
			mapValLogger.warn("validator failed: was caused by minimum tile count!");
			return false;
		}
		if (!checkWaterBorders(map)) {
			mapValLogger.warn("validator failed: was caused by too much water on borders!");
			return false;
		}
		if (!checkForTileAvailability(map)) {
			mapValLogger.warn(
					"validator failed: was caused because a tile could not be accessed(islands/inaccessable neighbor)");
			return false;
		}

		return true;
	}

	/*
	 * Tests if the map has the right proportions
	 */
	private static boolean checkMapSize(Map<Coordinate, Field> map) {

		// total size
		if (map.size() != MAPSIZE)
			return false;

		return true;
	}

	/*
	 * Base Castle can only be placed on a GRASS tile
	 */
	private static boolean checkForCastle(Map<Coordinate, Field> map) {

		for (var entry : map.entrySet()) {
			if (entry.getValue().getTileinfo().equals(ESpecial.BASE_CASTLE_IS_HERE))
				return true;
		}

		return false;
	}

	private static boolean checkMinTileCounts(Map<Coordinate, Field> map) {

		for (int i = 0; i < ETerrain.values().length; i++) {

			int cnt = 0;
			ETerrain curType = ETerrain.values()[i];

			for (var entry : map.entrySet()) {
				if (entry.getValue().getTiletype().equals(curType))
					cnt++;
			}

			if (curType.equals(ETerrain.GRASS) && cnt < MIN_GRASS_TILES) {
				return false;
			} else if (curType.equals(ETerrain.MOUNTAIN) && cnt < MIN_MOUNTAIN_TILES) {
				return false;
			} else if (curType.equals(ETerrain.WATER) && cnt < MIN_WATER_TILES) {
				return false;
			} else {
				continue;
			}

		}

		return true;
	}

	// check water tiles on borders
	private static boolean checkWaterBorders(Map<Coordinate, Field> map) {

		int topCnt = 0;
		int btmCnt = 0;
		int leftCnt = 0;
		int rightCnt = 0;

		for (var entry : map.entrySet()) {

			// top count: (?,0) coordinates
			if (Integer.compare(entry.getKey().getY(), ZERO) == 0
					&& (entry.getValue().getTiletype().equals(ETerrain.WATER)))
				topCnt++;

			// bottom count: (?,4) coordinates
			if (Integer.compare(entry.getKey().getY(), MAX_HEIGHT) == 0
					&& (entry.getValue().getTiletype().equals(ETerrain.WATER)))
				btmCnt++;

			// left count: (0,?) coordinates
			if (Integer.compare(entry.getKey().getX(), ZERO) == 0
					&& (entry.getValue().getTiletype().equals(ETerrain.WATER)))
				leftCnt++;

			// right count: (9,?) coordinates
			if (Integer.compare(entry.getKey().getX(), MAX_WIDTH) == 0
					&& (entry.getValue().getTiletype().equals(ETerrain.WATER)))
				rightCnt++;

		}

		if (topCnt > MAX_HORIZONTAL_WATERBORDER_NUMBER || btmCnt > MAX_HORIZONTAL_WATERBORDER_NUMBER)
			return false;
		else if (leftCnt > MAX_VERTICAL_WATERBORDER_NUMBER || rightCnt > MAX_VERTICAL_WATERBORDER_NUMBER)
			return false;
		else
			return true;

	}

	// check if all fields can be accessed by any other field on the map(checks
	// island therfore passively)
	private static boolean checkForTileAvailability(Map<Coordinate, Field> map) {

		// Inspiration for methods checkForTileAvailability() & isTraversable() was
		// found on:
		// https://www.geeksforgeeks.org/flood-fill-algorithm-implement-fill-paint

		final Set<Coordinate> visitedTiles = new HashSet<>();
		int cnt = 0;

		for (final Coordinate coordinates : map.keySet()) {

			if (visitedTiles.contains(coordinates)) {
				continue;
			}

			if (map.get(coordinates).getTiletype().equals(ETerrain.GRASS)
					|| map.get(coordinates).getTiletype().equals(ETerrain.MOUNTAIN)) {

				isTraversable(coordinates.getX(), coordinates.getY(), map, visitedTiles);
				cnt++;
			}
		}
		return (cnt == 1);

	}

	private static void isTraversable(final int x, final int y, final Map<Coordinate, Field> map,
			final Set<Coordinate> visitedTiles) {

		// coordinates have to be valid or we break out of the recursion
		if (x < ZERO || x > MAX_WIDTH || y < ZERO || y > MAX_HEIGHT) {
			return;
		}
		// we also break out if it is already a discovered/visited Tile
		Coordinate coordinates = new Coordinate(x, y);
		if (visitedTiles.contains(coordinates)) {
			return;
		}

		// water tiles are our border, we can't access them so we break the loop again
		if (map.get(coordinates).getTiletype().equals(ETerrain.WATER)) {
			return;
		}

		// valid field gets added into collection before visiting its neighbors
		visitedTiles.add(coordinates);
		isTraversable(x + 1, y, map, visitedTiles);
		isTraversable(x - 1, y, map, visitedTiles);
		isTraversable(x, y + 1, map, visitedTiles);
		isTraversable(x, y - 1, map, visitedTiles);

	}

}
