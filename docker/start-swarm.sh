#!/bin/bash
function startMachine {
    docker-machine stop $1
    docker-machine start $1
    docker-machine ssh $1 sudo cp $CERT /usr/local/share/ca-certificates/
    docker-machine ssh $1 sudo update-ca-certificates
}

CERT= # Your registry's certificate as seen by any of the manager nodes.  

startMachine manager1 
startMachine worker0
startMachine worker1



    
