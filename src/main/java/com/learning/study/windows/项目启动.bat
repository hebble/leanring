@echo off
title ��������
:head
cls
echo 1.����nacos
echo.
echo 2.����rabbit
echo.
echo 3.����zookeeper
echo.
echo 4.����kafka
echo.
echo 0.�˳�
set /p option=ִ������:
if "%option%"=="1" goto 1
if "%option%"=="2" goto 2
if "%option%"=="3" goto 3
if "%option%"=="4" goto 4
if "%option%"=="0" exit
echo ����Ԥ��������������룡 & pause & goto head
:1
D:
cd D:\develop\nacos8848_standalone\bin
start /max startup.cmd
pause & goto head
:2
D:
cd D:\develop\rabbitmq_server-3.9.8\sbin
start /max rabbitmq-server.bat
pause & goto head
:3
D:
cd D:\develop\apache-zookeeper-3.5.9-bin\bin
start /max zkServer.cmd
pause & goto head
:4
D:
cd D:\develop\kafka_2.12-2.0.0
start /max .\bin\windows\kafka-server-start.bat .\config\server.properties
pause & goto head
