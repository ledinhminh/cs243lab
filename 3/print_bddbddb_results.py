#!/usr/bin/env python

# Prints results of run_bddbddb.py analysis in human-readable format
# Note that only relations specified with 'outputtuples' will be
# properly outputted - merely using 'output' is NOT sufficient

# by Philip Guo, with code stolen from Michael Martin

import os, sys, re

RESULTS_DIR = 'results/'

# Maps domains to their respective *.map files
# (taken from results/fielddomains.pa)
domain_filenames = {
  'V': 'var.map',
  'H': 'heap.map',
  'T': 'type.map',
  'F': 'field.map',
  'I': 'invoke.map',
  'N': 'name.map',
  'M': 'method.map',
  'C': 'class.map',
  'STR': 'string.map',
}

main_classname = None

num_colon_RE = re.compile('^\d+: (.*)$')

# Given a string like "139: <real contents>" - just return "<real contents>"
# If it doesn't start with a number and a colon, don't worry about it
def filter_out_leading_number(s):
  m = num_colon_RE.search(s)
  if m:
    return m.group(1)
  else:
    return s


# Function originally written by Michael Martin
def decode (tuplesfile, *mapnames):
  maps = [[y.strip() for y in file(x).readlines()] for x in mapnames]
  result = []
  for line in file(tuplesfile):
    if '#' in line:
        line = line[:line.index('#')]
    svals = line.split()
    if len(svals) == len(maps):
      result.append([filter_out_leading_number(m[int(z)]) for (m,z) in zip(maps, svals)])

  return result

def decode_and_print(tuplesfile, *mapnames):
  for l in decode(tuplesfile, *mapnames):
    # Filtering action
    if main_classname:
      if len([e for e in l if main_classname in e]) == 0:
        continue

    print ' || '.join(l)


def main(argv):
  global main_classname

  if len(argv) < 3:
    print "Usage: ./print_bddbddb_results.py <main classname> <tuples_filename>"
    return 0

  main_classname = argv[1]
  tuples_filename = argv[2]

  if not os.path.exists(main_classname + '.class'):
    print 'Error: %s does not exist.  Are you sure you spelled it correctly?' % (main_classname + '.class')
    sys.exit(-1)

  first_line = None
  for line in open(tuples_filename):
    first_line = line.strip()
    break
    
  # We need to find the appropriate .map filenames for the domains
  # listed in tuples_filename

  # first_line should contain something of the form: # I0:15 M0:12
  # where in this case, I and M are the respective domains for the
  # tuples
  assert first_line.startswith('#')
  first_line = first_line[1:]
  domain_strs = first_line.split()

  # Returns 'I' in I0:15
  domain_RE = re.compile('([A-Z]+)\d+:\d+')

  map_filenames = [RESULTS_DIR + domain_filenames[domain_RE.search(s).group(1)] for s in domain_strs]

  decode_and_print(tuples_filename, *map_filenames)


if __name__ == "__main__":
  main(sys.argv)

