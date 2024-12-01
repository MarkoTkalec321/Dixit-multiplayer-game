package hr.java.game.dixitmultiplayergame.thread;

import hr.java.game.dixitmultiplayergame.model.GameMove;

import java.io.FileNotFoundException;

public class SaveNewGameMoveThread extends GameMoveThread implements Runnable{
    private GameMove gameMove;
    public SaveNewGameMoveThread(GameMove gameMove) {
        this.gameMove = gameMove;
    }

    @Override
    public void run() {
        try {
            saveNewGameMoveToFile(gameMove);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
