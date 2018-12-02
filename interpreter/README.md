CloudAtlas
==========

Build
-----

    ant

Server
----------

    cd bin
    rmiregistry &
    cd -
    ./server

Fetcher
-------

    ./fetcher

Client
------

    ./client

Standalone interpreter
----------------------

    ./interpreter
    ./interpreter < tests_standalone/1.in

Tests for single query interpreter
----------------------------------

Note: 
Different syntax than the regular standalone interpreter!
No query names.

    ./run_tests

Tests for standalone interpreter
--------------------------------

Note: 
Different behaviour than the regular standalone interpreter!
Printing query results after reading each line.

    ./run_standalone_tests
