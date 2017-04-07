#!/usr/bin/env bash

##!/bin/bash

#INPUT=$1
#RANDOM_NUMBER=$2
#OUTPUT=$3
#PFAIL=$4
#SFAIL=$5
#TIMEOUT=$6

javac -classpath ./commons-io-2.5.jar ./com/company/*.java
javah -jni com.company.TEA

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.

rm com/company/libTEA.so

gcc -shared -fpic -o ./com/company/libTEA.so -I/$JAVA_HOME/include -I/$JAVA_HOME/include/linux ./com/company/TEA.c

#java -classpath ./:./commons-io-2.5.jar  DataGenerator $INPUT $RANDOM_NUMBER

#java -classpath ./:./commons-io-2.5.jar SortDriver $INPUT $OUTPUT $PFAIL $SFAIL $TIMEOUT
