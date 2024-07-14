package client.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.GameLogic;
import network_logic.InvalidGameIdException;
import network_logic.NetworkClient;
import view.CLI;

public class MainClient {

	// ADDITIONAL TIPS ON THIS MATTER ARE GIVEN THROUGHOUT THE TUTORIAL SESSION!

	private static Logger mainLogger = LoggerFactory.getLogger(MainClient.class);

	/*
	 * Below, you can find an example of how to use both required HTTP operations,
	 * i.e., POST and GET to communicate with the server.
	 * 
	 * Note, this is only an example. Hence, your own implementation should NOT
	 * place all the logic in a single main method!
	 * 
	 * Further, I would recommend that you check out: a) The JavaDoc of the network
	 * message library, which describes all messages, and their ctors/methods. You
	 * can find it here http://swe1.wst.univie.ac.at/ b) The informal network
	 * documentation is given in Moodle, which describes which messages must be used
	 * when and how.
	 */
	public static void main(String[] args) {

		/*
		 * IMPORTANT: Parsing/Handling of starting parameters.
		 * 
		 * args[0] = Game Mode, you Can use this to know that your code is running on
		 * the evaluation server (if this is the case args[0] = TR). If this is the
		 * case, only a command line interface must be displayed. Also, no JavaFX and
		 * Swing UI components and classes must be used/executed by your Client in any
		 * way IF args[0] = TR.
		 * 
		 * args[1] = Server URL, will hold the server URL your Client should use. Note,
		 * only use the server URL supplied here as the URL used by you during the
		 * development and by the evaluation server (for grading) is NOT the same!
		 * args[1] enables your Client always to get the correct one.
		 * 
		 * args[2] = Holds the game ID which your Client should use. For testing
		 * purposes, you can create a new one by accessing
		 * http://swe1.wst.univie.ac.at:18235/games with your web browser. IMPORTANT: If
		 * a value is stored in args[2], you MUST use it! DO NOT create new games in
		 * your code in such a case!
		 * 
		 * DON'T FORGET TO EVALUATE YOUR FINAL IMPLEMENTATION WITH OUR TEST SERVER. THIS
		 * IS ALSO THE BASE FOR GRADING. THE TEST SERVER CAN BE FOUND AT:
		 * http://swe1.wst.univie.ac.at/
		 * 
		 * HINT: The assignment section in Moodle also explains all the important
		 * aspects about the start parameters/arguments. Use the Run Configurations (as
		 * shown during the IDE Screencast) in Eclipse to simulate the starting of an
		 * application with start parameters or implement your argument parsing code to
		 * become more flexible (e.g., to mix hardcoded and supplied parameters whenever
		 * the one or the other is available).
		 */
		// "debugmode"
		if (args.length == 3) {

			mainLogger.info("The start arguments used when launching the program are: '{}', '{}' and '{}'!", args[0],
					args[1], args[2]);

			// parse the parameters, otherwise the automatic evaluation will not work on
			// http://swe1.wst.univie.ac.at
			String serverBaseUrl = args[1];
			String gameId = args[2];

			game_state.GameStatus newGame = new game_state.GameStatus(gameId);
			GameLogic gameController;

			try {

				gameController = new GameLogic(gameId, new NetworkClient(serverBaseUrl, gameId), newGame);

				CLI display = new CLI(gameController.getGameModel(), gameController);

				gameController.runGame();

			} catch (InvalidGameIdException e) {
				e.printStackTrace();
				System.exit(0);
			}

			/*
			 * TIP: Check out the network protocol documentation. It shows you with a nice
			 * sequence diagram all the steps which are required to be executed by your
			 * client along with a general overview on the required behavior (e.g., when it
			 * is necessary to repeatedly ask the server for its state to determine if
			 * actions can be sent or not). When the client will need to wait for the other
			 * client and when you client should stop sending any more messages to the
			 * server.
			 */

			/*
			 * TIP: A game consists of two clients. How can I get two clients for testing
			 * purposes? Start your client two times. You can do this in Eclipse by hitting
			 * the green start button twice. Or you can start your jar file twice in two
			 * different terminals. When you hit the debug button twice, you can even debug
			 * both clients "independently" from each other (see, IDE Screencast in Moodle).
			 * 
			 * Alternative: Use the dummy competitor mode when creating new games to
			 * simplify your development phase. But note, this can, of course, only be a
			 * rough simulation. Why? Because some behavior observed by an actual second
			 * client, like network delay, will not be present, of course. So perform tests
			 * with your client running with two actual client instances too.
			 */

			/*
			 * TIP: To ease debugging and development, you can create special games. Such
			 * games can get assigned a dummy competitor, or you can stop and debug them
			 * without violating the maximum turn time limit. Check out the network protocol
			 * documentation for details on how to do so.
			 */

		}

		mainLogger.error("invalid argument length, while launching the program!");
	}

}
