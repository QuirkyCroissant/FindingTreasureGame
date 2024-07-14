package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.DijkstraPath;
import ai.GoalSetting;
import ai.Pathfinder;
import game_state.ClientAction;
import game_state.GameFlags;
import game_state.GameStatus;
import gaming_map.Halfmap;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import network_logic.NetworkClient;
import player.Player;

public class GameLogic {

	private String gameId;
	private NetworkClient netcode;
	private GameStatus gameModel;

	private static Logger gameLogicLogger = LoggerFactory.getLogger(GameLogic.class);

	public GameLogic(String gameId, NetworkClient netcode, GameStatus gameModel) {
		this.gameId = gameId;
		this.netcode = netcode;
		this.gameModel = gameModel;
	}

	public GameStatus getGameModel() {
		return this.gameModel;
	}

	public void runGame() {

		// register player to the server and gets a player ID if request was successful
		this.gameModel.getHostplayer().setPlayerId(this.netcode.sendPlayerRegistration(gameModel.getHostplayer()));

		transmitMapData();

		this.statusPolling();

		playerTurn(this.gameModel.getHostplayer().getPlayerId());

		System.exit(0);
	}

	private void transmitMapData() {

		PlayerState currentGameStat;
		ClientAction clientAction;
		// wait till ready
		do {
			currentGameStat = Converter.convertGameToIndivPlayerState(this.statusPolling(),
					this.gameModel.getHostplayer().getPlayerId());
			clientAction = (currentGameStat.getState().equals(EPlayerGameState.MustAct)) ? ClientAction.MUSTACT
					: ClientAction.WAITING;
		} while (clientAction.compareTo(ClientAction.WAITING) == 0);

		PlayerHalfMap serverHalfmap = Converter.convertHalfMapToServerMap(this.gameModel.getHostplayer().getPlayerId(),
				new Halfmap());
		netcode.sendMap(serverHalfmap, gameId, gameId);

	}

	private GameState statusPolling() {

		PlayerState currentState;
		GameState response;

		do {

			response = netcode.exampleForGetRequests();

			currentState = Converter.convertGameToIndivPlayerState(response,
					this.gameModel.getHostplayer().getPlayerId());

			if (currentState.getState().equals(EPlayerGameState.MustWait)) {
				try {
					Thread.sleep(400);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {

				break;
			}

		} while (true);

		// update game if there is a new gamestate
		if (!response.getGameStateId().equals(gameModel.getGameStateNr())) {
			updateGame(response);
		}

		return response;

	}

	private void updateGame(GameState response) {

		// updates game_model
		// generates new fullmap or alters it

		// updates the player information after the player registration and client safes
		// other-player information locally too
		if (response.getPlayers() != null && this.gameModel.getEnemy() == null) {
			for (var entry : response.getPlayers()) {
				if (entry.getUniquePlayerID().equals(gameModel.getHostplayer().getPlayerId())) {
					this.gameModel.setHostplayer(new Player(entry.getUniquePlayerID(), entry.getFirstName(),
							entry.getLastName(), entry.getUAccount()));

				} else {

					this.gameModel.setEnemy(new Player(entry.getUniquePlayerID(), entry.getFirstName(),
							entry.getLastName(), entry.getUAccount()));

				}
			}

		} else { // need to update the GameFlag for if our own player already has treasure in his
					// inventory

			for (var entry : response.getPlayers()) {
				if (entry.getUniquePlayerID().equals(gameModel.getHostplayer().getPlayerId())
						&& entry.hasCollectedTreasure()) {

					this.gameModel.setClientflag(GameFlags.SCOUT_CASTLE);

				}
			}

		}

		// gets half/fullmap back from server and saves it into local game
		if (response.getMap() != null && response.getMap().getMapNodes().size() == 100) {
			this.gameModel.setMatchMap(
					Converter.convertServerMapLocally(response.getMap(), this.gameModel.getHostplayer().getPlayerId()));
		}

		// at last changes the gamestate identification number
		this.gameModel.setGameStateNr(response.getGameStateId());

	}

	private void playerTurn(String playerID) {

		Pathfinder pathfinder = new DijkstraPath(this.gameModel.getMatchMap(), new GoalSetting());
		boolean gameHasEnded = false;
		EPlayerGameState status = (Converter.convertGameToIndivPlayerState(this.statusPolling(), playerID)).getState();

		while (!gameHasEnded) {
			switch (status) {

			case MustAct:

				this.netcode
						.sendMove(
								EMove.values()[pathfinder.findBestPath(
										this.gameModel.getMatchMap().getAvatarCoordinates(), this.gameModel).ordinal()],
								this.gameModel.getGameId(), playerID);
				break;

			case Won:
				gameLogicLogger.info("We Won!!!~");
				gameHasEnded = true;
				break;

			case Lost:
				gameLogicLogger.info("Mission Failed, we will get them next time :(");
				gameHasEnded = true;
				break;
			default:
				continue;
			}
			status = (Converter.convertGameToIndivPlayerState(this.statusPolling(), playerID)).getState();
		}

	}

}
