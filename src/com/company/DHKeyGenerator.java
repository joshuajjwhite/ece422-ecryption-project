package com.company;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.security.SecureRandom;

/**
 * Created by joshua on 02/04/17.
 */
public class DHKeyGenerator {

    private static int KEY_SIZE = 256;

    private BigInteger prime;
    private BigInteger generator;
    private BigInteger secret;
    private BigInteger sharedSecret = null;

    public DHKeyGenerator(){
        SecureRandom secRand = new SecureRandom();
        setPrime(BigInteger.probablePrime(KEY_SIZE, secRand));
        setGenerator(BigInteger.probablePrime(KEY_SIZE, secRand));
        setSecret(BigInteger.probablePrime(KEY_SIZE, secRand));
    }

    public DHKeyGenerator(String p, String g){
        SecureRandom secRand = new SecureRandom();
        setPrime(new BigInteger(p));
        setGenerator(new BigInteger(g));
        setSecret(BigInteger.probablePrime(KEY_SIZE, secRand));
    }

    private BigInteger generateSharedKey(){
        return getGenerator().modPow(getSecret(), getPrime());
    }

    public String shareSharedKey(){
        return generateSharedKey().toString();
    }

    private void generateSharedSectret(BigInteger sharedKey){
        setSharedSecret(sharedKey.modPow(getSecret(), getPrime()));
    }

    public void recieveSharedKey(String sharedKeyString){
        generateSharedSectret(new BigInteger(sharedKeyString));
    }

    public int[] generateIntKeyArray(){
        int length = (128/8)/4;
        //int shift = Integer.BYTES*8;
        int keyArray[] = new int[length];
        int index = 0;

        IntBuffer buffer = ByteBuffer.wrap(getSharedSecret().toByteArray()).asIntBuffer();
        int xor = 0;
        while (buffer.hasRemaining()) {
            xor ^= buffer.get();
            keyArray[index] = xor;
            index++;
        }

        return keyArray;
    }

    public long[] generateLongKeyArray(){
        int length = (KEY_SIZE/8)/8;
        //int shift = Integer.BYTES*8;
        long keyArray[] = new long[length];
        int index = 0;

        LongBuffer buffer = ByteBuffer.wrap(getSharedSecret().toByteArray()).asLongBuffer();
        long xor = 0;
        while (buffer.hasRemaining()) {
            xor ^= buffer.get();
            keyArray[index] = xor;
            index++;
        }

        return keyArray;
    }

    public static void main(String args[]){
        DHKeyGenerator server = new DHKeyGenerator();
        DHKeyGenerator client = new DHKeyGenerator(server.sharePrime(), server.shareGenerator());

        String ssc = server.shareSharedKey();
        String csc = client.shareSharedKey();

        server.recieveSharedKey(csc);
        client.recieveSharedKey(ssc);

        System.out.println("Server\n" + server.getGenerator().toString());
        System.out.println(server.getPrime().toString());
        System.out.println(server.getSecret().toString() + "\n");

        System.out.println("Client\n" + client.getGenerator().toString());
        System.out.println(client.getPrime().toString());
        System.out.println(client.getSecret().toString() + "\n");

        boolean thing = server.getSharedSecret().toString().equals(client.getSharedSecret().toString());

        System.out.println(thing);
    }

    private BigInteger getGenerator() {
        return generator;
    }

    private void setGenerator(BigInteger generator) {
        this.generator = generator;
    }

    private BigInteger getPrime() {
        return prime;
    }

    private void setPrime(BigInteger prime) {
        this.prime = prime;
    }

    private void setSecret(BigInteger secret) {
        this.secret = secret;
    }

    private BigInteger getSecret(){
        return this.secret;
    }

    public String sharePrime(){
        return getPrime().toString();
    }

    public String shareGenerator(){
        return getGenerator().toString();
    }

    private BigInteger getSharedSecret(){
        return this.sharedSecret;
    }

    private void setSharedSecret(BigInteger sharedSecret){
        this.sharedSecret = sharedSecret;
    }
}
