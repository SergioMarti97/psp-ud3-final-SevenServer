package main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private final static int SERVER_PORT = 6000;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println("Server open on port " + SERVER_PORT);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New player is connected: " + socket);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            ServerThread serverThread = new ServerThread(socket, objectOutputStream, objectInputStream);
            serverThread.start();
        }
    }

}
