package gaming_map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;;

class MapValidatorTests {

	private static Map<Coordinate, Field> validHalfMapGameBoard = new HashMap<>();
	private static Halfmap validHalfMap;

	@BeforeAll
	static void setUpBeforeClass() {

		Field grassTile = new Field(ETerrain.GRASS, ESpecial.NONE);
		Field mountainTile = new Field(ETerrain.MOUNTAIN, ESpecial.NONE);
		Field waterTile = new Field(ETerrain.WATER, ESpecial.NONE);

		// floods a halfmap with grassfields and column 6 is only inhabited by mountains
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 5; y++) {
				if (x == 5)
					validHalfMapGameBoard.put(new Coordinate(x, y), mountainTile);
				else if (x == 4 && y == 2)
					validHalfMapGameBoard.put(new Coordinate(x, y),
							new Field(ETerrain.GRASS, ESpecial.BASE_CASTLE_IS_HERE));
				else
					validHalfMapGameBoard.put(new Coordinate(x, y), grassTile);
			}
		}

		// generates water tiles in left upper and right lower corners
		validHalfMapGameBoard.replace(new Coordinate(0, 1), waterTile);
		validHalfMapGameBoard.replace(new Coordinate(0, 0), waterTile);
		validHalfMapGameBoard.replace(new Coordinate(1, 0), waterTile);
		validHalfMapGameBoard.replace(new Coordinate(2, 0), waterTile);
		validHalfMapGameBoard.replace(new Coordinate(3, 0), waterTile);

		validHalfMapGameBoard.replace(new Coordinate(9, 4), waterTile);
		validHalfMapGameBoard.replace(new Coordinate(9, 3), waterTile);
		validHalfMapGameBoard.replace(new Coordinate(8, 4), waterTile);

		Coordinate baseCastleCoordinate = new Coordinate(4, 2);

		validHalfMap = new Halfmap(validHalfMapGameBoard, baseCastleCoordinate);
	}

	@Test
	void givenHalfMapWithTooMuchTiles_RunHalfMapSizeCheck_ExpectFalse() {
		// arrange
		Halfmap tooLargeMap = new Halfmap(validHalfMap);

		tooLargeMap.getGameField().put(new Coordinate(666, 666), new Field(ETerrain.GRASS, ESpecial.NONE));

		// act
		boolean resultOfTest = true;
		try {

			Method checkMapSize = MapValidator.class.getDeclaredMethod("checkMapSize", Map.class);
			checkMapSize.setAccessible(true);
			resultOfTest = (boolean) checkMapSize.invoke(MapValidator.class, tooLargeMap.getGameField());

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		// assert
		assertThat(resultOfTest, is(equalTo(false)));

	}

	@Test
	void givenHalfMapWithNotEnoughWaterTiles_RunMinTileCheck_ExpectFalse() {
		// arrange
		Halfmap droughtMap = new Halfmap(validHalfMap);
		droughtMap.getGameField().replace(new Coordinate(0, 0), new Field(ETerrain.GRASS, ESpecial.NONE));
		droughtMap.getGameField().replace(new Coordinate(9, 4), new Field(ETerrain.GRASS, ESpecial.NONE));

		// act
		boolean resultOfTest = false;
		try {

			Method checkMinTilesWater = MapValidator.class.getDeclaredMethod("checkMinTileCounts", Map.class);
			checkMinTilesWater.setAccessible(true);
			resultOfTest = (boolean) checkMinTilesWater.invoke(MapValidator.class, droughtMap.getGameField());

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		// assert
		assertThat(resultOfTest, is(equalTo(false)));
	}

	@Test
	void giveHalfMapWithNotEnoughMountains_RunMinTileCheck_ExpectFalse() {
		// arrange
		Halfmap flatLandsMap = new Halfmap(validHalfMap);
		flatLandsMap.getGameField().replace(new Coordinate(5, 0), new Field(ETerrain.GRASS, ESpecial.NONE));

		// act
		boolean resultOfTest = false;
		try {

			Method checkMinTilesMountains = MapValidator.class.getDeclaredMethod("checkMinTileCounts", Map.class);
			checkMinTilesMountains.setAccessible(true);
			resultOfTest = (boolean) checkMinTilesMountains.invoke(MapValidator.class, flatLandsMap.getGameField());

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		// assert
		assertThat(resultOfTest, is(equalTo(false)));
	}

	@Test
	void givenHalfMapWithNotEnoughGrass_RunMinTileCheck_ExpectFalse() {
		// arrange
		Halfmap thatchedMap = new Halfmap(validHalfMap);

		for (var halfMapPair : thatchedMap.getGameField().entrySet()) {
			if (halfMapPair.getValue().getTiletype().equals(ETerrain.GRASS))
				thatchedMap.getGameField().replace(halfMapPair.getKey(), new Field(ETerrain.MOUNTAIN, ESpecial.NONE));
		}

		// act
		boolean resultOfTest = false;
		try {

			Method checkMinTilesGrass = MapValidator.class.getDeclaredMethod("checkMinTileCounts", Map.class);
			checkMinTilesGrass.setAccessible(true);
			resultOfTest = (boolean) checkMinTilesGrass.invoke(MapValidator.class, thatchedMap.getGameField());

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		// assert
		assertThat(resultOfTest, is(equalTo(false)));
	}

	@Test
	void givenHalfMapWithoutACastle_RunCastleCheck_ExpectFalse() {
		// arrange
		Halfmap castleLessMap = new Halfmap(validHalfMap);
		castleLessMap.getGameField().replace(new Coordinate(4, 2), new Field(ETerrain.GRASS, ESpecial.NONE));

		// act
		boolean resultOfTest = false;
		try {

			Method checkCastleLess = MapValidator.class.getDeclaredMethod("checkForCastle", Map.class);
			checkCastleLess.setAccessible(true);
			resultOfTest = (boolean) checkCastleLess.invoke(MapValidator.class, castleLessMap.getGameField());

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		// assert
		assertThat(resultOfTest, is(equalTo(false)));
	}

	@Test
	void givenHalfMapWithTooMuchWaterOnLongBorder_RunWaterBorderCheck_ExpectFalse() {
		// arrange
		Halfmap tooMuchWaterOnTopBorder = new Halfmap(validHalfMap);
		tooMuchWaterOnTopBorder.getGameField().replace(new Coordinate(4, 0), new Field(ETerrain.WATER, ESpecial.NONE));

		// act
		boolean resultOfTest = false;
		try {

			Method checkWaterBorders = MapValidator.class.getDeclaredMethod("checkWaterBorders", Map.class);
			checkWaterBorders.setAccessible(true);
			resultOfTest = (boolean) checkWaterBorders.invoke(MapValidator.class,
					tooMuchWaterOnTopBorder.getGameField());

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		// assert
		assertThat(resultOfTest, is(equalTo(false)));
	}

	@Test
	void givenHalfMapWithTooMuchWaterOnShortBorder_RunWaterBorderCheck_ExpectFalse() {
		// arrange
		Halfmap tooMuchWaterOnLeftBorder = new Halfmap(validHalfMap);
		tooMuchWaterOnLeftBorder.getGameField().replace(new Coordinate(0, 2), new Field(ETerrain.WATER, ESpecial.NONE));

		// act
		boolean resultOfTest = false;
		try {

			Method checkWaterBorders = MapValidator.class.getDeclaredMethod("checkWaterBorders", Map.class);
			checkWaterBorders.setAccessible(true);
			resultOfTest = (boolean) checkWaterBorders.invoke(MapValidator.class,
					tooMuchWaterOnLeftBorder.getGameField());

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		// assert
		assertThat(resultOfTest, is(equalTo(false)));
	}

	@Test
	void givenHalfMapWithInaccessableFields_RunISlandCheck_ExpectFalse() {
		// arrange
		Halfmap mapWithIsland = new Halfmap(validHalfMap);
		mapWithIsland.getGameField().replace(new Coordinate(1, 2), new Field(ETerrain.WATER, ESpecial.NONE));
		mapWithIsland.getGameField().replace(new Coordinate(2, 1), new Field(ETerrain.WATER, ESpecial.NONE));

		// act
		boolean resultOfTest = false;
		try {

			Method checkForTileAvailability = MapValidator.class.getDeclaredMethod("checkForTileAvailability",
					Map.class);
			checkForTileAvailability.setAccessible(true);
			resultOfTest = (boolean) checkForTileAvailability.invoke(MapValidator.class, mapWithIsland.getGameField());

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		// assert
		assertThat(resultOfTest, is(equalTo(false)));
	}

	@Test
	void givenValidHashMap_RunEveryValidationOnIt_ExpectTrue() {
		// arrange
		Map<Coordinate, Field> goodHalfMapBoard = new HashMap<>(validHalfMapGameBoard);

		// act
		boolean resultOfTest = MapValidator.validateTests(goodHalfMapBoard);

		// assert
		assertThat(resultOfTest, is(equalTo(true)));
	}

	@Test
	void generateValidHalfmapLikeAGameWould__GeneratesMApANdValidatesIt_ExpectTrue() {
		// arrange
		Halfmap validHalfmap = new Halfmap();

		// act
		boolean resultOfTest = MapValidator.validateTests(validHalfmap.getGameField());

		// assert
		assertThat(resultOfTest, is(equalTo(true)));
	}
}
