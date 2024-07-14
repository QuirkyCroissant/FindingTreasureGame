package server.converters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.servermap.Coordinate;
import server.servermap.ESpecial;
import server.servermap.ETerrainType;
import server.servermap.MapField;
import server.servermap.ServerMap;

public class MapFusion {

	private static final Logger logger = LoggerFactory.getLogger(MapFusion.class);
	private final Random rand = new Random();

	private static final int X_ELONGATION = 10;
	private static final int Y_ELONGATION = 5;

	private static final int HALFMAP_HORIZONTAL_BORDER = 9;

	public Map<String, ServerMap> initiateMapFusion(Entry<String, ServerMap> client1,
			Entry<String, ServerMap> client2) {

		logger.debug("Begins MapFusion!");
		this.assignTreasurePosition(client1.getValue());
		this.assignTreasurePosition(client2.getValue());

		Map<Coordinate, MapField> newMap = mergeHalfMaps(client1.getValue(), client2.getValue());

		// checks if the newly joined map is wide or quadratic
		boolean verticalJoined = false;
		for (var entry : newMap.entrySet()) {
			if (entry.getKey().getX() > HALFMAP_HORIZONTAL_BORDER) {
				verticalJoined = true;
				break;
			}
		}

		// determines enemy castles for the two submitter
		Coordinate enemyCastleOne = new Coordinate(client2.getValue().getHomeCastle());
		Coordinate enemyCastleTwo = new Coordinate(client1.getValue().getHomeCastle());

		// makes copy of joined map and hides the enemy castle fields
		// both versions
		var mergedMapForClientOne = newMap.entrySet().stream().collect(
				Collectors.toMap(entry -> new Coordinate(entry.getKey()), entry -> new MapField(entry.getValue())));
		mergedMapForClientOne.get(enemyCastleOne).setTileinfo(ESpecial.NONE);

		var mergedMapForClientTwo = newMap.entrySet().stream().collect(
				Collectors.toMap(entry -> new Coordinate(entry.getKey()), entry -> new MapField(entry.getValue())));
		mergedMapForClientTwo.get(enemyCastleTwo).setTileinfo(ESpecial.NONE);

		// good last the 2 versions get mapped to their respective entries that came
		// through the parameters
		Map<String, ServerMap> fusionedMap = new HashMap<>();

		fusionedMap.put(client1.getKey(),
				new ServerMap(mergedMapForClientOne, client1.getValue().getHomeCastle(),
						client1.getValue().getTreasurePosition(), client1.getValue().getEnemyCastle(),
						client1.getValue().getPlayerPos(), verticalJoined, true));
		fusionedMap.put(client2.getKey(),
				new ServerMap(mergedMapForClientTwo, client2.getValue().getHomeCastle(),
						client2.getValue().getTreasurePosition(), client2.getValue().getEnemyCastle(),
						client2.getValue().getPlayerPos(), verticalJoined, true));

		return fusionedMap;
	}

	private void assignTreasurePosition(ServerMap treasurelessMap) {

		// filters out map and returns list of grass entries and no castles
		List<Entry<Coordinate, MapField>> grassList = treasurelessMap.getServerMap().entrySet().stream()
				.filter(entry -> {
					MapField field = entry.getValue();
					return (field.getTiletype().equals(ETerrainType.Grass)
							&& !field.getTileinfo().equals(ESpecial.BASE_CASTLE_IS_HERE));
				}).toList();

		Coordinate treasureSpot = grassList.get(this.rand.nextInt(grassList.size())).getKey();
		logger.debug("Treasure location placed at {}", treasureSpot);
		treasurelessMap.setTreasurePosition(treasureSpot);
	}

