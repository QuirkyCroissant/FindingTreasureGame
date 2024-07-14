package server.servermap;

public enum ETerrainType {
	// values:
	Water(666), Grass(1), Mountain(2);

	private final Integer terraincost;

	private ETerrainType(final Integer cost) {
		this.terraincost = cost;
	}

	public int getTerrainCost() {
		return terraincost;
	}
}
