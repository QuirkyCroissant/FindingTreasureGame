package game_state;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import gaming_map.Fullmap;
import player.Player;

public class GameStatus {

	private String gameId;
	private String gameStateNr;
	private GameFlags clientflag = GameFlags.SCOUT_TREASURE;
	private ClientAction actionInfo;
	private Fullmap matchMap;
	private Player hostplayer;
	private Player enemy;

	// you can create multiple PropertyChangeSupport instances to enable the
	// individual monitoring of individual properties
	private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

	public GameStatus(String gameId) {

		this.gameId = gameId;
		this.hostplayer = new Player("Florian", "Hajek", "hajekf96");
	}

	public GameStatus(String gameId, String gameStateNr, ClientAction actionInfo, Player hostplayer) {

		this.gameId = gameId;
		this.gameStateNr = gameStateNr;
		this.actionInfo = actionInfo;
		this.hostplayer = hostplayer;

	}

	public GameStatus(String gameId, String gameStateNr, GameFlags clientflag, ClientAction actionInfo,
			Fullmap matchMap, Player hostplayer) {

		this.gameId = gameId;
		this.gameStateNr = gameStateNr;
		this.clientflag = clientflag;
		this.actionInfo = actionInfo;
		this.matchMap = matchMap;
		this.hostplayer = hostplayer;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getGameStateNr() {
		return gameStateNr;
	}

	public void setGameStateNr(String gameStateNr) {
		this.gameStateNr = gameStateNr;
	}

	public GameFlags getClientflag() {
		return clientflag;
	}

	public void setClientflag(GameFlags clientflag) {
		this.clientflag = clientflag;
	}

	public ClientAction getActionInfo() {
		return actionInfo;
	}

	public void setActionInfo(ClientAction actionInfo) {
		this.actionInfo = actionInfo;
	}

	public Fullmap getMatchMap() {
		return matchMap;
	}

	public void setMatchMap(Fullmap newMap) {
		Fullmap oldMap = this.matchMap;
		this.matchMap = newMap;

		// inform all interested parties about changes
		changes.firePropertyChange("matchMap", this.matchMap, oldMap);
	}

	public Player getHostplayer() {
		return hostplayer;
	}

	public void setHostplayer(Player hostplayer) {
		this.hostplayer = hostplayer;
	}

	public Player getEnemy() {
		return enemy;
	}

	public void setEnemy(Player enemy) {
		this.enemy = enemy;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// enables to register new listeners
		changes.addPropertyChangeListener(listener);
	}

}
