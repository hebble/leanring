package com.learning.study.docker;

/**
 * https://blog.csdn.net/qq_42449106/article/details/120716601 Dockerfile 部署springboot 项目暨保留字指令讲解
 */
public class DockerLearning {
    /**
     1.springboot项目docker部署
        1.1 编写dockerfile 文件
             FROM java:8

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
        1.2 构建镜像并运行
            docker build -t dockerdemo:0.1 .
            docker run -d --name demo -p 9090:8080 dockerdemo:0.1
        1.3 使用命令进入demo 容器内
            docker exec -it 6624cd0f0fad /bin/bash
            可以明显的看到，进入容器时的默认路径是 /usr/local/docker，容器启动时的执行命令是java -jar /usr/local/docker/demo-0.0.1.jar & /bin/sh -c echo “docker test” 。具体的含义请看下文讲解。

     2.保留字
        2.1 FROM
             FROM 命令可能是最重要的Dockerfile 命令。该命令定义了使用哪个基础镜像启动构建流程。基础镜像可以为任意镜像。如果基础镜像没有被发现，Docker 将试图从Docker image index 来查找该镜像。
             FROM命令必须是Dockerfile的首个命令。
                FROM java:8
        2.2 MAINTAINER 和LABEL
            这个命令用于声明镜像的作者与其邮箱，理论上它可以放置于Dockerfile 的任意位置，但是为了阅读方便，推荐还是放在FROM 的后面。
                MAINTAINER CBA “000@000.com”
            与之有类似功能的命令是LABEL，但是LABEL 更加灵活，其可以设置任何需要设置的元数据，并且可以轻松查看，例如我们可以使用以下命令替换MAINTAINER 命令：
                LABEL maintainer=“CBA”
        2.3 USER
            USER 指令设置运行镜像时要使用的用户名（或 UID）以及可选的用户组（或 GID），默认是root 用户。但是在为用户指定组时，用户将仅具有指定的组成员身份，任何其它已配置的组成员身份将被忽略。
                USER root
        2.4 RUN
            RUN指令会在前一条命令创建出的镜像的基础上创建一个容器，并在容器中运行命令，在命令结束运行后提交容器为新镜像，新镜像被Dockerfile中的下一条指令使用
                RUN mkdir -p /usr/local/docker
        2.5 CMD
            CMD指令有3种格式。
                 CMD < command> (shell格式)
                 CMD [“executable”, “param1”, “param2”] (exec格式，推荐使用)
                 CMD [“param1”, “param2”] (为ENTRYPOINT指令提供参数)
             在使用shell 和exec 格式时，命令在容器中的运行方式与RUN指令相同。
             但是，RUN指令在构建镜像时执行命令，并生成新的镜像；CMD指令在构建镜像时并不执行任何命令，而是在容器启动时默认将CMD指令作为第一条执行的命令。
             如果用户在命令行界面运行docker run 命令时指定了命令参数，则会覆盖CMD 指令中的命令。
            一个Dockerfile 中可以有多条CMD 指令，但只有最后一条CMD 指令有效。
                 CMD echo “test”
                 CMD echo “docker test”
        2.6 ENTRYPOINT
             ENTRYPOINT指令有两种格式。
                 ENTRYPOINT < command> (shell格式)
                 ENTRYPOINT [“executable”, “param1”, “param2”] (exec格式，推荐格式)
                 ENTRYPOINT 指令和CMD 类似，它也可用户指定容器启动时要执行的命令。
             但如果dockerfile 中也有CMD 指令，CMD 中的参数会被附加到ENTRYPOINT 指令的后面。
             如果这时docker run 命令带了参数，这个参数会覆盖掉CMD 指令的参数，并也会附加到ENTRYPOINT 指令的后面。
             可以看出，相对来说ENTRYPOINT 指令优先级更高。
                ENTRYPOINT [“java”,"-jar","/usr/local/docker/demo-0.0.1.jar"]
        2.7 COPY 和ADD
            ADD 指令的功能是将主机构建环境（上下文）目录中的文件和目录、以及一个URL 标记的文件 拷贝到镜像中。
                如果源是个归档文件（压缩文件），则docker 会自动解压。
                如果源是一个URL，那该URL 的内容将被下载并复制到容器中，下载后的文件权限自动设置为600 。
                如果目标路径不存在，则会自动创建目标路径。
            COPY 指令和ADD 指令功能和使用方式类似，但是COPY 只支持本地文件且不会做自动解压工作。
                COPY demo-0.0.1.jar /usr/local/docker/demo-0.0.1.jar
        2.8 ENV
             ENV 指令用来在镜像构建过程中设置环境变量，这些变量会永久地保存到该镜像创建的任何容器中。
             通过ENV 定义的环境变量，可以被后面的所有指令中使用，但不能被docker run 的命令参数引用。
                ENV my_path /usr/local/docker
        2.9 WORKDIR
            WORKDIR 指令用于指定容器的一个目录， 容器启动时所有执行的命令会在该目录下执行。如果这个工作目录不存在，则会自动创建一个。
            WORKDIR 指令可在dockerfile 中多次使用。如果提供了相对路径，则它将相对于上一个WORKDIR 指令的路径。例如：
                 WORKDIR /a
                 WORKDIR b
                 WORKDIR c
                 RUN pwd
            输出结果是 /a/b/c
        2.10 EXPOSE
            EXPOSE 指令声明docker 容器在运行时侦听的网络端口。也可以指定端口是侦听协议是TCP 还是UDP，如果未指定协议，则默认值为TCP。
            这个指令仅仅是声明容器打算使用什么端口而已，并不会自动在宿主机进行端口映射，尽管它可以这么做，但这个做法并不值得推荐。
            EXPOSE 8080
        2.11 VOLUME
            通过dockerfile 的 VOLUME 指令可以在镜像中创建挂载点，这样只要通过该镜像创建的容器都有了挂载点。其作用与使用docker run -v 命令相同。
            但是通过这种方式创建的挂载点，无法指定主机上对应的目录，是自动生成的。
        2.12 ONBUILD
             当我们在一个dockerfile 文件中加上ONBUILD指令，该指令对利用该dockerfile构建镜像（比如为A镜像）不会产生实质性影响。
             但是当我们编写一个新的dockerfile 文件来基于A 镜像构建一个镜像（比如为B镜像）时，这时构造A 镜像的dockerfile 文件中的ONBUILD 指令就生效了，在构建B 镜像的过程中，首先会执行ONBUILD 指令指定的指令，然后才会执行其它指令。

     3.docker可视化
        https://blog.csdn.net/zfw_666666/article/details/126538026
        (1)docker UI
        (2)Portainer
        (3)shipyard

     4.docker中镜像和容器的区别是什么
        docker中镜像和容器的区别：1、镜像是包含了各种环境或者服务的一个模板，而容器是镜像的一个实例；2、镜像是不能运行的，是静态的，而容器是可以运行的，是动态的。
        4.1 Docker镜像
             假设Linux内核是第0层，那么无论怎么运行Docker，它都是运行于内核层之上的。这个Docker镜像，是一个只读的镜像，位于第1层，它不能被修改或不能保存状态。
             一个Docker镜像可以构建于另一个Docker镜像之上，这种层叠关系可以是多层的。第1层的镜像层我们称之为基础镜像（Base Image），其他层的镜像（除了最顶层）我们称之为父层镜像（Parent Image）。这些镜像继承了他们的父层镜像的所有属性和设置，并在Dockerfile中添加了自己的配置。
             Docker镜像通过镜像ID进行识别。镜像ID是一个64字符的十六进制的字符串。但是当我们运行镜像时，通常我们不会使用镜像ID来引用镜像，而是使用镜像名来引用。要列出本地所有有效的镜像，可以使用命令
                docker images
            镜像可以发布为不同的版本，这种机制我们称之为标签（Tag）。
        4.2 Docker容器
            Docker容器可以使用命令创建：
                docker run imagename
             它会在所有的镜像层之上增加一个可写层。这个可写层有运行在CPU上的进程，而且有两个不同的状态：运行态（Running）和退出态（Exited）。这就是Docker容器。当我们使用docker run启动容器，Docker容器就进入运行态，当我们停止Docker容器时，它就进入退出态。
             当我们有一个正在运行的Docker容器时，从运行态到停止态，我们对它所做的一切变更都会永久地写到容器的文件系统中。要切记，对容器的变更是写入到容器的文件系统的，而不是写入到Docker镜像中的。
             我们可以用同一个镜像启动多个Docker容器，这些容器启动后都是活动的，彼此还是相互隔离的。我们对其中一个容器所做的变更只会局限于那个容器本身。
             如果对容器的底层镜像进行修改，那么当前正在运行的容器是不受影响的，不会发生自动更新现象。
             如果想更新容器到其镜像的新版本，那么必须当心，确保我们是以正确的方式构建了数据结构，否则我们可能会导致损失容器中所有数据的后果。
             64字符的十六进制的字符串来定义容器ID，它是容器的唯一标识符。容器之间的交互是依靠容器ID识别的，由于容器ID的字符太长，我们通常只需键入容器ID的前4个字符即可。当然，我们还可以使用容器名，但显然用4字符的容器ID更为简便。
     */
}
