package hr.java.game.dixitmultiplayergame.client.helpers;

import hr.java.game.dixitmultiplayergame.chat.ChatService;
import hr.java.game.dixitmultiplayergame.jndi.ConfigurationReader;
import hr.java.game.dixitmultiplayergame.model.ConfigurationKey;
import hr.java.game.dixitmultiplayergame.thread.GetLastGameMoveThread;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class ChatController {
    private static ChatService stub;
    private static void refreshChatTextArea(TextArea chatTextArea) {
        List<String> chatHistory = null;
        try {
            chatHistory = stub.returnChatHistory();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder();

        for(String message : chatHistory) {
            sb.append(message);
            sb.append("\n");
        }

        chatTextArea.setText(sb.toString());
    }
    @FXML
    public static void sendChatMessage(TextField chatMessageTextField, String username, TextArea chatTextArea) {
        String chatMessage = chatMessageTextField.getText();
        try {
            stub.sendChatMessage(username + ": " + chatMessage);
            refreshChatTextArea(chatTextArea);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    public static void initializeChatOrLabel(Boolean isSingleplayer, Label lastGameMoveLabel, TextArea chatTextArea) {
        if (!isSingleplayer) {
            try {
                int rmiPort = Integer.parseInt(ConfigurationReader.getValue(ConfigurationKey.RMI_PORT));
                String host = ConfigurationReader.getValue(ConfigurationKey.RMI_HOST);

                Registry registry = LocateRegistry.getRegistry(host, rmiPort);
                stub = (ChatService) registry.lookup(ChatService.REMOTE_OBJECT_NAME);
                System.out.println("Successfully connected to RMI server.");

            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> refreshChatTextArea(chatTextArea)));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.playFromStart();
        } else {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
                Platform.runLater(new GetLastGameMoveThread(lastGameMoveLabel));
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.playFromStart();
        }
    }
}
