package hr.java.game.dixitmultiplayergame.chat;

import hr.java.game.dixitmultiplayergame.jndi.ConfigurationReader;
import hr.java.game.dixitmultiplayergame.model.ConfigurationKey;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ChatServer {

    public static void main(String[] args) {
        try {
            int rmiPort = Integer.parseInt(ConfigurationReader.getValue(ConfigurationKey.RMI_PORT));
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            ChatService remoteService = new ChatServiceImpl();

            int randomKeyHint = Integer.parseInt(ConfigurationReader.getValue(ConfigurationKey.RANDOM_PORT_HINT));
            ChatService skeleton = (ChatService) UnicastRemoteObject.exportObject(remoteService, randomKeyHint);
            registry.rebind(ChatService.REMOTE_OBJECT_NAME, skeleton);
            System.out.println("Chat service ready!");

            Thread.sleep(Long.MAX_VALUE);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
