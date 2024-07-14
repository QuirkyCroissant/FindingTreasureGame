package server.gamehandler;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateUniqueGameID {
	private static final int ID_LENGTH = 5;
	private static final String AVAILABLE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private final Logger logger = LoggerFactory.getLogger(GenerateUniqueGameID.class);
	private final Set<String> knownGameIDs = new HashSet<>();
	private final Random randomIndexChooser = new Random();

	public String getUniqueID() {
		logger.info("Trying to generate Game ID");

		String potentialGameID;
		do {
			potentialGameID = generateRandomID();
		} while (!knownGameIDs.add(potentialGameID));

		logger.info("Game ID \"{}\" was created", potentialGameID);
		return potentialGameID;
	}

	private String generateRandomID() {
		StringBuilder gameIDBuilder = new StringBuilder();
		for (int currentIndex = 0; currentIndex < ID_LENGTH; currentIndex++) {
			int randomIndex = randomIndexChooser.nextInt(AVAILABLE_CHARACTERS.length());
			gameIDBuilder.append(AVAILABLE_CHARACTERS.charAt(randomIndex));
		}
		return gameIDBuilder.toString();
	}
}
