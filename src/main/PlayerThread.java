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

    public PlayerThread(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
    }

    @Override
    public void run() {
        super.run();
        try {
            boolean keepRunning = true;
            while (keepRunning) {
                String command = objectInputStream.readUTF();
                if (command.equals("disconnect")) {
                    socket.close();
                    objectOutputStream.close();
                    objectInputStream.close();
                    keepRunning = false;
                } else if (command.equals("getTurn")) {
                    if (Main.playersInGame.get(Main.playerTurn).equals(this)) {
                        objectOutputStream.writeBoolean(true);
                        objectOutputStream.flush();
                        doTurn();
                        Main.generateNextPlayerTurn();
                    } else {
                        objectOutputStream.writeBoolean(false);
                        objectOutputStream.flush();
                    }
                } else if (command.equals("getResult")) {
                    if (Main.playersInGame.isEmpty()) {
                        System.out.println("Result");
                    } else {
                        objectOutputStream.writeBoolean(false);
                        objectOutputStream.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Main.playersInGame.remove(this);
            System.out.println("Player disconnected: " + socket);
            System.out.println("Current Players: " + Main.playersInGame.size());
        }
    }

    private void doTurn() throws IOException {
        String command = objectInputStream.readUTF();
        if (command.equals("receiveMoreCards")) {
            System.out.println("receive card");
            Card card = Main.deck.takeCard();
            points += card.getSymbol().getPoints();
            objectOutputStream.writeObject(card);
            objectOutputStream.flush();
        } else if (command.equals("standUp")) {
            System.out.println("stand up");
            Main.playersInGame.remove(this);
            Main.playersStandUp.add(this);
        }
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
