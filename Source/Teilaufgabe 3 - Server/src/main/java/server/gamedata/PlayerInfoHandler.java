package server.gamedata;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerInfoHandler {

	Map<String, PlayerInfo> playerDatabase;
	private final Logger logger = LoggerFactory.getLogger(PlayerInfoHandler.class);

	public PlayerInfoHandler() {
		this.playerDatabase = new HashMap<>();
	}

	public void addPlayer(PlayerInfo player) {
		this.playerDatabase.put(player.getPlayerId(), player);
	}

	public Map<String, PlayerInfo> getPlayerbase() {
		return this.playerDatabase;
	}

	public PlayerInfo getSpecificPlayer(String id) {
		return this.playerDatabase.get(id);
	}

	public boolean isPlayerInDatabase(String id) {
		return this.playerDatabase.containsKey(id);
	}

	public String getPlayerThatHasToAct() {

		for (Entry<String, PlayerInfo> entry : this.playerDatabase.entrySet()) {
			if (entry.getValue().getActFlag().equals(EPlayerStatus.MustAct))
				return entry.getKey();
		}

		return "game completed";
	}

	/**
	 * Method that returns how many HalfMaps were already successfull submitted in
	 * the game at the current moment
	 */
	public long getAmountOfSubmittedHalfmaps() {

		return this.playerDatabase.values().stream().filter((PlayerInfo::hasAlreadyDeliveredMap)).count();
	}

	/**
	 * inverts the MUSTWAIT/-ACT cycle of the player entries
	 */
	public void alternatePlayerTurn() {
		this.playerDatabase.forEach((key, player) -> player.setActFlag(
				player.getActFlag() == EPlayerStatus.MustAct ? EPlayerStatus.MustWait : EPlayerStatus.MustAct));

		/*
		 * int cnt = 1; for (Entry<String, PlayerInfo> entry :
		 * this.playerDatabase.entrySet()) logger.info("Player{} {}: {}!", cnt++,
		 * entry.getKey(), entry.getValue().getActFlag().toString());
		 */

	}

	/**
	 * Method that takes the id of a player and a boolean that indicates whether the
	 * provided player is the one that won or lost the game, the remaining player
	 * subsequently gets the opposite placement for the given game
	 * 
	 * @param playerID       player identification string that is chosen to be the
	 *                       winner
	 * @param placementFocus true... Winner; false... Loser
	 * 
	 */
	public void settleGameScore(String playerID, boolean placementFocus) {

		EPlayerStatus focusFlag = placementFocus ? EPlayerStatus.Won : EPlayerStatus.Lost;
		EPlayerStatus inverseFlag = placementFocus ? EPlayerStatus.Lost : EPlayerStatus.Won;

		this.playerDatabase.forEach((key, player) -> {
			if (key.equals(playerID))
				player.setActFlag(focusFlag);
			else
				player.setActFlag(inverseFlag);
		});

		for (Entry<String, PlayerInfo> entry : this.playerDatabase.entrySet())
			logger.info("Player \"{}\": {}!", entry.getKey(), entry.getValue().getActFlag().toString());

	}

	@Override
	public String toString() {

		StringBuilder res = new StringBuilder();

		for (Entry<String, PlayerInfo> entry : this.playerDatabase.entrySet()) {
			res.append(entry.getKey() + ": " + entry.getValue().getActFlag().toString() + "; ");
		}
		return "PlayerInfoHandler [=" + res.toString() + "]";
	}

}
