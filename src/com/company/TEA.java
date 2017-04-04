package com.company;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by joshua on 31/03/17.
 */
public class TEA {


    public static native long[] encryptLongs(long[] plainInts, long[] key);
    public static native long[] decryptLongs(long[] cipherBytes, long[] key);

    static {
        System.loadLibrary("TEA");
    }


    public static String encrypt(String plainText, long[] keyArray) throws NoSuchAlgorithmException {

        String cypherText = null;
        long[] plainLongs = bytesToLongs(stringToBytes(plainText));
        long[] cypherLongs = new long[plainLongs.length];

        long[] valuesToCypher = new long[2];
        long[] valuesCyphered = new long[2];
        int index = 0;

        System.out.print("Plain longs Java ");
        for (long l : plainLongs) {
            System.out.print(Long.toString(l) + " ");
        }
        System.out.println("");

        while (index < plainLongs.length) {

            valuesToCypher[0] = plainLongs[index];

            if (((plainLongs.length - index) % 2 != 0) && ((plainLongs.length - index) < 2)) {
                valuesToCypher[1] = 0;
            } else {
                valuesToCypher[1] = plainLongs[index + 1];
            }

            valuesCyphered = encryptLongs(valuesToCypher, keyArray);
            index += 2;
        }

        System.out.print("Cyphered longs Java ");
        for (long l : valuesCyphered) {
            System.out.print(Long.toString(l) + " ");
        }
        System.out.println("");

        return cypherText;
    }


    /*LONGS________________________________________________________________LONGS*/

    private static long[] bytesToLongs(byte[] bytes){
        int size = (bytes.length / 8) + ((bytes.length % 8 == 0) ? 0 : 1);
        ByteBuffer bb = ByteBuffer.allocate(size *8);
        bb.put(bytes);

        //Java uses Big Endian. Network program uses Little Endian.
        bb.order(ByteOrder.BIG_ENDIAN);


        long[] result = new long[size];
        bb.rewind();
        while (bb.remaining() > 0) {
            result[bb.position()/8] = bb.getInt();
        }

        return result;
    }

    private static byte[] longToBytes(long[] longs){
        ByteBuffer byteBuffer = ByteBuffer.allocate(longs.length * 8);
        LongBuffer longBuffer = byteBuffer.asLongBuffer();
        longBuffer.put(longs);

        byte[] bytes = byteBuffer.array();

        int zeros = 0;
        for(int i = bytes.length-1; i >= 0 ;i--){
            if(bytes[i] == 0){zeros +=  1;}
            else{break;}
        }

        byte[] result = Arrays.copyOfRange(bytes, 0, bytes.length-zeros);

        return result;
    }


    /*INTS________________________________________________________________INTS*/

    public static String encrypt(String plainText, int[] keyArray) throws NoSuchAlgorithmException {

        String cypherText = null;
        int[] plainInts = bytesToInts(stringToBytes(plainText));
        int[] cypherInts = new int[plainInts.length];

        int[] valuesToCypher = new int[2];
        int[] valuesCyphered = new int[2];
        int index = 0;

        System.out.print("Plain Ints Java " );
        for(int i: plainInts){
            System.out.print(Integer.toString(i) + " ");
        }
        System.out.println("");

        while(index <  plainInts.length){

            valuesToCypher[0] = plainInts[index];

            if(((plainInts.length-index) % 2 != 0) && ((plainInts.length-index) < 2)){
                valuesToCypher[1] = 0;
            }
            else{
                valuesToCypher[1] = plainInts[index + 1];
            }

            //valuesCyphered = encryptInts(valuesToCypher, keyArray);
            index +=2;
        }


        return cypherText;
    }

    public static String decrypt(){
        return "Empty";
    }

    private static byte[] stringToBytes(String str){
        return str.getBytes();
    }

    private static int[] bytesToInts(byte[] bytes){
        int size = (bytes.length / 4) + ((bytes.length % 4 == 0) ? 0 : 1);
        ByteBuffer bb = ByteBuffer.allocate(size *4);
        bb.put(bytes);

        //Java uses Big Endian. Network program uses Little Endian.
        bb.order(ByteOrder.BIG_ENDIAN);


        int[] result = new int[size];
        bb.rewind();
        while (bb.remaining() > 0) {
            result[bb.position()/4] =bb.getInt();
        }

        return result;
    }

    private static byte[] intsToBytes(int[] ints){
        int size = (ints.length * 4) - (ints.length % 4);
        int otherSize = ints.length * 4;
        ByteBuffer byteBuffer = ByteBuffer.allocate(ints.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(ints);

        byte[] bytes = byteBuffer.array();

        int zeros = 0;
        for(int i = bytes.length-1; i >= 0 ;i--){
            if(bytes[i] == 0){zeros +=  1;}
            else{break;}
        }

        byte[] result = Arrays.copyOfRange(bytes, 0, bytes.length-zeros);

        return result;
    }

    private static String bytesToString(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, "UTF-8");
    }


    public static void main(String[] args){


        String str = "Hello World!";

        System.out.println("Initial String: " + str);

        System.out.print("Initial to bytes " );
        for(byte b: stringToBytes(str)){
            System.out.print(Byte.toString(b) + " ");
        }
        System.out.println("");
/*
        System.out.print("Initial bytes to ints " );
        for(int i: bytesToInts(stringToBytes(str))){
            System.out.print(Integer.toString(i) + " ");
        }
        System.out.println("");
*/
        System.out.print("Initial bytes to longs " );
        for(long l: bytesToLongs(stringToBytes(str))){
            System.out.print(Long.toString(l) + " ");
        }
        System.out.println("");
/*

        System.out.print("Working Back to bytes " );
        for(byte b: intsToBytes(bytesToInts(stringToBytes(str)))){
            System.out.print(Byte.toString(b) + " ");
        }
        System.out.println("");
*/

        System.out.print("Working Back to bytes " );
        for(byte b: longToBytes(bytesToLongs(stringToBytes(str)))){
            System.out.print(Byte.toString(b) + " ");
        }
        System.out.println("");

        try {
            System.out.println("Working Back to String: " + bytesToString(intsToBytes(bytesToInts(stringToBytes(str)))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

