package com.company;

import java.io.*;
import java.net.*;


/**
 * Created by joshua on 30/03/17.
 */
public class Server implements Runnable {

    static final int PORT = 16000;

    public Server(){}

    @Override
    public void run() {

        try {
            runServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void runServer(){
        //http://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(16000);
        } catch (IOException e) {
            e.printStackTrace();

        }

        int i = 0;

        while (true) {

            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            new Thread(new ServerConnection(socket)).start();
            i++;
        }
    }


}
