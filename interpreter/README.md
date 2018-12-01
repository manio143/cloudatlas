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

    ./run_tests

Tests for standalone interpreter
--------------------------------

    ./run_standalone_tests
