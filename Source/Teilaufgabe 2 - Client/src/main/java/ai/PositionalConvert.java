package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import gaming_map.Coordinate;
import gaming_map.Fullmap;

public class PositionalConvert {
	public static List<EDirection> convertPathToDirection(List<Coordinate> path, Fullmap mapdata) {

		List<EDirection> res = new ArrayList<>();

		for (Coordinate cord : path) {

			// we only want a move command from the second last to the last field. last
			// field doesn't need a move,
			// because we are already at our goal.
			if (path.indexOf(cord) == path.size() - 1)
				break;

			Coordinate nextCoord = (path.get(path.indexOf(cord) + 1));
			EDirection edir = getDirectionFormNeighbor(cord, nextCoord, mapdata);
			// we need to send the individual Move command X times to the server, whereas X
			// is the sum of the cost
			// of the enumeration cost value to first leave the current tile and to enter
			// the new tile.
			int moveAmount = mapdata.getWholemap().get(cord).getTiletype().getTerrainCost()
					+ mapdata.getWholemap().get(nextCoord).getTiletype().getTerrainCost();
			int i = 0;
			while (i++ < moveAmount) {
				res.add(edir);
			}
		}

		return res;
	}

	private static EDirection getDirectionFormNeighbor(Coordinate center, Coordinate next, Fullmap map) {

		Optional<EDirection> dir = Optional.empty();
		for (var c : map.getNeighbors(center)) {
			// we want direction from center node to the next one, it has to be one of its
			// neighbors
			if (c.equals(next)) {
				switch (Integer.compare(center.getX(), c.getX())) {
				case 1:
					dir = Optional.of(EDirection.LEFT);
					break;
				case -1:
					dir = Optional.of(EDirection.RIGHT);
					break;
				default:
					switch (Integer.compare(center.getY(), c.getY())) {
					case 1:
						dir = Optional.of(EDirection.UP);
						break;
					case -1:
						dir = Optional.of(EDirection.DOWN);
						break;
					default:
						break;
					}

				}
			}
		}

		if (dir.isEmpty())
			throw new NotNeighborException(center + " and " + next
					+ " are not neighbors and therefore can not be converted to EDirection enumerations!");

		return dir.get();

	}
}
