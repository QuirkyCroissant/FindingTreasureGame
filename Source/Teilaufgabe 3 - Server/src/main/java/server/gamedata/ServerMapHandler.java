package server.gamedata;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.converters.MapFusion;
import server.exceptions.MapFusionException;
import server.servermap.Coordinate;
import server.servermap.ETerrainType;
import server.servermap.ServerMap;

public class ServerMapHandler {

	private static final Logger logger = LoggerFactory.getLogger(ServerMapHandler.class);

	// playerId maps to his/hers map
	Map<String, ServerMap> serverMapDatabase;
	MapFusion mapIntertwiner = new MapFusion();
	Random rand = new Random();

	public ServerMapHandler() {
		this.serverMapDatabase = new HashMap<>();
	}

	public void addPlayersServerMap(String playerID, ServerMap map) {
		this.serverMapDatabase.put(playerID, map);
	}

	public Map<String, ServerMap> getServerMaps() {
		return this.serverMapDatabase;
	}

	/**
	 * Main Entry-Point function which is called AFTER both players submitted their
	 * halfmaps. Function calls the MapFusion-Converter to transform both halfs into
	 * one bigger global map; then it updates the serverMapDatabase of the
	 * ServerMapHandler to safe the changes.
	 */
	public void combineHalfMaps() {

		if (this.serverMapDatabase.size() != 2)
			throw new MapFusionException("Name: ServerMapHandler",
					"Message: Unsufficient Map Submittions. Current safed maps: " + this.serverMapDatabase.size());

		// gets two entries
		Entry<String, ServerMap> firstEntry = null;
		Entry<String, ServerMap> lastEntry = null;

		boolean first = true;
		for (Entry<String, ServerMap> elem : this.getServerMaps().entrySet()) {
			if (first) {
				first = false;
				firstEntry = elem;
			} else {
				lastEntry = elem;
			}
		}

		// find the correct new halfs and overwrite this.serverMapDatabase accordingly
		updateMapDatabaseWithCombinedMap(mapIntertwiner.initiateMapFusion(firstEntry, lastEntry));
	}

	/**
	 * after 2 halfmaps where transformed into one Fullmap, this method updates the
	 * old ServerMap Association to each player correctly, by looking at the home
	 * castle coordinates
	 */
	private void updateMapDatabaseWithCombinedMap(Map<String, ServerMap> fullmap) {
		this.serverMapDatabase = fullmap;
		logger.trace("Fullmap successfully updated!");
	}

	/**
	 * Method which modifies fullmap of a specific player and masquerades the enemy,
	 * by assigning him a random tile on the map.
	 * 
	 * @param mapNeedsAltering ServerMap that endures modification
	 * @param pov              point of view... defines from which player the map
	 *                         should be ajusted to
	 * @return ServerMap which has a dummy Enemy player position
	 * 
	 */
	public ServerMap buildFogOfWarMap(ServerMap mapNeedsAltering, String pov) {

		ServerMap modifiedMapData = new ServerMap(mapNeedsAltering);
		Coordinate[] potentialEnemyPositions;
		potentialEnemyPositions = modifiedMapData.getServerMap().keySet().stream()
				.filter(coord -> !modifiedMapData.getServerMap().get(coord).getTiletype().equals(ETerrainType.Water))
				.toArray(Coordinate[]::new);

		Coordinate camouflageEnemy = potentialEnemyPositions[rand.nextInt(potentialEnemyPositions.length)];

		// if enemy entry still does not exist we have to add an entry
		Optional<Coordinate> enemyOldLocation = Optional.ofNullable(modifiedMapData.getPlayerPos().get("other-player"));
		if (!enemyOldLocation.isPresent()) {
			modifiedMapData.getPlayerPos().put("other-player", camouflageEnemy);
		} else {
			modifiedMapData.getPlayerPos().replace("other-player", camouflageEnemy);
		}
		logger.info("Assigned fake Enemy Position for player\"{}\", on :{}", pov, camouflageEnemy);

		return modifiedMapData;
	}

}
