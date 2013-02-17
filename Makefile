#!/bin/bash

# if java/javac are not in your ${PATH}, set this line to the directory
# they are located in, with a trailing '/', e.g. 'JAVAPATH=/usr/bin/'
JAVAPATH=

compile: clean
	# set up
	echo "PWD=${PWD}"
	mkdir classes

	# compile	 
	${JAVAPATH}javac -target 1.5 \
	  -cp lib/joeq.jar \
	  -sourcepath src -d classes `find . -name "*.java"`

	# jar it all up
	cd classes; jar cf parun.jar `find . -name "*.class"`
	mv classes/parun.jar lib/parun.jar

	# set up parun
	cat bin/parun-template | sed -e "s|PARUNPATH|${PWD}|g" \
		| sed -e "s|JAVAPATH|${JAVAPATH}|g" > bin/parun
	chmod a+x bin/parun

clean:
	find . -name '*~' -delete
	find . -name '#*#' -delete
	rm -rf classes
	rm -rf bin/parun
	rm -rf lib/parun.jar

test:
	bin/parun optimize.FindRedundantNullChecks test.NullTest | diff -b src/test/NullTest.basic.out - 
	bin/parun optimize.FindRedundantNullChecks test.SkipList| diff -b src/test/SkipList.basic.out - 
	bin/parun optimize.OptimizeHarness --optimize test.SkipList --run-main test.SkipList --run-param 20
	@echo "===Expected: (24551, 106407)"
	@echo ""
	bin/parun optimize.OptimizeHarness --optimize test.QuickSort --run-main test.QuickSort --run-param 200
	@echo "===Expected: (32023, 136224)"

sl:
	bin/parun optimize.FindRedundantNullChecks test.SkipList
	cat src/test/SkipList.basic.out
