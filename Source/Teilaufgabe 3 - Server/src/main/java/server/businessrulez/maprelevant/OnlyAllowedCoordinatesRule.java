package server.businessrulez.maprelevant;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.businessrulez.IRule;
import server.exceptions.rules.MapValidationException;
import server.gamedata.GameStatus;

public class OnlyAllowedCoordinatesRule implements IRule {

	private static final int X_HALFMAP_MAX = 10;
	private static final int Y_HALFMAP_MAX = 5;
	private static final int HALFMAP_SIZE = X_HALFMAP_MAX * Y_HALFMAP_MAX;

	/**
	 * This Implementation of the IRules checks if the submitted halfmap 1st) is the
	 * right size, 2nd) if Coordinate Tiles are redundant and 3rd) if the
	 * coordinates have invalid integers in them
	 */
	@Override
	public void validateSubmittedMap(final Map<String, GameStatus> gameHistory, final String gameID,
			final PlayerHalfMap halfMap) {

		Collection<PlayerHalfMapNode> submittionMap = halfMap.getMapNodes();

		if (submittionMap.size() != HALFMAP_SIZE)
			throw new MapValidationException((halfMap.getMapNodes().size() != HALFMAP_SIZE)
					? "Submitted Map has not enough map nodes! Needs " + HALFMAP_SIZE
					: "Submitted Map has too much map nodes! Needs " + HALFMAP_SIZE);

		Set<PlayerHalfMapNode> knownTiles = new HashSet<PlayerHalfMapNode>();
		for (PlayerHalfMapNode currentNode : submittionMap) {
			if (knownTiles.contains(currentNode))
				throw new MapValidationException(
						"Redundant Coordinate(" + currentNode.getX() + "/" + currentNode.getY() + ") detected!");
			else if (currentNode.getX() > X_HALFMAP_MAX && currentNode.getY() > Y_HALFMAP_MAX && currentNode.getX() < 0
					&& currentNode.getY() < 0)
				throw new MapValidationException("Out Of Bounds! Invalid Coordinate(" + currentNode.getX() + "/"
						+ currentNode.getY() + ") found");
			else
				knownTiles.add(currentNode);

		}
	}
}
