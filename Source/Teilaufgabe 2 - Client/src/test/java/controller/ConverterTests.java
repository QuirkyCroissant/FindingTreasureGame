package controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import gaming_map.Coordinate;
import gaming_map.ESpecial;
import gaming_map.ETerrain;
import gaming_map.Field;
import gaming_map.Fullmap;
import gaming_map.Halfmap;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;

class ConverterTests {

	private static Converter converter;

	@Test
	void givenServerHalfMap_ConvertLocalHalfMap_ExpectValidServerHalfMap() {

		// arrange
		// build local class
		String playerNr = "1234";
		Map<Coordinate, Field> halfMapTiles = new HashMap<>();
		halfMapTiles.put(new Coordinate(0, 0), new Field(ETerrain.GRASS, ESpecial.BASE_CASTLE_IS_HERE));
		Halfmap localHalfmap = new Halfmap(halfMapTiles, new Coordinate(0, 0));

		// build equivalent server class
		List<PlayerHalfMapNode> halfMapNodes = new ArrayList<>();
		halfMapNodes.add(new PlayerHalfMapNode(0, 0, true, messagesbase.messagesfromclient.ETerrain.Grass));
		PlayerHalfMap expectedPlayerHalfMap = new PlayerHalfMap(playerNr, halfMapNodes);

		// act
		PlayerHalfMap realResult = converter.convertHalfMapToServerMap(playerNr, localHalfmap);

		// assert
		assertThat(expectedPlayerHalfMap, is(equalTo(realResult)));

	}

	@Test
	void givenFullMap_ConvertServerFullMap_ExpectValidLocalFullMap() {
		// arrange
		// build local class
		boolean testResult = true;
		String playerNr = "1234";
		Map<String, Coordinate> ppos = new HashMap<>();
		ppos.put(playerNr, new Coordinate(0, 0));
		ppos.put("other-player", new Coordinate(0, 2));

		Map<Coordinate, Field> fullMapTiles = new HashMap<>();
		fullMapTiles.put(new Coordinate(0, 0), new Field(ETerrain.GRASS, ESpecial.BASE_CASTLE_IS_HERE));
		fullMapTiles.put(new Coordinate(0, 1), new Field(ETerrain.GRASS, ESpecial.TREASURE_IS_HERE));
		fullMapTiles.put(new Coordinate(0, 2), new Field(ETerrain.GRASS, ESpecial.ENEMIES_CASTLE_IS_HERE));
		// we have that map; 00 is the basecastle, 01 treasure, 0,2 castle of enemy,
		// ppos map, not vertical joined, we know treasure and castle location
		Fullmap expectedLocalFullmap = new Fullmap(fullMapTiles, new Coordinate(0, 0), new Coordinate(0, 1),
				new Coordinate(0, 2), ppos, false, true, true);

		// build equivalent server class
		List<FullMapNode> fullMapNodes = new ArrayList<>();
		fullMapNodes.add(
				new FullMapNode(messagesbase.messagesfromclient.ETerrain.Grass, EPlayerPositionState.MyPlayerPosition,
						ETreasureState.NoOrUnknownTreasureState, EFortState.MyFortPresent, 0, 0));
		fullMapNodes.add(
				new FullMapNode(messagesbase.messagesfromclient.ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
						ETreasureState.MyTreasureIsPresent, EFortState.NoOrUnknownFortState, 0, 1));
		fullMapNodes.add(new FullMapNode(messagesbase.messagesfromclient.ETerrain.Grass,
				EPlayerPositionState.EnemyPlayerPosition, ETreasureState.NoOrUnknownTreasureState,
				EFortState.EnemyFortPresent, 0, 2));

		FullMap serverFullMap = new FullMap(fullMapNodes);

		// act
		Fullmap realResult = converter.convertServerMapLocally(serverFullMap, playerNr);

		// assert
		assertThat(true, is(equalTo(realResult.equals(expectedLocalFullmap))));

	}

}
