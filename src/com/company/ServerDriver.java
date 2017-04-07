package com.company;

/**
 * Created by joshua on 06/04/17.
 */
public class ServerDriver {

    public static void main(String[] args){
        System.out.print("Server Started, press Ctrl-C to stop: ");
        Thread server = new Thread(new Server());
        server.start();
    }
}
