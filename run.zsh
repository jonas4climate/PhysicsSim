#!/bin/zsh
javac -d bin -sourcepath src src/mysim/Sim.java
java -cp bin mysim/Sim  > logs/log.txt
