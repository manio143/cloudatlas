#!/bin/sh
#
# Distributed Systems Lab
# Copyright (C) Konrad Iwanicki, 2012-2014
#
# This file contains code samples for the distributed systems
# course. It is intended for internal use only.
#

java -cp $PWD/bin:lib/cup.jar:lib/JLex.jar -Djava.rmi.server.codebase=file:$PWD/bin -Djava.rmi.server.hostname=localhost -Djava.security.policy=general.policy pl.edu.mimuw.cloudatlas.fetcher.Fetcher localhost fetcher.ini
