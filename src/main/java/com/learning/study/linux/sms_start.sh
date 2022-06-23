#!/bin/sh
cd /home/user/sms-pool/sms-service
#/usr/local/jdk1.8/bin/java -jar sms-service-dev.jar &
nohup /usr/bin/java -jar -Duser.timezone=GMT+08 sms-service-dev.jar > sms-service-dev.jar.log 2>&1 &
echo $! > /var/run/sms.pid





