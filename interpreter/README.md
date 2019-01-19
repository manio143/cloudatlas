#CloudAtlas

Distributed system for collecting metrics and calculating queries on these, based on a real system named CloudAtlas. 
The scheme of the system is very similar: each node stores the attributes of itself and its siblings for each zone it belongs to.
The zones are layered hierarchically, with child zones consisting only of one node representing a real machine.
Higher level zones do not represent single machines, but groups of nodes, with the root node encompassing the whole system.
The metrics are stored in Agent, which puts up an RMI API for inspecting and fetching of the metrics.
Each agent should be associated with a single Fetcher, which feeds the data it gathers on the host.
The Client is used to inspect the Agent. It puts a WWW interface that allows users to inspect the zones, add new queries etc.
To install or uninstall a query, it must be validated using the Signer, which operates using generated security keys.
The queries are written in SQL-like language, which is customly interpreted in our code.

Have fun!

##Build

    ant

##RMI registry

    cd bin
    rmiregistry &
    cd -

##Keys generation
    
    ./generateKeys

##Agent

Configured in utility/server.ini

    ./server

##Fetcher

Configured in utility/fetcher.ini

Metrics gathered using utility/metrics_file

    ./fetcher

##Client

Configured in utility/client.ini

By default the Client WWW interface can be accessed at localhost:8000

    ./client
    
##Signer

    ./signer    

##Docker
    
###Build

    docker build . -t cloudatlas

###Compose

    docker-compose up -d  
    

##Interpreter


###Standalone

    ./interpreter
    ./interpreter < tests_standalone/1.in

###Tests for single query interpreter

Note: 
Different syntax than the regular standalone interpreter!

(No query names)

    ./run_tests

###Tests for standalone interpreter

Note: 
Different behaviour than the regular standalone interpreter!

(Printing query results after reading each line)

    ./run_standalone_tests
