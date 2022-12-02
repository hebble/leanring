
#基础镜像
FROM java:8

#把当前目录下的jar包拷贝进docker镜像里
COPY *.jar /testcompose.jar

#CMD命令用于指定这个容器启动的时候要运行的命令(CMD一行只执行一个命令，可以写多个CMD)
CMD echo "---server.port=8080---"

#对外暴露8080端口
EXPOSE 8080

#ENTRYPOINT命令用于指定这个容器启动的时候要运行的命令(可以追加命令)
ENTRYPOINT java -jar testcompose.jar