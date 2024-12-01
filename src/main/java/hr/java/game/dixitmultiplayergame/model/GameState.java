package hr.java.game.dixitmultiplayergame.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import hr.java.game.dixitmultiplayergame.server.Server;

import java.io.*;
import java.util.*;

public class GameState implements Serializable {
    public static String storytellerUsername;
    public static List<String> playerCards;
    public static String playerWithTurn;
    public static String description;
    public static String storytellerCardId;
    public static List<String> submittedCards = new ArrayList<>();
    public static String playerCardId;
    public static Integer currentPlayerTurnIndex = -1;
    public static Integer storytellerIndex = -1;
    public static Boolean drawNewCards = false;
    public static Boolean isGameInProgress = false;
    public static Map<String, List<String>> playersCards = new TreeMap<>();
    public static Map<String, List<String>> playersCardsSnapshot = new TreeMap<>();
    public static Map<String, String> playersVotedCard = new HashMap<>();
    public static Map<String, Integer> playersVotes = new HashMap<>();
    public static Map<String, Integer> playersPoints = new HashMap<>();
    @JsonCreator
    public GameState() {
    }
    public static void saveGameState() throws IOException {
        System.out.println("Saving GameState...");

        GameStateData gameStateData = new GameStateData();

        gameStateData.storytellerUsername = storytellerUsername;
        gameStateData.playerCards = playerCards;
        gameStateData.playerWithTurn = playerWithTurn;
        gameStateData.description = description;
        gameStateData.storytellerCardId = storytellerCardId;
        gameStateData.submittedCards = submittedCards;
        gameStateData.playersCardsSnapshot = playersCardsSnapshot;
        gameStateData.playerCardId = playerCardId;
        gameStateData.currentPlayerTurnIndex = currentPlayerTurnIndex;
        gameStateData.storytellerIndex = storytellerIndex;
        gameStateData.drawNewCards = drawNewCards;
        gameStateData.isGameInProgress = isGameInProgress;
        gameStateData.playersCards = playersCards;
        gameStateData.playersVotedCard = playersVotedCard;
        gameStateData.playersVotes = playersVotes;
        gameStateData.playersPoints = playersPoints;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Server.SAVE_GAME_FILE_NAME))) {
            oos.writeObject(gameStateData);
        }
    }
    public static GameStateData loadGameState() throws IOException, ClassNotFoundException {
        System.out.println("Loading GameState...");

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Server.SAVE_GAME_FILE_NAME))) {
            return (GameStateData) ois.readObject();
        }

    }

}
