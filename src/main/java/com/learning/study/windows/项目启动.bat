@echo off
title 批量启动
:head
cls
echo 1.运行nacos
echo.
echo 2.运行rabbit
echo.
echo 3.运行zookeeper
echo.
echo 4.运行kafka
echo.
echo 0.退出
set /p option=执行命令:
if "%option%"=="1" goto 1
if "%option%"=="2" goto 2
if "%option%"=="3" goto 3
if "%option%"=="4" goto 4
if "%option%"=="0" exit
echo 超出预定命令，请重新输入！ & pause & goto head
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
