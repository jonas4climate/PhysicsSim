all: 
	mkdir -p bin
	mkdir -p logs
	javac -d bin -sourcepath src src/mysim/Sim.java

run: all
	java -cp bin mysim/Sim  > logs/raw.log

javadoc:
	javadoc src/mysim/* -d javadocs

clean: 
	rm -r logs
	rm -r bin