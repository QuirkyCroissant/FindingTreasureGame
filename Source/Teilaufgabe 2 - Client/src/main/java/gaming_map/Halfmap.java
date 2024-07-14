package gaming_map;

import java.util.HashMap;
import java.util.Map;

public class Halfmap implements GameMap {

	private Map<Coordinate, Field> gameField = new HashMap<>();
	private Coordinate baseCastle;

	public Halfmap() {

		this.gameField = (new MapGenerator()).getBuiltMap();

		for (var entry : gameField.entrySet()) {
			if (entry.getValue().getTileinfo().equals(ESpecial.BASE_CASTLE_IS_HERE))
				this.baseCastle = entry.getKey();
		}
	}

	public Halfmap(Map<Coordinate, Field> inputmap, Coordinate base) {
		this.gameField = inputmap;
		this.baseCastle = base;
	}

	public Halfmap(Halfmap halfmap) {
		this(new HashMap<>(halfmap.getGameField()), new Coordinate(halfmap.getBaseCastle()));
	}

	public Map<Coordinate, Field> getGameField() {
		return gameField;
	}

	public void setGameField(Map<Coordinate, Field> halfmap) {
		this.gameField = halfmap;
	}

	public Coordinate getBaseCastle() {
		return baseCastle;
	}

	public void setBaseCastle(Coordinate baseCastle) {
		this.baseCastle = baseCastle;
	}

}
