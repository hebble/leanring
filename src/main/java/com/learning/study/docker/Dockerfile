 FROM java:8

 #作者信息
 MAINTAINER CBA "000@000.com"

 USER root

 RUN mkdir -p /usr/local/docker
 COPY demo-0.0.1.jar /usr/local/docker/demo-0.0.1.jar

 ENV my_path /usr/local/docker
 WORKDIR $my_path

 ENTRYPOINT ["nohup","java","-jar","/usr/local/docker/demo-0.0.1.jar","&"]

 CMD echo "test"
 CMD echo "docker test"

 EXPOSE 8080


 ###############################################

 FROM java:8

 #作者信息
 MAINTAINER gjw<1994371810@qq.com>

 #指定环境变量 工作目录
 ENV WORKDIR "/app"

 #指定工作目录
 WORKDIR ${WORKDIR}

 #暴露端口
 EXPOSE 8899

 ADD ./target/*.jar ${WORKDIR}
 #启动 如果启动时想要添加额外的配置可以通过docker run -e PARAM="-Dserver.port=8000 -Dspring.profiles.active-prod"
 ENTRYPOINT ["/bin/bash","-c","java -Dfile.encoding=utf8 ${PARAM} -jar ${WORKDIR}/app.jar"]