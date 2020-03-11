#!/bin/bash

# 
# Script to run jaguar on defects4j Closure
#

CP="$(defects4j export -p cp.test -w closure)"
PROJECT_DIR="closure"
CLASSES_DIR="build/classes/"
TESTS_DIR="build/test/"
JAGUAR_JAR="br.usp.each.saeg.jaguar.core-1.0.0-jar-with-dependencies.jar"
AGENT_JAR="ba-dua-agent-rt-0.4.1-SNAPSHOT-all.jar"
HEURISTIC="Ochiai"
LOG_LEVEL="DEBUG" # ALL / TRACE / DEBUG / INFO / WARN / ERROR

java -jar $JAGUAR_JAR \
	--agent $AGENT_JAR \
    --classpath $CP \
	--projectDir $PROJECT_DIR \
	--classesDir $CLASSES_DIR \
	--testsDir $TESTS_DIR \
	--logLevel $LOG_LEVEL \
	--dataflow
