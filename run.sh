#!/bin/sh
mkdir -p bin
mkdir -p logs
javac -d bin -sourcepath src src/mysim/Sim.java
java -cp bin mysim/Sim  > logs/raw.log
