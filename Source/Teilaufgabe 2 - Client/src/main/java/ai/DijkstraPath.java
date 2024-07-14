package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game_state.GameFlags;
import game_state.GameStatus;
import gaming_map.Coordinate;
import gaming_map.ETerrain;
import gaming_map.Fullmap;

public class DijkstraPath implements Pathfinder {

	private static final int VISITED_PENALTY = 2;
	private static final int ZERO = 0;

	private Queue<EDirection> commandQueue = new LinkedList<>();
	private Fullmap naviMap;
	private GoalSetting moveMotivation;

	private static final Logger dijkstraLogger = LoggerFactory.getLogger(DijkstraPath.class);

	public DijkstraPath(Fullmap naviMap, GoalSetting moveMotivation) {
		this.naviMap = naviMap;
		this.moveMotivation = moveMotivation;
	}

	public DijkstraPath() {
	}

	public EDirection findBestPath(Coordinate startPoint, GameStatus newStatus) {

		Fullmap newMap = newStatus.getMatchMap();

		// alter to new map
		setNaviMap(newMap);

		// checks if there where any sudden changes(discovered treasures or castles) and
		// changes the goal coordinates accordingly
		boolean suddenOccurenceHasHappened = checkGoal(newStatus);

		if (suddenOccurenceHasHappened) {

			dijkstraLogger.trace("current goal coordinate is: {}", this.moveMotivation.getCoords());

			List<Coordinate> shortestPathList = calculateShortestPath(startPoint, moveMotivation.getCoords());
			List<EDirection> listOfDirections = PositionalConvert.convertPathToDirection(shortestPathList, naviMap);

			Queue<EDirection> results = new LinkedList<>(listOfDirections);
			this.commandQueue = results;

			return this.commandQueue.poll();

		} else {
			return this.commandQueue.poll();
		}

	}

	public void setNaviMap(Fullmap naviMap) {
		this.naviMap = naviMap;
	}

	/*
	 * Method which checks before every round if something major happened, like our
	 * Avatar found the Treasure, the Enemies Castle or we ran out of
	 * Movement/EDirection commands
	 */
	private boolean checkGoal(GameStatus gs) {

		// Treasure or Castle was found appearing on the map
		if (this.naviMap.isFoundCastle() || this.naviMap.isFoundTreasure()) {
			boolean newGoalDetected = this.moveMotivation.evaluateGoalSetting(this.naviMap);

			if (newGoalDetected) {
				this.discardCurrentPath();
				return true;
			}
			return false;

		} else if (gs.getClientflag().equals(GameFlags.SCOUT_CASTLE)
				&& !this.moveMotivation.getGoaltype().equals(EGoals.CASTLE)) {
			// the treasure appears to already be in our possession

			this.moveMotivation.setGoaltype(EGoals.CASTLE);
			this.moveMotivation.reconNewTiles(this.naviMap);
			return true;

		} else if (this.commandQueue.isEmpty()) {
			this.moveMotivation.reconNewTiles(this.naviMap);
			return true;
		}

		return false;
	}

	public GoalSetting getMoveMotivation() {
		return moveMotivation;
	}

	private List<Coordinate> calculateShortestPath(Coordinate start, Coordinate goal) {

		Map<Coordinate, Integer> distances = new HashMap<>();
		Map<Coordinate, Coordinate> previousNodes = new HashMap<>();
		PriorityQueue<Coordinate> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
		Set<Coordinate> knownMapTiles = this.moveMotivation.getVisitedNotes();

		for (Coordinate cords : this.naviMap.getWholemap().keySet()) {
			distances.put(cords, Integer.MAX_VALUE);
		}

		// Start node has to have zero cost, for dijkstra to work
		distances.put(start, ZERO);
		queue.add(start);

		while (!queue.isEmpty()) {
			Coordinate currentCoordinateOutOfQueue = queue.poll();

			// if we reached the the end/our objective coordinates,
			// in other words our shortest path so we can stop the loop
			if (currentCoordinateOutOfQueue.equals(goal)) {
				break;
			}

			int currDist = distances.get(currentCoordinateOutOfQueue);

			// loop that calculates the costs of all neighbours of the current node
			// alters the hashmaps accordingly
			for (Coordinate currentNeighborTile : this.naviMap.getNeighbors(currentCoordinateOutOfQueue)) {

				int leaveTileCost = this.naviMap.getWholemap().get(currentCoordinateOutOfQueue).getTiletype()
						.getTerrainCost();
				int enterTileCost = this.naviMap.getWholemap().get(currentNeighborTile).getTiletype().getTerrainCost();
				int knownTilePenalty = ZERO;

				if (knownMapTiles.contains(currentNeighborTile)) {
					knownTilePenalty += VISITED_PENALTY;
				}

				int totalEdgesCost = currDist + (enterTileCost + leaveTileCost + knownTilePenalty);

				if (totalEdgesCost < distances.get(currentNeighborTile)) {
					distances.put(currentNeighborTile, totalEdgesCost);
					previousNodes.put(currentNeighborTile, currentCoordinateOutOfQueue);
					queue.add(currentNeighborTile);
				}
			}

		}

		List<Coordinate> pathList = new ArrayList<>();
		Coordinate current = goal;
		while (previousNodes.containsKey(current)) {
			pathList.add(current);
			current = previousNodes.get(current);
		}
		pathList.add(start);
		Collections.reverse(pathList);

		this.savePathTiles(pathList);

		return pathList;

	}

	// saves tiles on the calculated path as "already visited" ones, but also if we
	// have mountains
	// their respective perimeter neighbors
	private void savePathTiles(List<Coordinate> pathList) {

		List<Coordinate> mountNeighbors = new LinkedList<>();

		for (Coordinate coordinate : pathList) {
			if (this.naviMap.getWholemap().get(coordinate).getTiletype().equals(ETerrain.MOUNTAIN)) {
				mountNeighbors.addAll(this.naviMap.getValleyNeighbors(coordinate));
			}
		}

		this.moveMotivation.addVisitedTiles(pathList);
		this.moveMotivation.addVisitedTiles(mountNeighbors);
	}

	public void setCommandQueue(Queue<EDirection> q) {
		this.commandQueue = q;
	}

	public void discardCurrentPath() {
		this.commandQueue.clear();
	}

}
