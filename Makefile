JAVAC_CMD=javac -target 1.5

all: AddressBook.class

clean:
	rm -f *.class
	rm -rf results/
	rm -f runparesults

AddressBook.class: AddressBook.java
	$(JAVAC_CMD) AddressBook.java

