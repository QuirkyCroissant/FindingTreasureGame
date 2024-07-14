package controller;

import static org.mockito.Mockito.atLeast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import game_state.ClientAction;
import game_state.GameStatus;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import network_logic.NetworkClient;
import player.Player;

class GameLogicTests {

	private static GameLogic controller;
	private static NetworkClient mockNetClient = Mockito.mock(NetworkClient.class);
	private static GameStatus testGameStatus;
	private static GameState serverResponse;
	private static GameState serverResponse2;

	private static int accessCount = 0;

	@BeforeAll
	static void setupTestData() {

		Player testPlayer = new Player("p1234", "John", "Doe", "doej69");
		testGameStatus = new GameStatus("g1234", "gameNr1", ClientAction.MUSTACT, testPlayer);
		controller = new GameLogic("g1234", mockNetClient, testGameStatus);

		// build Server Fullmaps and GameStates
		Set<PlayerState> collectionPlayers = new HashSet<>();
		collectionPlayers
				.add(new PlayerState(testPlayer.getFirstName(), testPlayer.getLastName(), testPlayer.getUaccount(),
						EPlayerGameState.MustAct, new UniquePlayerIdentifier(testPlayer.getPlayerId()), false));
		collectionPlayers.add(new PlayerState(testPlayer.getFirstName(), testPlayer.getLastName(),
				testPlayer.getUaccount(), EPlayerGameState.MustWait, new UniquePlayerIdentifier("4321"), false));

		Set<PlayerState> collectionPlayers2 = new HashSet<>();
		collectionPlayers2
				.add(new PlayerState(testPlayer.getFirstName(), testPlayer.getLastName(), testPlayer.getUaccount(),
						EPlayerGameState.Won, new UniquePlayerIdentifier(testPlayer.getPlayerId()), false));
		collectionPlayers2.add(new PlayerState(testPlayer.getFirstName(), testPlayer.getLastName(),
				testPlayer.getUaccount(), EPlayerGameState.Lost, new UniquePlayerIdentifier("4321"), false));

		// build equivalent server class
		List<FullMapNode> fullMapNodes = new ArrayList<>();
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				if (x == 0 && y == 0) {
					fullMapNodes.add(new FullMapNode(messagesbase.messagesfromclient.ETerrain.Grass,
							EPlayerPositionState.MyPlayerPosition, ETreasureState.NoOrUnknownTreasureState,
							EFortState.MyFortPresent, x, y));
				} else if (y == 1 && x != 3) {
					fullMapNodes.add(new FullMapNode(messagesbase.messagesfromclient.ETerrain.Water,
							EPlayerPositionState.NoPlayerPresent, ETreasureState.NoOrUnknownTreasureState,
							EFortState.NoOrUnknownFortState, x, y));
				} else {
					fullMapNodes.add(new FullMapNode(messagesbase.messagesfromclient.ETerrain.Grass,
							EPlayerPositionState.NoPlayerPresent, ETreasureState.NoOrUnknownTreasureState,
							EFortState.NoOrUnknownFortState, x, y));
				}
			}
		}
		FullMap serverFullMap = new FullMap(fullMapNodes);

		serverResponse = new GameState(serverFullMap, collectionPlayers, "gameNr2");

		serverResponse2 = new GameState(serverFullMap, collectionPlayers2, "gameNr3");
	}

	@Test
	void givenVariousGameStates_PlayTwoRounds_ExpectRightMoveSentAndRequesting2GameStates() {
		// arrange
		Mockito.when(mockNetClient.exampleForGetRequests()).then(res -> {
			if (accessCount++ == 0)
				return serverResponse;
			else
				return serverResponse2;
		});

		// act
		try {
			Method playerTurn = controller.getClass().getDeclaredMethod("playerTurn", String.class);
			playerTurn.setAccessible(true);
			playerTurn.invoke(controller, testGameStatus.getHostplayer().getPlayerId());

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		// assert
		try {
			Mockito.verify(mockNetClient, atLeast(2)).exampleForGetRequests();
			Mockito.verify(mockNetClient).sendMove(EMove.Right, "g1234", "p1234");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
