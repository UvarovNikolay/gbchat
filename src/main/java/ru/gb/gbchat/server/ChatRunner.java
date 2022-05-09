package ru.gb.gbchat.server;

public class ChatRunner {

    public static void main(String[] args) {
        final  ChatServer server = new ChatServer();
        server.run();
    }
}