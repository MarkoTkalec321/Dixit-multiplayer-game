package hr.java.game.dixitmultiplayergame.server.helpers;

import hr.java.game.dixitmultiplayergame.model.GameState;
import hr.java.game.dixitmultiplayergame.model.MessageType;
import hr.java.game.dixitmultiplayergame.server.ClientHandler;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class SendDataController {
    public static void sendLoadedDataToClient(ObjectOutputStream outputStream, String clientUsername) throws IOException {
        List<String> clientCards = GameState.playersCards.get(clientUsername);
        List<String> clientCardsSnapshot = GameState.playersCardsSnapshot.get(clientUsername);

        outputStream.writeObject(MessageType.UPDATE_PLAYER_CARDS_LOADED);
        outputStream.writeObject(clientCardsSnapshot);
        outputStream.reset();
        outputStream.flush();

        for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
            clientHandler.outputStream.writeObject(MessageType.SERVER_SENDING_LOADED_DATA);
            clientHandler.outputStream.writeObject(GameState.playersVotedCard);
            clientHandler.outputStream.writeObject(GameState.playersPoints);
            clientHandler.outputStream.writeObject(GameState.submittedCards);
            clientHandler.outputStream.reset();
            clientHandler.outputStream.flush();
        }
    }
    public static void sendPlayerListToClient(ObjectOutputStream outputStream, List<String> usernames) throws IOException {
        outputStream.writeObject(MessageType.UPDATE_PLAYER_LIST);
        outputStream.writeObject(usernames);
        outputStream.flush();
    }
    public static void sendStorytellerUsernameToClient(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeObject(MessageType.UPDATE_STORYTELLER);
        outputStream.writeObject(GameState.storytellerUsername);
        outputStream.flush();
    }
    public static void sendPlayerWithTurnToClient() throws IOException {
        boolean areAllCardsSubmitted = false;
        if (GameState.submittedCards.size() == ClientHandler.clientHandlers.size()) {
            areAllCardsSubmitted = true;
        } else {
            areAllCardsSubmitted = false;
        }
        for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
            clientHandler.outputStream.writeObject(MessageType.UPDATE_PLAYER_WITH_TURN);
            clientHandler.outputStream.writeObject(GameState.playerWithTurn);
            clientHandler.outputStream.writeObject(areAllCardsSubmitted);
            clientHandler.outputStream.flush();
        }
    }
    public static void sendSubmittedCardsForVotingPhase (List<String> submittedCards) throws IOException {
        for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
            clientHandler.outputStream.writeObject(MessageType.SERVER_SEND_SUBMITTED_CARDS_FOR_VOTING);
            clientHandler.outputStream.writeObject(submittedCards);
            clientHandler.outputStream.reset();
            clientHandler.outputStream.flush();
        }
    }
}
