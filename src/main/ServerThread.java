package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread {

    private Socket socket;

    private ObjectOutputStream objectOutputStream;

    private ObjectInputStream objectInputStream;

    public ServerThread(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
    }

    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                System.out.println(objectInputStream.readUTF());
                objectOutputStream.writeUTF("Hi Server");
                objectOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
