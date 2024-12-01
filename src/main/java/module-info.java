module hr.java.game.dixitmultiplayergame {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires java.naming;
    requires java.rmi;

    exports hr.java.game.dixitmultiplayergame.model to com.fasterxml.jackson.databind;

    opens hr.java.game.dixitmultiplayergame.model to com.fasterxml.jackson.databind;

    opens hr.java.game.dixitmultiplayergame to javafx.fxml;

    exports hr.java.game.dixitmultiplayergame.chat to java.rmi;

    exports hr.java.game.dixitmultiplayergame.server to javafx.graphics;
    opens hr.java.game.dixitmultiplayergame.server to javafx.fxml;
    exports hr.java.game.dixitmultiplayergame.client to javafx.graphics;
    opens hr.java.game.dixitmultiplayergame.client to javafx.fxml;
    exports hr.java.game.dixitmultiplayergame.client.helpers to javafx.graphics;
    opens hr.java.game.dixitmultiplayergame.client.helpers to javafx.fxml;
}

