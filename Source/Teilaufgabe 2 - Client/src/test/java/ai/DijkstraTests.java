package ai;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import game_state.GameStatus;
import gaming_map.Coordinate;
import gaming_map.ESpecial;
import gaming_map.ETerrain;
import gaming_map.Field;
import gaming_map.Fullmap;

class DijkstraTests {

	private static Map<Coordinate, Field> validFullMapGameBoard = new HashMap<>();
	private static Fullmap validQuadraticFullmap;
	private static DijkstraPath pathfinder;

	@BeforeAll
	static void setUpBeforeClass() {

		Field grassTile = new Field(ETerrain.GRASS, ESpecial.NONE);
		Field mountainTile = new Field(ETerrain.MOUNTAIN, ESpecial.NONE);
		Field waterTile = new Field(ETerrain.WATER, ESpecial.NONE);

		// floods a halfmap with grassfields and column 6 is only inhabited by mountains
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				if (x == 4 && y == 2)
					validFullMapGameBoard.put(new Coordinate(x, y),
							new Field(ETerrain.GRASS, ESpecial.BASE_CASTLE_IS_HERE));
				else if (x == 4 && y > 1)
					validFullMapGameBoard.put(new Coordinate(x, y), grassTile);
				else if (x == 8 && y == 9)
					validFullMapGameBoard.put(new Coordinate(x, y), mountainTile);
				else if (x > 4 && y == 9)
					validFullMapGameBoard.put(new Coordinate(x, y), grassTile);
				else if ((x > 4 && x < 7) && y == 6)
					validFullMapGameBoard.put(new Coordinate(x, y), grassTile);
				else if ((x > 5 && x < 9) && y == 5)
					validFullMapGameBoard.put(new Coordinate(x, y), grassTile);
				else if (x == 8 && y > 5)
					validFullMapGameBoard.put(new Coordinate(x, y), grassTile);
				else
					validFullMapGameBoard.put(new Coordinate(x, y), waterTile);

			}
		}

		Coordinate baseCastleCoordinate = new Coordinate(4, 2);
		Map<String, Coordinate> playerPos = new HashMap<>();
		playerPos.put("myPlayer", baseCastleCoordinate);

		validQuadraticFullmap = new Fullmap(validFullMapGameBoard, baseCastleCoordinate, playerPos);
		validQuadraticFullmap.setHomeCastle(baseCastleCoordinate);
	}

	@Test
	void givenASpecificFullMapWithTwoPaths_RunDijkstra_ExpectThatShortestPathIsChosen() {

		System.out.println(validQuadraticFullmap);

		// arrange
		Fullmap twoPathMap = new Fullmap(validQuadraticFullmap);
		GoalSetting goal = new GoalSetting(new Coordinate(9, 9), EGoals.TREASURE);
		pathfinder = new DijkstraPath(twoPathMap, goal);
		Object[] methodArgs = new Object[] { new Coordinate(4, 2), goal.getCoords() };

		// act
		boolean resultOfTest = true;
		List<Coordinate> resultingPath = new ArrayList<>();
		try {

			Method calculateShortestPath = DijkstraPath.class.getDeclaredMethod("calculateShortestPath",
					Coordinate.class, Coordinate.class);
			calculateShortestPath.setAccessible(true);
			resultingPath = (List<Coordinate>) calculateShortestPath.invoke(pathfinder, methodArgs);

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		// assert
		assertThat(resultOfTest, is(equalTo(resultingPath.size() - 1 == 12)));
	}

	@Test
	void givenASpecificFullMapWithTwoPaths_RunDijkstraTwoTimes_ExpectThatOnSecondRunAlternativePathIsChosen() {

		// arrange
		Fullmap alternativePathMap = new Fullmap(validQuadraticFullmap);
		GoalSetting goal = new GoalSetting(new Coordinate(9, 9), EGoals.TREASURE);
		pathfinder = new DijkstraPath(alternativePathMap, goal);
		Object[] methodArgs = new Object[] { new Coordinate(4, 2), goal.getCoords() };

		// act
		boolean resultOfTest = true;
		List<Coordinate> firstResultingPath = new ArrayList<>();
		List<Coordinate> secondResultingPath = new ArrayList<>();

		try {

			Method calculateShortestPath = DijkstraPath.class.getDeclaredMethod("calculateShortestPath",
					Coordinate.class, Coordinate.class);
			calculateShortestPath.setAccessible(true);
			firstResultingPath = (List<Coordinate>) calculateShortestPath.invoke(pathfinder, methodArgs);
			secondResultingPath = (List<Coordinate>) calculateShortestPath.invoke(pathfinder, methodArgs);

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		// assert
		assertThat(resultOfTest, is(equalTo(firstResultingPath.equals(secondResultingPath) == false)));
	}

	@Test
	void givenASpecificFullMapWithTwoPaths_RunDijkstraTwoTimesAndRevealTreasurePositionOnSecondRun_ExpectThatOnSecondRunAlternativePathToTreasureIsChosen() {

		// arrange
		Fullmap beginPathMap = new Fullmap(validQuadraticFullmap);
		GoalSetting goal = new GoalSetting(new Coordinate(9, 9), EGoals.TREASURE);
		pathfinder = new DijkstraPath(beginPathMap, goal);
		Object[] methodArgs = new Object[] { new Coordinate(4, 2), goal.getCoords() };

		// need a new FullMap that now found a Treasure tile
		Fullmap resetPathMap = new Fullmap(validQuadraticFullmap);
		resetPathMap.setFoundTreasure(new Coordinate(4, 9));
		resetPathMap.setFoundTreasure(true);

		GameStatus newGoalFromServer = new GameStatus("1234");
		newGoalFromServer.setMatchMap(resetPathMap);

		// act
		boolean resultOfTest = true;
		List<Coordinate> firstResultingPath = new ArrayList<>();
		List<Coordinate> secondResultingPath = new ArrayList<>();

		try {

			// calculates first route normaly
			Method calculateShortestPath = DijkstraPath.class.getDeclaredMethod("calculateShortestPath",
					Coordinate.class, Coordinate.class);
			calculateShortestPath.setAccessible(true);
			firstResultingPath = (List<Coordinate>) calculateShortestPath.invoke(pathfinder, methodArgs);

			// updates new goal by listening to new GameState
			pathfinder.setNaviMap(newGoalFromServer.getMatchMap());
			Method checkGoal = DijkstraPath.class.getDeclaredMethod("checkGoal", GameStatus.class);
			checkGoal.setAccessible(true);

			// calls checkGoal method to alter the goal coordinates in the pathfinder
			// instance
			boolean updatedGoal = (boolean) checkGoal.invoke(pathfinder, newGoalFromServer);

			methodArgs[1] = pathfinder.getMoveMotivation().getCoords();

			if (updatedGoal) {
				secondResultingPath = (List<Coordinate>) calculateShortestPath.invoke(pathfinder, methodArgs);
			}

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		// assert
		assertThat(resultOfTest, is(equalTo(firstResultingPath.equals(secondResultingPath) == false)));
	}

}
