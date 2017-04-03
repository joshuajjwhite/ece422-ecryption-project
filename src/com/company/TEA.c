#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include "../../com_company_TEA.h"

/*
 * Class:     com_company_TEA
 * Method:    encrypt
 * Signature: ()Ljava/lang/String;
 */
 JNIEXPORT jbyteArray JNICALL Java_com_company_TEA_encryptBytes
  (JNIEnv *env, jobject obj, jbyteArray plainBytes, jbyteArray key){

    jbyte* plainByteArray = (*env)->GetByteArrayElements(env, plainBytes, NULL);
    jbyte* key = (*env)->GetByteArrayElements(env, key, NULL);

    jsize lengthOfArray = (*env)->GetArrayLength(env, plainBytes);
    jbyte cypherByteArray[lengthOfArray];

    int i;
    for(i = 0; i < lengthOfArray; i++){
        cypherByteArray[i] = plainByteArray[i];
    }

    //cypherByteArray[0] = 9;
    (*env)->SetByteArrayRegion(env, plainBytes, 0, lengthOfArray, cypherByteArray);
    return plainBytes;
  }

/*
 * Class:     com_company_TEA
 * Method:    decrypt
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT void JNICALL Java_com_company_TEA_decryptBytes
   (JNIEnv *env, jclass myClass, jbyteArray cypherByteArray, jbyteArray key){



}

jbyte amalgamateKeyBytes(jbyte* key){
    long amalgamatedKeyBytes[4];

}

void encrypt (long *v, long *k){

    /* TEA encryption algorithm */
    unsigned long y = v[0], z=v[1], sum = 0;
    unsigned long delta = 0x9e3779b9, n=32;

  	while (n-- > 0){
  		sum += delta;
  		y += (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
  		z += (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
  	}

  	v[0] = y;
  	v[1] = z;
}



void decrypt (long *v, long *k){
    /* TEA decryption routine */
    unsigned long n=32, sum, y=v[0], z=v[1];
    unsigned long delta=0x9e3779b9l;

	sum = delta<<5;
	while (n-- > 0){
		z -= (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
		y -= (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		sum -= delta;
	}
	v[0] = y;
	v[1] = z;
}




