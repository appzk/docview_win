#!/bin/bash
#SYNOPSIS - Start Docview service.
#Author: Godwin
#Version: 2012-10-23 v1.0

# Identify the script name
SCRIPT=`basename $0`

function run() {
	ls ../logs
	if [ "$?" != "0" ];then
	mkdir -p ../logs
	fi
	
	nohup mvn -f ../pom.xml jetty:run -P$1 &> ../logs/startup.log &
	tail -f ../logs/startup.log
}

# Check the first argument for instructions
case "$1" in
    dev)
        run dev
        ;;

    test)
        run test
        ;;

    prod)
        run prod
        ;;

    *)
        echo "Usage: $SCRIPT {dev|test|prod}"
        exit 1
        ;;
esac

exit 0