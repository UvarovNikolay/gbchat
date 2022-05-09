package ru.gb.gbchat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private final List<ClientHandler> clients;
    private final AuthService authService;

    public ChatServer() {
        clients = new ArrayList<>();
        authService = new InMemoryAuthService();
        authService.start();
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                System.out.println("Ожидаем подключение клиента...");
                final Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(socket, this, authService);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сервера", e);
        }
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler client: clients) {
            if (client.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    public void sendMsgToNick(String senderNick, String recipientNick, String message) {
        for (ClientHandler client : clients) {
                if (client.getNick().equals(recipientNick)) {
                    client.sendMessage(senderNick + ": " + message);
                }
        }
    }
}