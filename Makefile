JAVAC_CMD=javac -target 1.5

all: AddressBook.class

clean:
	rm -f *.class
	rm -rf results/
	rm -f runparesults

AddressBook.class: AddressBook.java
	$(JAVAC_CMD) AddressBook.java

p1:
	python run_bddbddb.py AddressBook submit/alias.dtl
	python print_bddbddb_results.py AddressBook results/hALIAS.tuples | column -t -s"||"

p2:
	python run_bddbddb.py AddressBook submit/alias_refined.dtl
	python print_bddbddb_results.py AddressBook results/hALIAS.tuples | column -t -s"||"
