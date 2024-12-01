package hr.java.game.dixitmultiplayergame.server.helpers;

import hr.java.game.dixitmultiplayergame.model.GameState;
import hr.java.game.dixitmultiplayergame.model.MessageType;
import hr.java.game.dixitmultiplayergame.server.ClientHandler;
import hr.java.game.dixitmultiplayergame.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EndRoundController {
    public static void handleChosenCardSelected(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        String chosenCardId = (String) inputStream.readObject();
        String username = (String) inputStream.readObject();

        GameState.playersVotedCard.put(username, chosenCardId);

        String playerWhoWasVotedFor = whichPlayerHasVotedCardInSavedCards(chosenCardId);
        GameState.playersVotes.put(playerWhoWasVotedFor, GameState.playersVotes.getOrDefault(playerWhoWasVotedFor, 0) + 1);

        if (allPlayersVoted()) {
            System.out.println("All players have voted!");

            DistributePointsController.distributePointsAndSendToClient();
            revealStorytellerCard();

            List<String> winners = checkWinner();

            if (winners.isEmpty()) {
                continueNextRound();
            } else {
                for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
                    clientHandler.outputStream.writeObject(MessageType.GAME_OVER);
                    clientHandler.outputStream.writeObject(winners);
                    clientHandler.outputStream.reset();
                    clientHandler.outputStream.flush();
                }
            }

        } else {
            System.out.println("Waiting for more players to vote...");
        }
    }
    private static String whichPlayerHasVotedCardInSavedCards(String chosenCardId) {
        for (Map.Entry<String, List<String>> allPlayersAllCards : GameState.playersCards.entrySet()) {
            String playerUsernameAll = allPlayersAllCards.getKey();
            List<String> playerCards = allPlayersAllCards.getValue();

            if (playerCards != null && playerCards.contains(chosenCardId)) {
                return playerUsernameAll;
            }
        }
        throw new IllegalStateException("No matching player found, but it was expected to find one.");
    }
    private static boolean allPlayersVoted() {
        int emptyCount = 0;
        for (String chosenCardId : GameState.playersVotedCard.values()) {
            if (chosenCardId == null || chosenCardId.isEmpty()) {
                emptyCount++;
            }
            if (emptyCount > 1) {
                return false;
            }
        }
        return emptyCount == 1;
    }
    private static void revealStorytellerCard() throws IOException {
        for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
            clientHandler.outputStream.writeObject(MessageType.REVEAL_STORYTELLER_CARD);
            clientHandler.outputStream.writeObject(GameState.storytellerCardId);
            clientHandler.outputStream.flush();
        }
    }
    private static List<String> checkWinner() {
        List<String> winners = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : GameState.playersPoints.entrySet()) {
            int points = entry.getValue();
            if (points >= Server.maxPoints) {
                Server.maxPoints = points;
            }
        }
        for (Map.Entry<String, Integer> entry : GameState.playersPoints.entrySet()) {
            if (entry.getValue() == Server.maxPoints) {
                winners.add(entry.getKey());
            }
        }
        return winners;
    }
    private static void continueNextRound() throws IOException {

        assignNextStoryteller();
        ClientHandler.assignPlayerWithTurn(GameState.storytellerUsername);

        GameState.submittedCards.clear();

        for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
            SendDataController.sendPlayerWithTurnToClient();
            SendDataController.sendStorytellerUsernameToClient(clientHandler.outputStream);
            if (GameState.drawNewCards) {
                GameState.playerCards = Server.drawCards();
                GameState.playersCards.put(clientHandler.getClientUsername(), GameState.playerCards);

                List<String> temp = new ArrayList<>(GameState.playerCards);
                GameState.playersCardsSnapshot.put(clientHandler.getClientUsername(), temp);

                clientHandler.outputStream.writeObject(MessageType.UPDATE_PLAYER_CARDS);
                clientHandler.outputStream.writeObject(GameState.playerCards);
                clientHandler.outputStream.flush();
            }
        }
        clearRoundData();
    }
    private static void assignNextStoryteller() {
        Integer currentStorytellerIndex = GameState.storytellerIndex;
        GameState.storytellerIndex = (currentStorytellerIndex + 1) % ClientHandler.clientHandlers.size();
        GameState.storytellerUsername = ClientHandler.clientHandlers.get(GameState.storytellerIndex).getClientUsername();
    }
    private static void clearRoundData() {
        GameState.description = "";
        GameState.storytellerCardId = "";
        GameState.submittedCards.clear();
        GameState.playerCardId = "";
        GameState.playersVotedCard.replaceAll((k, v) -> null);
        GameState.playersVotes.replaceAll((k, v) -> 0);
        GameState.drawNewCards = false;
        System.out.println("Round data cleared!");
    }
}
