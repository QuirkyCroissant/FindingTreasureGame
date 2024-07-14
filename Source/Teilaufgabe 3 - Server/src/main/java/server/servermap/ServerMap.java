package server.servermap;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ServerMap {

	private Map<Coordinate, MapField> wholemap = new HashMap<>();
	private Coordinate homeCastle;
	private Coordinate treasurePosition;
	private Coordinate castlePosition;
	private Map<String, Coordinate> playerPos = new HashMap<>();

	// if size is that of a fullmap than boolean will be true
	private boolean completeMap;

	private boolean isVerticallyJoined = false;

	// halfmap constructor
	public ServerMap(Map<Coordinate, MapField> map, Coordinate home, Map<String, Coordinate> playerPos) {
		this.wholemap = map;
		this.homeCastle = home;
		this.castlePosition = new Coordinate(9, 4);
		this.playerPos = playerPos;
		this.completeMap = false;
	}

	// fullmap constructor
	public ServerMap(Map<Coordinate, MapField> map, Coordinate home, Coordinate treasure, Coordinate castle,
			Map<String, Coordinate> pPos, boolean verticalJoined, boolean complete) {
		this.wholemap = map;
		this.homeCastle = home;
		this.playerPos = pPos;
		this.castlePosition = castle;
		this.treasurePosition = treasure;
		this.isVerticallyJoined = verticalJoined;
		this.completeMap = complete;

	}

	// Deep Copy Constructor
	public ServerMap(ServerMap otherMap) {
		// first stream copies hashmap, second parameter copies coordinate
		this(otherMap.getServerMap().entrySet().stream()
				.collect(Collectors.toMap(entry -> new Coordinate(entry.getKey().getX(), entry.getKey().getY()),
						entry -> new MapField(entry.getValue()))),
				new Coordinate(otherMap.getHomeCastle()), new Coordinate(otherMap.getTreasurePosition()),
				new Coordinate(otherMap.getEnemyCastle()),
				otherMap.getPlayerPos().entrySet().stream()
						.collect(Collectors.toMap(Entry::getKey, entry -> new Coordinate(entry.getValue()))),
				otherMap.isVerticallyJoined, otherMap.isMapComplete());
	}

	public Map<Coordinate, MapField> getServerMap() {
		return wholemap;
	}

	public void setServerMap(Map<Coordinate, MapField> wholemap) {
		this.wholemap = wholemap;
	}

	public Coordinate getHomeCastle() {
		return homeCastle;
	}

	public void setHomeCastle(Coordinate homeCastle) {
		this.homeCastle = homeCastle;
	}

	public Coordinate getTreasurePosition() {
		return treasurePosition;
	}

	public void setTreasurePosition(Coordinate treasurePosition) {
		this.treasurePosition = treasurePosition;
	}

	public Coordinate getEnemyCastle() {
		return castlePosition;
	}

	public void setEnemyCastle(Coordinate castlePosition) {
		this.castlePosition = castlePosition;
	}

	public Map<String, Coordinate> getPlayerPos() {
		return playerPos;
	}

	public void setPlayerPos(Map<String, Coordinate> playerPos) {
		this.playerPos = playerPos;
	}

	public boolean isMapComplete() {
		return completeMap;
	}

	public void setMapComplete(boolean completeMap) {
		this.completeMap = completeMap;
	}

	public boolean isVerticallyJoined() {
		return isVerticallyJoined;
	}

	public void setVerticallyJoined(boolean isVerticallyJoined) {
		this.isVerticallyJoined = isVerticallyJoined;
	}

	// iterates through the coordinates of the gamemap and returns vertical +
	// horizontal adjacent coordinates in a Set if they arent water tiles
	public Collection<Coordinate> getNeighbors(Coordinate center) {
		Set<Coordinate> neighborSet = new HashSet<>();

		// checks if the adjacent fields are valid neighbor tiles of the Fullmap and
		// adds them to the Set
		for (int i = -1; i < 2; i = i + 2) {
			Coordinate potentialHorizontalNeighbor = new Coordinate(center.getX() + i, center.getY());

			if (this.wholemap.containsKey(potentialHorizontalNeighbor)
					&& !this.wholemap.get(potentialHorizontalNeighbor).getTiletype().equals(ETerrainType.Water))
				neighborSet.add(potentialHorizontalNeighbor);
		}

		for (int i = -1; i <= 1; i = i + 2) {
			Coordinate potentialVerticalNeighbor = new Coordinate(center.getX(), center.getY() + i);

			if (this.wholemap.containsKey(potentialVerticalNeighbor)
					&& !this.wholemap.get(potentialVerticalNeighbor).getTiletype().equals(ETerrainType.Water)) {
				neighborSet.add(potentialVerticalNeighbor);
			}
		}

		return neighborSet;
	}

	@Override
	public String toString() {

		StringBuilder res = new StringBuilder();

		int xLimit = 0;
		int yLimit = 0;

		if (this.completeMap) {

			if (this.isVerticallyJoined) {
				xLimit = 20;
				yLimit = 5;
			} else {
				xLimit = 10;
				yLimit = 10;
			}
		} else {
			xLimit = 9;
			yLimit = 4;
		}
		Coordinate myplayerPos = new Coordinate(0, 0);
		Coordinate enemyPos = new Coordinate(0, 0);

		for (var entry : playerPos.entrySet()) {
			if (!entry.getKey().equals("other-player")) {
				myplayerPos = entry.getValue();
			} else {
				enemyPos = entry.getValue();
			}

		}

		for (int y = 0; y < yLimit; y++) {
			for (int x = 0; x < xLimit; x++) {
				Coordinate searchCoords = new Coordinate(x, y);
				MapField curField = this.wholemap.get(searchCoords);
				ESpecial curSpecial = curField.getTileinfo();
				ETerrainType curTerrain = curField.getTiletype();

				if (curSpecial.equals(ESpecial.BASE_CASTLE_IS_HERE) && !searchCoords.equals(myplayerPos)) {
					res.append("\u001B[51;101m|C>\u001B[0m");
				} else if (searchCoords.equals(myplayerPos)) {
					res.append("\u001B[0;105m P \u001B[0;0m");
				} else if (searchCoords.equals(enemyPos)) {
					res.append("\u001B[0;100m E \u001B[0;0m");
				} else {

					switch (curTerrain) {
					case Grass:
						res.append("\u001B[51;102m,,,\u001B[0m");
						break;
					case Mountain:
						res.append("\u001B[51;215m/^\\\u001B[0m");
						break;
					default:
						res.append("\u001B[51;106m~~~\u001B[0m");
						break;
					}
				}
			}
			res.append("\n");
		}

		return res.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(castlePosition, homeCastle, isVerticallyJoined, playerPos, treasurePosition, wholemap);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerMap other = (ServerMap) obj;
		return Objects.equals(castlePosition, other.castlePosition) && Objects.equals(homeCastle, other.homeCastle)
				&& isVerticallyJoined == other.isVerticallyJoined && Objects.equals(playerPos, other.playerPos)
				&& Objects.equals(treasurePosition, other.treasurePosition) && Objects.equals(wholemap, other.wholemap);
	}

}
