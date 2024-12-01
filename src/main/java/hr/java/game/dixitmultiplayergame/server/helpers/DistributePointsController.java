package hr.java.game.dixitmultiplayergame.server.helpers;

import hr.java.game.dixitmultiplayergame.model.GameState;
import hr.java.game.dixitmultiplayergame.model.MessageType;
import hr.java.game.dixitmultiplayergame.server.ClientHandler;

import java.io.IOException;
import java.util.Map;

public class DistributePointsController {
    public static void distributePointsAndSendToClient() throws IOException {
        for (Map.Entry<String, Integer> pointsEntry : GameState.playersVotes.entrySet()) {
            String playerUsername = pointsEntry.getKey();
            Integer points = pointsEntry.getValue();

            if (playerUsername.equals(GameState.storytellerUsername) && (points == GameState.playersCards.size() - 1)) {
                allVotedForStoryteller();
                break;
            }
            if (playerUsername.equals(GameState.storytellerUsername) && points == 0) {
                nobodyVotedForStoryteller();
                break;
            }
            if (playerUsername.equals(GameState.storytellerUsername) && (points < GameState.playersCards.size() - 1)) {
                atLeastOnePlayerVotedForStorytellerButNotALl();
                break;
            }
        }
    }
    private static void allVotedForStoryteller() throws IOException {
        for (Map.Entry<String, Integer> entry : GameState.playersPoints.entrySet()) {
            String playerUsername = entry.getKey();

            if (!playerUsername.equals(GameState.storytellerUsername)) {
                GameState.playersPoints.put(playerUsername, GameState.playersPoints.getOrDefault(playerUsername, 0) + 2);
            }
        }
        sendPointsHashMapToClient();
    }
    private static void nobodyVotedForStoryteller() throws IOException {
        for (Map.Entry<String, Integer> entry : GameState.playersVotes.entrySet()) {
            String playerUsername = entry.getKey();
            Integer votes = entry.getValue();

            if (!playerUsername.equals(GameState.storytellerUsername)) {
                GameState.playersPoints.put(playerUsername, GameState.playersPoints.getOrDefault(playerUsername, 0) + 2);
            }
            if (!playerUsername.equals(GameState.storytellerUsername) && votes > 0) {
                GameState.playersPoints.put(playerUsername, GameState.playersPoints.getOrDefault(playerUsername, 0) + votes);
            }
        }
        sendPointsHashMapToClient();
    }
    private static void atLeastOnePlayerVotedForStorytellerButNotALl() throws IOException {
        for (Map.Entry<String, Integer> entry : GameState.playersVotes.entrySet()) {
            String playerUsername = entry.getKey();
            Integer votes = entry.getValue();

            if (playerUsername.equals(GameState.storytellerUsername)) {
                GameState.playersPoints.put(playerUsername, GameState.playersPoints.getOrDefault(playerUsername, 0) + 3);
            }
            if (!playerUsername.equals(GameState.storytellerUsername) && votes > 0) {
                GameState.playersPoints.put(playerUsername, GameState.playersPoints.getOrDefault(playerUsername, 0) + votes);
            }
        }

        for (Map.Entry<String, String> entry : GameState.playersVotedCard.entrySet()) {
            String playerUsername = entry.getKey();
            String card = entry.getValue();

            if (!playerUsername.equals(GameState.storytellerUsername) && card.equals(GameState.storytellerCardId)) {
                GameState.playersPoints.put(playerUsername, GameState.playersPoints.getOrDefault(playerUsername, 0) + 3);
            }
        }
        sendPointsHashMapToClient();
    }
    private static void sendPointsHashMapToClient() throws IOException {
        for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
            clientHandler.outputStream.writeObject(MessageType.UPDATE_POINTS);
            clientHandler.outputStream.writeObject(GameState.playersPoints);
            clientHandler.outputStream.reset();
            clientHandler.outputStream.flush();
        }
    }

}
