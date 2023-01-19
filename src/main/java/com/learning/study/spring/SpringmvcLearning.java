package com.learning.study.spring;

public class SpringmvcLearning {
    /**
    1、什么是Spring MVC ？简单介绍下你对springMVC的理解?
       Spring MVC是一个基于Java的实现了MVC设计模式的请求驱动类型的轻量级Web框架，通过把Model，View，Controller分离，将web层进行职责解耦，把复杂的web应用分成逻辑清晰的几部分，简化开发，减少出错，方便组内开发人员之间的配合。

    2.SpringMVC的流程？
       （1）用户发送请求至前端控制器DispatcherServlet；
       （2）DispatcherServlet收到请求后，调用HandlerMapping处理器映射器，请求获取Handler；
       （3）处理器映射器根据请求url找到具体的处理器Handler，生成处理器对象及处理器拦截器(如果有则生成)，一并返回给DispatcherServlet；
       （4）DispatcherServlet 调用 HandlerAdapter处理器适配器，请求执行Handler；
       （5）HandlerAdapter 经过适配调用 具体处理器进行处理业务逻辑；
       （6）Handler执行完成返回ModelAndView；
       （7）HandlerAdapter将Handler执行结果ModelAndView返回给DispatcherServlet；
       （8）DispatcherServlet将ModelAndView传给ViewResolver视图解析器进行解析；
       （9）ViewResolver解析后返回具体View；
       （10）DispatcherServlet对View进行渲染视图（即将模型数据填充至视图中）
       （11）DispatcherServlet响应用户。

       前端控制器 DispatcherServlet：接收请求、响应结果，相当于转发器，有了DispatcherServlet 就减少了其它组件之间的耦合度。
       处理器映射器 HandlerMapping：根据请求的URL来查找Handler
       处理器适配器 HandlerAdapter：负责执行Handler
       处理器 Handler：处理器，需要程序员开发
       视图解析器 ViewResolver：进行视图的解析，根据视图逻辑名将ModelAndView解析成真正的视图（view）
       视图View：View是一个接口， 它的实现类支持不同的视图类型，如jsp，freemarker，pdf等等

    3.SpringMVC怎么样设定重定向和转发的？
       （1）转发：在返回值前面加"forward:"，譬如"forward:user.do?name=method4"
       （2）重定向：在返回值前面加"redirect:"，譬如"redirect:http://www.baidu.com"

    4.springMVC和struts2的区别有哪些?
       （1）springmvc的入口是一个servlet即前端控制器（DispatchServlet），而struts2入口是一个filter过虑器（StrutsPrepareAndExecuteFilter）。
       （2）springmvc是基于方法开发(一个url对应一个方法)，请求参数传递到方法的形参，可以设计为单例或多例(建议单例)，struts2是基于类开发，传递参数是通过类的属性，只能设计为多例。
       （3）Struts采用值栈存储请求和响应的数据，通过OGNL存取数据，springmvc通过参数解析器是将request请求内容解析，并给方法形参赋值，将数据和视图封装成ModelAndView对象，
           最后又将ModelAndView中的模型数据通过reques域传输到页面。Jsp视图解析器默认使用jstl。

    5.SpringMvc怎么和AJAX相互调用的？
       通过Jackson框架就可以把Java里面的对象直接转化成Js可以识别的Json对象。具体步骤如下 ：
       （1）加入Jackson.jar
       （2）在配置文件中配置json的映射
       （3）在接受Ajax方法里面可以直接返回Object、List等，但方法前面要加上@ResponseBody注解。

    6.Spring MVC的异常处理 ？
       可以将异常抛给Spring框架，由Spring框架来处理；我们只需要配置简单的异常处理器，在异常处理器中添视图页面即可。

    7.SpringMvc的控制器是不是单例模式？如果是，有什么问题？怎么解决？
        是单例模式，在多线程访问的时候有线程安全问题，解决方案是在控制器里面不能写可变状态量，如果需要使用这些可变状态，可以使用ThreadLocal机制解决，为每个线程单独生成一份变量副本，独立操作，互不影响。

    8.拦截器和过滤器的区别 https://blog.csdn.net/weixin_46120382/article/details/118574708
        (1)实现原理不同
            过滤器和拦截器 底层实现方式大不相同，过滤器 是基于函数回调的，拦截器 则是基于Java的反射机制（动态代理）实现的。
        (2)使用范围不同
            我们看到过滤器 实现的是 javax.servlet.Filter 接口，而这个接口是在Servlet规范中定义的，也就是说过滤器Filter 的使用要依赖于Tomcat等容器，导致它只能在web程序中使用。
            拦截器(Interceptor) 它是一个Spring组件，并由Spring容器管理，并不依赖Tomcat等容器，是可以单独使用的。不仅能应用在web程序中，也可以用于Application、Swing等程序中
        (3)触发时机不同
            见图一个请求触发的组件.png
            过滤器Filter是在请求进入容器后，但在进入servlet之前进行预处理，请求结束是在servlet处理完以后。
            拦截器 Interceptor 是在请求进入servlet后，在进入Controller之前进行预处理的，Controller 中渲染了对应的视图之后请求结束。
        (4)拦截的请求范围不同
            过滤器Filter执行了两次，拦截器Interceptor只执行了一次。
            这是因为过滤器几乎可以对所有进入容器的请求起作用，
            而拦截器只会对Controller中请求或访问static目录下的资源请求起作用。
        (5)拦截器可以获取IOC容器中的各个bean，而过滤器就不行，这点很重要，在拦截器里注入一个service，可以调用业务逻辑。
     */


















}
