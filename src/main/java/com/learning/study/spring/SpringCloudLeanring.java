package com.learning.study.spring;

/**
 * https://blog.csdn.net/a6636656/article/details/124429257
 */
public class SpringCloudLeanring {
    /**
     Spring Cloud 是一系列框架的有序集合。它利用 Spring Boot 的开发便利性巧妙地简化了分布式系统基础设施的开发，如服务发现注册、配置中心、消息总线、负载均衡、断路器、数据监控等，都可以用 Spring Boot 的开发风格
     做到一键启动和部署。Spring Cloud 并没有重复制造轮子，它只是将目前各家公司开发的比较成熟、经得起实际考验的服务框架组合起来，通过 Spring Boot 风格进行再封装屏蔽掉了复杂的配置和实现原理，最终给开发者留出了
     一套简单易懂、易部署和易维护的分布式系统开发工具包。

     1.微服务架构
        1.1 什么是微服务架构
            微服务架构就是将单体的应用程序分成多个应用程序，这多个应用程序就成为微服务，每个微服务运行在自己的进程中，并使用轻量级的机制通信。这些服务围绕业务能力来划分，并通过自动化部署机制来独立部署。这些服务可以
            使用不同的编程语言，不同数据库，以保证最低限度的集中式管理。
        1.2 为什么需要学习Spring Cloud
            首先springcloud基于spingboot的优雅简洁，可还记得我们被无数xml支配的恐惧？可还记得 springmvc，mybatis错综复杂的配置，有了spingboot，这些东西都不需要了，spingboot好处不 再赘诉，springcloud就基于
            SpringBoot把市场上优秀的服务框架组合起来，通过Spring Boot风格进行再封装屏蔽掉了复杂的配置和实现原理
            什么叫做开箱即用？即使是当年的黄金搭档dubbo+zookeeper下载配置起来也是颇费心神的！而springcloud完成这些只需要一个jar的依赖就可以了！
            springcloud大多数子模块都是直击痛点，像zuul解决的跨域，fegin解决的负载均衡，hystrix的熔断机制等等等等
        1.3 Spring Cloud 是什么
             Spring Cloud是一系列框架的有序集合。它利用Spring Boot的开发便利性巧妙地简化了分布式系统基础设施的开发，如服务发现注册、配置中心、智能路由、消息总线、负载均衡、断路器、数据监控等，都可以用Spring Boot的开发风格做到一键启动和部署。
             Spring Cloud并没有重复制造轮子，它只是将各家公司开发的比较成熟、经得起实际考验的服务框架组合起来，通过Spring Boot风格进行再封装屏蔽掉了复杂的配置和实现原理，最终给开发者留出了一套简单易懂、易部署和易维护的分布式系统开发工具包。
        1.4 SpringCloud的优缺点
             优点：
                 1.耦合度比较低。不会影响其他模块的开发。
                 2.减轻团队的成本，可以并行开发，不用关注其他人怎么开发，先关注自己的开发。
                 3.配置比较简单，基本用注解就能实现，不用使用过多的配置文件。
                 4.微服务跨平台的，可以用任何一种语言开发。
                 5.每个微服务可以有自己的独立的数据库也有用公共的数据库。
                 6.直接写后端的代码，不用关注前端怎么开发，直接写自己的后端代码即可，然后暴露接口，通过组件进行服务通信。
             缺点：
                 1.部署比较麻烦，给运维工程师带来一定的麻烦。
                 2.针对数据的管理比麻烦，因为微服务可以每个微服务使用一个数据库。
                 3.系统集成测试比较麻烦
                 4.性能的监控比较麻烦。【最好开发一个大屏监控系统】
            总的来说优点大过于缺点，目前看来Spring Cloud是一套非常完善的分布式框架，目前很多企业开始用微服务、Spring Cloud的优势是显而易见的。因此对于想研究微服务架构的同学来说，学习Spring Cloud是一个不错的选择。
        1.5 SpringBoot和SpringCloud的区别？
             SpringBoot专注于快速方便的开发单个个体微服务。
             SpringCloud是关注全局的微服务协调整理治理框架，它将SpringBoot开发的一个个单体微服务整合并管理起来，
             为各个微服务之间提供，配置管理、服务发现、断路器、路由、微代理、事件总线、全局锁、决策竞选、分布式会话等等集成服务
             SpringBoot可以离开SpringCloud独立使用开发项目， 但是SpringCloud离不开SpringBoot ，属于依赖的关系
             SpringBoot专注于快速、方便的开发单个微服务个体，SpringCloud关注全局的服务治理框架。
        1.6 SpringCloud由什么组成
             这就有很多了，我讲几个开发中最重要的
             Spring Cloud Eureka：服务注册与发现
             Spring Cloud Zuul：服务网关
             Spring Cloud Ribbon：客户端负载均衡
             Spring Cloud Feign：声明性的Web服务客户端
             Spring Cloud Hystrix：断路器
             Spring Cloud Confifig：分布式统一配置管理
             等20几个框架，开源一直在更新
        1.7 Spring Cloud 和dubbo区别?
            

     */
}