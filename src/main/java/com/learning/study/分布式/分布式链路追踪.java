package com.learning.study.分布式;

/**
 * https://blog.csdn.net/locken123/article/details/120332819 Skywalking学习及整合springboot
 */
public class 分布式链路追踪 {
    /**
     1.Skywalking介绍
         Skywalking是一个国产的开源框架，2015年有吴晟个人开源，2017年加入Apache孵化器，国人开源的产品，主要开发人员来自于华为，2019年4月17日Apache董事会批准SkyWalking成为顶级项目，支持Java、.Net、NodeJs等探针，数据存储支持Mysql、
         Elasticsearch等，跟Pinpoint一样采用字节码注入的方式实现代码的无侵入，探针采集数据粒度粗，但性能表现优秀，且对云原生支持，目前增长势头强劲，社区活跃。
         Skywalking是分布式系统的应用程序性能监视工具，专为微服务，云原生架构和基于容器（Docker，K8S,Mesos）架构而设计，它是一款优秀的APM（Application Performance Management）工具，包括了分布式追踪，性能指标分析和服务依赖分析等。

     2.链路追踪框架对比
         Zipkin:
            是Twitter开源的调用链路分析工具，目前基于Spingcloud sleuth得到了广泛的应用，特点是轻量，部署简单。
         Pinpoint:
            一个韩国团队开源的产品，运用了字节码增强技术，只需要在启动时添加启动参数即可，对代码无侵入，目前支持Java和PHP语言，底层采用HBase来存储数据，探针收集的数据粒度非常细，但性能损耗大，因其出现的时间较长，完成度也很高，应用的公司较多
         Skywalking:
            是本土开源的基于字节码注入的调用链路分析以及应用监控分析工具，特点是支持多种插件，UI功能较强，接入端无代码侵入。
         CAT:
            是由国内美团点评开源的，基于Java语言开发，目前提供Java、C/C++、Node.js、Python、Go等语言的客户端，监控数据会全量统计，国内很多公司在用，例如美团点评、携程、拼多多等，CAT跟下边要介绍的Zipkin都需要在应用程序中埋点，对代码侵入性强。
         其他成熟的
             Naver 的 Pinpoint
             Apache 的 HTrace
             阿里的鹰眼 Tracing
             京东的 Hydra
             新浪的 Watchman
         详见分布式链路追踪技术对比.webp

     3.Skywalking架构
        SkyWalking 逻辑上分为四部分: 探针, 平台后端, 存储和用户界面。
         探针: 基于不同的来源可能是不一样的, 但作用都是收集数据, 将数据格式化为 SkyWalking 适用的格式.
         平台后端: 支持数据聚合, 数据分析以及驱动数据流从探针到用户界面的流程。分析包括 Skywalking 原生追踪和性能指标以及第三方来源，包括 Istio 及 Envoy telemetry , Zipkin 追踪格式化等。 你甚至可以使用 Observability Analysis Language 对原生度量指标 和 用于扩展度量的计量系统 自定义聚合分析。
         存储: 通过开放的插件化的接口存放 SkyWalking 数据. 你可以选择一个既有的存储系统, 如 ElasticSearch, H2 或 MySQL 集群(Sharding-Sphere 管理),也可以选择自己实现一个存储系统. 当然, 我们非常欢迎你贡献新的存储系统实现。
         UI: 一个基于接口高度定制化的Web系统，用户可以可视化查看和管理 SkyWalking 数据。

     4.Spring Cloud与Skywalking实战 https://blog.csdn.net/locken123/article/details/120332819
        (1)ES安装
        (2)Skywalking安装
        (3)服务修改启动参数
            -javaagent:/mnt/skywalking/agent/skywalking-agent.jar
            -Dskywalking.agent.service_name=test-service
            -Dskywalking.collector.backend_service=192.168.60.34:11800 -jar test-0.0.1-SNAPSHOT.jar
        (4)Skywalking 添加tid
            添加依赖
             <dependency>
                 <groupId>org.apache.skywalking</groupId>
                 <artifactId>apm-toolkit-logback-1.x</artifactId>
                 <version>6.2.0</version>
             </dependency>

             替换logback.xml中配置
             <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
                <pattern>%d{ISO8601} | %thread | %-5level | %msg%n</pattern>
             </encoder>

             <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
                <layout class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.TraceIdPatternLogbackLayout">
                    <pattern>%d{ISO8601} | %tid | %thread | %-5level | %msg%n</pattern>
                </layout>
             </encoder>
     */
}
