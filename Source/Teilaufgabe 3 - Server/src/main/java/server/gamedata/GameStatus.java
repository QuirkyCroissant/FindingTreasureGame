package server.gamedata;

import java.util.UUID;

public class GameStatus {

	private final String gameID;
	// Map-Handler(has methods for Map and saves the map)
	private ServerMapHandler mapData;
	// player-handler (holds players and manages them)
	private PlayerInfoHandler managePlayers;
	// GameState ID UUID
	private String stateID;
	private static final int ONE = 1;
	private int gameRound = ONE;

	public GameStatus(String gameID) {
		this.gameID = gameID;
		this.stateID = UUID.randomUUID().toString();
		this.mapData = new ServerMapHandler();
		this.managePlayers = new PlayerInfoHandler();
	}

	// adds players if capacity is not yet reached
	public void increasePlayerSize(PlayerInfo newPotentialPlayer) {
		managePlayers.addPlayer(newPotentialPlayer);

	}

	public String getGameID() {
		return this.gameID;
	}

	public String getStateID() {
		return stateID;
	}

	public void updateStateID() {
		this.stateID = UUID.randomUUID().toString();
	}

	public PlayerInfoHandler getManagePlayers() {
		return this.managePlayers;
	}

	public ServerMapHandler getMapData() {
		return this.mapData;
	}

	public boolean isGameOngoing() {
		return !this.managePlayers.getPlayerThatHasToAct().equals("game completed")
				&& this.managePlayers.getPlayerbase().size() == 2;
	}

	public boolean areEnemiesStillUnderCover() {
		return this.gameRound < 16;
	}
}
