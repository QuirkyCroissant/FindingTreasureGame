package server.gamehandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import server.businessrulez.GameIDAvaibleRule;
import server.businessrulez.IRule;
import server.businessrulez.PlayerActionRule;
import server.businessrulez.TwoPlayerRule;
import server.businessrulez.ValidPlayerIDRule;
import server.businessrulez.maprelevant.BorderWaterRule;
import server.businessrulez.maprelevant.CastleExistenceRule;
import server.businessrulez.maprelevant.MapSubmittionRule;
import server.businessrulez.maprelevant.MinTerrainRule;
import server.businessrulez.maprelevant.NoIslandRule;
import server.businessrulez.maprelevant.OnlyAllowedCoordinatesRule;
import server.converters.NetConverter;
import server.exceptions.GenericExampleException;
import server.exceptions.rules.RuleBreakException;
import server.gamedata.EPlayerStatus;
import server.gamedata.GameStatus;
import server.gamedata.PlayerInfo;
import server.gamedata.PlayerInfoHandler;
import server.servermap.ServerMap;

@Component
public class GameHandlerLogic {

	private final Logger logger = LoggerFactory.getLogger(GameHandlerLogic.class);
	private final GenerateUniqueGameID gameIDGenerator = new GenerateUniqueGameID();
	private final NetConverter converter = new NetConverter();

	@Autowired
	private Map<String, GameStatus> gameHistory = new LinkedHashMap<>();
	private static final int GAME_CEILING = 99;

	/**
	 * List of businessrules which implement a IRule-Inteface and is used as a
	 * repertoire of rules that can be used to check through some tests if input
	 * data in the various end points are valid or safe
	 */
	private final List<IRule> businessRulesList = List.of(new GameIDAvaibleRule(), new ValidPlayerIDRule(),
			new TwoPlayerRule(), new PlayerActionRule(), new MapSubmittionRule(), new CastleExistenceRule(),
			new OnlyAllowedCoordinatesRule(), new NoIslandRule(), new BorderWaterRule(), new MinTerrainRule());

	public GameHandlerLogic() {
		logger.debug("GameHandlerLogic constructor called.");
	}

	public synchronized String initiateNewGame() {

		logger.trace("Create new game");
		// checks before adding game if max capacity is reached, if yes, than it deletes
		// oldest game
		if (gameHistory.size() > GAME_CEILING) {
			List<Map.Entry<String, GameStatus>> entries = new ArrayList<>(gameHistory.entrySet());

			int dumpCeiling = gameHistory.size() - GAME_CEILING;
			for (int i = 0; i < dumpCeiling; i++) {
				Entry<String, GameStatus> mapEntry = entries.get(i);
				String gameKey = mapEntry.getKey();

				this.gameHistory.remove(gameKey);
				logger.debug("Game \"{}\" has been discarded!", gameKey);
			}
		}

		String uniqueGameID = gameIDGenerator.getUniqueID();
		gameHistory.put(uniqueGameID, new GameStatus("uniqueGameID"));
		return uniqueGameID;
	}

	/**
	 * Every 2 minutes the Server checks if we have games that are already completed
	 * and deletes them out of the LinkedHashMap
	 */
	public void tidyUpGameHistoryCeiling() {
		logger.info("Currently {} Games are up and running", this.gameHistory.size());

		// using iterator to delete entry while iterating through collection
		Iterator<Entry<String, GameStatus>> iterator = this.gameHistory.entrySet().iterator();
		int deletionCounter = 0;
		while (iterator.hasNext()) {
			Entry<String, GameStatus> entry = iterator.next();
			if (!entry.getValue().isGameOngoing()) {
				String overdueGameID = entry.getKey();
				iterator.remove();
				deletionCounter++;
				logger.debug("Game \"{}\" has been discarded!", overdueGameID);
			}
		}
		if (deletionCounter > 0)
			logger.debug("{} overdue games have been discarded!", deletionCounter);
	}

	public void registerPlayer(UniqueGameIdentifier submittedGameID, UniquePlayerIdentifier newPlayerID,
			PlayerRegistration newPlayer) {
		String gameID = submittedGameID.getUniqueGameID();
		String playerID = newPlayerID.getUniquePlayerID();

		this.businessRulesList.forEach(rule -> {
			rule.confirmGameIDExistance(gameHistory, gameID);
			rule.validatePlayerRegistration(gameHistory, gameID, newPlayer);
		});

		PlayerInfo convertedPlayerInfo = converter.convertNetworkPlayerToPlayerInfo(playerID, newPlayer);
		gameHistory.get(gameID).increasePlayerSize(convertedPlayerInfo);
		logger.info("New player registered for game {}: {}", gameID, convertedPlayerInfo.getPlayerId());

		// if we have enough players we set the MustAct Flag on one of them to enable
		// the next endpoint
		Map<String, PlayerInfo> playerbase = gameHistory.get(gameID).getManagePlayers().getPlayerbase();
		if (playerbase.size() == 2) {
			String toActPlayerID = playerbase.keySet().iterator().next();
			playerbase.get(toActPlayerID).setActFlag(EPlayerStatus.MustAct);
		}

		gameHistory.get(gameID).updateStateID();
	}

	public void manageIncomingMap(String gameID, PlayerHalfMap halfmap) {

		GameStatus gameInQuestion = this.gameHistory.get(gameID);
		String submittedPlayerID = halfmap.getUniquePlayerID();

		// checks all business rules in regards to the halfMaps and if the test fails it
		// throws an exception and the player loses
		try {
			this.businessRulesList.forEach(rule -> {
				rule.confirmGameIDExistance(gameHistory, gameID);
				rule.confirmPlayerID(gameInQuestion, gameID, submittedPlayerID);
				rule.validateSubmittedMap(this.gameHistory, gameID, halfmap);
			});
		} catch (

		GenericExampleException e) {
			if (e instanceof RuleBreakException)
				gameInQuestion.getManagePlayers().settleGameScore(submittedPlayerID, false);
			throw e;
		}

		PlayerInfoHandler players = gameInQuestion.getManagePlayers();

		ServerMap mapSubmittion = converter.convertPlayerHalfMapToServerMap(halfmap, submittedPlayerID);

		gameInQuestion.getMapData().addPlayersServerMap(submittedPlayerID, mapSubmittion);
		logger.info("New HalfMap submitted for Game \"{}\", by \"{}\"", gameID, submittedPlayerID);
		players.getSpecificPlayer(submittedPlayerID).setAlreadyDeliveredMap(true);

		/*
		 * FULL MAP FUSION IF WE HAVE 2 MAPS
		 */

		// checks if both maps are now already submitted and if so combines them
		logger.info("{} Maps saved in System for Game \"{}\"",
				gameInQuestion.getManagePlayers().getAmountOfSubmittedHalfmaps(), gameID);

		if (Long.compare(gameInQuestion.getManagePlayers().getAmountOfSubmittedHalfmaps(), 2) == 0) {
			gameInQuestion.getMapData().combineHalfMaps();
		}

		gameInQuestion.updateStateID();
		players.alternatePlayerTurn();
	}

	public GameState manageIncomingGameStateRequest(String uniqueGameID, String uniquePlayerID) {

		this.businessRulesList.forEach(rule -> {
			rule.confirmGameIDExistance(gameHistory, uniqueGameID);
			rule.confirmPlayerID(this.gameHistory.get(uniqueGameID), uniqueGameID, uniquePlayerID);
		});

		return converter.builtGameState(this.gameHistory.get(uniqueGameID), uniquePlayerID);
	}
}
