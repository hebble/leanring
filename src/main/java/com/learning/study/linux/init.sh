#!/bin/sh
DIR=/home/ubuntu
cd $DIR
mkdir gather-ferry-data gather-ferry-upload gather-web sms-gateway
mkdir gather-ferry-upload/log gather-web/log sms-gateway/log
touch gather-ferry-upload/gather-ferry-upload-ferry.jar.log gather-web/gather-web-ferry.jar.log sms-gateway/sms-gateway-ferry.jar.log
