package hr.java.game.dixitmultiplayergame.thread;

import hr.java.game.dixitmultiplayergame.model.GameMove;
import hr.java.game.dixitmultiplayergame.util.GameMoveUtils;

import java.io.FileNotFoundException;

public abstract class GameMoveThread {
    private static Boolean gameMoveFileAccessInProgress = false;
    protected synchronized void saveNewGameMoveToFile(GameMove gameMove) throws FileNotFoundException {
        while(gameMoveFileAccessInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        gameMoveFileAccessInProgress = true;

        GameMoveUtils.saveNewGameMove(gameMove);

        gameMoveFileAccessInProgress = false;

        notifyAll();
    }
    protected synchronized GameMove getLastGameMoveFromFile() {
        while(gameMoveFileAccessInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        gameMoveFileAccessInProgress = true;

        GameMove lastGameMove = GameMoveUtils.getLastGameMove();

        gameMoveFileAccessInProgress = false;

        notifyAll();

        return lastGameMove;
    }
}
