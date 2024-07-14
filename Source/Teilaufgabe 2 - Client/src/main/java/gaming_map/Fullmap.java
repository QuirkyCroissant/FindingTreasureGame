package gaming_map;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Fullmap implements GameMap {

	private Map<Coordinate, Field> wholemap = new HashMap<>();
	private Coordinate homeCastle;
	private Coordinate treasurePosition;
	private Coordinate castlePosition;
	private Map<String, Coordinate> playerPos = new HashMap<>();
	private boolean isVerticallyJoined = false;
	private boolean foundTreasure = false;
	private boolean foundCastle = false;

	public Fullmap(Map<Coordinate, Field> map, Coordinate home, Coordinate treasure, Coordinate castle,
			Map<String, Coordinate> pPos, boolean verticalJoined, boolean boolTreasure, boolean boolCastle) {
		this.wholemap = map;
		this.homeCastle = home;
		this.playerPos = pPos;
		this.castlePosition = castle;
		this.treasurePosition = treasure;
		this.isVerticallyJoined = verticalJoined;
		this.foundTreasure = boolTreasure;
		this.foundCastle = boolCastle;

	}

	public Fullmap(Map<Coordinate, Field> map, Coordinate treasure, Coordinate castle, Map<String, Coordinate> pPos,
			boolean verticalJoined) {
		this.wholemap = map;
		this.playerPos = pPos;
		this.castlePosition = castle;
		this.treasurePosition = treasure;
		this.isVerticallyJoined = verticalJoined;

	}

	public Fullmap(Map<Coordinate, Field> map, Coordinate castle, Map<String, Coordinate> pPos) {
		this.wholemap = map;
		this.playerPos = pPos;
		this.castlePosition = castle;
		this.treasurePosition = null;
		this.isVerticallyJoined = false;

	}

	public Fullmap(Fullmap validQuadraticFullmap) {
		this(new HashMap<>(validQuadraticFullmap.getWholemap()), new Coordinate(validQuadraticFullmap.getHomeCastle()),
				validQuadraticFullmap.getPlayerPos());
	}

	public Map<Coordinate, Field> getWholemap() {
		return wholemap;
	}

	public void setWholemap(Map<Coordinate, Field> wholemap) {
		this.wholemap = wholemap;
	}

	public Coordinate getHomeCastle() {
		return homeCastle;
	}

	public void setHomeCastle(Coordinate homeCastle) {
		this.homeCastle = homeCastle;
	}

	public Coordinate getFoundTreasure() {
		return treasurePosition;
	}

	public void setFoundTreasure(Coordinate foundTreasure) {
		this.treasurePosition = foundTreasure;
	}

	public Coordinate getFoundCastle() {
		return castlePosition;
	}

	public void setFoundCastle(Coordinate foundCastle) {
		this.castlePosition = foundCastle;
	}

	public Map<String, Coordinate> getPlayerPos() {
		return playerPos;
	}

	public Coordinate getAvatarCoordinates() {

		Coordinate res = new Coordinate(666, 666);

		for (var elem : this.playerPos.entrySet()) {
			if (!elem.getKey().equals("other-player"))
				res = elem.getValue();
		}

		return res;
	}

	public void setPlayerPos(Map<String, Coordinate> playerPos) {
		this.playerPos = playerPos;
	}

	public boolean isVerticallyJoined() {
		return isVerticallyJoined;
	}

	public void setVerticallyJoined(boolean isVerticallyJoined) {
		this.isVerticallyJoined = isVerticallyJoined;
	}

	public boolean isFoundTreasure() {
		return this.foundTreasure;
	}

	public boolean isFoundCastle() {
		return this.foundCastle;
	}

	public void setFoundCastle(boolean foundCastle) {
		this.foundCastle = foundCastle;
	}

	public void setFoundTreasure(boolean foundTreasure) {
		this.foundTreasure = foundTreasure;
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
					&& !this.wholemap.get(potentialHorizontalNeighbor).getTiletype().equals(ETerrain.WATER))
				neighborSet.add(potentialHorizontalNeighbor);
		}

		for (int i = -1; i <= 1; i = i + 2) {
			Coordinate potentialVerticalNeighbor = new Coordinate(center.getX(), center.getY() + i);

			if (this.wholemap.containsKey(potentialVerticalNeighbor)
					&& !this.wholemap.get(potentialVerticalNeighbor).getTiletype().equals(ETerrain.WATER)) {
				neighborSet.add(potentialVerticalNeighbor);
			}
		}

		return neighborSet;
	}

	// 360degrees adjacent fields of a mountain field gets their coordinates and
	// returns them as a Set
	public Collection<Coordinate> getValleyNeighbors(Coordinate centerPeak) {
		Set<Coordinate> neighborSet = new HashSet<>();

		// checks if the adjacent fields are valid neighbor tiles of the Fullmap and
		// adds them to the Set
		for (int i = -1; i < 2; i++) {
			for (int y = -1; y < 2; y++) {
				// ignores mountain
				if (i == 0 && y == 0)
					continue;

				Coordinate currentValley = new Coordinate(centerPeak.getX() + i, centerPeak.getY() + y);

				if (this.wholemap.containsKey(currentValley)
						&& !this.wholemap.get(currentValley).getTiletype().equals(ETerrain.WATER))
					neighborSet.add(currentValley);
			}
		}

		return neighborSet;
	}

	@Override
	public String toString() {

		StringBuilder res = new StringBuilder();

		int xLimit = 0;
		int yLimit = 0;
		if (this.isVerticallyJoined) {
			xLimit = 20;
			yLimit = 5;
		} else {
			xLimit = 10;
			yLimit = 10;
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
				Field curField = this.wholemap.get(searchCoords);
				ESpecial curSpecial = curField.getTileinfo();
				ETerrain curTerrain = curField.getTiletype();

				if (curSpecial.equals(ESpecial.BASE_CASTLE_IS_HERE) && !searchCoords.equals(myplayerPos)) {
					res.append("\u001B[51;101m|C>\u001B[0m");
				} else if (searchCoords.equals(myplayerPos)) {
					res.append("\u001B[0;105m P \u001B[0;0m");
				} else if (searchCoords.equals(enemyPos)) {
					res.append("\u001B[0;100m E \u001B[0;0m");
				} else {

					switch (curTerrain) {
					case GRASS:
						res.append("\u001B[51;102m,,,\u001B[0m");
						break;
					case MOUNTAIN:
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
		return Objects.hash(castlePosition, foundCastle, foundTreasure, homeCastle, isVerticallyJoined, playerPos,
				treasurePosition, wholemap);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Fullmap other = (Fullmap) obj;
		return Objects.equals(castlePosition, other.castlePosition) && foundCastle == other.foundCastle
				&& foundTreasure == other.foundTreasure && Objects.equals(homeCastle, other.homeCastle)
				&& isVerticallyJoined == other.isVerticallyJoined && Objects.equals(playerPos, other.playerPos)
				&& Objects.equals(treasurePosition, other.treasurePosition) && Objects.equals(wholemap, other.wholemap);
	}

}
