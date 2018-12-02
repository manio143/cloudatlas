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

Note: different syntax than regular interpreter!

    ./run_tests

Tests for multi query interpreter
--------------------------------

Note: different syntax than regular interpreter!

    ./run_querysets_tests
