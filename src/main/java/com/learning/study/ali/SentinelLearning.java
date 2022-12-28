package com.learning.study.ali;

/**
 * https://blog.csdn.net/weixin_49385823/article/details/123765376 Sentinel (springboot整合、feign整合、docker安装sentinel)和 Sleuth+Zipkin服务链路追踪
 */
public class SentinelLearning {
    /**
     1.Sentinel 是什么？
        随着微服务的流行，服务和服务之间的稳定性变得越来越重要。Sentinel 以流量为切入点，从流量控制、熔断降级、系统负载保护等多个维度保护服务的稳定性
         Sentinel 的历史：
             2012 年，Sentinel 诞生，主要功能为入口流量控制。
             2013-2017 年，Sentinel 在阿里巴巴集团内部迅速发展，成为基础技术模块，覆盖了所有的核心场景。Sentinel 也因此积累了大量的流量归整场景以及生产实践。
             2018 年，Sentinel 开源，并持续演进。
             2019 年，Sentinel 朝着多语言扩展的方向不断探索，推出 C++ 原生版本，同时针对 Service Mesh 场景也推出了 Envoy 集群流量控制支持，以解决 Service Mesh 架构下多语言限流的问题。
             2020 年，推出 Sentinel Go 版本，继续朝着云原生方向演进。
         Sentinel 分为两个部分:
             核心库（Java 客户端）不依赖任何框架/库，能够运行于所有 Java 运行时环境，同时对 Dubbo / Spring Cloud 等框架也有较好的支持。
             控制台（Dashboard）基于 Spring Boot 开发，打包后可以直接运行，不需要额外的 Tomcat 等应用容器。
         Sentinel 可以简单的分为 Sentinel 核心库和 Dashboard。核心库不依赖 Dashboard，但是结合 Dashboard 可以取得最好的效果

     2.基本概念及作用
        基本概念：
             资源：是 Sentinel 的关键概念。它可以是 Java 应用程序中的任何内容，例如，由应用程序提供的服务，或由应用程序调用的其它应用提供的服务，甚至可以是一段代码。在接下来的文档中，我们都会用资源来描述代码块。
             只要通过 Sentinel API 定义的代码，就是资源，能够被 Sentinel 保护起来。大部分情况下，可以使用方法签名，URL，甚至服务名称作为资源名来标示资源。
             规则：围绕资源的实时状态设定的规则，可以包括流量控制规则、熔断降级规则以及系统保护规则。所有规则可以动态实时调整。
        主要作用：
             流量控制
             熔断降级
             系统负载保护
        我们说的资源，可以是任何东西，服务，服务里的方法，甚至是一段代码。使用 Sentinel 来进行资源保护，主要分为几个步骤:
             定义资源
             定义规则
             检验规则是否生效
        先把可能需要保护的资源定义好，之后再配置规则。也可以理解为，只要有了资源，我们就可以在任何时候灵活地定义各种流量控制规则。在编码的时候，只需要考虑这个代码是否需要保护，如果需要保护，就将之定义为一个资源。

     3.流量控制
        3.1 什么是流量控制?
            流量控制在网络传输中是一个常用的概念，它用于调整网络包的发送数据。然而，从系统稳定性角度考虑，在处理请求的速度上，也有非常多的讲究。任意时间到来的请求往往是随机不可控的，而系统的处理能力是有限的。我们需要根据系统的处理能力对流量进行控制。Sentinel 作为一个调配器，可以根据需要把随机的请求调整成合适的形状，如下图所示：
                 资源的调用关系，例如资源的调用链路，资源和资源之间的关系；
                 运行指标，例如 QPS、线程数等；
                 控制的效果，例如直接限流（快速失败）、冷启动（Warm Up）、匀速排队（排队等待）等。
            Sentinel 的设计理念是让您自由选择控制的角度，并进行灵活组合，从而达到想要的效果。
        3.2 QPS流量控制
            当 QPS 超过某个阈值的时候，则采取措施进行流量控制。流量控制的效果包括以下几种：直接拒绝、Warm Up、匀速排队。
            (1)直接拒绝
                直接拒绝（RuleConstant.CONTROL_BEHAVIOR_DEFAULT）方式是默认的流量控制方式，当QPS超过任意规则的阈值后，新的请求就会被立即拒绝，拒绝方式为抛出FlowException。这种方式适用于对系统处理能力确切已知的情况下，比如通过压测确定了系统的准确水位时。
                 ​ 阈值类型选择：QPS
                 ​ 单机阈值：2
                综合起来的配置效果就是，该接口的限流策略是每秒最多允许2个请求进入。
            (2)Warm Up（预热）
                Warm Up（RuleConstant.CONTROL_BEHAVIOR_WARM_UP）方式，即预热/冷启动方式。当系统长期处于低水位的情况下，当流量突然增加时，直接把系统拉升到高水位可能瞬间把系统压垮。通过"冷启动"，让通过的流量缓慢增加，在一定时间内逐渐增加到阈值上限，给冷系统一个预热的时间，避免冷系统被压垮。
                疯狂访问：http://localhost:18080/hi
                可以发现前几秒会发生熔断，几秒钟之后就完全没有问题了
            (3)匀速排队
                匀速排队（RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER）方式会严格控制请求通过的间隔时间，也即是让请求以均匀的速度通过，对应的是漏桶算法。
                测试配置：1s处理一个请求，排队等待，等待时间20s。
        3.3 关联限流
            关联限流：当关联的资源请求达到阈值时，就限流自己。
        3.4 链路限流
            一棵典型的调用树如下图所示：
                     machine-root
                      /       \
                    /         \
                 Entrance1     Entrance2
                    /             \
                    /               \
             DefaultNode(nodeA)   DefaultNode(nodeA)
            上图中来自入口 Entrance1 和 Entrance2 的请求都调用到了资源 NodeA，Sentinel 允许只根据某个入口的统计信息对资源限流。
        3.5 线程数限流
            并发线程数限流用于保护业务线程数不被耗尽。
            例如，当应用所依赖的下游应用由于某种原因导致服务不稳定、响应延迟增加，对于调用者来说，意味着吞吐量下降和更多的线程数占用，极端情况下甚至导致线程池耗尽。为应对太多线程占用的情况，业内有使用隔离的方案，比如通过不同业务逻辑使用不同线程池
            来隔离业务自身之间的资源争抢（线程池隔离）。这种隔离方案虽然隔离性比较好，但是代价就是线程数目太多，线程上下文切换的 overhead 比较大，特别是对低延时的调用有比较大的影响。Sentinel 并发线程数限流不负责创建和管理线程池，而是简单统计当前
            请求上下文的线程数目，如果超出阈值，新的请求会被立即拒绝，效果类似于信号量隔离。
            配置如下：如果请求的并发数超过一个就限流

     4.熔断降级
         Sentinel除了流量控制以外，对调用链路中不稳定的资源进行熔断降级也是保障高可用的重要措施之一。
         Sentinel 熔断降级会在调用链路中某个资源出现不稳定状态时（例如调用超时或异常比例升高），对这个资源的调用进行限制，让请求快速失败，避免影响到其它的资源而导致级联错误。当资源被降级后，在接下来的降级时间窗口之内，对该资源的调用都自动熔断（默认行为是抛出 DegradeException）。
         Sentinel 和 Hystrix 的原则是一致的: 当调用链路中某个资源出现不稳定，例如，表现为 timeout，异常比例升高的时候，则对这个资源的调用进行限制，并让请求快速失败，避免影响到其它的资源，最终产生雪崩的效果。
         限流降级指标有三个：
             平均响应时间（RT）
             异常比例
             异常数
        4.1 平均响应时间（RT）
             平均响应时间 (DEGRADE_GRADE_RT)：当资源的平均响应时间超过阈值（DegradeRule 中的 count，以 ms 为单位，默认上限是4900ms）之后，资源进入准降级状态。如果1s之内持续进入 5 个请求，它们的 RT 都持续超过这个阈值，那么在接下来的时间窗口（DegradeRule 中的 timeWindow，以 s 为单位）
             之内，对这个方法的调用都会自动地返回（抛出 DegradeException）。在下一个时间窗口到来时, 会接着再放入5个请求, 再重复上面的判断。
            例如：配置 超时时间100ms，熔断时间10s
        4.2 异常比例
             异常比例 (DEGRADE_GRADE_EXCEPTION_RATIO)：当资源的每秒请求量 >= 5，且每秒异常总数占通过量的比值超过阈值（DegradeRule 中的 count）之后，资源进入降级状态，即在接下的时间窗口（DegradeRule中的 timeWindow，以 s 为单位）之内，对这个方法的调用都会自动地返回。
             异常比率的阈值范围是 [0.0, 1.0]，代表 0% - 100%。
        4.3 异常数
            异常数 (DEGRADE_GRADE_EXCEPTION_COUNT)：当资源近 1 分钟的异常数目超过阈值之后会进行熔断。

     5.规则持久化
         无论是通过硬编码的方式来更新规则，还是通过接入 Sentinel Dashboard 后，在页面上操作更新规则，都无法避免一个问题，那就是服务重启后，规则就丢失了，因为默认情况下规则是保存在内存中的。
         我们在 Dashboard 上为客户端配置好了规则，并推送给了客户端。这时由于一些因素客户端出现异常，服务不可用了，当客户端恢复正常再次连接上 Dashboard 后，这时所有的规则都丢失了，我们还需要重新配置一遍规则，这肯定不是我们想要的。
         持久化配置分以下3步：
            (1)引入依赖
                 <dependency>
                     <groupId>com.alibaba.csp</groupId>
                     <artifactId>sentinel-datasource-nacos</artifactId>
                 </dependency>
            (2)添加配置
                 # 这里datasource后的consumer是数据源名称，可以随便写，推荐使用服务名
                 spring.cloud.sentinel.datasource.consumer.nacos.server-addr=localhost:8848
                 spring.cloud.sentinel.datasource.consumer.nacos.dataId=${spring.application.name}-sentinel-rules
                 spring.cloud.sentinel.datasource.consumer.nacos.groupId=SENTINEL_GROUP
                 spring.cloud.sentinel.datasource.consumer.nacos.data-type=json
                 # 规则类型，取值见：org.springframework.cloud.alibaba.sentinel.datasource.RuleType
                 spring.cloud.sentinel.datasource.consumer.nacos.rule_type=flow
            (3)nacos中创建流控规则
                 配置内容如下：
                 [
                     {
                         "resource": "/hello",
                         "limitApp": "default",
                         "grade": 1,
                         "count": 2,
                         "strategy": 0,
                         "controlBehavior": 0,
                         "clusterMode": false
                     }
                 ]
                 resource：资源名称
                 limitApp：限流应用，就是用默认就可以
                 grade：阈值类型，0表示线程数，1表示qps
                 count：单机阈值
                 strategy：流控模式，0-直接，1-关联， 2-链路
                 controlBehavior：流控效果。0-快速失败，1-warm up 2-排队等待
                 clusterMode：是否集群
     */
}
