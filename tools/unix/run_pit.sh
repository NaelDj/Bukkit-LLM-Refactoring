#!/usr/bin/env bash
set -euo pipefail

mvn -q test-compile org.pitest:pitest-maven:mutationCoverage
