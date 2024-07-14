package server.converters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import server.exceptions.PlayerConversionException;
import server.gamedata.GameStatus;
import server.gamedata.PlayerInfo;
import server.servermap.Coordinate;
import server.servermap.ESpecial;
import server.servermap.ETerrainType;
import server.servermap.MapField;
import server.servermap.ServerMap;

public class NetConverter {

	public PlayerInfo convertNetworkPlayerToPlayerInfo(String playerID, PlayerRegistration netPlayer) {

		String givenName = netPlayer.getStudentFirstName();
		String lastName = netPlayer.getStudentLastName();
		String uName = netPlayer.getStudentUAccount();
		if (givenName.equals("") || lastName.equals("") || uName.equals(""))
			throw new PlayerConversionException("Name: Player Conversion", "Message: ");

		return new PlayerInfo(playerID, givenName, lastName, uName);

	}

	public ServerMap convertPlayerHalfMapToServerMap(PlayerHalfMap halfmap, String playerID) {

		Map<Coordinate, MapField> tileMapOfServerMap = new HashMap<>();
		Coordinate homeCastle = new Coordinate(Integer.MAX_VALUE, Integer.MAX_VALUE);
		Map<String, Coordinate> playerPos = new HashMap<>();

		for (PlayerHalfMapNode elem : halfmap.getMapNodes()) {

			Coordinate newCoordinateOfNextIteration = new Coordinate(elem.getX(), elem.getY());

			if (elem.isFortPresent()) {
				tileMapOfServerMap.put(newCoordinateOfNextIteration,
						new MapField(ETerrainType.valueOf(elem.getTerrain().toString()), ESpecial.BASE_CASTLE_IS_HERE));
				homeCastle = new Coordinate(elem.getX(), elem.getY());
				playerPos.put(playerID, homeCastle);

			} else
				tileMapOfServerMap.put(newCoordinateOfNextIteration,
						new MapField(ETerrainType.valueOf(elem.getTerrain().toString()), ESpecial.NONE));

		}

		return new ServerMap(tileMapOfServerMap, homeCastle, playerPos);
	}

	public GameState builtGameState(GameStatus gameStatus, String playerID) {

		// GameState()
		List<PlayerState> players = new ArrayList<>();
		for (var entry : gameStatus.getManagePlayers().getPlayerbase().entrySet()) {
			players.add(convertPersonInfoToPlayerState(entry.getValue(), playerID));
		}

		Collection<PlayerState> playerCollection = new ArrayList<>();
		playerCollection.addAll(players);

		Optional<FullMap> convertedFullmap = convertServerToFullMap(gameStatus, playerID);

		if (convertedFullmap.isEmpty())
			return new GameState(playerCollection, gameStatus.getStateID());
		else
			return new GameState(convertedFullmap.get(), players, gameStatus.getGameID());
	}

	public PlayerState convertPersonInfoToPlayerState(PlayerInfo player, String submitterID) {

		String playerID = (player.getPlayerId().equals(submitterID)) ? player.getPlayerId() : "other-player";

		return new PlayerState(player.getFirstName(), player.getLastName(), player.getUaccount(),
				EPlayerGameState.valueOf(player.getActFlag().toString()), new UniquePlayerIdentifier(playerID),
				player.isTreasureInventory());
	}

	private Optional<FullMap> convertServerToFullMap(GameStatus game, String id) {

		Map<String, ServerMap> serverMaps = game.getMapData().getServerMaps();

		Optional<Map.Entry<String, ServerMap>> entryOptional = serverMaps.entrySet().stream()
				.filter(entry -> entry.getKey().equals(id)).findFirst();

		if (entryOptional.isEmpty())
			return Optional.empty();

		ServerMap map;
		if (game.areEnemiesStillUnderCover() && entryOptional.get().getValue().getServerMap().size() == 100)
			map = game.getMapData().buildFogOfWarMap(entryOptional.get().getValue(), id);
		else
			map = entryOptional.get().getValue();

		List<FullMapNode> nodes = new ArrayList<>();
		for (var entry : map.getServerMap().entrySet()) {
			nodes.add(new FullMapNode(ETerrain.valueOf(entry.getValue().getTiletype().toString()),
					convertToNetworkEPlayerPosState(map.getPlayerPos(), entry.getKey()),
					(entry.getValue().getTileinfo().equals(ESpecial.TREASURE_IS_HERE))
							? ETreasureState.MyTreasureIsPresent
							: ETreasureState.NoOrUnknownTreasureState,
					convertToNetworkEFortState(map.getServerMap(), entry.getKey()), entry.getKey().getX(),
					entry.getKey().getY()));
		}

		Collection<FullMapNode> nodeCollection = new ArrayList<>();
		nodeCollection.addAll(nodes);

		return Optional.of(new FullMap(nodeCollection));
	}

	private EPlayerPositionState convertToNetworkEPlayerPosState(Map<String, Coordinate> playerPosMap,
			Coordinate current) {

		Coordinate myPlayer = new Coordinate(Integer.MAX_VALUE, Integer.MAX_VALUE);
		Coordinate otherPlayer = new Coordinate(Integer.MAX_VALUE, Integer.MAX_VALUE);

		for (var entry : playerPosMap.entrySet()) {
			if (!entry.getKey().equals("other-player"))
				myPlayer = entry.getValue();
			else
				otherPlayer = entry.getValue();
		}

		if (current.equals(myPlayer) && current.equals(otherPlayer))
			return EPlayerPositionState.BothPlayerPosition;
		else if (current.equals(myPlayer))
			return EPlayerPositionState.MyPlayerPosition;
		else if (current.equals(otherPlayer))
			return EPlayerPositionState.EnemyPlayerPosition;
		else
			return EPlayerPositionState.NoPlayerPresent;
	}

	private EFortState convertToNetworkEFortState(Map<Coordinate, MapField> board, Coordinate coordOfInterest) {

		if (board.get(coordOfInterest).getTileinfo().equals(ESpecial.BASE_CASTLE_IS_HERE))
			return EFortState.MyFortPresent;
		else if (board.get(coordOfInterest).getTileinfo().equals(ESpecial.ENEMIES_CASTLE_IS_HERE))
			return EFortState.EnemyFortPresent;
		else
			return EFortState.NoOrUnknownFortState;

	}
}
