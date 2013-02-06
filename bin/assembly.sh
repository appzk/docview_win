#!/bin/bash

PROJECT_NAME="docview"

function assembly() {
	echo "===========>> START assembly <<==========="
	ls ../logs
	if [ "$?" != "0" ];then
		echo "Creating ../logs directory..."
		sleep 1
		mkdir -p ../logs
	fi
	
	tagName=${PROJECT_NAME}_${1}_4.0.1
	tagTime=`date "+%Y%m%d%H%M"`
	tagType=.tar.gz
	
	echo `date "+%Y%m%d %H:%M:%S"`" mvn -f../pom.xml clean assembly:assembly -P$1 ..................."
	mvn -f../pom.xml clean assembly:assembly -P$1
	
	ls ../target/${PROJECT_NAME}*${tagType}
	if [ "$?" == "0" ]; then
	    tagName=${tagName}_${tagTime}${tagType}
	    echo "Changing name to ${tagName}..."
	    sleep 1
	    mv ../target/${PROJECT_NAME}*${tagType} ../target/${tagName}
	    echo -e "\e[1;42mSuccess. please check file: ../target/${tagName}!\e[0m"
	else
		echo -e "\e[1;41mERROR: GZIP file not found!\e[0m"
		exit 1
	fi
	echo "===========>> assembly END. <<==========="
}

# Check the first argument for instructions
case "$1" in
    dev)
        assembly dev
        ;;

    test)
        assembly test
        ;;

    prod)
        assembly prod
        ;;

    *)
        echo "Usage: $SCRIPT {dev|test|prod}"
        exit 1
        ;;
esac

exit 0