package gaming_map;

public enum ETerrain {
	// values:
	WATER(666), GRASS(1), MOUNTAIN(2);

	private final Integer terraincost;

	private ETerrain(final Integer cost) {
		this.terraincost = cost;
	}

	public int getTerrainCost() {
		return terraincost;
	}

}
