package server.main;

import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import messagesbase.ResponseEnvelope;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import server.exceptions.GenericExampleException;
import server.gamehandler.GameHandlerLogic;

@EnableScheduling
@RestController
@RequestMapping(value = "/games")
public class ServerEndpoints {

	// ADDITIONAL TIPS ON THIS MATTER ARE GIVEN THROUGHOUT THE TUTORIAL SESSION!
	// Note, the same network messages which you have used for the Client (along
	// with its documentation) apply to the Server too.

	private GameHandlerLogic logic = new GameHandlerLogic();

	/*
	 * Please do NOT add all the necessary code in the methods provided below. When
	 * following the single responsibility principle, those methods should only
	 * contain the bare minimum related to network handling. Such as the converts
	 * which convert the objects from/to internal data objects to/from messages.
	 * Include the other logic (e.g., new game creation and game id handling) by
	 * means of composition (i.e., other classes should provide it).
	 */

	// below you can find two example endpoints (i.e., one GET and one POST based
	// endpoint, which are all endpoint types that you need),
	// Hence, all the other endpoints can be defined similarly.

	// example for a GET endpoint based on /games
	// similar to the client, the HTTP method and the expected data types are
	// specified at the server-side too
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody UniqueGameIdentifier newGame(
			@RequestParam(required = false, defaultValue = "false", value = "enableDebugMode") boolean enableDebugMode,
			@RequestParam(required = false, defaultValue = "false", value = "enableDummyCompetition") boolean enableDummyCompetition) {

		UniqueGameIdentifier gameIdentifier = new UniqueGameIdentifier(logic.initiateNewGame());
		return gameIdentifier;

	}

	// example for a POST endpoint based on /games/{gameID}/players
	@RequestMapping(value = "/{gameID}/players", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<UniquePlayerIdentifier> registerPlayer(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerRegistration playerRegistration) {
		UniquePlayerIdentifier newPlayerID = new UniquePlayerIdentifier(UUID.randomUUID().toString());

		// tries to register player to games
		logic.registerPlayer(gameID, newPlayerID, playerRegistration);

		ResponseEnvelope<UniquePlayerIdentifier> playerIDMessage = new ResponseEnvelope<>(newPlayerID);
		return playerIDMessage;
	}

	// example for a POST endpoint based on /games/{gameID}/halfmaps
	@RequestMapping(value = "/{gameID}/halfmaps", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<?> receiveMap(@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerHalfMap halfmap) {

		logic.manageIncomingMap(gameID.getUniqueGameID(), halfmap);

		return new ResponseEnvelope<>();

	}

	@RequestMapping(value = "/{gameID}/states/{playerID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<GameState> receiveGameStateRequest(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @PathVariable UniquePlayerIdentifier playerID) {

		GameState requestedState = logic.manageIncomingGameStateRequest(gameID.getUniqueGameID(),
				playerID.getUniquePlayerID());

		return new ResponseEnvelope<>(requestedState);

	}

	@Scheduled(fixedRate = 120000)
	private void dropExcessiveGames() {
		logic.tidyUpGameHistoryCeiling();
	}

	/*
	 * Note, this is only the most basic way of handling exceptions in Spring (but
	 * sufficient for our task) it would, for example struggle if you use multiple
	 * controllers. Add the exception types to the @ExceptionHandler which your
	 * exception handling should support the superclass catches subclasses aspect of
	 * try/catch also applies here. Hence, we recommend to simply extend your own
	 * Exceptions from the GenericExampleException. For larger projects, one would
	 * most likely want to use the HandlerExceptionResolver; see here
	 * https://www.baeldung.com/exception-handling-for-rest-with-spring
	 * 
	 * Ask yourself: Why is handling the exceptions in a different method than the
	 * endpoint methods a good solution? This applies a principle from Block 4,
	 * which one?
	 */
	@ExceptionHandler({ GenericExampleException.class })
	public @ResponseBody ResponseEnvelope<?> handleException(GenericExampleException ex, HttpServletResponse response) {
		ResponseEnvelope<?> result = new ResponseEnvelope<>(ex.getErrorName(), ex.getMessage());

		// reply with 200 OK as defined in the network documentation
		// Side note: We only do this here for simplicity reasons. For future projects,
		// you should check out HTTP status codes and
		// what they can be used for. Note, the WebClient used during the Client
		// implementation can react
		// to them using the .onStatus(...) method.
		response.setStatus(HttpServletResponse.SC_OK);
		return result;
	}
}
