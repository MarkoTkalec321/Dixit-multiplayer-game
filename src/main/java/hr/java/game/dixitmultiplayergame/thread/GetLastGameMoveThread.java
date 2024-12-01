package hr.java.game.dixitmultiplayergame.thread;

import hr.java.game.dixitmultiplayergame.model.GameMove;
import javafx.scene.control.Label;

public class GetLastGameMoveThread extends GameMoveThread implements Runnable{
    private Label label;

    public GetLastGameMoveThread(Label label) {
        this.label = label;
    }

    @Override
    public void run() {
        GameMove lastGameMove = getLastGameMoveFromFile();
        label.setText("Last game move: "
                + lastGameMove.getCardName() + "; "
                + lastGameMove.getFormattedLocalDateTime());
    }
}
