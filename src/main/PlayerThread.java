package main;

import main.card.Card;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PlayerThread extends Thread implements Comparable<PlayerThread> {

    private Socket socket;

    private ObjectOutputStream objectOutputStream;

    private ObjectInputStream objectInputStream;

    private float points;

    private boolean keepRunning;

    public PlayerThread(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
    }

    @Override
    public void run() {
        super.run();
        try {
            keepRunning = true;
            while (keepRunning) {
                bodyOptions();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            Main.playersInGame.remove(this);
            System.out.println("Player disconnected: " + socket);
            System.out.println("Current Players: " + Main.playersInGame.size());
        }
    }

    private void bodyOptions() throws IOException {
        String command = objectInputStream.readUTF();
        switch (command) {
            case "disconnect":
                disconnect();
                break;
            case "getTurn":
                getTurn();
                break;
            case "getResult":
                getResult();
                break;
            case "standUp":
                standUp();
                break;
        }
    }

    private void disconnect() throws IOException {
        socket.close();
        objectOutputStream.close();
        objectInputStream.close();
        keepRunning = false;
    }

    private void getTurn() throws IOException {
        objectOutputStream.writeUTF("getTurn");
        objectOutputStream.flush();

        if (Main.isTurnPlayer(this)) {
            objectOutputStream.writeBoolean(true);
            objectOutputStream.flush();
            doTurn();
            Main.generateNextPlayerTurn();
        } else {
            objectOutputStream.writeBoolean(false);
            objectOutputStream.flush();
        }
    }

    private void getResult() throws IOException {
        objectOutputStream.writeUTF("getResult");

        if (Main.playersInGame.isEmpty()) {
            Main.getPlayersResult();
            if (Main.playerWinner == null) {
                objectOutputStream.writeUTF("draw");
            } else if (Main.playerWinner == this) {
                objectOutputStream.writeUTF("win");
            } else {
                objectOutputStream.writeUTF("lose");
            }
        } else {
            objectOutputStream.writeUTF("inProgress");
        }

        objectOutputStream.flush();
    }

    private void doTurn() throws IOException {
        String command = objectInputStream.readUTF();
        switch (command) {
            case "receiveMoreCards":
                receiveMoreCards();
                break;
            case "standUp":
                standUp();
                break;
            case "disconnect":
                disconnect();
                break;
        }
    }

    private void receiveMoreCards() throws IOException {
        System.out.println("receive card");
        Card card = Main.deck.takeCard();
        points += card.getSymbol().getPoints();
        objectOutputStream.writeUTF("receiveMoreCards");
        objectOutputStream.flush();
        objectOutputStream.writeObject(card);
        objectOutputStream.flush();
    }

    private void standUp() throws IOException {
        System.out.println("stand up");
        objectOutputStream.writeUTF("standUp");
        objectOutputStream.flush();
        Main.playersInGame.remove(this);
        Main.playersStandUp.add(this);
    }

    public float getPoints() {
        return points;
    }

    @Override
    public int compareTo(PlayerThread o) {
        if (this.points > o.points) {
            return this.points > 7.5f ? 1 : -1;
        } else if (this.points < o.points) {
            return o.points > 7.5f ? -1 : 1;
        }

        return 0;
    }

}
