package hr.java.game.dixitmultiplayergame.client.helpers;

import hr.java.game.dixitmultiplayergame.client.Client;
import hr.java.game.dixitmultiplayergame.model.GameMove;
import hr.java.game.dixitmultiplayergame.server.Server;
import hr.java.game.dixitmultiplayergame.thread.SaveNewGameMoveThread;
import hr.java.game.dixitmultiplayergame.util.DialogUtils;
import hr.java.game.dixitmultiplayergame.util.XmlUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ButtonController {
    private static String nonStorytellerSelectedCardFilename = "";
    private static Integer cardCounter = 0;
    private static List<GameMove> gameMoves = new ArrayList<>();
    public static void submitCard (List<ImageView> playerCardViews, ImageView selectedCard, List<String> playerCards,
                                   Boolean isStoryteller, TextArea cardDescription, Client client, String username) throws IOException {

        playerCardViews.forEach(card -> card.setDisable(false));

        if (selectedCard == null) {
            DialogUtils.showUnsuccessfulDialog("You must select a card.");
            return;
        }
        int selectedIndex = playerCardViews.indexOf(selectedCard);
        if (selectedIndex == -1 || selectedIndex >= playerCards.size()) {
            DialogUtils.showUnsuccessfulDialog("Selected card is invalid.");
            return;
        }
        String selectedCardFilename = playerCards.get(selectedIndex);

        if (isStoryteller) {
            String description = cardDescription.getText();
            if (description.isEmpty()) {
                DialogUtils.showUnsuccessfulDialog("Description can't be empty.");
                return;
            }
            if (description.length() > 150) {
                DialogUtils.showUnsuccessfulDialog("Description can't exceed 150 characters.");
                return;
            }

            client.sendSubmittedCardAndDescriptionToServer(description, selectedCardFilename);
            selectedCard.setVisible(false);

        } else {

            client.sendSubmittedCardToServer(selectedCardFilename);
            nonStorytellerSelectedCardFilename = selectedCardFilename;
            selectedCard.setVisible(false);
        }

        DialogUtils.showSuccessDialog("Successfully submitted.");

        cardCounter++;

        if (cardCounter == Server.CARDS_PER_PLAYER) {
            cardCounter = 0;
            client.sendDrawNewCards(username);
        }
    }
    public static void submitSingleplayer (ImageView selectedCard, List<ImageView> playerCardViews, List<String> playerCards) {
        if (selectedCard == null) {
            DialogUtils.showUnsuccessfulDialog("You must select a card.");
            return;
        }
        int selectedIndex = playerCardViews.indexOf(selectedCard);
        if (selectedIndex == -1 || selectedIndex >= playerCards.size()) {
            DialogUtils.showUnsuccessfulDialog("Selected card is invalid.");
            return;
        }
        String selectedCardFilename = playerCards.get(selectedIndex);

        GameMove gameMove = new GameMove();
        gameMove.setCardName(selectedCardFilename);
        gameMove.setLocalDateTime(LocalDateTime.now());

        gameMoves.add(gameMove);
        XmlUtils.saveGameMovesToXml(gameMoves);

        SaveNewGameMoveThread saveNewGameMoveThread = new SaveNewGameMoveThread(gameMove);
        Thread thread = new Thread(saveNewGameMoveThread);
        thread.start();

        selectedCard.setVisible(false);
    }
    public static void replayGame (Boolean isSingleplayer, List<ImageView> playerCardViews, List<String> playerCards) {
        if (isSingleplayer) {

            playerCardViews.forEach(card -> card.setVisible(true));

            List<GameMove> gameMovesList = XmlUtils.readGameMovesFromXml();
            AtomicInteger moveIndex = new AtomicInteger(0);

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                GameMove gameMove = gameMovesList.get(moveIndex.get());
                String cardFilename = gameMove.getCardName();

                for (int i = 0; i < playerCards.size(); i++) {
                    if (playerCards.get(i).equals(cardFilename)) {
                        ImageView cardView = playerCardViews.get(i);

                        cardView.setVisible(false);
                        break;
                    }
                }
                moveIndex.set(moveIndex.get() + 1);
            }));

            timeline.setCycleCount(gameMovesList.size());
            timeline.playFromStart();
        } else {
            DialogUtils.showUnsuccessfulDialog("Replay only works in singleplayer mode!");
        }
    }
    public static void chooseCard (ImageView selectedCard, List<ImageView> chosenCardViews, List<String> chosenCards,
                                   Client client, String username, String votedCard, Button chooseCardButton) throws IOException {
        if (selectedCard == null) {
            DialogUtils.showUnsuccessfulDialog("You must select a card.");
            return;
        }
        int selectedIndex = chosenCardViews.indexOf(selectedCard);
        if (selectedIndex == -1 || selectedIndex >= chosenCards.size()) {
            DialogUtils.showUnsuccessfulDialog("Selected card is invalid.");
            return;
        }

        if (votedCard == null) {
            String chosenCardFilename = chosenCards.get(selectedIndex);

            if(nonStorytellerSelectedCardFilename.equals(chosenCardFilename)) {
                DialogUtils.showUnsuccessfulDialog("You can't choose your own card.");
                return;
            }

            client.sendChosenCardToServer(chosenCardFilename, username);
            nonStorytellerSelectedCardFilename = "";
        }

        chooseCardButton.setDisable(true);
        DialogUtils.showSuccessDialog("The card has been chosen.");
    }
    public static void saveToFile(List<String> playerList, Client client) throws IOException {
        if (playerList.size() >= 3) {
            client.sendSaveRequestToServer();
        } else {
            DialogUtils.showUnsuccessfulDialog("Cannot save the game.");
        }
    }
}
