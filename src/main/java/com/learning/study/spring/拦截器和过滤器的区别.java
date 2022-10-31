package com.learning.study.spring;

/**
 * https://blog.csdn.net/weixin_46120382/article/details/118574708 Spring 拦截器和过滤器的区别？
 */
public class 拦截器和过滤器的区别 {
    /**
     1.实现原理不同
        过滤器和拦截器 底层实现方式大不相同，过滤器 是基于函数回调的，拦截器 则是基于Java的反射机制（动态代理）实现的。
     2.使用范围不同
        我们看到过滤器 实现的是 javax.servlet.Filter 接口，而这个接口是在Servlet规范中定义的，也就是说过滤器Filter 的使用要依赖于Tomcat等容器，导致它只能在web程序中使用。
        拦截器(Interceptor) 它是一个Spring组件，并由Spring容器管理，并不依赖Tomcat等容器，是可以单独使用的。不仅能应用在web程序中，也可以用于Application、Swing等程序中
     3.触发时机不同
        见图一个请求触发的组件.png
        过滤器Filter是在请求进入容器后，但在进入servlet之前进行预处理，请求结束是在servlet处理完以后。
        拦截器 Interceptor 是在请求进入servlet后，在进入Controller之前进行预处理的，Controller 中渲染了对应的视图之后请求结束。
     4.拦截的请求范围不同
         过滤器Filter执行了两次，拦截器Interceptor只执行了一次。
         这是因为过滤器几乎可以对所有进入容器的请求起作用，
         而拦截器只会对Controller中请求或访问static目录下的资源请求起作用。
     5.拦截器可以获取IOC容器中的各个bean，而过滤器就不行，这点很重要，在拦截器里注入一个service，可以调用业务逻辑。
     */
}
