package com.learning.study.ali;

public class NacosLearning {
    /**
     Nacos 可以作为服务注册中心、配置中心。
    1.服务注册与调用入门
       为什么要将服务注册到Nacos？(为了更好的查找这些服务)
       在Nacos中服务提供者是如何向Nacos注册中心(Registry)续约的？(5秒心跳包)
       对于Nacos来讲它是如何判断服务实例的状态？(检测心跳包 ，15,30)
       服务消费方是如何调用服务提供方的服务的？(RestTemplate)
    2.Nacos中的负载均衡底层是如何实现的？
       通过Ribbon实现，Ribbon中定义了一些负载均衡算法，然后基于这些算法从服务实例中获取一个实例为消费方法提供服务
       Ribbon基于负载均衡策略进行服务调用, 所有策略都会实现IRule接口
    3.我们可以自己定义负载均衡策略吗?
       可以，基于IRule接口进行策略定义，也可以参考NacosRule进行实现
    4.配置中心
       什么是配置中心？
           存储项目配置信息的一个服务
       为什么要使用配置中心？
           集中管理配置信息，动态发布配置信息
       市场有哪些主流的配置中心？
           Apollo，Nacos，.....
       配置中心一般都会配置什么内容？
           可能会经常变化的配置信息,例如连接池，日志，线程池，限流熔断规则
       什么信息一般不会写到配置中心？
           服务端口，服务名，服务的注册地址，配置中心
       项目中为什么要定义bootstrap.yml文件？
           此文件被读取的优先级比较高，可以在服务启动时读取配置中心的数据
        Nacos配置中心宕机了，我们的服务还可以读取到配置信息吗?
           可以从内存，客户端获取配置中心的配置信息以后，会将配置信息在本地存储一份
        微服务应用中我们的客户端如何从配置中心获取信息？
           我们的服务一般会先从内存中读取配置信息，同时我们的微服务还可以定时向nacos配置中心发请求拉取(pull)更新的配置信息
        微服务应用中客户端如何感知配置中心的数据变化？
           1.4.x版本以后nacos客户端会基于长轮询机制从nacos获取配置信息，所谓的长轮询就是没有配置更新时，会在nacos服务端的队列进行等待
        默认是AP还是CP?如果切换成CP
           默认是AP, 切换CP只需执行命令: curl -X PUT '$NACOS_SERVER:8848/nacos/v1/ns/operator/switches?entry=serverMode&value=CP'
    5.Nacos持久化
        从Nacos介绍，到Nacos做注册中心、做配置中心，一直都没有提及持久化的问题,我们服务的信息、配置的信息都放在哪的？
        当我们使用默认配置启动Nacos时，所有配置文件都被Nacos保存在了内置的数据库中。
        在0.7版本之前，在单机模式时nacos使用嵌入式数据库实现数据的存储，不方便观察数据存储的基本情况。0.7版本增加了支持mysql数据源能力
        如果使用内嵌数据库，注定会有存储上限，本文带大家一起将Nacos中的数据实现持久化
        目前Nacos仅支持Mysql数据库，且版本要求：5.6.5+
        5.1 初始化数据库
             Nacos的数据库脚本文件在我们下载Nacos-server时的压缩包中就有
             进入nacos-server-1.0.1\nacos\conf目录，初始化文件：nacos-mysql.sql
             此处我创建一个名为 mynacos 的数据库，然后执行初始化脚本，成功后会生成 11 张表
        5.2 修改配置文件
             这里是需要修改Nacos-server的配置文件
             Nacos-server其实就是一个Java工程或者说是一个Springboot项目，他的配置文件在nacos-server-1.0.1\nacos\conf目录下，名为 application.properties，在文件底部添加数据源配置：
             spring.datasource.platform=mysql
             db.num=1
             db.url.0=jdbc:mysql://127.0.0.1:3306/mynacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
             db.user=root
             db.password=123456
         Nacos通过集中式存储来保证数据的持久化，同时也为Nacos集群部署奠定了基础
         试想一下，如果我们以之前的方式启动Nacos，如果想组建Nacos集群，那各个节点中的数据唯一性就是最大的问题
         Nacos采用了单一数据源，直接解决了分布式和集群部署中的一致性问题。
     */
}
