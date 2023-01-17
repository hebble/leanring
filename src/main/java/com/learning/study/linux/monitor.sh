#! /bin/bash

source /etc/profile
 
cd /home/user/sms-pool/sms-service
 
procnum=`ps -ef|grep sms-service-dev.jar|grep -v grep|wc -l`
if [ $procnum -eq 0 ]
then
	    echo `date +%Y-%m-%d` `date +%H:%M:%S` "restart service" >>/home/user/sms-pool/sms-service/restart.log
	    ./start.sh
fi
