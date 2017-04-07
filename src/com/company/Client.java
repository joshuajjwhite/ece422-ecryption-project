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

    private long[] encryptionKey;

    private static long[] ACCOUNT_ENCRYPTION = {41371378120415169l, -777526119039919309l,
            -4459589122261330969l, 2364346053016476091l};
    private static int PORT = 16000;

    public Client(){}

    @Override
    public void run() {

        try {
            Socket clientSocket = new Socket("localhost", PORT);;
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));

            System.out.print("Username: ");
            String username = inFromUser.readLine();
            System.out.print("Password: ");
            String password = inFromUser.readLine();

            handshake(outToServer, inFromServer);
            keyExchange(outToServer, inFromServer);
            if(!credentialsExchange(outToServer, inFromServer, username, password)){
                return;
            }

            while(true){
                System.out.print("\nType a filename to request or \"exit\": ");
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

    public boolean credentialsExchange(DataOutputStream outToConnection, BufferedReader inFromConnection, String username, String password) throws IOException {
        encryptedWrite(username, outToConnection);
        encryptedWrite(password, outToConnection);

        String authResponse = encryptedRead(inFromConnection);
        if(authResponse.equals("AuthenticationFailed")){
            System.out.print("Authentication failed, close Client\n\n");
            return false;
        }
        return true;
    }

    public void keyExchange(DataOutputStream outToConnection, BufferedReader inFromConnection) throws IOException {
        DHKeyGenerator dhk = new DHKeyGenerator();
        outToConnection.writeBytes(dhk.sharePrime() + "\n");
        outToConnection.writeBytes(dhk.shareGenerator() + "\n");

        String sharedKey = inFromConnection.readLine();
        dhk.recieveSharedKey(sharedKey);

        outToConnection.writeBytes(dhk.shareSharedKey() + "\n");

        long[] encryptionKey = dhk.generateLongKeyArray();
        setEncryptionKey(encryptionKey);
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

    public void setEncryptionKey(long[] encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public long[] getEncryptionKey(){
        return this.encryptionKey;
    }
}
