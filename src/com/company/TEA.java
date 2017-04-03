package com.company;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

/**
 * Created by joshua on 31/03/17.
 */
public class TEA {

    public static native byte[] encryptBytes(byte[] plainBytes, byte[] key);
    public static native void decryptBytes(byte[] cipherBytes, byte[] key);

    static {
        System.loadLibrary("TEA");
    }

    public static String encrypt(String plainText) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // for example
        SecretKey secretKey = keyGen.generateKey();

        String cypherText = null;
        byte[] cypherBytes = null;
        byte[] plainBytes = plainText.getBytes();
        for(byte b: plainBytes) {
            System.out.print(Byte.toString(b));
        }

        System.out.println("");
        cypherBytes = encryptBytes(plainBytes, secretKey.getEncoded());
        for(byte b: cypherBytes) {
            System.out.print(Byte.toString(b));
        }
        System.out.println("");
        return cypherText;
    }

    public static String decrypt(){
        return "Empty";
    }
}

