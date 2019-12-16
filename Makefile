compile:
	mkdir -p bin
	javac -d bin -sourcepath src src/mysim/Sim.java

run: compile
	java -cp bin mysim/Sim

log: compile
	mkdir -p logs
	java -cp bin mysim/Sim  > logs/raw.log

javadoc:
	javadoc src/mysim/* -d javadocs

clean: 
	rm -rf logs
	rm -rf bin
