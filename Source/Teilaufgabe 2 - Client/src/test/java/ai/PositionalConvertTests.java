package ai;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import gaming_map.Coordinate;
import gaming_map.ESpecial;
import gaming_map.ETerrain;
import gaming_map.Field;
import gaming_map.Fullmap;

class PositionalConvertTests {

	private static List<Coordinate> coordinateList1 = new ArrayList<>();
	private static List<EDirection> directionsList1 = new ArrayList<>();
	private static List<Coordinate> coordinateList2 = new ArrayList<>();
	private static List<EDirection> directionsList2 = new ArrayList<>();
	private static Fullmap testMap;

	@BeforeAll
	static void InitializeDirectionList() {
		coordinateList1.add(new Coordinate(0, 0)); // grass
		coordinateList1.add(new Coordinate(1, 0)); // grass
		coordinateList1.add(new Coordinate(1, 1)); // mountain

		coordinateList2.add(new Coordinate(1, 2)); // grass
		coordinateList2.add(new Coordinate(0, 2)); // grass
		coordinateList2.add(new Coordinate(0, 1)); // grass

		Map<Coordinate, Field> map = new HashMap<>();
		map.put(new Coordinate(0, 0), new Field(ETerrain.GRASS, ESpecial.BASE_CASTLE_IS_HERE));
		map.put(new Coordinate(0, 1), new Field(ETerrain.GRASS, ESpecial.NONE));
		map.put(new Coordinate(0, 2), new Field(ETerrain.GRASS, ESpecial.NONE));
		map.put(new Coordinate(1, 0), new Field(ETerrain.GRASS, ESpecial.NONE));
		map.put(new Coordinate(1, 1), new Field(ETerrain.MOUNTAIN, ESpecial.NONE));
		map.put(new Coordinate(1, 2), new Field(ETerrain.GRASS, ESpecial.ENEMIES_CASTLE_IS_HERE));
		map.put(new Coordinate(2, 0), new Field(ETerrain.WATER, ESpecial.NONE));
		map.put(new Coordinate(2, 1), new Field(ETerrain.GRASS, ESpecial.NONE));
		map.put(new Coordinate(2, 2), new Field(ETerrain.GRASS, ESpecial.NONE));

		Map<String, Coordinate> playerPositions = new HashMap<>();
		playerPositions.put("test", new Coordinate(0, 0));
		playerPositions.put("other-player", new Coordinate(1, 2));

		testMap = new Fullmap(map, new Coordinate(1, 2), playerPositions);

		directionsList1.add(EDirection.RIGHT);
		directionsList1.add(EDirection.RIGHT);

		directionsList1.add(EDirection.DOWN);
		directionsList1.add(EDirection.DOWN);
		directionsList1.add(EDirection.DOWN);

		directionsList2.add(EDirection.LEFT);
		directionsList2.add(EDirection.LEFT);

		directionsList2.add(EDirection.UP);
		directionsList2.add(EDirection.UP);

	}

	// data driven test
	private static Stream<Arguments> addTestListData() {
		return Stream.of(Arguments.of(coordinateList1, directionsList1, true),
				Arguments.of(coordinateList2, directionsList2, true));
	}

	@ParameterizedTest
	@MethodSource("addTestListData")
	void givenCustomCoordinatePaths_ConvertPathIntoEDirectionList_ExpectMatchWithControllLists(
			List<Coordinate> PathList, List<EDirection> DirList) {
		// arrange

		boolean resultOfTest = true;

		// act
		List<EDirection> resultList = PositionalConvert.convertPathToDirection(PathList, testMap);

		// assess
		assertThat(resultOfTest, is(equalTo(DirList.equals(resultList))));
	}

	// negative test
	@Test
	void givenTwoCoordinatesWhichAreNoNeighbors_TryToConvertThePathToEDirection_ExpectNoNeighborException() {
		// arrange

		List<Coordinate> evilCoordinateList = new ArrayList<>();
		evilCoordinateList.add(new Coordinate(0, 0));
		evilCoordinateList.add(new Coordinate(1, 2));

		// act
		Executable noNeighborsCode = () -> {
			PositionalConvert.convertPathToDirection(evilCoordinateList, testMap);
		};

		// assess
		Assertions.assertThrows(NotNeighborException.class, noNeighborsCode);

	}

}
