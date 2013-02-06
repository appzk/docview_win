#!/bin/bash
ls ../logs
if [ "$?" != "0" ];then
mkdir -p ../logs
fi

nohup ant -f build.xml 'start_service' >> ../logs/startup.log &
echo 'start log docview.'
echo '-------------------------- startup logs ---------------------'
sleep 1
tail -f ../logs/startup.log
