package com.learning.study.third;

public class NginxLearning {
    /**
     1.什么是nginx？
        Nginx是一个高性能的HTTP和反向代理服务器，也是一个IMAP/POP3/SMTP服务器
        Nginx是一款轻量级的Web服务器/反向代理服务器及电子邮件（IMAP/POP3）代理服务器 目前使用的最多的web服务器或者代理服务器，像淘宝、新浪、网易、迅雷等都在使用

     2.为什么要用Nginx？
        优点：
             跨平台、配置简单
             非阻塞、高并发连接：处理2-3万并发连接数，官方监测能支持5万并发
             内存消耗小：开启10个nginx才占150M内存 成本低廉：开源
             内置的健康检查功能：如果有一个服务器宕机，会做一个健康检查，再发送的请求就不会发送到宕机的服务器了。重新将请求提交到其他的节点上。
             节省宽带：支持GZIP压缩，可以添加浏览器本地缓存
             稳定性高：宕机的概率非常小
             master/worker结构：一个master进程，生成一个或者多个worker进程
             接收用户请求是异步的：浏览器将请求发送到nginx服务器，它先将用户请求全部接收下来，再一次性发送给后端web服务器，极大减轻了web服务器的压力
             一边接收web服务器的返回数据，一边发送给浏览器客户端
             网络依赖性比较低，只要ping通就可以负载均衡
             可以有多台nginx服务器
             事件驱动：通信机制采用epoll模型

     3.为什么Nginx性能这么高？
        得益于它的事件处理机制： 异步非阻塞事件处理机制：运用了epoll模型，提供了一个队列，排队解决

     4.nginx是如何实现高并发的？
        一个主进程，多个工作进程，每个工作进程可以处理多个请求，每进来一个request，会有一个worker进程去处理。但不是全程的处理，处理到可能发生阻塞的地方，比如向上游
        （后端）服务器转发request，并等待请求返回。那么，这个处理的worker继续处理其他请求，而一旦上游服务器返回了，就会触发这个事件，worker才会来接手，这个request
        才会接着往下走。由于web server的工作性质决定了每个request的大部份生命都是在网络传输中，实际上花费在server机器上的时间片不多。这是几个进程就解决高并发的秘密
        所在。即@skoo所说的webserver刚好属于网络io密集型应用，不算是计算密集型。

     5.为什么不使用多线程？
        因为线程创建和上下文的切换非常消耗资源，线程占用内存大，上下文切换占用cpu也很高，采用epoll模型避免了这个缺点

     6.Nginx是如何处理一个请求的呢？
         首先，nginx在启动时，会解析配置文件，得到需要监听的端口与ip地址，然后在nginx的master进程里面
         先初始化好这个监控的socket(创建socket，设置addrreuse等选项，绑定到指定的ip地址端口，再listen)
         然后再fork(一个现有进程可以调用fork函数创建一个新进程。由fork创建的新进程被称为子进程 )出多个子进程出来
         然后子进程会竞争accept新的连接。此时，客户端就可以向nginx发起连接了。当客户端与nginx进行三次握手，与nginx建立好一个连接后
         此时，某一个子进程会accept成功，得到这个建立好的连接的socket，然后创建nginx对连接的封装，即ngx_connection_t结构体
         接着，设置读写事件处理函数并添加读写事件来与客户端进行数据的交换。最后，nginx或客户端来主动关掉连接，到此，一个连接就寿终正寝了

     7.正向代理和负向代理
        7.1 正向代理
             一个位于客户端和原始服务器(origin server)之间的服务器，为了从原始服务器取得内容，客户端向代理发送一个请求并指定目标(原始服务器)
             然后代理向原始服务器转交请求并将获得的内容返回给客户端。客户端才能使用正向代理
             正向代理总结就一句话：代理端代理的是客户端
        7.2 负向代理
             反向代理（Reverse Proxy）方式是指以代理服务器来接受internet上的连接请求，然后将请求，发给内部网络上的服务器
             并将从服务器上得到的结果返回给internet上请求连接的客户端，此时代理服务器对外就表现为一个反向代理服务器
             反向代理总结就一句话：代理端代理的是服务端

     8.什么是动态资源、静态资源分离？
         动态资源、静态资源分离是让动态网站里的动态网页根据一定规则把不变的资源和经常变的资源区分开来，动静资源做好了拆分以后
         我们就可以根据静态资源的特点将其做缓存操作，这就是网站静态化处理的核心思路
         动态资源、静态资源分离简单的概括是：动态文件与静态文件的分离

     9.为什么要做动、静分离？
         在我们的软件开发中，有些请求是需要后台处理的（如：.jsp,.do等等），有些请求是不需要经过后台处理的（如：css、html、jpg、js等等文件）
         这些不需要经过后台处理的文件称为静态文件，否则动态文件。因此我们后台处理忽略静态文件。这会有人又说那我后台忽略静态文件不就完了吗
         当然这是可以的，但是这样后台的请求次数就明显增多了。在我们对资源的响应速度有要求的时候，我们应该使用这种动静分离的策略去解决
         动、静分离将网站静态资源（HTML，JavaScript，CSS，img等文件）与后台应用分开部署，提高用户访问静态代码的速度，降低对后台应用访问
         这里我们将静态资源放到nginx中，动态资源转发到tomcat服务器中

     10.负载均衡
         负载均衡即是代理服务器将接收的请求均衡的分发到各服务器中
         负载均衡主要解决网络拥塞问题，提高服务器响应速度，服务就近提供，达到更好的访问质量，减少后台服务器大并发压力

     10.Nginx三大功能
         反向代理
         负载均衡
         动静分离

     11.Nginx惊群
         惊群效应（thundering herd）是指多进程（多线程）在同时阻塞等待同一个事件的时候（休眠状态），如果等待的这个事件发生，那么他就会唤醒等待的所有进程（或者线程），
        但是最终却只能有一个进程（线程）获得这个时间的 “控制权”，对该事件进行处理，而其他进程（线程）获取 “控制权” 失败，只能重新进入休眠状态，这种现象和性能浪费就叫做惊群效应。

     12.Nginx开启gzip压缩
         Nginx 开启 Gzip 压缩功能， 可以使网站的 css、js 、xml、html 文件在传输时进行压缩，提高访问速度, 进而优化 Nginx 性能。
         网站加载的速度取决于浏览器必须下载的所有文件的大小。减少要传输的文件的大小可以使网站不仅加载更快，而且对于那些宽带是按量计费的人来说也更友好。
         gzip 是一种流行的数据压缩程序。您可以使用 gzip 压缩 Nginx 实时文件。这些文件在检索时由支持它的浏览器解压缩，好处是 web 服务器和浏览器之间传输的数据量更小，速度更快。
         gzip 不一定适用于所有文件的压缩。例如，文本文件压缩得非常好，通常会缩小两倍以上。另一方面，诸如 JPEG或 PNG 文件之类的图像已经按其性质进行压缩，使用 gzip 压缩很难有好
        的压缩效果或者甚至没有效果。压缩文件会占用服务器资源，因此最好只压缩那些压缩效果好的文件。

     13.Nginx 502错误原因和解决方法
        不管你是做运维还是做开发，哪怕你是游客，时不时会遇到 502 Bad Gateway 或 504 Gateway Time-out。出现这页面，把服务重启下，再实在不行重启下服务器，问题就解决了，特殊情况请继续阅读。

     14.Nginx负载均衡的策略有哪些?
        (1)轮询(默认)
            每个请求按时间顺序逐一分配到不同的后端服务器，如果后端某个服务器宕机，能自动剔除故障系统。
        (2)权重
            weight的值越大，分配到的访问概率越高，主要用于后端每台服务器性能不均衡的情况下。其次是为在主从的情况下设置不同的权值，达到合理有效的地利用主机资源。
        (3)ip_hash( IP绑定)
            每个请求按访问IP的哈希结果分配，使来自同一个IP的访客固定访问一台后端服务器，并且可以有效解决动态网页存在的session共享问题
        (4)fair(第三方插件)
            必须安装upstream_fair模块。
            对比 weight、ip_hash更加智能的负载均衡算法，fair算法可以根据页面大小和加载时间长短智能地进行负载均衡，响应时间短的优先分配。
        (5)url_hash(第三方插件)
            必须安装Nginx的hash软件包
            按访问url的hash结果来分配请求，使每个url定向到同一个后端服务器，可以进一步提高后端缓存服务器的效率。

     15.location的作用是什么？
        location指令的作用是根据用户请求的URI来执行不同的应用，也就是根据用户请求的网站URL进行匹配，匹配成功即进行相关的操作。
            location语法
                 匹配符 匹配规则 优先级
                 =    精确匹配    1
                 ^~    以某个字符串开头    2
                 ~    区分大小写的正则匹配    3
                 ~*    不区分大小写的正则匹配    4
                 !~    区分大小写不匹配的正则    5
                 !~*    不区分大小写不匹配的正则    6
                 /    通用匹配，任何请求都会匹配到    7
             Location正则案例
                 #优先级1,精确匹配，根路径
                 location=/{
                    return400;
                 }
                 #优先级2,以某个字符串开头,以av开头的，优先匹配这里，区分大小写
                 location^~/av{
                    root/data/av/;
                 }
                 #优先级3，区分大小写的正则匹配，匹配/media*****路径
                 location~/media{
                    alias/data/static/;
                 }
                 #优先级4，不区分大小写的正则匹配，所有的****.jpg|gif|png都走这里
                 location~*.*\.(jpg|gif|png|js|css)${
                    root/data/av/;
                 }
                 #优先7，通用匹配
                 location/{
                    return403;
                 }

     16.在Nginx中，解释如何在URL中保留双斜线?
         要在URL中保留双斜线，就必须使用merge_slashes_off;
         语法:merge_slashes [on/off]
         默认值: merge_slashes on
         环境: http，server

     17.请解释什么是C10K问题?
        C10K问题是指无法同时处理大量客户端(10,000)的网络套接字。
     */
}
