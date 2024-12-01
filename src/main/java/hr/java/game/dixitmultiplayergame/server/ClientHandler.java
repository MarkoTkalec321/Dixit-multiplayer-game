package hr.java.game.dixitmultiplayergame.server;

import hr.java.game.dixitmultiplayergame.model.GameState;
import hr.java.game.dixitmultiplayergame.model.GameStateData;
import hr.java.game.dixitmultiplayergame.model.MessageType;
import hr.java.game.dixitmultiplayergame.server.helpers.EndRoundController;
import hr.java.game.dixitmultiplayergame.server.helpers.SendDataController;
import hr.java.game.dixitmultiplayergame.server.helpers.TurnHandler;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    public String clientUsername;
    public ObjectOutputStream outputStream;
    public ObjectInputStream inputStream;

    public ClientHandler(Socket socket) throws IOException, ClassNotFoundException {
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                Object object = inputStream.readObject();
                //System.out.println("Received object: " + object);

                if (object instanceof MessageType messageType) {
                    switch (messageType) {
                        case ASSIGN_PLAYER -> assignPlayerAndInitialize();
                        case STORYTELLER_CARD_AND_DESCRIPTION_SELECTED -> TurnHandler.handleStorytellerTurn(inputStream);
                        case PLAYER_CARD_SELECTED -> TurnHandler.handlePlayerTurn(inputStream);
                        case CHOSEN_CARD_SELECTED -> EndRoundController.handleChosenCardSelected(inputStream);
                        case DRAW_CARDS -> drawCardsForPlayer();
                        case SEND_SAVE_REQUEST_TO_SERVER -> handleSaveRequest();
                        default -> System.out.println("Unknown message type received.");
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Connection lost with " + clientUsername);
            closeEverything(socket, inputStream, outputStream);
        }
    }
    private void assignPlayerAndInitialize() throws IOException, ClassNotFoundException {
        this.clientUsername = (String) inputStream.readObject();
        GameStateData loadedGameStateData = (GameStateData) inputStream.readObject();
        Boolean singleplayerMode = (Boolean) inputStream.readObject();

        if (!singleplayerMode) {
            if (clientHandlers.size() < Server.MAX_PLAYERS) {
                if (GameState.isGameInProgress) {
                    outputStream.writeObject(MessageType.GAME_ALREADY_IN_PROGRESS);
                    outputStream.flush();
                    closeEverything(socket, inputStream, outputStream);
                } else {
                    clientHandlers.add(this);

                    if (loadedGameStateData != null) {
                        getAllClientUsernamesAndSendToServer();
                        loadData(loadedGameStateData);
                        SendDataController.sendLoadedDataToClient(outputStream, clientUsername);
                        assignStoryteller(loadedGameStateData.storytellerUsername);
                        assignPlayerWithTurn(loadedGameStateData.playerWithTurn);
                    } else {
                        assignStorytellerAndPlayerWithTurnAndSendUsernamesToClient();
                        drawInitialCards();
                    }
                    SendDataController.sendStorytellerUsernameToClient(outputStream);
                    SendDataController.sendPlayerWithTurnToClient();
                }
            } else {
                outputStream.writeObject(MessageType.MAX_PLAYERS_REACHED);
                outputStream.flush();
                closeEverything(socket, inputStream, outputStream);
            }
        } else {
            clientHandlers.add(this);
            drawInitialCards();
            getAllClientUsernamesAndSendToServer();
        }
    }
    private void assignStorytellerAndPlayerWithTurnAndSendUsernamesToClient() throws IOException {
        if (clientHandlers.size() == 1) {
            String storytellerUsername = clientHandlers.get(0).clientUsername;
            assignStoryteller(storytellerUsername);
            assignPlayerWithTurn(storytellerUsername);
        }

        loadData(null);

        getAllClientUsernamesAndSendToServer();
    }
    private void assignStoryteller(String storytellerUsername) {
        GameState.storytellerUsername = storytellerUsername;
        GameState.storytellerIndex = getPlayerIndex(storytellerUsername);
    }
    private void loadData(GameStateData loadedGameStateData) {
        if (loadedGameStateData != null) {
            GameState.playersVotedCard = new HashMap<>(loadedGameStateData.playersVotedCard);
            GameState.playersPoints = new HashMap<>(loadedGameStateData.playersPoints);
            GameState.playersVotes = new HashMap<>(loadedGameStateData.playersVotes);
            GameState.submittedCards = loadedGameStateData.submittedCards;
            GameState.storytellerCardId = loadedGameStateData.storytellerCardId;
            GameState.description = loadedGameStateData.description;

            GameState.playersCards = new TreeMap<>(loadedGameStateData.playersCards);
            GameState.playersCardsSnapshot = new TreeMap<>(loadedGameStateData.playersCardsSnapshot);

        } else {
            GameState.playersVotedCard.put(clientUsername, null);
            GameState.playersPoints.put(clientUsername, 0);
            GameState.playersVotes.put(clientUsername, 0);
        }
    }
    private void getAllClientUsernamesAndSendToServer() throws IOException {
        List<String> usernames = clientHandlers.stream()
                .map(ClientHandler::getClientUsername)
                .collect(Collectors.toList());

        for (ClientHandler clientHandler : clientHandlers) {
            SendDataController.sendPlayerListToClient(clientHandler.outputStream, usernames);
        }
    }
    public String getClientUsername() {
        return clientUsername;
    }
    private void drawInitialCards() throws IOException {
        GameState.playerCards = Server.drawCards();
        List<String> temp = new ArrayList<>(GameState.playerCards);

        GameState.playersCards.put(clientUsername, GameState.playerCards);
        GameState.playersCardsSnapshot.put(clientUsername, temp);
        outputStream.writeObject(MessageType.UPDATE_PLAYER_CARDS);
        outputStream.writeObject(GameState.playerCards);
        outputStream.flush();
    }
    private void drawCardsForPlayer() throws IOException, ClassNotFoundException {
        GameState.drawNewCards = true;
    }
    public static void removeCardFromPlayersCardsSnapshot(String cardId) {
        for (Map.Entry<String, List<String>> entry : GameState.playersCardsSnapshot.entrySet()) {
            List<String> cards = entry.getValue();

            if (cards.contains(cardId)) {
                cards.remove(cardId);
                break;
            }
        }
    }
    public static Boolean isLastPlayerTurn() {
        int storytellerIndex = getPlayerIndex(GameState.storytellerUsername);
        int lastPlayerIndex = (storytellerIndex - 1 + clientHandlers.size()) % clientHandlers.size();
        return GameState.currentPlayerTurnIndex == lastPlayerIndex;
    }
    public static void shuffleAndSendSubmittedCardsForVotingPhase(List<String> submittedCards) throws IOException {
        Collections.shuffle(submittedCards);
        SendDataController.sendSubmittedCardsForVotingPhase(submittedCards);
    }
    public static void assignNextPlayer() throws IOException {
        GameState.currentPlayerTurnIndex = (GameState.currentPlayerTurnIndex + 1) % clientHandlers.size();
        String nextPlayerUsername = clientHandlers.get(GameState.currentPlayerTurnIndex).getClientUsername();
        assignPlayerWithTurn(nextPlayerUsername);

        SendDataController.sendPlayerWithTurnToClient();
    }
    public static void assignPlayerWithTurn(String username) {
        GameState.playerWithTurn = username;
        GameState.currentPlayerTurnIndex = getPlayerIndex(username);
    }
    private static int getPlayerIndex(String username) {
        for (int i = 0; i < clientHandlers.size(); i++) {
            if (clientHandlers.get(i).getClientUsername().equals(username)) {
                return i;
            }
        }
        return -1;
    }
    private void closeEverything(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        Server.closeEverything(socket, inputStream, outputStream);
    }
    private void handleSaveRequest() throws IOException {GameState.saveGameState();}
}