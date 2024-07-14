package ai;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gaming_map.Coordinate;
import gaming_map.ETerrain;
import gaming_map.Fullmap;

public class GoalSetting {
	private Coordinate goalCoords;
	private EGoals goaltype;
	private Set<Coordinate> visitedNotes = new HashSet<>();

	private static final Logger goalSettingLogger = LoggerFactory.getLogger(GoalSetting.class);

	// variables which set the boundaries of own halfmap and enemy half map
	private int upperBound = Integer.MAX_VALUE;
	private int lowerBound = Integer.MIN_VALUE;

	public GoalSetting(Coordinate cords, EGoals goaltype) {
		this.goalCoords = cords;
		this.goaltype = goaltype;
	}

	public GoalSetting() {
		this.goaltype = EGoals.TREASURE;
	}

	public Coordinate getCoords() {
		return goalCoords;
	}

	public void setCords(Coordinate cords) {
		this.goalCoords = cords;
	}

	public EGoals getGoaltype() {
		return goaltype;
	}

	public void setGoaltype(EGoals goaltype) {
		this.goaltype = goaltype;
	}

	// adds tiles that potentially are already visited
	public void addVisitedTiles(List<Coordinate> pathList) {
		this.visitedNotes.addAll(pathList);
	}

	public Set<Coordinate> getVisitedNotes() {
		return this.visitedNotes;
	}

	// Alters goal to treasure or castle location. if treasure is found change goal
	// to
	// castle so that the calculated path should get computed into the enemy castle
	// direction
	public boolean evaluateGoalSetting(Fullmap map) {

		if (map.isFoundTreasure() && !this.goaltype.equals(EGoals.CASTLE)) {
			this.goalCoords = map.getFoundTreasure();
			this.setGoaltype(EGoals.CASTLE);

			goalSettingLogger.debug("Treasure has been discovered on position: {}", this.goalCoords);

			return true;
		}

		if (map.isFoundCastle() && this.goaltype.equals(EGoals.CASTLE)) {
			this.goalCoords = map.getFoundCastle();
			goalSettingLogger.debug("Enemies Castle has been discovered on position: {}", this.goalCoords);

			return true;
		}

		return false;
	}

	public void reconNewTiles(Fullmap map) {

		Random generator = new Random();
		// refreshes or alters bounds to later filter our tiles of interest
		// if map.isVertical() == true; then our boundary variables are the
		// representative boundary for x and vice versa y if its false
		refreshBoundaries(map);
		Coordinate[] values;

		// use streams to filter out own half/enemy half notes determined by the goal
		// enum type to create the array of eglitable coordinates
		if (map.isVerticallyJoined()) {
			values = map.getWholemap().keySet().stream()
					.filter(coord -> coord.getX() < this.upperBound && coord.getX() >= this.lowerBound
							&& map.getWholemap().get(coord).getTiletype().equals(ETerrain.GRASS))
					.toArray(Coordinate[]::new);
		} else {
			values = map.getWholemap().keySet().stream()
					.filter(coord -> coord.getY() < this.upperBound && coord.getY() >= this.lowerBound
							&& map.getWholemap().get(coord).getTiletype().equals(ETerrain.GRASS))
					.toArray(Coordinate[]::new);

		}

		while (true) {
			Coordinate randomTile = values[generator.nextInt(values.length)];

			if (!this.visitedNotes.contains(randomTile) && !map.getAvatarCoordinates().equals(randomTile)) {

				this.setCords(randomTile);
				goalSettingLogger.info("Avatar wants to go to Point: {}", randomTile);
				goalSettingLogger.info("Avatar currently searches for the {}", this.goaltype.name());
				return;
			}

		}

	}

	private void refreshBoundaries(Fullmap mapinfo) {

		Coordinate homeCastlePos = mapinfo.getHomeCastle();

		if (mapinfo.isVerticallyJoined()) { // fullmap is a wide map
			if (homeCastlePos.getX() < 10) { // LEFT-SIDE -> HOMECASTLE

				// inverts bounds if the goal type changes
				switch (this.goaltype) {
				case TREASURE:
					this.upperBound = 10;
					this.lowerBound = 0;
					break;
				default:
					this.upperBound = 20;
					this.lowerBound = 10;
				}
			} else { // RIGHT-SIDE -> HOMECASTLE

				// inverts bounds if the goal type changes
				switch (this.goaltype) {
				case TREASURE:
					this.upperBound = 20;
					this.lowerBound = 10;
					break;
				default:
					this.upperBound = 10;
					this.lowerBound = 0;
				}

			}

		} else { // Fullmap is a square!

			if (homeCastlePos.getY() < 5) { // TOP-SIDE -> HOMECASTLE
				// inverts bounds if the goal type changes
				switch (this.goaltype) {
				case TREASURE:
					this.upperBound = 5;
					this.lowerBound = 0;
					break;
				default:
					this.upperBound = 10;
					this.lowerBound = 5;
				}

			} else { // BOTTOM-SIDE -> HOMECASTLE

				// inverts bounds if the goal type changes
				switch (this.goaltype) {
				case TREASURE:
					this.upperBound = 10;
					this.lowerBound = 5;
					break;
				default:
					this.upperBound = 5;
					this.lowerBound = 0;
				}

			}

		}

	}

}
