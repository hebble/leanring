package com.learning.study.third;

/**
 * https://blog.csdn.net/finally_vince/article/details/124039859 面试重灾区丨如何解决Tomcat优化问题？
 */
public class TomcatLearning {
    /**
     1.Tomcat如何优化?你在项目中优化Tomcat解决什么问题?
        对于tomcat的优化，主要是从2个方面入手，一是 tomcat 自身的配置(优化tomcat连接池, 使用nio2, 禁用ajp)，
        另一个是 tomcat 所运行的 jvm 的调优
        1.1 优化tomcat连接池
            tomcat 中每一个用户请求都是一个线程，频繁地创建线程会造成性能浪费，所以可以使用线程池提高性能，修改 conf/server.xml 配置文件。
            参数说明：
                maxThreads：最大并发数，默认设置是200，一般建议在500~1000，根据硬件设施和业务来判断
                minSpareThreads：Tomcat 初始化时创建的线程数，默认设置 25
                prestartminSpareThreads：在tomcat 初始化的时候就初始化minSpareThreads的参数值，如果不等于 true，minSpareThreads的值就没啥效果了
                maxQueueSize：最大的等待队列数，超过则拒绝请求
        1.2 优化tomcat运行模式(nio2)
            Tomcat是一个小型的轻量级应用服务器，也是JavaEE开发人员最常用的服务器之一。不过，许多开发人员不知道的是，Tomcat Connector(Tomcat连接器)有bio、nio、apr三种运行模式，
            那么这三种运行模式有什么区别呢，我们又如何修改Tomcat Connector的运行模式来提高Tomcat的运行性能呢？
            (1)bio(blocking I/O)
                顾名思义，即阻塞式I/O操作，表示Tomcat使用的是传统的Java I/O操作(即java.io包及其子包)。Tomcat在默认情况下，就是以bio模式运行的。遗憾的是，就一般而言，bio模式是三种运行模式中性能最低的一种。我们可以通过Tomcat Manager来查看服务器的当前状态。
            (2)nio（new I/O)
                是Java SE 1.4及后续版本提供的一种新的I/O操作方式(即java.nio包及其子包)。Java nio是一个基于缓冲区、并能提供非阻塞I/O操作的Java API，因此nio也被看成是non-blocking I/O的缩写。它拥有比传统I/O操作(bio)更好的并发运行性能。
                要让Tomcat以nio模式来运行也比较简单，我们只需要在Tomcat安装目录/conf/server.xml文件中将如下配置：
                    protocol属性值改为org.apache.coyote.http11.Http11NioProtocol即可：
                此时，我们就可以在Tomcat Manager中看到当前服务器状态页面的HTTP协议的Connector运行模式已经从http-bio-8080变成了http-nio-8080。
            (3)apr(Apache Portable Runtime/Apache可移植运行时)
                是Apache HTTP服务器的支持库。你可以简单地理解为，Tomcat将以JNI的形式调用Apache HTTP服务器的核心动态链接库来处理文件读取或网络传输操作，从而大大地提高Tomcat对静态文件的处理性能。(对于静态文件处理一般我们用Nginx)
            我们这个版本的 tomcat 我们可以看到，默认使用的是 nio 模式，在tomcat8 中还提供了最新的 nio2，速度更快，建议使用 nio2，NIO2是JDK7新增的文件及网络I/O特性，他继承自NIO，同时添加了众多特性及功能改进，其中最重要的即是对异步I/O（AIO）的支持。
                protocol属性值改为org.apache.coyote.http11.Http11Nio2Protocol即可：
        1.3 禁用AJP连接
            AJP（Apache JServer Protocol）web 服务器和 Servlet 容器通过 TCP 连接来交互，为了节省 Socket 创建的昂贵代价，web服务器会尝试维护一个永久 TCP连接到 servlet 容器，并且在多个请求和响应周期过程会重用连接,这个协议对 Apache 处理静态内容性能很高。
            但是我们一般是使用 nginx+tomcat 的架构，所以用不着 AJP 协议，所以把 AJP 连接器禁用，小伙伴们可以根据自己 tomcat 的版本，修改 conf/server.xml 文件，将 AJP 服务禁用掉即可。
            8.5.59版本默认已注释掉AJP
        1.4 Tomcat JVM 参数优化
            在Tomcat的bin目录下创建setenv.sh文件
            export JAVA_OPTS=""
            -Djava.awt.headless=true 对于一个Java服务器来说经常要处理一些图形元素，例如地图的创建或者图形和图表等要使用AWT，而 AWT 依赖显示设备、键盘和鼠标的主机，通常服务器没有这些硬件设备，这个时候就会报异常，这个参数用来解决这个问题
            -Djava.net.preferIPv4Stack=true  禁用 IPV6
            -server 服务器启动模式，更大、更高的并发处理能力，更快更强捷的JVM垃圾回收机制，可以获得更多的负载与吞吐量
            -Xms2g 设置JVM初始堆内存
            -Xmx2g 设置JVM最大堆内存
            -Xmn520m 设置年轻代大小
            -Xss256k 是指设定每个线程的堆栈大小。一般设置不超过1M，要不然容易出现out of memory。
            -XX:PermSize=128m设置非堆内存初始值，默认是物理内存的1/64；在数据量的很大的文件导出时，一定要把这两个值设置上，否则会出现内存溢出的错误。(现在不常用)
            -XX:MaxPermSize=128m设置最大非堆内存的大小，默认是32M,建议达到物理内存的1/4(现在不常用)
            -XX:+DisableExplicitGC 禁用显示GC，即代码中调用 System.gc()无效
            -XX:+UseConcMarkSweepGC  使用 CMS 垃圾收集器
            -XX:+CMSParallelRemarkEnabled 降低标记停顿
            -XX:+UseCMSCompactAtFullCollection  在full GC时做压缩，使 memory 碎片减少
            -XX:LargePageSizeInBytes=128m 单个页大小调整，Java进程占用最大内存
            -XX:+UseFastAccessorMethods get,set 方法转成本地代码（对于jvm来说是冗余代码，jvm将进行优化）
            -XX:+UseCMSInitiatingOccupancyOnly 如果不指定, 只是用设定的回收阈值CMSInitiatingOccupancyFraction,则JVM仅在第一次使用设定值,后续则自动调整会导致上面的那个参数不起作用。
            -XX:CMSInitiatingOccupancyFraction=70 是指设定CMS在对内存占用率达到70%的时候开始GC
     */
}





































