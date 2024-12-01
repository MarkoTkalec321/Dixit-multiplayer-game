package hr.java.game.dixitmultiplayergame.client;

import hr.java.game.dixitmultiplayergame.client.helpers.UpdateDataController;
import hr.java.game.dixitmultiplayergame.client.helpers.VisualElementsController;
import hr.java.game.dixitmultiplayergame.model.GameStateData;
import hr.java.game.dixitmultiplayergame.model.MessageType;
import hr.java.game.dixitmultiplayergame.server.Server;
import hr.java.game.dixitmultiplayergame.util.DialogUtils;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String username;
    public Client(Socket socket, String username, GameStateData gameStateData, Boolean isSingleplayer) throws IOException {
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.socket = socket;
        this.username = username;

        addPlayerToServerAndInitialize(gameStateData, isSingleplayer);

    }
    public void listenForUpdates(DixitController controller, UpdateDataController updateDataController,
                                 VisualElementsController visualElementsController) {
        new Thread(() -> {
            try {
                while (!socket.isClosed() && socket.isConnected()) {
                    Object object = inputStream.readObject();
                    //System.out.println("Received object: " + object.toString());
                    handleMessage(object, controller, updateDataController, visualElementsController);
                }
            } catch (EOFException e) {
                System.out.println("Connection closed by the server.");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void handleMessage(Object object, DixitController controller, UpdateDataController updateDataController,
                               VisualElementsController visualElementsController) throws IOException, ClassNotFoundException {
        if (object instanceof MessageType messageType) {
            switch (messageType) {
                case MAX_PLAYERS_REACHED ->  handleMaxPlayersReached();
                case GAME_ALREADY_IN_PROGRESS -> handleGameAlreadyInProgress();
                case UPDATE_PLAYER_LIST -> handleUpdatePlayerList(updateDataController);
                case UPDATE_PLAYER_CARDS -> handleUpdatePlayerCards(updateDataController);
                case UPDATE_STORYTELLER -> handleUpdateStoryteller(updateDataController);
                case UPDATE_PLAYER_WITH_TURN -> handleUpdatePlayerWithTurn(visualElementsController);
                case UPDATE_STORYTELLER_DESCRIPTION -> handleUpdateStorytellerDescription(updateDataController);
                case SERVER_SEND_SUBMITTED_CARDS_FOR_VOTING -> handleSubmittedCardsForVoting(updateDataController, visualElementsController);
                case UPDATE_POINTS -> handleUpdatePoints(updateDataController);
                case REVEAL_STORYTELLER_CARD -> handleRevealStorytellerCard(updateDataController);
                case SERVER_SENDING_LOADED_DATA -> handleLoadMaps(updateDataController, visualElementsController);
                case UPDATE_PLAYER_CARDS_LOADED -> handleUpdatePlayerCardsLoaded(updateDataController);
                case GAME_OVER -> handleGameOver(updateDataController);
                default -> System.out.println("Unknown message type received.");
            }
        }
    }
    private void addPlayerToServerAndInitialize(GameStateData gameStateData, Boolean isSingleplayer) throws IOException {
        outputStream.writeObject(MessageType.ASSIGN_PLAYER);
        outputStream.writeObject(username);
        outputStream.writeObject(gameStateData);
        outputStream.writeObject(isSingleplayer);
        outputStream.flush();
    }
    private void handleMaxPlayersReached() {
        Platform.runLater(() -> {
            DialogUtils.showUnsuccessfulDialog("Unable to join the game: max amount of players is 6.");
            Server.closeEverything(socket, inputStream, outputStream);
            System.exit(0);
        });
    }
    private void handleGameAlreadyInProgress() {
        Platform.runLater(() -> {
            DialogUtils.showUnsuccessfulDialog("Unable to join the game: game already in progress.");
            Server.closeEverything(socket, inputStream, outputStream);
            System.exit(0);
        });
    }
    @SuppressWarnings("unchecked")
    private void handleUpdatePlayerList(UpdateDataController updateDataController) throws IOException, ClassNotFoundException {
        List<String> updatedPlayerList = (List<String>) inputStream.readObject();
        Platform.runLater(() -> updateDataController.updatePlayerNames(updatedPlayerList, username));
    }
    @SuppressWarnings("unchecked")
    private void handleUpdatePlayerCards(UpdateDataController updateDataController) throws IOException, ClassNotFoundException {
        List<String> playerCards = (List<String>) inputStream.readObject();
        Platform.runLater(() -> updateDataController.updatePlayerCards(playerCards));
    }
    private void handleUpdateStoryteller(UpdateDataController updateDataController) throws IOException, ClassNotFoundException {
        String storytellerUsername = (String) inputStream.readObject();
        Platform.runLater(() -> updateDataController.setStorytellerUsername(storytellerUsername));
        Platform.runLater(() -> updateDataController.setStoryteller(storytellerUsername.equals(username)));
    }
    private void handleUpdatePlayerWithTurn(VisualElementsController visualElementsController) throws IOException, ClassNotFoundException {
        String playerWithTurn = (String) inputStream.readObject();
        boolean areAllCardsSubmitted = (boolean) inputStream.readObject();
        Platform.runLater(() -> visualElementsController.setArrowForPlayerWithTurn(playerWithTurn));
        Platform.runLater(() -> visualElementsController.setSubmitButton(playerWithTurn, areAllCardsSubmitted));
    }
    private void handleUpdateStorytellerDescription(UpdateDataController updateDataController) throws IOException, ClassNotFoundException {
        String description = (String) inputStream.readObject();
        Platform.runLater(() -> updateDataController.setDescription(description));
    }
    @SuppressWarnings("unchecked")
    private void handleSubmittedCardsForVoting(UpdateDataController updateDataController, VisualElementsController visualElementsController) throws IOException, ClassNotFoundException {
        List<String> cardsForVoting = (List<String>) inputStream.readObject();
        Platform.runLater(() -> updateDataController.updateCardsForVoting(cardsForVoting));
        Platform.runLater(visualElementsController::setButtons);
    }
    @SuppressWarnings("unchecked")
    private void handleUpdatePoints(UpdateDataController updateDataController) throws IOException, ClassNotFoundException {
        HashMap<String, Integer> allPlayersPoints = (HashMap<String, Integer>) inputStream.readObject();
        Platform.runLater(() -> updateDataController.updatePoints(allPlayersPoints));
    }
    private void handleRevealStorytellerCard(UpdateDataController updateDataController) throws IOException, ClassNotFoundException {
        String storytellerCard = (String) inputStream.readObject();
        Platform.runLater(() -> updateDataController.revealStorytellerCard(storytellerCard));
    }
    @SuppressWarnings("unchecked")
    private void handleLoadMaps(UpdateDataController updateDataController, VisualElementsController visualElementsController) throws IOException, ClassNotFoundException {
        HashMap<String, String> playersVotedCard = (HashMap<String, String>) inputStream.readObject();
        HashMap<String, Integer> playersPoints = (HashMap<String, Integer>) inputStream.readObject();
        List<String> cardsForVoting = (List<String>) inputStream.readObject();

        Platform.runLater(() -> updateDataController.loadMaps(playersVotedCard, playersPoints, cardsForVoting));

        if (cardsForVoting != null) {
            Platform.runLater(visualElementsController::setButtons);
        }
    }
    @SuppressWarnings("unchecked")
    private void handleUpdatePlayerCardsLoaded(UpdateDataController updateDataController) throws IOException, ClassNotFoundException {
        List<String> playerCardsSnapshot = (List<String>) inputStream.readObject();
        Platform.runLater(() -> updateDataController.updatePlayerCards(playerCardsSnapshot));
    }
    @SuppressWarnings("unchecked")
    private void handleGameOver(UpdateDataController updateDataController) throws IOException, ClassNotFoundException {
        List<String> winners = (List<String>) inputStream.readObject();
        Platform.runLater(() -> updateDataController.endGame(winners));
    }
    public void sendDrawNewCards(String username) throws IOException {
        outputStream.writeObject(MessageType.DRAW_CARDS);
        outputStream.writeObject(username);
        outputStream.flush();
    }
    public void sendSubmittedCardAndDescriptionToServer(String description, String cardId) throws IOException {
        System.out.println("\nsendSubmittedCardAndDescriptionToServer");
        System.out.println("cardId: " + cardId + " " + "description: " + description);
        outputStream.writeObject(MessageType.STORYTELLER_CARD_AND_DESCRIPTION_SELECTED);
        outputStream.writeObject(cardId);
        outputStream.writeObject(description);
        outputStream.flush();
    }
    public void sendSubmittedCardToServer(String cardId) throws IOException {
        outputStream.writeObject(MessageType.PLAYER_CARD_SELECTED);
        outputStream.writeObject(cardId);
        outputStream.flush();
    }
    public void sendChosenCardToServer(String cardId, String username) throws IOException {
        outputStream.writeObject(MessageType.CHOSEN_CARD_SELECTED);
        outputStream.writeObject(cardId);
        outputStream.writeObject(username);
        outputStream.flush();
    }
    public void sendSaveRequestToServer() throws IOException {
        outputStream.writeObject(MessageType.SEND_SAVE_REQUEST_TO_SERVER);
        DialogUtils.showSuccessDialog("Game has been saved!");
    }
}

