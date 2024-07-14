
# Finding Treasure Game: Client-Server Java Project

This project implements a game with both client and server components. The client handles the user interface, game logic, and AI, while the server manages game data, game logic, and client interactions.

## Client

The client is responsible for interacting with the user, executing game logic, and communicating with the server. The main components of the client are:

### AI Package
- **DijkstraPath.java**: Implements Dijkstra's algorithm for pathfinding.
- **GoalSetting.java**: Defines goals and objectives for the AI.
- **Pathfinder.java**: Main class for pathfinding logic.

### Controller Package
- **Converter.java**: Converts data between different formats.
- **GameLogic.java**: Contains the main game logic.

### Game State Package
- **ClientAction.java**: Represents actions taken by the client.
- **GameFlags.java**: Contains flags indicating game state.
- **GameStatus.java**: Represents the status of the game.

### Gaming Map Package
- **Coordinate.java**: Represents coordinates on the game map.
- **Fullmap.java**: Represents the full game map.
- **MapGenerator.java**: Generates game maps.
- **MapValidator.java**: Validates the generated maps.

### Network Logic Package
- **NetworkClient.java**: Handles network communication with the server.

### View Package
- **CLI.java**: Command-line interface for the game.
- **UX.java**: Handles user experience elements.

## Server

The server is responsible for managing game data, processing game logic, and handling client requests. The main components of the server are:

### Exceptions Package
- **RuleBreakException.java**: Custom exception for rule violations.

### Game Data Package
- **GameStatus.java**: Represents the status of the game.
- **PlayerInfo.java**: Contains information about players.
- **ServerMapHandler.java**: Manages server-side map handling.

### Game Handler Package
- **GameHandlerLogic.java**: Contains the main game handling logic.
- **GenerateUniqueGameID.java**: Generates unique game IDs.

### Hibernate Package
- **SQLiteDialect.java**: Custom SQLite dialect for Hibernate.

### Main Package
- **MainServer.java**: Entry point for the server application.
- **ServerEndpoints.java**: Defines the server endpoints.

### Server Map Package
- **ServerMap.java**: Represents the server-side game map.

## Missing Components

- The server implementation is missing the `move` endpoint, which is required to handle client move requests.

## Getting Started

To run the client and server, follow the instructions below:

### Prerequisites

- Java Development Kit (JDK)
- Maven

### Running the Server

1. Navigate to the server directory:
   ```bash
   cd Source/Teilaufgabe\ 3\ -\ Server
   ```
2. Build the server:
   ```bash
   mvn clean install
   ```
3. Run the server:
   ```bash
   mvn exec:java -Dexec.mainClass="server.main.MainServer"
   ```

### Running the Client

1. Navigate to the client directory:
   ```bash
   cd Source/Teilaufgabe\ 2\ -\ Client
   ```
2. Build the client:
   ```bash
   mvn clean install
   ```
3. Run the client:
   ```bash
   mvn exec:java -Dexec.mainClass="client.main.MainClient"
   ```

## Disclaimer
This software project was developed in IDE Eclipse 2022-03 Version

## Contributing

Contributions are welcome! Please submit a pull request or open an issue to discuss any changes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
