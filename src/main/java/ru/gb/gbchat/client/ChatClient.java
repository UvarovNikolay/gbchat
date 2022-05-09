package ru.gb.gbchat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private final ClientController controller;

    public ChatClient(ClientController controller) {
        this.controller = controller;
    }

    public void openConnection() throws IOException {
        socket = new Socket("localhost", 8189);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                waitAuth();
                readMessage();
            } finally {
                closeConnection();
            }
        }).start();
    }

    private void closeConnection() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка отключения", e);
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка отключения", e);
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка отключения", e);
        }
    }

    private void readMessage() {
        while (true) {
            try {
                final String msg = in.readUTF();
                if ("/end".equals(msg)) {
                    controller.toggleBoxesVisibility(false);
                    break;
                }
                controller.addMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitAuth() {
        while (true) {
            try {
                final String msg = in.readUTF();
                if (msg.startsWith("/auth")) {
                    final String[] split = msg.split(" ");
                    final String nick = split[1];
                    controller.toggleBoxesVisibility(true);
                    controller.addMessage("Успешная авторизация под ником " + nick);
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}