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
    private DHKeyGenerator dhk;
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
            //System.out.println("Checking \"" + TEA.decrypt(account[0], ACCOUNT_ENCRYPTION) + "\" and " + username );
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
        String filename = encryptedRead(inFromConnection, encryptionKey);
        if(filename.equals("exit")){
            return false;
        }

        String fileRead = readServerFile(filename);

        if(fileRead == null){
            encryptedWrite("FILE_NOT_FOUND", outToConnection, encryptionKey);

        }
        encryptedWrite("ACK", outToConnection, encryptionKey);
        encryptedWrite(filename, outToConnection, encryptionKey);
        encryptedWrite(fileRead, outToConnection, encryptionKey);
        return true;
    }

    public boolean credentialsExchangeAuthenticate(DataOutputStream outToConnection, BufferedReader inFromConnection) throws IOException {
        String username = encryptedRead(inFromConnection, encryptionKey);
        String password = encryptedRead(inFromConnection, encryptionKey);

        if(! authenticate(username,password)){
            encryptedWrite("AuthenticationFailed", outToConnection, encryptionKey);

            return false;
        }
        else{
            encryptedWrite("ACK", outToConnection, encryptionKey);
            return true;
        }
    }

    public void keyExchange(DataOutputStream outToConnection, BufferedReader inFromConnection) throws IOException {
        String p = readFromConnection(inFromConnection);
        String g = readFromConnection(inFromConnection);

        setDhk(new DHKeyGenerator(p,g));

        outToConnection.writeBytes(getDhk().shareSharedKey() + "\n");

        String sharedKey = inFromConnection.readLine();
        getDhk().recieveSharedKey(sharedKey);

        long[] encryptionKey = getDhk().generateLongKeyArray();
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

    public void encryptedWrite(String message, DataOutputStream outToConnection, long[] encryptionKey){
        try {
            message = TEA.encrypt(message, encryptionKey);
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

    public String encryptedRead(BufferedReader reader, long[] encryptionKey){
        try {
            return TEA.decrypt(readFromConnection(reader), encryptionKey);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "EncryptedReadFailure";
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

    public static void addAccount(String username, String password) throws IOException, NoSuchAlgorithmException {
        FileHandler accounts = new FileHandler( SERVER_FILE_DIRECTORY + "accounts.txt");
        accounts.writeToFile(TEA.encrypt(username, ACCOUNT_ENCRYPTION) + "," +
                TEA.encrypt(password, ACCOUNT_ENCRYPTION));
    }

    public long[] getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(long[] encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
}
