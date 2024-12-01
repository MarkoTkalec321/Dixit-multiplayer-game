package hr.java.game.dixitmultiplayergame.client.helpers;

import hr.java.game.dixitmultiplayergame.client.DixitController;
import hr.java.game.dixitmultiplayergame.util.DialogUtils;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

public class UpdateDataController {
    private final DixitController dixitController;
    public UpdateDataController(DixitController dixitController) {
        this.dixitController = dixitController;
    }
    public void setStorytellerUsername(String username) {
        dixitController.storytellerUsername.setText(username);
    }
    public void setStoryteller(boolean isStoryteller) {
        dixitController.cardDescription.setDisable(!isStoryteller);
        dixitController.isStoryteller = isStoryteller;
    }
    public void setDescription(String description) {
        dixitController.cardDescription.setText(description);
    }
    public void updatePlayerNames(List<String> players, String localUsername) {
        List<String> otherPlayers = new ArrayList<>(players);
        dixitController.playerList = players;
        otherPlayers.remove(localUsername);
        dixitController.defaultPlayer1.setText(localUsername);

        switch(otherPlayers.size()){
            case 0 -> {
                dixitController.defaultPane2.setVisible(false);
                dixitController.defaultPane3.setVisible(false);
                dixitController.defaultPane4.setVisible(false);
                dixitController.defaultPane5.setVisible(false);
                dixitController.defaultPane6.setVisible(false);
                System.out.println("At least 2 more players need to join to start a game.");
            }
            case 1 -> {
                dixitController.defaultPlayer2.setText(otherPlayers.get(0));
                dixitController.defaultPane2.setVisible(true);
                dixitController.defaultPane3.setVisible(false);
                dixitController.defaultPane4.setVisible(false);
                dixitController.defaultPane5.setVisible(false);
                dixitController.defaultPane6.setVisible(false);
                System.out.println("At least 1 more player needs to join to start a game.");
            }
            case 2 -> {
                dixitController.defaultPlayer2.setText(otherPlayers.get(0));
                dixitController.defaultPlayer6.setText(otherPlayers.get(1));
                dixitController.defaultPane2.setVisible(true);
                dixitController.defaultPane6.setVisible(true);
                dixitController.defaultPane3.setVisible(false);
                dixitController.defaultPane4.setVisible(false);
                dixitController.defaultPane5.setVisible(false);
            }
            case 3 -> {
                dixitController.defaultPlayer2.setText(otherPlayers.get(0));
                dixitController.defaultPlayer4.setText(otherPlayers.get(1));
                dixitController.defaultPlayer6.setText(otherPlayers.get(2));
                dixitController.defaultPane2.setVisible(true);
                dixitController.defaultPane4.setVisible(true);
                dixitController.defaultPane6.setVisible(true);
                dixitController.defaultPane3.setVisible(false);
                dixitController.defaultPane5.setVisible(false);
            }
            case 4 -> {
                dixitController.defaultPlayer2.setText(otherPlayers.get(0));
                dixitController.defaultPlayer3.setText(otherPlayers.get(1));
                dixitController.defaultPlayer5.setText(otherPlayers.get(2));
                dixitController.defaultPlayer6.setText(otherPlayers.get(3));
                dixitController.defaultPane2.setVisible(true);
                dixitController.defaultPane3.setVisible(true);
                dixitController.defaultPane5.setVisible(true);
                dixitController.defaultPane6.setVisible(true);
                dixitController.defaultPane4.setVisible(false);
            }
            case 5 -> {
                dixitController.defaultPlayer2.setText(otherPlayers.get(0));
                dixitController.defaultPlayer3.setText(otherPlayers.get(1));
                dixitController.defaultPlayer4.setText(otherPlayers.get(2));
                dixitController.defaultPlayer5.setText(otherPlayers.get(3));
                dixitController.defaultPlayer6.setText(otherPlayers.get(4));
                dixitController.defaultPane2.setVisible(true);
                dixitController.defaultPane3.setVisible(true);
                dixitController.defaultPane4.setVisible(true);
                dixitController.defaultPane5.setVisible(true);
                dixitController.defaultPane6.setVisible(true);
            }
            default -> System.out.println("Player number has to be between 3 and 6!");
        }
        dixitController.playerTextList = List.of(dixitController.defaultPlayer1, dixitController.defaultPlayer2, dixitController.defaultPlayer3,
                dixitController.defaultPlayer4, dixitController.defaultPlayer5, dixitController.defaultPlayer6);
    }
    public void updatePlayerCards(List<String> playerCards) {
        dixitController.playerCards = playerCards;

        for (int i = 0; i < dixitController.playerCardViews.size(); i++) {
            if (i < playerCards.size()) {
                String cardFileName = playerCards.get(i);
                Image cardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + cardFileName)));
                dixitController.playerCardViews.get(i).setImage(cardImage);
                dixitController.playerCardViews.get(i).setVisible(true);
            } else {
                dixitController.playerCardViews.get(i).setVisible(false);
            }
        }
    }
    public void updateCardsForVoting(List<String> cardsForVoting) {
        dixitController.chosenCards = cardsForVoting;
        dixitController.chooseCardText.setVisible(true);

        for (int i = 0; i < cardsForVoting.size(); i++) {
            String cardFileName = cardsForVoting.get(i);
            Image cardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + cardFileName)));
            dixitController.chosenCardViews.get(i).setVisible(true);
            dixitController.chosenCardViews.get(i).setImage(cardImage);
        }
    }
    public void revealStorytellerCard(String storytellerCard) {
        try {
            String resourcePath = "/images/" + storytellerCard;
            Image cardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)));
            dixitController.hoveredCard.setImage(cardImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        VisualElementsController.temporaryHideData(dixitController.paneViews, dixitController.hoveredCard, dixitController.storytellerCardText);
        dixitController.chosenCardViews.forEach(imageView -> imageView.setVisible(false));
        dixitController.storytellerUsername.setText("");

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            VisualElementsController.clearClientSideData(dixitController.paneViews, dixitController.hoveredCard,
                    dixitController.storytellerCardText, dixitController.cardDescription,
                    dixitController.chosenCards, dixitController.chooseCardButton,
                    dixitController.chooseCardText);
        });

        delay.play();
    }
    public void updatePoints(HashMap<String, Integer> allPlayersPoints) {
        for (Map.Entry<String, Integer> entry : allPlayersPoints.entrySet()) {
            String playerUsername = entry.getKey();
            Integer points = entry.getValue();

            for (Text playerText : dixitController.playerTextList) {
                if (playerText.getText().equals(playerUsername)) {
                    Pane parentPane = (Pane) playerText.getParent();
                    Text pointsPlayer = VisualElementsController.findPointsPlayerText(parentPane);

                    if (pointsPlayer != null) {
                        pointsPlayer.setText(points + "/10");
                    }
                }
            }
        }
    }
    public void endGame(List<String> winners) {
        StringBuilder winnersMessage = new StringBuilder("Game Over\n");
        if (winners.size() == 1) {
            winnersMessage.append("Winner: ").append(winners.get(0));
        } else {
            winnersMessage.append("Winners: ");
            for (String winner : winners) {
                winnersMessage.append(winner).append(", ");
            }
            winnersMessage.setLength(winnersMessage.length() - 2);
        }
        DialogUtils.showWinnerDialog(winnersMessage.toString());
        System.exit(0);
    }
    public void loadMaps(HashMap<String, String> playersVotedCard, HashMap<String, Integer> playersPoints, List<String> cardsForVoting) {
        updatePoints(playersPoints);
        updateCardsForVoting(cardsForVoting);

        for (Map.Entry<String, String> entry : playersVotedCard.entrySet()) {
            String username = entry.getKey();
            String votedCard = entry.getValue();

            if (votedCard != null) {
                dixitController.votedCard = votedCard;
            }
        }
    }
}