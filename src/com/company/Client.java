package com.company;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static java.lang.Thread.sleep;

/**
 * Created by joshua on 30/03/17.
 */
public class Client implements Runnable {

    private DHKeyGenerator dhk;
    private Socket socket;

    private String userID;
    private String password;

    private long[] encryptionKey;
    private static long[] ACCOUNT_ENCRYPTION = {41371378120415169l, -777526119039919309l,
            -4459589122261330969l, 2364346053016476091l};

    public Client(){
        setDhk(new DHKeyGenerator());
    }

    @Override
    public void run() {

        try {
            Socket clientSocket = new Socket("localhost", 16000);
            setSocket(clientSocket);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));

            System.out.print("Username: ");
            setUserID(inFromUser.readLine());
            System.out.print("Password: ");
            setPassword(inFromUser.readLine());

            handshake(outToServer, inFromServer);
            keyExchange(outToServer, inFromServer);
            if(!credentialsExchange(outToServer, inFromServer)){
                return;
            }

            while(true){
                System.out.print("Type a filename to request or \"exit\": ");
                String filename = inFromUser.readLine();
                if(filename.equals("exit")){
                    encryptedWrite("exit",outToServer);
                    sleep(500);
                    break;
                }

                if(requestFiles(filename, outToServer, inFromServer)){
                    filename = encryptedRead(inFromServer);
                    String fileBody = encryptedRead(inFromServer);

                    presentFile(filename,fileBody);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handshake(DataOutputStream outToConnection, BufferedReader inFromConnection) throws IOException {
        outToConnection.writeBytes("Handshake\n");
        String serverStr = inFromConnection.readLine();

        if(!serverStr.equals("ACK")){
            System.out.println("Handshake Failed");
            return;
        }
    }

    public void presentFile(String filename, String fileBody){
        System.out.println("\nFile Found:");
        System.out.println("---------------- " + filename + " ---------------");
        System.out.println(fileBody);
    }

    public boolean requestFiles(String filename, DataOutputStream outToConnection, BufferedReader inFromConnection) throws IOException {
        encryptedWrite(filename, outToConnection);
        if(!encryptedRead(inFromConnection).equals("ACK")){
            System.out.println("404 File Not Found");
            return false;
        }

        return true;
    }

    public boolean credentialsExchange(DataOutputStream outToConnection, BufferedReader inFromConnection) throws IOException {
        encryptedWrite(getUserID(), outToConnection);
        encryptedWrite(getPassword(), outToConnection);

        String authResponse = encryptedRead(inFromConnection);
        if(authResponse.equals("AuthenticationFailed")){
            System.out.print("Authentication failed, close Client\n\n");
            return false;
        }
        return true;
    }

    public void keyExchange(DataOutputStream outToConnection, BufferedReader inFromConnection) throws IOException {
        outToConnection.writeBytes(getDhk().sharePrime() + "\n");
        outToConnection.writeBytes(getDhk().shareGenerator() + "\n");

        String sharedKey = inFromConnection.readLine();
        getDhk().recieveSharedKey(sharedKey);

        outToConnection.writeBytes(getDhk().shareSharedKey() + "\n");

        long[] encryptionKey = getDhk().generateLongKeyArray();
        setEncryptionKey(encryptionKey);
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

    public void writeToConnection(String message, DataOutputStream outToConnection){
        try {
            outToConnection.writeBytes(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void encryptedWrite(String message, DataOutputStream outToConnection){
        try {
            message = TEA.encrypt(message, getEncryptionKey());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writeToConnection(message, outToConnection);
    }

    public String readFromConnection(BufferedReader reader){
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ReadFailure";
    }

    public String encryptedRead(BufferedReader reader){
        try {
            return TEA.decrypt(readFromConnection(reader), getEncryptionKey());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "EncryptedReadFailure";
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

    public void setEncryptionKey(long[] encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public long[] getEncryptionKey(){
        return this.encryptionKey;
    }
}
