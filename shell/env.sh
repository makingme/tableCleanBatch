#!/bin/bash
################################################################################
#	This script set information such as Os, Environment, Database etc.
################################################################################

. ./shfunc.sh

#===============================================================================
#	OS						(FIX-IT)
#===============================================================================
#----- HPUX / AIX / OSX / LINUX / WIN
export APP_OS=LINUX
export LANG=ko_KR.utf-8

#===============================================================================
#	JAVA					(FIX-IT)
#===============================================================================
export APP_JAVA=java

#===============================================================================
#	APP VARIABLES		(FIX-IT)
#===============================================================================
export APP_HOME=/home/lab/cleanBatch

export APP_CONF=$APP_HOME/conf
export APP_CONF_FILE=$APP_CONF/config.json
export LOGBACK=-Dlogback.configurationFile=$APP_CONF/logback.xml

export APP_JAR=$APP_HOME/target/cleanBatch.jar

export APP_LOG=$APP_HOME/logs
export ENV_NAME=production

#===============================================================================
#	CLASS PATH			(FIX-IT)
#===============================================================================
export APP_IN_JAR=`findDir $APP_HOME/target/`
export CLASSPATH=$APP_HOME/conf:$APP_IN_JAR:$CLASSPATH
export PATH=.:$PATH

#===============================================================================
#	HOST CONFIG			(FIX-IT)
#===============================================================================
export APP_HOST=`hostname`

#===============================================================================
#	SHELL CONFIG		(FIX-IT)
#===============================================================================
if [ "$APP_OS" = "HPUX" ]
then
#----- HPUX
	export UMS_PS_OPT="-efx"
	export UMS_JVMOPT="-XX:+UseGetTimeOfDay -XX:+UseHighResolutionTimer -Xeprof:off"
elif [ "$APP_OS" = "AIX" ]
then
#----- AIX
	export UMS_PS_OPT=-ef
	export UMS_JVMOPT=
elif [ "$APP_OS" = "LINUX" ]
then
#----- LINUX
	export UMS_PS_OPT=-ef
	export UMS_JVMOPT="-XX:+UseParallelGC -Xms250m -Xmx250m"
elif [ "$APP_OS" = "OSX" ]
then
#----- OSX
	export UMS_PS_OPT=-ex
fi
