JAVAC_CMD=javac -target 1.5

all: AddressBook.class

clean:
	rm -f *.class
	rm -rf results/
	rm -f runparesults
	rm -f *.txt

AddressBook.class: AddressBook.java
	$(JAVAC_CMD) AddressBook.java


ex1: AddressBook.class
	python run_bddbddb.py AddressBook pacs.dtl
	python print_bddbddb_results.py AddressBook results/hP.tuples > ex1.txt

p1: AddressBook.class
	python run_bddbddb.py AddressBook submit/alias.dtl
	python print_bddbddb_results.py AddressBook results/hALIAS.tuples | column -t -s"||"
	python print_bddbddb_results.py AddressBook results/hALIAS.tuples > problem1.txt

p2: AddressBook.class
	python run_bddbddb.py AddressBook submit/alias_refined.dtl
	python print_bddbddb_results.py AddressBook results/hALIAS.tuples | column -t -s"||"

p3: AddressBook.class
	python run_bddbddb.py AddressBook submit/pacs_field_insensitive.dtl
	python print_bddbddb_results.py AddressBook results/hP.tuples | column -t -s"||"
	python print_bddbddb_results.py AddressBook results/hP.tuples > problem3.txt
	wc -l results/hP.tuples

p4: AddressBook.class
	python run_bddbddb.py AddressBook pa.dtl
	python print_bddbddb_results.py AddressBook results/hP.tuples > problem4.txt

