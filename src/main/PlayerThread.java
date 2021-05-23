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

    private int position;

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
            e.printStackTrace();
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

        if (Main.playersInGame.get(Main.playerTurn).equals(this)) {
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
        if (Main.playersInGame.isEmpty()) {
            Main.getPlayersResult();
            if (Main.playerWinner == null) {
                objectOutputStream.writeUTF("Draw");
            } else if (Main.playerWinner == this) {
                objectOutputStream.writeUTF("Win");
            } else {
                objectOutputStream.writeUTF("Lose");
            }
        } else {
            objectOutputStream.writeBoolean(false);
        }
        objectOutputStream.flush();
    }

    private void doTurn() throws IOException {
        String command = objectInputStream.readUTF();
        switch (command) {
            case "receiveMoreCards":
                System.out.println("receive card");
                Card card = Main.deck.takeCard();
                points += card.getSymbol().getPoints();
                objectOutputStream.writeUTF("receiveMoreCards");
                objectOutputStream.flush();
                objectOutputStream.writeObject(card);
                objectOutputStream.flush();
                break;
            case "standUp":
                System.out.println("stand up");
                Main.playersInGame.remove(this);
                Main.playersStandUp.add(this);
                break;
            case "disconnect":
                disconnect();
                break;
        }
    }

    public float getPoints() {
        return points;
    }

    @Override
    public int compareTo(PlayerThread o) {
        if (this.points > o.points) {
            return this.points > 7.5f ? -1 : 1;
        } else if (this.points < o.points) {
            return o.points > 7.5f ? 1 : -1;
        }

        return 0;
    }

}
