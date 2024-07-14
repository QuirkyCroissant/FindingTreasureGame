package ai;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import gaming_map.Coordinate;
import gaming_map.ESpecial;
import gaming_map.ETerrain;
import gaming_map.Field;
import gaming_map.Fullmap;

class GoalSettingTests {

	private static Fullmap QuadraticFullmap;
	private static Fullmap WideFullmap;

	@BeforeAll
	static void setUpBeforeClass() {

		Map<Coordinate, Field> quadraticMapGameBoard = new HashMap<>();
		Map<Coordinate, Field> WideMapGameBoard = new HashMap<>();

		Field grassTile = new Field(ETerrain.GRASS, ESpecial.NONE);

		Random rng = new Random();

		// generate quadratic map
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				quadraticMapGameBoard.put(new Coordinate(x, y), grassTile);
			}
		}

		List<Coordinate> quadraticCoordsArray = quadraticMapGameBoard.keySet().stream().toList();
		Coordinate quadraticCastle;
		do {
			quadraticCastle = quadraticCoordsArray.get(rng.nextInt(quadraticCoordsArray.size()));
		} while (quadraticCastle.getY() >= 5);

		quadraticMapGameBoard.replace(quadraticCastle, new Field(ETerrain.GRASS, ESpecial.BASE_CASTLE_IS_HERE));
		Map<String, Coordinate> quadraticPlayerPos = new HashMap<>();
		quadraticPlayerPos.put("myPlayer", quadraticCastle);

		QuadraticFullmap = new Fullmap(quadraticMapGameBoard, quadraticCastle, quadraticPlayerPos);
		QuadraticFullmap.setHomeCastle(quadraticCastle);
		QuadraticFullmap.setVerticallyJoined(false);

		// generate Wide Map

		for (int x = 0; x < 20; x++) {
			for (int y = 0; y < 5; y++) {
				WideMapGameBoard.put(new Coordinate(x, y), grassTile);
			}
		}

		List<Coordinate> wideCoordsArray = WideMapGameBoard.keySet().stream().toList();
		Coordinate wideCastle;
		do {
			wideCastle = wideCoordsArray.get(rng.nextInt(wideCoordsArray.size()));
		} while (wideCastle.getX() <= 9);

		WideMapGameBoard.replace(wideCastle, new Field(ETerrain.GRASS, ESpecial.BASE_CASTLE_IS_HERE));
		// System.out.println(wideCastle);

		Map<String, Coordinate> widePlayerPos = new HashMap<>();
		widePlayerPos.put("myPlayer", wideCastle);

		WideFullmap = new Fullmap(WideMapGameBoard, wideCastle, widePlayerPos);
		WideFullmap.setHomeCastle(wideCastle);
		WideFullmap.setVerticallyJoined(true);

	}

	@Test
	void givenCustomFullMapAndBestowPlayerTreasure_RunGoalSetter_ExpectThatNewGoalIsOnEnemiesSide() {

		// arrange
		Fullmap quadraticMap = new Fullmap(QuadraticFullmap);
		quadraticMap.setVerticallyJoined(false);

		Fullmap wideMap = new Fullmap(WideFullmap);
		wideMap.setVerticallyJoined(true);

		// EGoal CASTLE signalises that the treasure has been found
		GoalSetting quadraticGoal = new GoalSetting(quadraticMap.getHomeCastle(), EGoals.CASTLE);
		GoalSetting wideGoal = new GoalSetting(wideMap.getHomeCastle(), EGoals.CASTLE);

		// act
		boolean resultOfTest = false;

		quadraticGoal.reconNewTiles(QuadraticFullmap);
		wideGoal.reconNewTiles(WideFullmap);

		boolean inOwnTerritoryQuad = (quadraticGoal.getCoords().getY() < 5);
		boolean inOwnTerritoryWide = (wideGoal.getCoords().getX() < 9);

		// assert
		assertThat(resultOfTest, is(equalTo(inOwnTerritoryQuad && inOwnTerritoryWide)));
	}

	@Test
	void goalSetterSearchesForTreasureInOwnSide_Expected_true() {

		// arrange
		Fullmap quadraticMap = new Fullmap(QuadraticFullmap);
		quadraticMap.setVerticallyJoined(false);

		Fullmap wideMap = new Fullmap(WideFullmap);
		wideMap.setVerticallyJoined(true);

		// EGoal CASTLE signalises that the treasure has been found
		GoalSetting quadraticGoal = new GoalSetting(quadraticMap.getHomeCastle(), EGoals.TREASURE);
		GoalSetting wideGoal = new GoalSetting(wideMap.getHomeCastle(), EGoals.TREASURE);

		// act
		boolean resultOfTest = true;

		quadraticGoal.reconNewTiles(QuadraticFullmap);
		wideGoal.reconNewTiles(WideFullmap);

		boolean inOwnTerritoryQuad = (quadraticGoal.getCoords().getY() < 5);
		boolean inOwnTerritoryWide = (wideGoal.getCoords().getX() > 9);

		// assert
		assertThat(resultOfTest, is(equalTo(inOwnTerritoryWide)));
		assertThat(resultOfTest, is(equalTo(inOwnTerritoryQuad)));
	}

}
