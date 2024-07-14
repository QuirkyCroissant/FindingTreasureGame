package network_logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import messagesbase.ResponseEnvelope;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.ERequestState;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerMove;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import player.Player;
import reactor.core.publisher.Mono;

public class NetworkClient {

	private WebClient baseWebClient;

	private String gameId;
	private String playerId;

	private static final int GAMEID_LENGTH = 5;
	private static final Logger netLogger = LoggerFactory.getLogger(NetworkClient.class);

	public NetworkClient(String serverBaseUrl, String gameId) throws InvalidGameIdException {

		if (gameId.length() != GAMEID_LENGTH)
			throw new InvalidGameIdException("GameID that was given through the input parameter had wrong length.");

		this.baseWebClient = WebClient.builder().baseUrl(serverBaseUrl + "/games")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE) // the network protocol uses
																							// XML
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();

		this.gameId = gameId;

	}

	public GameState exampleForGetRequests() {

		Mono<ResponseEnvelope> webAccess = this.baseWebClient.method(HttpMethod.GET)
				.uri("/" + gameId + "/states/" + this.playerId).retrieve().bodyToMono(ResponseEnvelope.class);

		// WebClient support asynchronous message exchange. In SE1 we use a synchronous
		// one for the sake of simplicity. So calling block is fine.
		ResponseEnvelope<GameState> requestResult = webAccess.block();

		// always check for errors, and if some are reported, at least print them to the
		// console (logging should always be preferred!)
		// so that you become aware of them during debugging! The provided server gives
		// you constructive error messages.
		if (requestResult.getState() == ERequestState.Error) {
			netLogger.error("Client Request GameState Error: {}", requestResult.getExceptionMessage());
		}

		return requestResult.getData().get();
	}

	public String sendPlayerRegistration(Player player) {

		PlayerRegistration playerReg = new PlayerRegistration(player.getFirstName(), player.getLastName(),
				player.getUaccount());

		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + gameId + "/players")
				.body(BodyInserters.fromValue(playerReg)) // specify the data which is sent to the server
				.retrieve().bodyToMono(ResponseEnvelope.class); // specify the object returned by the server

		// WebClient support asynchronous message exchange. In SE1 we use a synchronous
		// one for the sake of simplicity. So calling block (which should normally be
		// avoided) is fine.
		ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();

		// always check for errors, and if some are reported, at least print them to the
		// console (logging should always be preferred!)
		// so that you become aware of them during debugging! The provided server gives
		// you constructive error messages.
		if (resultReg.getState() == ERequestState.Error) {
			netLogger.error("Player Registration error: {}", resultReg.getExceptionMessage());
		} else {
			UniquePlayerIdentifier uniqueID = resultReg.getData().get();

			netLogger.info("My Player ID: {}", uniqueID.getUniquePlayerID());
			netLogger.info("http://swe1.wst.univie.ac.at:18235/games/{}/states/{}", this.gameId,
					uniqueID.getUniquePlayerID());

			this.playerId = uniqueID.getUniquePlayerID();

			return this.playerId;
		}

		return "N/A";
	}

	public void sendMap(PlayerHalfMap halfmap, String gameId, String playerId) {

		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + gameId + "/halfmaps")
				.body(BodyInserters.fromValue(halfmap)) // specify the data which is sent to the server
				.retrieve().bodyToMono(ResponseEnvelope.class); // specify the object returned by the server

		ResponseEnvelope<PlayerHalfMap> resultMap = webAccess.block();

		if (resultMap.getState() == ERequestState.Error) {
			netLogger.error("Client error, errormessage: {}", resultMap.getExceptionMessage());
		} else {
			netLogger.info("Map successfully sent!");
		}

	}

	public void sendMove(EMove move, String gameId, String playerId) {

		System.out.println("---- Move Sent: " + move.name() + "------------");

		PlayerMove movCommand = PlayerMove.of(playerId, move);

		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + gameId + "/moves")
				.body(BodyInserters.fromValue(movCommand)) // specify the data which is sent to the server
				.retrieve().bodyToMono(ResponseEnvelope.class); // specify the object returned by the server

		ResponseEnvelope<PlayerMove> resultMap = webAccess.block();

		if (resultMap.getState() == ERequestState.Error) {
			netLogger.error("Client Movement Error: {}", resultMap.getExceptionMessage());
		} else {
			netLogger.info("Movement {} Command Sent.", move.name());

		}

	}

}
