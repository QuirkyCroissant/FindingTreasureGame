package view;

import java.beans.PropertyChangeListener;

import controller.GameLogic;
import game_state.GameFlags;
import game_state.GameStatus;
import gaming_map.Coordinate;
import gaming_map.ESpecial;
import gaming_map.ETerrain;
import gaming_map.Field;
import gaming_map.Fullmap;

public class CLI extends UX {

	private static int turn = 0;
	private GameLogic controller;

	public CLI(GameStatus model, GameLogic controller) {
		this.controller = controller;
		model.addPropertyChangeListener(modelChangedListener);
	}

	// using a lambda expression or anonymous classes to define the handler of an
	// event change listener
	// here, we show how to use lambda expressions
	final PropertyChangeListener modelChangedListener = event -> {

		Object model = event.getSource();

		if (model instanceof GameStatus) {

			GameStatus castedModel = (GameStatus) model;
			if (!castedModel.getMatchMap().getWholemap().isEmpty())

				System.out.println("MyPlayer: " + castedModel.getMatchMap().getAvatarCoordinates().toString()
						+ " \t EnemyPlayer: " + castedModel.getMatchMap().getPlayerPos().get("other-player").toString()
						+ "\t Player-Turn: " + (++turn) + "/160");
			// SCOUT_TREASURE
			String treasureFound = (castedModel.getClientflag().equals(GameFlags.SCOUT_TREASURE))
					? "\u001B[31m still searching for it\u001B[0m"
					: "\u001B[32mfound it\u001B[0m";

			System.out.println("Treasure in Inventory: " + treasureFound);

			System.out.println(illustrateMap(castedModel));
		}
	};

	@Override
	protected String illustrateMap(GameStatus gamestat) {

		Fullmap map = gamestat.getMatchMap();
		StringBuilder res = new StringBuilder();

		int xLimit = 0;
		int yLimit = 0;
		if (map.isVerticallyJoined()) {
			xLimit = 20;
			yLimit = 5;
		} else {
			xLimit = 10;
			yLimit = 10;
		}
		Coordinate myplayerPos = new Coordinate(0, 0);
		Coordinate enemyPos = new Coordinate(0, 0);

		for (var entry : map.getPlayerPos().entrySet()) {
			if (!entry.getKey().equals("other-player")) {
				myplayerPos = entry.getValue();
			} else {
				enemyPos = entry.getValue();
			}

		}

		for (int y = 0; y < yLimit; y++) {
			for (int x = 0; x < xLimit; x++) {
				Coordinate searchCoords = new Coordinate(x, y);
				Field curField = map.getWholemap().get(searchCoords);
				ESpecial curSpecial = curField.getTileinfo();
				ETerrain curTerrain = curField.getTiletype();

				if (curSpecial.equals(ESpecial.BASE_CASTLE_IS_HERE) && !searchCoords.equals(myplayerPos)) {
					res.append("\u001B[51;101m|C>\u001B[0m");
				} else if (curSpecial.equals(ESpecial.ENEMIES_CASTLE_IS_HERE) && !searchCoords.equals(myplayerPos)) {
					res.append("\u001B[51;93;100m|C>\u001B[0m");
				} else if (searchCoords.equals(myplayerPos)) {
					res.append("\u001B[0;105m P \u001B[0;0m");
				} else if (searchCoords.equals(enemyPos)) {
					res.append("\u001B[0;100m E \u001B[0;0m");
				} else if (curSpecial.equals(ESpecial.TREASURE_IS_HERE)) {
					res.append("\u001B[1;30;103m[*]\u001B[0;0m");
				} else if (curSpecial.equals(ESpecial.TREASURE_IS_HERE) && searchCoords.equals(myplayerPos)) {
					res.append("\u001B[1;103m P \u001B[0;0m");
				} else {

					switch (curTerrain) {
					case GRASS:
						res.append("\u001B[51;102m,,,\u001B[0m");
						break;
					case MOUNTAIN:
						res.append("\u001B[51;215m/^\\\u001B[0m");
						break;
					default:
						res.append("\u001B[51;106m~~~\u001B[0m");
						break;
					}
				}
			}
			res.append("\n");
		}

		return res.toString();

	}

}
