#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include "../../com_company_TEA.h"

void encrypt (long *v, long *k);
void decrypt (long *v, long *k);

/*
 * Class:     com_company_TEA
 * Method:    encrypt
 * Signature: ()Ljava/lang/String;
 */
 JNIEXPORT jlongArray JNICALL Java_com_company_TEA_encryptLongs
  (JNIEnv *env, jobject obj, jlongArray plainLongs, jlongArray key){

    jlong* valuesToCypher = (*env)->GetLongArrayElements(env, plainLongs, NULL);
    jlong* keys = (*env)->GetLongArrayElements(env, key, NULL);
    jsize len = (*env)->GetArrayLength(env, plainLongs);

    long val1 = valuesToCypher[0];
    long val2 = valuesToCypher[1];

    //printf("Plain Ints C: %li  &  %li\n", val1, val2);

    encrypt(valuesToCypher, keys);

    val1 = valuesToCypher[0];
    val2 = valuesToCypher[1];

    //printf("Cyphered Ints C: %li  &  %li\n", val1, val2);

    (*env)->SetLongArrayRegion(env, plainLongs, 0, len, valuesToCypher);
    (*env)->ReleaseLongArrayElements(env, plainLongs, valuesToCypher, 0);

    return plainLongs;
  }

/*
 * Class:     com_company_TEA
 * Method:    decrypt
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jlongArray JNICALL Java_com_company_TEA_decryptLongs
   (JNIEnv *env, jclass myClass, jlongArray cypheredLongs, jlongArray key){

    jlong* valuesToDecypher = (*env)->GetLongArrayElements(env, cypheredLongs, NULL);
    jlong* keys = (*env)->GetLongArrayElements(env, key, NULL);
    jsize len = (*env)->GetArrayLength(env, cypheredLongs);

    long val1 = valuesToDecypher[0];
    long val2 = valuesToDecypher[1];

    //printf("Cyphered Ints C: %li  &  %li\n", val1, val2);

    decrypt(valuesToDecypher, keys);

    val1 = valuesToDecypher[0];
    val2 = valuesToDecypher[1];

    //printf("Decyphered Ints C: %li  &  %li\n", val1, val2);

    (*env)->SetLongArrayRegion(env, cypheredLongs, 0, len, valuesToDecypher);
    (*env)->ReleaseLongArrayElements(env, cypheredLongs, valuesToDecypher, 0);

    return cypheredLongs;
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




