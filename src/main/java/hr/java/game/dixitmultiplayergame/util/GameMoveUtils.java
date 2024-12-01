package hr.java.game.dixitmultiplayergame.util;

import hr.java.game.dixitmultiplayergame.model.GameMove;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameMoveUtils {
    public static final String GAME_MOVE_HISTORY_FILE_NAME = "gameMoves/gameMoves.dat";
    private static List<GameMove> gameMoveList = new ArrayList<>();
    public static void saveNewGameMove (GameMove newGameMove) throws FileNotFoundException {
        gameMoveList.add(newGameMove);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GAME_MOVE_HISTORY_FILE_NAME))) {
            oos.writeObject(gameMoveList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @SuppressWarnings("unchecked")
    public static GameMove getLastGameMove () {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(GAME_MOVE_HISTORY_FILE_NAME)))
        {
            gameMoveList = (List<GameMove>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return gameMoveList.get(gameMoveList.size() - 1);
    }
}
