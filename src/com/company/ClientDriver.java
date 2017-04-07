package com.company;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;

public class ClientDriver {

    public static void main(String[] args) {
	// write your code here
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        System.out.println("\nWelcome to the White File Service!");

        while(true){
            System.out.print("\nWhat would you like to do? (exit, login, add-user): ");
            try {
                switch (inFromUser.readLine()){
                    case "login":
                        Thread client = new Thread(new Client());
                        client.start();
                        client.join();
                        break;
                    case "add-user":
                        System.out.print("Enter a new Username: ");
                        String username = inFromUser.readLine();
                        System.out.print("Enter a new Password: ");
                        String password = inFromUser.readLine();
                        ServerConnection.addAccount(username, password);
                        break;
                    case "exit":
                        System.out.println("Bye\n");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid Input");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }
}
