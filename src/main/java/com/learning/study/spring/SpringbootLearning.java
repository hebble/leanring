package com.learning.study.spring;

public class SpringbootLearning {
    /**
     * 1.Springboot 配置文件加载优先级
     *springboot 启动会扫描以下位置的application.properties或者application.yml文件作为Spring boot的默认配置文件
     * –file:./config/
     * –file:./
     * –classpath:/config/
     * –classpath:/
     * 优先级由高到底，高优先级的配置会覆盖低优先级的配置。
     *
     * 2.Springboot 自动装配原理
     *  spring-boot-starter-parent 点进去后发现还有一个父依赖, spring-boot-dependencies, 以后我们导入依赖默认是不需要写版本；但是如果导入的包没有在依赖中管理着就需要手动配置版本了；
     *  spring-boot-starter-xxx：就是spring-boot的场景启动器，xxx就是某种场景
     *  见到***-springboot-boot-starter：第三方为我们提供的简化开发的场景启动器
     *  @SpringBootApplication其实就是下面三个注解的集合
     *      @SpringBootConfiguration: SpringBoot就应该运行这个类的main方法来启动SpringBoot应用
     *      @EnableAutoConfiguration: 告诉SpringBoot开启自动配置功能，这样自动配置才能生效
     *      @ComponentScan()
     *
     * 步骤总结：
     *      SpringBoot先加载所有的自动配置类 xxxxxAutoConfiguration
     *      每个自动配置类按照条件进行生效，默认都会绑定配置文件指定的值。xxxxProperties里面拿。xxxProperties和配置文件进行了绑定
     *      生效的配置类就会给容器中装配很多组件
     *      只要容器中有这些组件，相当于这些功能就有了
     *      定制化配置
     *      用户直接自己@Bean替换底层的组件
     *      用户去看这个组件是获取的配置文件什么值就去修改。
     *      如果用户自己配置了以用户的优先
     *总流程:
     *      xxxxxAutoConfiguration —> 组件 —> xxxxProperties里面拿值 ----> application.properties
     */
}
