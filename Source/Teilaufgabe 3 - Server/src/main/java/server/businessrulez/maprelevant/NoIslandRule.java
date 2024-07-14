package server.businessrulez.maprelevant;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import server.businessrulez.IRule;
import server.exceptions.rules.MapValidationException;
import server.gamedata.GameStatus;
import server.servermap.Coordinate;

public class NoIslandRule implements IRule {

	/**
	 * This implementation of IRule function checks if there are any islands on the
	 * submitted halfmap(which is not allowed). First get all non-water tiles and
	 * deletes every entry in a recursive loop, if set is not empty in the end than
	 * we have a rule violation
	 */
	@Override
	public void validateSubmittedMap(final Map<String, GameStatus> gameHistory, final String gameID,
			final PlayerHalfMap halfMap) {

		Set<Coordinate> accessibleTiles = halfMap.getMapNodes().stream()
				.filter(entry -> !entry.getTerrain().equals(ETerrain.Water))
				.map(entry -> new Coordinate(entry.getX(), entry.getY())).collect(Collectors.toSet());

		Coordinate initialStartPoint = accessibleTiles.stream().findFirst().orElseThrow();
		deletionWithFloodFill(initialStartPoint, accessibleTiles);
		if (!accessibleTiles.isEmpty())
			throw new MapValidationException("At least one Island has been detected on submitted HalfMap!");
	}

	/**
	 * after deleting the coordinate form the Set, if successful it recursively
	 * checks the deleted coordinates neighbors, if unsuccessful it skips checking
	 * its neighbors
	 * 
	 * @param currentTile  current Coordinate that gets deleted out of Map
	 * @param tilesStorage Set that hold all the Coordinates
	 */
	private void deletionWithFloodFill(Coordinate currentTile, Set<Coordinate> tilesStorage) {
		if (tilesStorage.remove(currentTile)) {
			deletionWithFloodFill(new Coordinate(currentTile.getX() + 1, currentTile.getY()), tilesStorage);
			deletionWithFloodFill(new Coordinate(currentTile.getX() - 1, currentTile.getY()), tilesStorage);
			deletionWithFloodFill(new Coordinate(currentTile.getX(), currentTile.getY() + 1), tilesStorage);
			deletionWithFloodFill(new Coordinate(currentTile.getX(), currentTile.getY() - 1), tilesStorage);
		}
	}
}
