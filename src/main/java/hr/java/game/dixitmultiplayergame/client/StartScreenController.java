package hr.java.game.dixitmultiplayergame.client;

import hr.java.game.dixitmultiplayergame.client.helpers.UpdateDataController;
import hr.java.game.dixitmultiplayergame.client.helpers.VisualElementsController;
import hr.java.game.dixitmultiplayergame.model.GameState;
import hr.java.game.dixitmultiplayergame.model.GameStateData;
import hr.java.game.dixitmultiplayergame.util.DialogUtils;
import hr.java.game.dixitmultiplayergame.util.DocumentationUtils;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartScreenController extends Application{
    @FXML
    private TextField textFieldPlayerName;
    @FXML
    private CheckBox singleplayer;
    public static Stage mainStage;
    private Map<String, Stage> playerStages = new HashMap<>();

    @Override
    public void stop(){
        System.exit(0);
    }

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hr/java/game/dixitmultiplayergame/startScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Dixit Start Screen");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void joinGame() throws IOException {
        String username = textFieldPlayerName.getText();

        if (username.length() < 3 || username.length() > 10) {
            DialogUtils.showUnsuccessfulDialog("Player name should be between 3 and 10 characters.");
            return;
        }

        Boolean isSingleplayer = singleplayer.isSelected();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hr/java/game/dixitmultiplayergame/dixit.fxml"));
        Parent root = fxmlLoader.load();

        DixitController controller = fxmlLoader.getController();
        UpdateDataController updateDataController = new UpdateDataController(controller);
        VisualElementsController visualElementsController = new VisualElementsController(controller);

        Client client = new Client(new Socket("localhost", 1234), username, null, isSingleplayer);
        client.listenForUpdates(controller, updateDataController, visualElementsController);

        controller.initializeClient(client, username, isSingleplayer);

        showDixitScreen(username, root);
    }
    private void showDixitScreen(String playerName, Parent root) {
        Scene scene = new Scene(root, 921, 761);
        getStage().setTitle("Player: " + playerName);
        getStage().setScene(scene);
        getStage().show();
    }
    @FXML
    private void loadGameFromFile() throws IOException, ClassNotFoundException {
        GameStateData gameStateData = GameState.loadGameState();

        for (Map.Entry<String, List<String>> playerData : gameStateData.playersCards.entrySet()) {
            String username = playerData.getKey();

            if (!playerStages.containsKey(username)) {

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hr/java/game/dixitmultiplayergame/dixit.fxml"));
                Parent root = fxmlLoader.load();

                DixitController controller = fxmlLoader.getController();
                UpdateDataController updateDataController = new UpdateDataController(controller);
                VisualElementsController visualElementsController = new VisualElementsController(controller);

                Client client = new Client(new Socket("localhost", 1234), username, gameStateData, false);
                client.listenForUpdates(controller, updateDataController, visualElementsController);

                controller.initializeClient(client, username, false);

                Stage playerStage = new Stage();
                playerStage.setTitle("Player: " + username);
                playerStage.setScene(new Scene(root, 921, 761));
                playerStages.put(username, playerStage);
                playerStage.show();
            }
        }
        DialogUtils.showSuccessDialog("Data has been loaded!");
        mainStage.close();
    }
    @FXML
    public void generateHtmlDocumentation() {
        DocumentationUtils.generateDocumentation();
    }
    public static Stage getStage(){
        return mainStage;
    }
    public static void main(String[] args) {
        launch(args);
    }

}
