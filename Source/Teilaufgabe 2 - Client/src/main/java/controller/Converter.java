package controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import gaming_map.Coordinate;
import gaming_map.ESpecial;
import gaming_map.Field;
import gaming_map.Fullmap;
import gaming_map.Halfmap;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;

public class Converter {

	private Converter() {
	}

	public static PlayerHalfMap convertHalfMapToServerMap(String playerId, Halfmap localHalfmap) {

		Set<PlayerHalfMapNode> result = new HashSet<>();

		for (var elem : localHalfmap.getGameField().entrySet()) {

			// Castle bool converter
			boolean bfort = (elem.getValue().getTileinfo().equals(ESpecial.BASE_CASTLE_IS_HERE));

			var i = elem.getValue().getTiletype().ordinal();
			ETerrain terrain = ETerrain.values()[i];

			int x = elem.getKey().getX();
			int y = elem.getKey().getY();

			result.add(new PlayerHalfMapNode(x, y, bfort, terrain));

		}

		return (new PlayerHalfMap(playerId, result));
	}

	public static Fullmap convertServerMapLocally(FullMap servermap, String playerID) {

		HashMap<Coordinate, Field> returnmap = new HashMap<>();
		HashMap<String, Coordinate> playerPos = new HashMap<>();
		Coordinate home = new Coordinate();
		Coordinate treasure = new Coordinate();
		Coordinate castle = new Coordinate();
		int verticalJoined = 0;
		boolean treasureFound = false;
		boolean castleFound = false;

		for (var entry : servermap.getMapNodes()) {

			if (entry.getX() > 9) {
				verticalJoined++;
			}

			Coordinate currCoords = new Coordinate(entry.getX(), entry.getY());

			gaming_map.ETerrain terrain = gaming_map.ETerrain.values()[entry.getTerrain().ordinal()];

			// Enemy Castle i get from the EFortState
			// Treasure position we get from ETreasureState
			ESpecial especial;

			if (entry.getTreasureState().equals(ETreasureState.MyTreasureIsPresent)) {
				especial = ESpecial.TREASURE_IS_HERE;
				treasure = currCoords;
				treasureFound = true;
			} else if (entry.getFortState().equals(EFortState.EnemyFortPresent)) {
				especial = ESpecial.ENEMIES_CASTLE_IS_HERE;
				castle = currCoords;
				castleFound = true;
			} else if (entry.getFortState().equals(EFortState.MyFortPresent)) {
				especial = ESpecial.BASE_CASTLE_IS_HERE;
				home = currCoords;
			} else {
				especial = ESpecial.NONE;
			}

			// Player position i get from the EPlayerPosition enum
			switch (entry.getPlayerPositionState()) {
			case MyPlayerPosition:
				playerPos.put(playerID, currCoords);
				break;
			case EnemyPlayerPosition:
				playerPos.put("other-player", currCoords);
				break;
			case BothPlayerPosition:
				playerPos.put(playerID, currCoords);
				playerPos.put("other-player", currCoords);
				break;
			default:
				break;
			}

			Field curField = new Field(terrain, especial);

			returnmap.put(currCoords, curField);

		}

		boolean vJoined = (verticalJoined > 0);

		return new Fullmap(returnmap, home, treasure, castle, playerPos, vJoined, treasureFound, castleFound);
	}

	public static PlayerState convertGameToIndivPlayerState(GameState state, String playerId) {

		PlayerState res = new PlayerState();
		for (var entry : state.getPlayers()) {
			if (entry.getUniquePlayerID().equals(playerId)) {
				res = entry;
			}
		}
		return res;
	}

}
