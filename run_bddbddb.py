#!/usr/bin/env python

# stolen from original code by Michael Martin
# modified by Philip Guo

import sys
import os
import os.path
import re

pql_home = os.getenv ("PQLHOME", os.path.curdir)

java_cmd = "java"

# If True, delete all the temporary Datalog &c files.
#cleanup = False
cleanup = True

basecp = os.getenv ("CLASSPATH", ".").split (os.path.pathsep)
print "base classpath directories: %s" % str(basecp)

verbose = True

def check_env_vars ():
    global pql_home, pql_jar, jline_jar
    
    pql_ok = pql_home is not None and os.path.exists(os.path.join(pql_home, "PQL-0.2.jar"))

    if not pql_ok:
        print "PQLHOME is not set or set to improper directory"

    pql_jar = os.path.join (pql_home, "PQL-0.2.jar")
    jline_jar = os.path.join (pql_home, "jline.jar")

# Clear old results
def clean (resultdir="results"):
    if not resultdir.endswith (os.path.sep):
        resultdir += os.path.sep
    for (root, subs, files) in os.walk(resultdir, False):
        for f in files:
            if verbose:
                print "Deleting file "+os.path.join(root, f)
                os.remove (os.path.join(root, f))
        if verbose:
            print "Deleting directory "+root
        os.rmdir(root)

# Generate CI relations, including the maps and tuples the translator needs

# TODO: switches for selecting bogosity of summaries
def gen_relations (main_class, path_dirs, resultdir="results"):
    if not resultdir.endswith (os.path.sep):
        resultdir += os.path.sep
    classpath = os.path.pathsep.join([os.path.curdir, pql_jar] + path_dirs)
    cmd = '%s -cp "%s" -Xmx1024m -Dpa.dumppath=%s -Dpa.specialmapinfo=yes -Dpa.dumpunmunged=yes -Dpa.signaturesinlocs=yes joeq.Main.GenRelations %s' % (java_cmd, classpath, resultdir, main_class)
    if verbose:
        print cmd
    (in_p, out_p) = os.popen4(cmd)
    result_str = out_p.read()
    out_p.close()
    in_p.close()
    if verbose:
        print result_str
    cmd = '%s -mx600m -Dbasedir=%s -Dresultdir=%s -Dpa.discovercallgraph=yes -cp "%s" net.sf.bddbddb.BuildEquivalenceRelation H0 H0 heap.map I0 invoke.map' % (java_cmd, resultdir, resultdir, classpath)
    if verbose:
        print cmd
    (in_p, out_p) = os.popen4(cmd)
    result_str = out_p.read()
    out_p.close()
    if verbose:
        print result_str
    # TODO: Make generic with calls in os
    os.system ("mv map_* results/")

# Generic solver routines

# Original version - 'program' is a string containing the program name
def datalog_solve(program, filename, numberingtype="scc"):
    f = file (filename, "wt")
    print>>f, program
    f.close()
    classpath = os.path.pathsep.join ([pql_jar, os.path.curdir])
    cmd = '%s -cp "%s" -mx600m -Dlearnbestorder=n -Dsingleignore=yes -Dbasedir=./results/ -Dbddcache=1500000 -Dbddnodes=40000000 -Dnumberingtype=%s -Dpa.clinit=no -Dpa.filternull=yes -Dpa.unknowntypes=no net.sf.bddbddb.Solver %s' % (java_cmd, classpath, numberingtype, filename)
    if verbose:
        print cmd
    (in_p, out_p) = os.popen2(cmd)
    result_str = out_p.read()
    out_p.close()    
    in_p.close()
    if cleanup:
        os.remove (filename)
    print result_str
    return result_str


# Modified version - 'program_filename' is a filename containing the
# datalog program
def datalog_solve_filename(program_filename, numberingtype="scc"):
    classpath = os.path.pathsep.join ([pql_jar, os.path.curdir])
    cmd = '%s -cp "%s" -mx600m -Dlearnbestorder=n -Dsingleignore=yes -Dbasedir=./results/ -Dbddcache=1500000 -Dbddnodes=40000000 -Dnumberingtype=%s -Dpa.clinit=no -Dpa.filternull=yes -Dpa.unknowntypes=no net.sf.bddbddb.Solver %s' % (java_cmd, classpath, numberingtype, program_filename)
    if verbose:
        print cmd
    (in_p, out_p) = os.popen2(cmd)
    result_str = out_p.read()
    out_p.close()
    in_p.close()
    print result_str
    return result_str

