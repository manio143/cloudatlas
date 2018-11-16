#!/bin/bash

javac -d bin -cp bin:lib/cup.jar:lib/JLex.jar \
src/pl/edu/mimuw/cloudatlas/model/*.java \
src/pl/edu/mimuw/cloudatlas/interpreter/*.java \
src/pl/edu/mimuw/cloudatlas/interpreter/query/*.java \
src/pl/edu/mimuw/cloudatlas/interpreter/query/Absyn/*.java
