echo 'Shutting down docview ...'
nohup mvn -f../pom.xml jetty:stop &> ../logs/startup.log &
sleep 5
tail -10 ../logs/startup.log

#Check property file
PROPS=../target/classes/server_params.properties
if [ ! -f $PROPS ]; then
    echo -e "\e[1;41mError: file ${PROPS} not found!\e[0m"
    exit 0
fi

#Get ports(DOCVIEW_PORTS)
DOCVIEW_PORTS=`grep port ${PROPS} | awk -F = '{print $2}'`

for PORT in ${DOCVIEW_PORTS}
do
    echo -e "\nstopping port ${PORT}..................."
    sleep 1
    PID=`netstat -antlp | grep ${PORT} | grep LISTEN | awk '{print $7}' | awk -F / '{print $1}'`
    kill -9 $PID
done