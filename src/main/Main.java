package main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static List<PlayerThread> playersInGame;

    public static List<PlayerThread> playersStandUp;

    private static List<PlayerThread> playersResult;

    public static int playerTurn = 0;

    public static PlayerThread playerWinner;

    public static Deck deck;

    private final static int SERVER_PORT = 6000;

    public static void main(String[] args) throws IOException {
        playersInGame = new ArrayList<>();
        playersStandUp = new ArrayList<>();
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println("Server open on port " + SERVER_PORT);
        deck = new Deck();

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New player is connected: " + socket);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            PlayerThread playerThread = new PlayerThread(socket, objectOutputStream, objectInputStream);
            playerThread.start();

            playersInGame.add(playerThread);
            System.out.println("Current Players: " + Main.playersInGame.size());
        }
    }

    public static void generateNextPlayerTurn() {
        if (playerTurn + 1 >= playersInGame.size()) {
            playerTurn = 0;
            return;
        }
        playerTurn++;
    }

    public static void getPlayersResult() {
        if (!playersInGame.isEmpty()) {
            return;
        }

        playersResult = new ArrayList<>(playersStandUp);
        Collections.sort(playersResult);

        if (playersResult.size() == 1) {
            playerWinner = playersResult.get(0);
        } else if (playersResult.get(0).getPoints() > playersResult.get(1).getPoints()) {
            playerWinner = playersResult.get(0);
        } else {
            playerWinner = null;
        }

    }

}
