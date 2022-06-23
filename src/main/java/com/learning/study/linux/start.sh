#!/bin/sh
echo -e "\033[31m ======================================== \033[0m"
echo -e "\033[31m 开始启动项目\033[0m"
echo -e "\033[32m 1.启动sms-gateway\033[0m"
echo -e "\033[32m 2.启动gather-web\033[0m"
echo -e "\033[32m 3.启动gather-ferry-upload\033[0m"
read -p "请输入操作(1|2|3):" number
echo -e "\033[31m 你的操作为: \033[0m"$number

##开始启动项目

##猫池网关
if test $number = "1" ;then
 echo "---------------启动sms-gateway开始"
pid=`ps -ef|grep sms-gateway-ferry.jar|grep -v grep|awk '{print $2}'`
kill -9 $pid

cd /home/ubuntu/sms-gateway
rm -rf sms-gateway-ferry.jar
mv /home/ubuntu/temp/sms-gateway-0.0.1-SNAPSHOT.jar /home/ubuntu/sms-gateway/sms-gateway-ferry.jar

cd /home/ubuntu/sms-gateway
nohup /usr/bin/java -jar -Duser.timezone=GMT+08 sms-gateway-ferry.jar --spring.profiles.active=ferry > sms-gateway-ferry.jar.log 2>&1 &
echo "---------------启动sms-gateway结束"
fi

##采集web
if test $number = "2";then
 echo "---------------启动gather-web开始"
pid=`ps -ef|grep gather-web-ferry.jar|grep -v grep|awk '{print $2}'`
kill -9 $pid

cd /home/ubuntu/gather-web
rm -rf gather-web-ferry.jar
mv /home/ubuntu/temp/gather-web-0.0.1-SNAPSHOT.jar /home/ubuntu/gather-web/gather-web-ferry.jar

cd /home/ubuntu/gather-web
nohup java -jar -Duser.timezone=GMT+08 /home/ubuntu/gather-web/gather-web-ferry.jar --spring.profiles.active=ferry > gather-web-ferry.jar.log 2>&1 &
 echo "---------------启动gather-web结束"
fi

##采集数据上传摆渡
if test $number = "3";then
 echo "---------------启动gather-ferry-upload开始"
pid=`ps -ef|grep gather-ferry-upload-ferry.jar|grep -v grep|awk '{print $2}'`
kill -9 $pid

cd /home/ubuntu/gather-ferry-upload
rm -rf gather-ferry-upload-ferry.jar
mv /home/ubuntu/temp/gather-ferry-upload-0.0.1-SNAPSHOT.jar /home/ubuntu/gather-ferry-upload/gather-ferry-upload-ferry.jar

cd /home/ubuntu/gather-ferry-upload
nohup java -jar -Duser.timezone=GMT+08 /home/ubuntu/gather-ferry-upload/gather-ferry-upload-ferry.jar --spring.profiles.active=ferry > gather-ferry-upload-ferry.jar.log 2>&1 &

 echo "---------------启动gather-ferry-upload结束"
fi
