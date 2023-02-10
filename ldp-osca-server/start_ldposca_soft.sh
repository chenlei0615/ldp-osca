#!/bin/bash

pidqty=`ps -ef | grep libreoffice | grep -v "grep" | awk '{print $2}' | wc -l`
if [ $pidqty != 0 ] ;then
   ps -ef|grep "libreoffice" | grep -v "grep" |awk '{print "kill -9 "$2}'|sh;
fi

sleep 5 && /opt/libreoffice7.3/program/soffice --headless --accept="socket,host=127.0.0.1,port=8100;urp;" --nofirststartwizard &
