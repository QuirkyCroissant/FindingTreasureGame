package ai;

import game_state.GameStatus;
import gaming_map.Coordinate;

public interface Pathfinder {
	public EDirection findBestPath(Coordinate start, GameStatus map);
}
