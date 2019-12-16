compile:
	mkdir -p bin
	javac -d bin -sourcepath src src/mysim/Sim.java

all: compile
	java -cp bin mysim/Sim

log: compile
	mkdir -p logs
	java -cp bin mysim/Sim  > logs/raw.log
	cp logs/raw.log example.log

javadoc:
	javadoc src/mysim/* -d javadocs

clean: 
	rm -r logs
	rm -r bin
