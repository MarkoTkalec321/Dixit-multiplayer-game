package hr.java.game.dixitmultiplayergame.client;

import hr.java.game.dixitmultiplayergame.client.helpers.ButtonController;
import hr.java.game.dixitmultiplayergame.client.helpers.ChatController;
import hr.java.game.dixitmultiplayergame.client.helpers.VisualElementsController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.*;

public class DixitController{
    @FXML
    private ImageView playerCard1;
    @FXML
    private ImageView playerCard2;
    @FXML
    private ImageView playerCard3;
    @FXML
    private ImageView playerCard4;
    @FXML
    private ImageView playerCard5;
    @FXML
    private ImageView playerCard6;
    @FXML
    private ImageView chosenCard1;
    @FXML
    private ImageView chosenCard2;
    @FXML
    private ImageView chosenCard3;
    @FXML
    private ImageView chosenCard4;
    @FXML
    private ImageView chosenCard5;
    @FXML
    private ImageView chosenCard6;
    @FXML
    public ImageView hoveredCard;
    @FXML
    public Text defaultPlayer1;
    @FXML
    public Text defaultPlayer2;
    @FXML
    public Text defaultPlayer3;
    @FXML
    public Text defaultPlayer4;
    @FXML
    public Text defaultPlayer5;
    @FXML
    public Text defaultPlayer6;
    @FXML
    public Pane defaultPane1;
    @FXML
    public Pane defaultPane2;
    @FXML
    public Pane defaultPane3;
    @FXML
    public Pane defaultPane4;
    @FXML
    public Pane defaultPane5;
    @FXML
    public Pane defaultPane6;
    @FXML
    private Pane defaultPaneCards;
    @FXML
    private Pane defaultStorytellerPane;
    @FXML
    public TextArea cardDescription;
    @FXML
    public Text storytellerUsername;
    @FXML
    public Button submitButton;
    @FXML
    public Button chooseCardButton;
    @FXML
    public Text chooseCardText;
    @FXML
    public Text storytellerCardText;
    @FXML
    private TextField chatMessageTextField;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private Label lastGameMoveLabel;
    @FXML
    private Button singleplayerButton;
    public List<ImageView> playerCardViews;
    public List<ImageView> chosenCardViews;
    public List<Pane> paneViews;
    private Client client;
    public String username;
    private Boolean isSingleplayer;
    public List<String> playerList;
    public boolean isStoryteller;
    public ImageView selectedCard;
    public List<String> playerCards;
    public List<String> chosenCards;
    public List<Text> playerTextList;
    public String votedCard;
    private VisualElementsController visualElementsController;

    @FXML
    private void initialize() {
        hoveredCard.setVisible(false);
        ImageView[] imageViews = {playerCard1, playerCard2, playerCard3, playerCard4,
                playerCard5, playerCard6, chosenCard1, chosenCard2, chosenCard3,
                chosenCard4, chosenCard5, chosenCard6};

        for (ImageView imageView : imageViews) {
            imageView.setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    showHoveredCard(event);
                }
            });
            imageView.setOnMouseReleased(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    hideHoveredCard(event);
                }
            });
        }
        visualElementsController = new VisualElementsController(this);

        playerCardViews = List.of(playerCard1, playerCard2, playerCard3, playerCard4, playerCard5, playerCard6);
        chosenCardViews = List.of(chosenCard1, chosenCard2, chosenCard3, chosenCard4, chosenCard5, chosenCard6);
        paneViews = List.of(defaultPane1, defaultPane2, defaultPane3, defaultPane4, defaultPane5, defaultPane6, defaultPaneCards, defaultStorytellerPane);
    }
    private void showHoveredCard(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            hoveredCard.setImage(((ImageView) event.getSource()).getImage());
            hoveredCard.setVisible(true);
        }
    }
    private void hideHoveredCard(MouseEvent event) {
        if(event.getButton() == MouseButton.SECONDARY){
            hoveredCard.setVisible(false);
        }
    }
    public void initializeClient(Client client, String username, Boolean isSingleplayer) {
        this.client = client;
        this.username = username;
        this.isSingleplayer = isSingleplayer;
        singleplayerButton.setVisible(isSingleplayer);

        ChatController.initializeChatOrLabel(isSingleplayer, lastGameMoveLabel, chatTextArea);
    }
    @FXML
    private void sendChatMessage() {ChatController.sendChatMessage(chatMessageTextField, username, chatTextArea);}
    @FXML
    private void submitCard() throws IOException {ButtonController.submitCard(playerCardViews, selectedCard, playerCards, isStoryteller, cardDescription, client, username);}
    @FXML
    private void submitSingleplayer() {ButtonController.submitSingleplayer(selectedCard, playerCardViews, playerCards);}
    @FXML
    private void replayGame() {ButtonController.replayGame(isSingleplayer, playerCardViews, playerCards);}
    @FXML
    private void chooseCard() throws IOException {ButtonController.chooseCard(selectedCard, chosenCardViews, chosenCards, client, username, votedCard, chooseCardButton);}
    @FXML
    public void saveToFile() throws IOException { ButtonController.saveToFile(playerList, client);}
    @FXML
    public void selectCardEffect(MouseEvent mouseEvent) {
        visualElementsController.selectCardEffect(mouseEvent);
    }
}