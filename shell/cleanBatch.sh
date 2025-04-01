#!/bin/bash
APP_PNM=UMS_CLEAN_BATCH
APP_PID=301_$APP_PNM
PIDS=""
export APP_PNM=$APP_PNM
. ./env.sh
APP_LOGF=$APP_LOG/$APP_PNM.log
export CLEAN_BATCH_CONFIG=$APP_CONF_FILE
runutil $APP_PID $1 "$2"

echo $CLASSPATH
nohup $APP_JAVA $UMS_JVMOPT -Dpnm=$APP_PID $LOGBACK -server -jar $APP_JAR > $APP_LOG/SYSOUT 2>&1 &

aftercheck $APP_PID $!
