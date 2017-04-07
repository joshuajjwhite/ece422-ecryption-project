package com.company;

import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by joshua on 30/03/17.
 */
public class ServerConnection implements Runnable {

    protected static String SERVER_FILE_DIRECTORY = "./server-files/";
    private static long[] ACCOUNT_ENCRYPTION = {41371378120415169l, -777526119039919309l,
            -4459589122261330969l, 2364346053016476091l};

    private Socket socket;
    private long[] encryptionKey;

    public ServerConnection(Socket socket){
        setSocket(socket);
    }

    @Override
    public void run() {
        try {
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(getSocket().getOutputStream());

            handshake(outToClient, inFromClient);

            keyExchange(outToClient, inFromClient);

            if(!credentialsExchangeAuthenticate(outToClient, inFromClient)){
                return;
            }

            while(true){
                if(!provideFiles(outToClient, inFromClient)){
                    System.out.println("Closing Server Connection");
                    break;
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handshake(DataOutputStream outToConnection, BufferedReader inFromConnection) throws IOException {
        String clientStr = inFromConnection.readLine();
        if(clientStr.equals("Handshake")){
            outToConnection.writeBytes("ACK" + "\n");
        }
        else{
            System.out.print("Server didn't get handshake");
        }
    }

    private boolean authenticate(String username, String password) throws IOException {
        FileHandler accounts = new FileHandler( SERVER_FILE_DIRECTORY + "accounts.txt");
        String[] account;
        for(String str: accounts.readByLine()){
            account = str.split("],");
            if(TEA.decrypt(account[0], ACCOUNT_ENCRYPTION).equals(username)){

                if(TEA.decrypt(account[1], ACCOUNT_ENCRYPTION).equals(password)){
                    return true;
                }
                else{System.out.print("Wrong Password --- ");}
            }
        }
        System.out.println("Authentication Failed");
        return false;
    }

    public boolean provideFiles(DataOutputStream outToConnection, BufferedReader inFromConnection) throws IOException {
        String filename = encryptedRead(inFromConnection);
        if(filename.equals("exit")){
            return false;
        }

        String fileRead = readServerFile(filename);

        if(fileRead == null){
            encryptedWrite("FILE_NOT_FOUND", outToConnection);
            return true;
        }
        encryptedWrite("ACK", outToConnection);
        encryptedWrite(filename, outToConnection);
        encryptedWrite(fileRead, outToConnection);
        return true;
    }

    public boolean credentialsExchangeAuthenticate(DataOutputStream outToConnection, BufferedReader inFromConnection) throws IOException {
        String username = encryptedRead(inFromConnection);
        String password = encryptedRead(inFromConnection);

        if(! authenticate(username,password)){
            encryptedWrite("AuthenticationFailed", outToConnection);

            return false;
        }
        else{
            encryptedWrite("ACK", outToConnection);
            return true;
        }
    }

    public void keyExchange(DataOutputStream outToConnection, BufferedReader inFromConnection) throws IOException {
        String p = readFromConnection(inFromConnection);
        String g = readFromConnection(inFromConnection);

        DHKeyGenerator dhk = new DHKeyGenerator(p,g);

        outToConnection.writeBytes(dhk.shareSharedKey() + "\n");

        String sharedKey = inFromConnection.readLine();
        dhk.recieveSharedKey(sharedKey);

        long[] encryptionKey = dhk.generateLongKeyArray();
        setEncryptionKey(encryptionKey);
    }

    public String readServerFile(String fileName) throws IOException {
        return new FileHandler(SERVER_FILE_DIRECTORY + fileName).readFromFile();
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

    public static void addAccount(String username, String password) throws IOException, NoSuchAlgorithmException {
        FileHandler accounts = new FileHandler( SERVER_FILE_DIRECTORY + "accounts.txt");
        accounts.writeToFile(TEA.encrypt(username, ACCOUNT_ENCRYPTION) + "," +
                TEA.encrypt(password, ACCOUNT_ENCRYPTION));
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public long[] getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(long[] encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
}
