package hr.java.game.dixitmultiplayergame.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class Server {
    private ServerSocket serverSocket;
    private static List<String> deck = new ArrayList<>();
    public static final int CARDS_PER_PLAYER = 6;
    public static final int MAX_PLAYERS = 6;
    public static int maxPoints = 10;
    public static final String SAVE_GAME_FILE_NAME = "saveGame/gameSave.dat";
    public static void initializeDeck() throws IOException, URISyntaxException {
        Path resourcePath = Paths.get(Objects.requireNonNull(Server.class.getResource("/images")).toURI());
        try (var paths = Files.list(resourcePath)) {
            deck = paths
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());

            Collections.shuffle(deck);
        }
    }
    public static List<String> drawCards() {
        if (deck.size() < CARDS_PER_PLAYER) {
            throw new IllegalStateException("Not enough cards in the deck");
        }
        List<String> drawnCards = new ArrayList<>();
        for (int i = 0; i < CARDS_PER_PLAYER; i++) {
            drawnCards.add(deck.remove(0));
        }

        return drawnCards;
    }
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public void startServer() {
        try {
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void closeServerSocket() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeEverything(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        try {
            if(inputStream != null) {
                inputStream.close();
            }
            if(outputStream != null) {
                outputStream.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        initializeDeck();

        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}