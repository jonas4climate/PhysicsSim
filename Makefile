BIN="bin"
SRC="src/main/java"
SIM="src/main/java/physicssim/Sim.java"

compile:
	mkdir -p $(BIN)
	javac -d $(BIN) -sourcepath $(SRC) $(SIM)

run: compile
	java -cp $(BIN) $(SIM)

log: compile
	mkdir -p logs
	java -cp $(BIN) $(SIM) > logs/raw.log

javadoc:
	javadoc $(SRC)/physicssim/* -d javadoc

clean: 
	rm -rf logs
	rm -rf $(BIN)
	rm -rf javadoc

