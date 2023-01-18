package com.learning.study.spring;

/**
 * https://blog.csdn.net/m0_46316970/article/details/125898849 Spring Boot的自动装配原理
 */
public class SpringbootLearning {
    /**
    1.Springboot 配置文件加载优先级
        springboot 启动会扫描以下位置的application.properties或者application.yml文件作为Spring boot的默认配置文件
              –file:./config/
              –file:./
              –classpath:/config/
              –classpath:/
        优先级由高到底，高优先级的配置会覆盖低优先级的配置。

    2.Springboot 自动装配原理
        2.1 启动依赖介绍
           spring-boot-starter-parent 点进去后发现还有一个父依赖, spring-boot-dependencies, 以后我们导入依赖默认是不需要写版本；但是如果导入的包没有在依赖中管理着就需要手动配置版本了；
           spring-boot-starter-xxx：就是spring-boot的场景启动器，xxx就是某种场景
           见到***-springboot-boot-starter：第三方为我们提供的简化开发的场景启动器

        2.2 自动装配原理
            SpringBoot 定义了一套接口规范，这套规范规定：SpringBoot 在启动时会扫描外部引用 jar 包中的META-INF/spring.factories文件，将文件中配置的类型信息加载到 Spring 容器（此处涉及
            到 JVM 类加载机制与 Spring 的容器知识），并执行类中定义的各种操作。对于外部 jar 来说，只需要按照 SpringBoot 定义的标准，就能将自己的功能装置进 SpringBoot。
           @SpringBootApplication其实就是下面三个注解的集合
               @SpringBootConfiguration: SpringBoot就应该运行这个类的main方法来启动SpringBoot应用
               @EnableAutoConfiguration: 告诉SpringBoot开启自动配置功能，这样自动配置才能生效
               @ComponentScan()

            @EnableAutoConfiguration:实现自动装配的核心注解
                EnableAutoConfiguration 只是一个简单地注解，自动装配核心功能的实现实际是通过 AutoConfigurationImportSelector类。

            总结:
                Spring Boot 通过@EnableAutoConfiguration开启自动装配，通过 SpringFactoriesLoader 最终加载META-INF/spring.factories中的自动配置类实现自动装配，自动配置类其实就是通过@Conditional
                按需加载的配置类，想要其生效必须引入spring-boot-starter-xxx包实现起步依赖。

    3.springboot自动装配过程
       步骤总结：
           SpringBoot先加载所有的自动配置类 xxxxxAutoConfiguration
           每个自动配置类按照条件进行生效，默认都会绑定配置文件指定的值。xxxxProperties里面拿。xxxProperties和配置文件进行了绑定
           生效的配置类就会给容器中装配很多组件
           只要容器中有这些组件，相当于这些功能就有了
           定制化配置
           用户直接自己@Bean替换底层的组件
           用户去看这个组件是获取的配置文件什么值就去修改。
           如果用户自己配置了以用户的优先
        总流程:
           xxxxxAutoConfiguration —> 组件 —> xxxxProperties里面拿值 ----> application.properties

    4.spring.factories中这么多配置，每次启动都要全部加载么？(按需加载)
        一遍筛选过滤，@ConditionalOnXXX 中的所有条件都满足，该类才会生效
        4.1 Spring Boot 提供的条件注解如下：
             @ConditionalOnBean：当容器里有指定 Bean 的条件下
             @ConditionalOnMissingBean：当容器里没有指定 Bean 的情况下
             @ConditionalOnSingleCandidate：当指定 Bean 在容器中只有一个，或者虽然有多个但是指定首选 Bean
             @ConditionalOnClass：当类路径下有指定类的条件下
             @ConditionalOnMissingClass：当类路径下没有指定类的条件下
             @ConditionalOnProperty：指定的属性是否有指定的值
             @ConditionalOnResource：类路径是否有指定的值
             @ConditionalOnExpression：基于 SpEL 表达式作为判断条件
             @ConditionalOnJava：基于 Java 版本作为判断条件
             @ConditionalOnJndi：在 JNDI 存在的条件下差在指定的位置
             @ConditionalOnNotWebApplication：当前项目不是 Web 项目的条件下
             @ConditionalOnWebApplication：当前项目是 Web 项 目的条件下
         4.2 组合Conditional(使用SpEL表达式)
             介绍与、或、非
            @ConditionalOnExpression("'${spring.profiles.active:dev}' != 'dev'") //非
            @ConditionalOnExpression("'${crane.condition.a}' == 'a' and '${crane.condition.b}' == 'b'") //与
            @ConditionalOnExpression("'${spring.profiles.active}' == 'dev' or '${spring.profiles.active}' == 'test'") //或

    5.如何实现一个starter？
         便于自己理解，这里自己可以实现一个 starter，实现自定义线程池：
        (1)创建threadpool-spring-boot-starter工程
        (2)引入 Spring Boot 相关依赖
        (3)创建ThreadPoolAutoConfiguration
        (4)在threadpool-spring-boot-starter工程的 resources 包下创建META-INF/spring.factories文件, 添加内容如下:
             org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
             com.openlab.ThreadPoolAutoConfiguration
        (5)自定义ThreadPoolProperties, 获取application.properties的参数(可以参照RedisProperties), 供ThreadPoolAutoConfiguration使用
        (6)最后新建工程引入threadpool-spring-boot-starter, application.properties配置ThreadPoolProperties所需参数
     */
}
