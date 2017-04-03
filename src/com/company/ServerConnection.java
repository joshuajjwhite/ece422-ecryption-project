package com.company;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by joshua on 30/03/17.
 */
public class ServerConnection implements Runnable {

    private Socket socket;
    private DHKeyGenerator dhk;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;

    public ServerConnection(Socket socket){
        setSocket(socket);
    }

    @Override
    public void run() {
        try {
            handshake();

            System.out.println("Closing Server Connection");
            getSocket().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handshake() throws IOException {
        BufferedReader inFromClient =
                new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
        DataOutputStream outToClient = new DataOutputStream(getSocket().getOutputStream());
        //setInFromClient(inFromClient);
        //setOutToClient(outToClient);

        String clientStr = inFromClient.readLine();
        if(clientStr.equals("Handshake")){
            outToClient.writeBytes("Ack\n");
        }
        else{
            System.out.print("Server didn't get handshake");
        }
    }

    public void thing() throws Exception{

        String clientSentence;
        String capitalizedSentence;

        BufferedReader inFromClient =
                new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
        DataOutputStream outToClient = new DataOutputStream(getSocket().getOutputStream());
        clientSentence = inFromClient.readLine();
        System.out.println("Received: " + clientSentence);
        capitalizedSentence = clientSentence.toUpperCase() + '\n';
        outToClient.writeBytes(capitalizedSentence);

        System.out.println("Closing Server Connection");
        getSocket().close();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setDhk(DHKeyGenerator dhk){
        this.dhk = dhk;
    }

    public DHKeyGenerator getDhk(){
        return this.dhk;
    }

    public BufferedReader getInFromClient() {
        return inFromClient;
    }

    public void setInFromClient(BufferedReader inFromClient) {
        this.inFromClient = inFromClient;
    }

    public DataOutputStream getOutToClient() {
        return outToClient;
    }

    public void setOutToClient(DataOutputStream outToClient) {
        this.outToClient = outToClient;
    }
}