# Woohoo, interactive action!
def datalog_solve_filename_interactive(program_filename, numberingtype="scc"):
    classpath = os.path.pathsep.join ([pql_jar, jline_jar, os.path.curdir])
    cmd = '%s -cp "%s" -mx600m -Dlearnbestorder=n -Dsingleignore=yes -Dbasedir=./results/ -Dbddcache=1500000 -Dbddnodes=40000000 -Dnumberingtype=%s -Dpa.unknowntypes=no jline.ConsoleRunner net.sf.bddbddb.Interactive %s' % (java_cmd, classpath, numberingtype, program_filename)
    if verbose:
        print cmd
    os.system(cmd)
    #(in_p, out_p) = os.popen2(cmd)
    #result_str = out_p.read()
    #out_p.close()
    #in_p.close()
    #print result_str
    #return result_str


    
# Number contexts, fixing VC if necessary

numbering = """# -*- Mode: C; indent-tabs-mode: nil; c-basic-offset: 4 -*-
### Context-sensitive inclusion-based pointer analysis using cloning
# 
# Calculates the numbering based on the call graph relation.
# 
# Author: John Whaley

.basedir "results"
.include "fielddomains.pa"

.bddnodes 10000000
.bddcache 1000000

# found by findbestorder:
#.bddvarorder G0_C0_C1_N0_F0_I0_M1_M0_V1xV0_VC1xVC0_T0_Z0_T1_H0_H1
.bddvarorder C0_C1_N0_F0_I0_M1_M0_V1xV0_VC1xVC0_T0_Z0_T1_H0_H1_G0

### Relations

mI (method:M0, invoke:I0, name:N0) input
IE0 (invoke:I0, target:M0) input

roots (method:M0) input

mI0 (method:M0, invoke:I0)
IEnum (invoke:I0, target:M0, ccaller:VC1, ccallee:VC0) output

### Rules

mI0(m,i) :- mI(m,i,_).
IEnum(i,m,vc2,vc1) :- roots(m), mI0(m,i), IE0(i,m). number
"""

def fix_contexts():
    f_str = datalog_solve (numbering, "number.dtl")
    m = re.search(r"paths = (\d+)", f_str)
    if m is not None:
        paths = m.group(1)
        print "Requires %s paths." % paths
        domains = []
        i = file(os.path.join("results", "fielddomains.pa"), "rt")
        for l in i:
            if l.startswith("VC "):
                domains.append("VC %s" % paths)
            else:
                domains.append(l.strip())
        i.close()
        i = file(os.path.join("results", "fielddomains.pa"), "wt")
        for l in domains:
            print>>i, l
        i.close()


# Do the PQL query

dumptypes = """.basedir "results"

### Domains

.include "fielddomains.pa"

.bddvarorder M1_I0_N0_F0_M0_V1xV0_H1_Z0_T0_T1_H0
### Relations

aT (type1:T0, type2:T1) input outputtuples
"""

def run_pql(pql_query):
    datalog_solve (dumptypes, "dumpat.dtl")

    pql_cp = os.path.pathsep.join([os.path.curdir, pql_jar])
    cmd = "%s -cp %s -Dpql.datalog.pacs=no net.sf.pql.datalog.DatalogGenerator %s" % (java_cmd, pql_cp, pql_query)
    if verbose:
        print cmd
    (i_p, o_p, e_p) = os.popen3 (cmd)
    dtl = o_p.read()
    problems = e_p.read()
    print problems
    datalog_solve (dtl, "pql_conv.dtl")

# Map resulting tuples to invocation sites
def tuple_map (src, names, target):
    src = os.path.join(os.path.curdir, "results", src)
    names = os.path.join(os.path.curdir, "results", names)
    n = [x.strip() for x in file(names)]
    l = [n[int(x)] for x in file(src) if x[0] != '#' and int(x) < len(n)]
    o = file(target, "wt")
    for x in l:
        print>>o, x
    o.close()

if __name__ == "__main__":
  if len(sys.argv) < 3:
      print "Usage: ./run_bddbddb.py <main_class> <datalog_filename>"
      print "(Fast mode - if you've already gen'ed relations): ./run_bddbddb.py <main_class> <datalog_filename> --fast"
      sys.exit(1)

  check_env_vars()

  # Michael tells me to split these parts up so that students can make
  # multiple queries without regenerating this stuff every time ...

  if len(sys.argv) < 4 or sys.argv[3] != '--fast':
    clean()
    print "Generating relations..."
    gen_relations (sys.argv[1], basecp)
    print "De-munging names ..."
    os.system('mv results/unmunged_method.map results/method.map')
    os.system('mv results/unmunged_name.map results/name.map')
  else:
    os.system('rm results/*.tuples')

  
  print "Preparing for running context-sensitive pointer analysis ..."

  print "Counting contexts..."
  fix_contexts()
    
  print "Numbering contexts..."
  datalog_solve (numbering, "numbering.dtl")

  print "Running analysis in file ..."
  datalog_solve_filename(sys.argv[2])

  print "Done"
  sys.exit(0)

