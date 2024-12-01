package hr.java.game.dixitmultiplayergame.server.helpers;

import hr.java.game.dixitmultiplayergame.model.GameState;
import hr.java.game.dixitmultiplayergame.model.MessageType;
import hr.java.game.dixitmultiplayergame.server.ClientHandler;

import java.io.IOException;
import java.io.ObjectInputStream;

public class TurnHandler {
    public static void handleStorytellerTurn(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        String storytellerCardId = (String) inputStream.readObject();
        String description = (String) inputStream.readObject();

        GameState.isGameInProgress = true;

        GameState.storytellerCardId = storytellerCardId;

        ClientHandler.removeCardFromPlayersCardsSnapshot(storytellerCardId);

        GameState.description = description;
        GameState.submittedCards.add(storytellerCardId);

        for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
            if (!clientHandler.getClientUsername().equals(GameState.storytellerUsername)) {
                clientHandler.outputStream.writeObject(MessageType.UPDATE_STORYTELLER_DESCRIPTION);
                clientHandler.outputStream.writeObject(description);
                clientHandler.outputStream.flush();
            }
        }

        if(ClientHandler.isLastPlayerTurn()) {
            ClientHandler.shuffleAndSendSubmittedCardsForVotingPhase(GameState.submittedCards);
        } else {
            ClientHandler.assignNextPlayer();
        }
    }
    public static void handlePlayerTurn(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        String playerCardId = (String) inputStream.readObject();
        GameState.playerCardId = playerCardId;

        ClientHandler.removeCardFromPlayersCardsSnapshot(playerCardId);

        GameState.submittedCards.add(playerCardId);

        if(ClientHandler.isLastPlayerTurn()) {
            ClientHandler.shuffleAndSendSubmittedCardsForVotingPhase(GameState.submittedCards);
        } else {
            ClientHandler.assignNextPlayer();
        }
    }
}
