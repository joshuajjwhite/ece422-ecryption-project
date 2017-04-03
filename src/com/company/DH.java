package com.company;

import javax.crypto.KeyAgreement;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by joshua on 02/04/17.
 */
public class DH {

    /**
     *
     */
    private DHParameterSpec dhParamSpec;
    private KeyPair keyPair;
    private KeyAgreement keyAgree;
    private PublicKey otherPubKey = null;
    private byte[] sharedSecret = null;

    public DH() {
        AlgorithmParameterGenerator paramGen
                = null;
        try {
            paramGen = AlgorithmParameterGenerator.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        paramGen.init(128);
        AlgorithmParameters params = paramGen.generateParameters();
        try {
            setDhParamSpec((DHParameterSpec)params.getParameterSpec
                    (DHParameterSpec.class));

        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }

        generateKeypair();
        initKeyAgreement();
    }

    public DH(byte[] pubKeyEnc){
        /*
         * Let's turn over to Bob. Bob has received Alice's public key
         * in encoded format.
         * He instantiates a DH public key from the encoded key material.
         */
        KeyFactory bobKeyFac = null;
        try {
            bobKeyFac = KeyFactory.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec
                (pubKeyEnc);
        PublicKey alicePubKey = null;
        try {
            alicePubKey = bobKeyFac.generatePublic(x509KeySpec);
            setOtherPubKey(alicePubKey);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        /*
         * Bob gets the DH parameters associated with Alice's public key.
         * He must use the same parameters when he generates his own key
         * pair.
         */
        setDhParamSpec(((DHPublicKey)alicePubKey).getParams());

        try {
            generateKeypair(pubKeyEnc);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        initKeyAgreement();
    }

    public void generateKeypair(){
        System.out.println("ALICE: Generate DH keypair ...");
        KeyPairGenerator aliceKpairGen = null;
        try {
            aliceKpairGen = KeyPairGenerator.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            aliceKpairGen.initialize(dhParamSpec);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        setKeyPair(aliceKpairGen.generateKeyPair());
    }

    public void generateKeypair(byte[] pubKeyEnc) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        // Bob creates his own DH key pair
        System.out.println("BOB: Generate DH keypair ...");
        KeyPairGenerator bobKpairGen = KeyPairGenerator.getInstance("DH");
        bobKpairGen.initialize(getDhParamSpec());
        setKeyPair(bobKpairGen.generateKeyPair());
    }

    public void initKeyAgreement(){
        // Alice creates and initializes her DH KeyAgreement object
        System.out.println("ALICE: Initialization ...");
        try {
            setKeyAgree(KeyAgreement.getInstance("DH"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            getKeyAgree().init(getKeyPair().getPrivate());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    /*
         * Alice uses Bob's public key for the first (and only) phase
         * of her version of the DH
         * protocol.
         * Before she can do so, she has to instantiate a DH public key
         * from Bob's encoded key material.
         */
    public void doPhase(byte[] pubKeyEnc) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        if(getOtherPubKey().equals(null)){
            KeyFactory aliceKeyFac = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKeyEnc);
            PublicKey bobPubKey = aliceKeyFac.generatePublic(x509KeySpec);

            setOtherPubKey(bobPubKey);
            System.out.println("ALICE: Execute PHASE1 ...");
            getKeyAgree().doPhase(bobPubKey, true);
        }
        else{
            /*
         * Bob uses Alice's public key for the first (and only) phase
         * of his version of the DH
         * protocol.
         */
            System.out.println("BOB: Execute PHASE1 ...");
            getKeyAgree().doPhase(getOtherPubKey(), true);
        }

    }

    /*
         * At this stage, both Alice and Bob have completed the DH key
         * agreement protocol.
         * Both generate the (same) shared secret.
         */
    public void generateSharedSecret(){
        byte[] aliceSharedSecret = getKeyAgree().generateSecret();
        setSharedSecret(aliceSharedSecret);
        int aliceLen = aliceSharedSecret.length;
    }



    /*byte[] bobSharedSecret = new byte[aliceLen];
    int bobLen;
        try {
        // show example of what happens if you
        // provide an output buffer that is too short
        bobLen = bobKeyAgree.generateSecret(bobSharedSecret, 1);
    } catch (ShortBufferException e) {
        System.out.println(e.getMessage());
    }
    // provide output buffer of required size
    bobLen = bobKeyAgree.generateSecret(bobSharedSecret, 0);
    */


    public byte[] getEncoded(){
        return getKeyPair().getPublic().getEncoded();
    }


    public static void main(String argv[]) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        DH alice = new DH();
        DH bob = new DH(alice.getEncoded());

        alice.doPhase(bob.getEncoded());
        bob.doPhase(alice.getEncoded());

        System.out.println("Alice secret: " + alice.getSharedSecret().toString());
        System.out.println("Bob secret: " + bob.getSharedSecret().toString());

        if (!java.util.Arrays.equals(alice.getSharedSecret(), bob.getSharedSecret())) {
            try {
                throw new Exception("Shared secrets differ");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Shared secrets are the same");
        }
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public KeyAgreement getKeyAgree() {
        return keyAgree;
    }

    public void setKeyAgree(KeyAgreement keyAgree) {
        this.keyAgree = keyAgree;
    }

    public void setDhParamSpec(DHParameterSpec dhParamSpec){
        this.dhParamSpec = dhParamSpec;
    }

    public DHParameterSpec getDhParamSpec(){
        return this.dhParamSpec;
    }

    public PublicKey getOtherPubKey() {
        return otherPubKey;
    }

    public void setOtherPubKey(PublicKey otherPubKey) {
        this.otherPubKey = otherPubKey;
    }

    public byte[] getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(byte[] sharedSecret) {
        this.sharedSecret = sharedSecret;
    }
}
