package hr.java.game.dixitmultiplayergame.client.helpers;

import hr.java.game.dixitmultiplayergame.client.DixitController;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.List;

public class VisualElementsController {
    private final DixitController dixitController;
    public VisualElementsController(DixitController dixitController) {
        this.dixitController = dixitController;
    }
    public void selectCardEffect(MouseEvent mouseEvent) {
        ImageView card = (ImageView) mouseEvent.getSource();

        if (dixitController.selectedCard != null) {
            dixitController.selectedCard.setEffect(null);
        }
        dixitController.selectedCard = card;
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0.40);
        dixitController.selectedCard.setEffect(colorAdjust);
    }
    public void setArrowForPlayerWithTurn(String playerWithTurn) {
        for (Text playerText : dixitController.playerTextList) {
            Pane parentPane = (Pane) playerText.getParent();
            ImageView arrowImageView = findArrowImageView(parentPane);

            if (arrowImageView != null) {
                boolean isPlayerWithTurn = playerText.getText().equals(playerWithTurn);
                arrowImageView.setVisible(isPlayerWithTurn);
            }
        }
    }
    private ImageView findArrowImageView(Pane pane) {
        for (Node node : pane.getChildren()) {
            if (node instanceof ImageView imageView) {
                String id = imageView.getId();
                if (id != null && id.contains("arrow")) {
                    return imageView;
                }
            }
        }
        throw new IllegalStateException("No arrow found, but it was expected to find one.");
    }
    public void setSubmitButton(String playerWithTurn, Boolean areAllCardsSubmitted) {
        if (dixitController.playerList.size() >= 3) {
            if (dixitController.username.equals(playerWithTurn) && !areAllCardsSubmitted) {
                dixitController.submitButton.setDisable(false);
            } else {
                dixitController.submitButton.setDisable(true);
            }
        }
    }
    public void setButtons() {
        dixitController.submitButton.setDisable(true);
        dixitController.chooseCardButton.setDisable(dixitController.isStoryteller);
    }
    public static Text findPointsPlayerText(Pane pane) {
        for (Node node : pane.getChildren()) {
            if (node instanceof Text text) {
                String id = text.getId();
                if (id != null && id.contains("pointsPlayer")) {
                    return text;
                }
            }
        }
        throw new IllegalStateException("No pointsPlayer found, but it was expected to find one.");
    }
    public static void temporaryHideData(List<Pane> paneViews, ImageView hoveredCard, Text storytellerCardText) {
        paneViews.forEach(pane -> pane.setVisible(false));
        hoveredCard.setVisible(true);
        storytellerCardText.setVisible(true);
    }
    public static void clearClientSideData
            (List<Pane> paneViews, ImageView hoveredCard, Text storytellerCardText,
             TextArea cardDescription, List<String> chosenCards, Button chooseCardButton,
             Text chooseCardText)
    {
        paneViews.forEach(pane -> {
            boolean containsSpecificText = pane.getChildren().stream()
                    .filter(node -> node instanceof Text)
                    .map(node -> (Text) node)
                    .anyMatch(text -> text.getText().contains("Player"));

            pane.setVisible(!containsSpecificText);
        });
        hoveredCard.setVisible(false);
        storytellerCardText.setVisible(false);
        cardDescription.clear();
        chosenCards.clear();
        chooseCardButton.setDisable(true);
        chooseCardText.setVisible(false);
    }
}