	/**
	 * Method takes 2 ServerMap objects and combines their HashMap<Coordinate,
	 * MapField>'s. A random draw decides in what the 2nd parameter gets joined with
	 * the focal point. The map which has to alter its 'Tiles of Interests'(Castle,
	 * Treasure and initial Player Position), will have its instance variables
	 * altered accordingly to.
	 * 
	 * @param map1 focal point.
	 * @param map2 second ServerMap
	 * @return Map<Coordinate, MapField>
	 */
	private Map<Coordinate, MapField> mergeHalfMaps(ServerMap map1, ServerMap map2) {

		Map<Coordinate, MapField> joinedMap;

		// randomly chooses a direction how map2 will be joined to map 1
		EMapConnectionDirection[] connectionsArray = EMapConnectionDirection.values();
		switch (connectionsArray[rand.nextInt(connectionsArray.length)]) {
		case TOP:
			logger.debug("Connecting firstHalfMap with secondHalfMap: {}-side.", EMapConnectionDirection.TOP);

			// generates new Map through old values of the map that doesn't have to alter
			// its data.
			joinedMap = map2.getServerMap().entrySet().stream()
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

			// the other hashmap of the other ServerMap gets appended to the previously
			// declared variable, with altered Coordinates.
			joinedMap.putAll(map1.getServerMap().entrySet().stream()
					.collect(Collectors.toMap(
							entry -> new Coordinate(entry.getKey().getX(), entry.getKey().getY() + Y_ELONGATION),
							Entry::getValue)));

			// special 'Tiles of Interests' have to be updated
			relocateTilesOfInterestsVertically(map1, Y_ELONGATION);

			break;

		case RIGHT:
			logger.debug("Connecting firstHalfMap with secondHalfMap: {}-side.", EMapConnectionDirection.RIGHT);

			// generates new Map through old values of the map that doesn't have to alter
			// its data.
			joinedMap = map1.getServerMap().entrySet().stream()
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

			// the other hashmap of the other ServerMap gets appended to the previously
			// declared variable, with altered Coordinates.
			joinedMap.putAll(map2.getServerMap().entrySet().stream()
					.collect(Collectors.toMap(
							entry -> new Coordinate(entry.getKey().getX() + X_ELONGATION, entry.getKey().getY()),
							Entry::getValue)));

			// special 'Tiles of Interests' have to be updated
			relocateTilesOfInterestsHorizontal(map2, X_ELONGATION);
			break;

		case BOTTOM:
			logger.debug("Connecting firstHalfMap with secondHalfMap: {}-side.", EMapConnectionDirection.BOTTOM);

			// generates new Map through old values of the map that doesn't have to alter
			// its data.
			joinedMap = map1.getServerMap().entrySet().stream()
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

			// the other hashmap of the other ServerMap gets appended to the previously
			// declared variable, with altered Coordinates.
			joinedMap.putAll(map2.getServerMap().entrySet().stream()
					.collect(Collectors.toMap(
							entry -> new Coordinate(entry.getKey().getX(), entry.getKey().getY() + Y_ELONGATION),
							Entry::getValue)));

			// special 'Tiles of Interests' have to be updated
			relocateTilesOfInterestsVertically(map2, Y_ELONGATION);
			break;

		default:
			logger.debug("Connecting firstHalfMap with secondHalfMap: {}-side.", EMapConnectionDirection.LEFT);

			// generates new Map through old values of the map that doesn't have to alter
			// its data.
			joinedMap = map2.getServerMap().entrySet().stream()
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

			// the other hashmap of the other ServerMap gets appended to the previously
			// declared variable, with altered Coordinates.
			joinedMap.putAll(map1.getServerMap().entrySet().stream()
					.collect(Collectors.toMap(
							entry -> new Coordinate(entry.getKey().getX() + X_ELONGATION, entry.getKey().getY()),
							Entry::getValue)));

			// special 'Tiles of Interests' have to be updated
			relocateTilesOfInterestsHorizontal(map1, X_ELONGATION);
			break;

		}

		return joinedMap;
	}

	/**
	 * Method that Alters 'Tiles of Interests'(Castle, Treasure and initial Player
	 * Position) on the y achsis through a integer value
	 * 
	 * @param transformingMap    ServerMap which attributes are to be altered
	 * @param verticalElongation integer value which decides the severity of the
	 *                           relocation to the top
	 */
	private void relocateTilesOfInterestsVertically(ServerMap transformingMap, int verticalElongation) {

		// finds home castle and sets new location
		Coordinate homeCastle = transformingMap.getServerMap().entrySet().stream()
				.filter(entry -> entry.getValue().getTileinfo().equals(ESpecial.BASE_CASTLE_IS_HERE)).findFirst().get()
				.getKey();
		transformingMap.setHomeCastle(new Coordinate(homeCastle.getX(), homeCastle.getY() + verticalElongation));

		// alters Treasure position
		transformingMap.setTreasurePosition(new Coordinate(transformingMap.getTreasurePosition().getX(),
				transformingMap.getTreasurePosition().getY() + Y_ELONGATION));

		// alters player position
		// "other-player" find other player with this string
		transformingMap.getPlayerPos().entrySet().forEach(entry -> {
			if (!entry.getKey().equals("other-player"))
				entry.setValue(new Coordinate(entry.getValue().getX(), entry.getValue().getY() + Y_ELONGATION));
		});
		logger.debug("Relocated Castle, Treasure and PlayerPos by " + Y_ELONGATION + " on vertical axis",
				transformingMap);

	}

	/**
	 * Method that Alters 'Tiles of Interests'(Castle, Treasure and initial Player
	 * Position) on the x achsis through a integer value
	 * 
	 * @param transformingMap      ServerMap which attributes are to be altered
	 * @param horizontalElongation integer value which decides the severity of the
	 *                             relocation to the right
	 */
	private void relocateTilesOfInterestsHorizontal(ServerMap transformingMap, int horizontalElongation) {

		// finds home castle and sets new location
		Coordinate homeCastle = transformingMap.getServerMap().entrySet().stream()
				.filter(entry -> entry.getValue().getTileinfo().equals(ESpecial.BASE_CASTLE_IS_HERE)).findFirst().get()
				.getKey();
		transformingMap.setHomeCastle(new Coordinate(homeCastle.getX() + horizontalElongation, homeCastle.getY()));

		// alters Treasure position
		transformingMap
				.setTreasurePosition(new Coordinate(transformingMap.getTreasurePosition().getX() + horizontalElongation,
						transformingMap.getTreasurePosition().getY()));

		// alters player position
		// "other-player" find other player with this string
		transformingMap.getPlayerPos().entrySet().forEach(entry -> {
			if (!entry.getKey().equals("other-player"))
				entry.setValue(new Coordinate(entry.getValue().getX() + horizontalElongation, entry.getValue().getY()));
		});
		logger.debug("Relocated Castle, Treasure and PlayerPos by {} on horizontal axis {}", horizontalElongation,
				transformingMap);

	}

}
