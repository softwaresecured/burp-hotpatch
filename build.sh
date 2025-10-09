#!/bin/bash 

# use if your default JDK is not v21
#
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH 

./gradlew --info clean build
