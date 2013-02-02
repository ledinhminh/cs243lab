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
	cat bin/parun-template | sed -e "s|PARUNPATH|${PWD}|g" | sed -e "s|JAVAPATH|${JAVAPATH}|g" > bin/parun
	chmod a+x bin/parun

clean:
	find . -name '*~' -delete
	find . -name '#*#' -delete
	rm -rf classes
	rm -rf bin/parun
	rm -rf lib/parun.jar
	rm -f t1*
	rm -f t2*

test:
	bin/parun flow.Flow submit.MySolver flow.ConstantProp test.Test > t1cp
	diff t1cp src/test/test.cp.out
	bin/parun flow.Flow submit.MySolver flow.Liveness test.Test > t1lv
	diff t1lv src/test/test.lv.out
	bin/parun flow.Flow submit.MySolver submit.ReachingDefs test.Test > t1rd
	diff t1rd src/test/test.rd.out
	
	bin/parun flow.Flow submit.MySolver flow.ConstantProp test.TestTwo > t2cp
	diff t2cp src/test/test2.cp.out
	bin/parun flow.Flow submit.MySolver flow.Liveness test.TestTwo > t2lv
	diff t2lv src/test/test2.lv.out
	bin/parun flow.Flow submit.MySolver submit.ReachingDefs test.TestTwo > t2rd
	diff t2rd src/test/test2.rd.out
	
	bin/parun flow.Flow submit.MySolver submit.Faintness submit.TestFaintness > t1f
	diff t1f src/test/TestFaintness.out

ft:
	bin/parun flow.Flow submit.MySolver submit.Faintness submit.TestFaintness 
