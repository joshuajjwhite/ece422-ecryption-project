package com.company;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;

/**
 * Created by joshua on 30/03/17.
 */
public class Client implements Runnable {

    private DHKeyGenerator dhk;
    private Socket socket;
    private BufferedReader inFromServer = null;
    private DataOutputStream outToServer = null;

    private String userID;
    private String password;

    public Client(String userID, String password){
        setDhk(new DHKeyGenerator());
        setUserID(userID);
        setPassword(password);
    }

    @Override
    public void run() {

        try {
            handshake();
            //getSocket().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handshake() throws IOException {
        Socket clientSocket = new Socket("localhost", 16000);
        setSocket(clientSocket);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        //setInFromServer(inFromServer);
        //setOutToServer(outToServer);

        System.out.println("Handshake");
        outToServer.writeBytes("Handshake\n");
        String serverStr = inFromServer.readLine();

        if(serverStr.equals("Ack")){
            System.out.print("Handshake Established\n");
        }

        //Key Exchange
        //System.out.println(getDhk().sharePrime());
        //System.out.println(getDhk().shareGenerator());

        outToServer.writeBytes(getDhk().sharePrime() + "\n");
        outToServer.writeBytes(getDhk().shareGenerator() + "\n");

        String sharedKey = inFromServer.readLine();
        getDhk().recieveSharedKey(sharedKey);

        outToServer.writeBytes(getDhk().shareSharedKey() + "\n");

        try {
            TEA.encrypt("Hello World", getDhk().generateLongKeyArray());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void thing() throws Exception{


        String sentence;
        String modifiedSentence;
        System.out.print("Waiting for input: ");
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        Socket clientSocket = new Socket("localhost", 16000);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        sentence = inFromUser.readLine();
        outToServer.writeBytes(sentence + '\n');
        modifiedSentence = inFromServer.readLine();
        System.out.println("FROM SERVER: " + modifiedSentence);
        System.out.println("Closing Client Socket");
        clientSocket.close();


    }



    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public DHKeyGenerator getDhk() {
        return dhk;
    }

    public void setDhk(DHKeyGenerator dhk) {
        this.dhk = dhk;
    }

    public BufferedReader getInFromServer() {
        return inFromServer;
    }

    public void setInFromServer(BufferedReader inFromServer) {
        this.inFromServer = inFromServer;
    }

    public OutputStream getOutToServer() {
        return outToServer;
    }

    public void setOutToServer(DataOutputStream outToServer) {
        this.outToServer = outToServer;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long[] keyArray(){
        return getDhk().generateLongKeyArray();
    }
}
