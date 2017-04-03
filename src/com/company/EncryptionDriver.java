package com.company;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class EncryptionDriver {

    public static void main(String[] args) {
	// write your code here
        Thread server = new Thread(new Server());
        Thread client1 = new Thread(new Client("joshua", "iloveolivia"));
        //Thread client2 = new Thread(new Client());

        server.start();
        client1.start();
        //client2.start();


        /*try {
            TEA.encrypt("Hello World");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        */
    }
}
