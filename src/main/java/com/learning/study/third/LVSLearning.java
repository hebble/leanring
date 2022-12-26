package com.learning.study.third;

/**
 * https://blog.csdn.net/qq_57258570/article/details/121392398 LVS负载均衡介绍
 * https://blog.csdn.net/weixin_44687753/article/details/88900343 LVS和Nginx实现负载均衡功能的比较
 * https://blog.csdn.net/weixin_67510296/article/details/125263857 LVS面试题
 * https://blog.csdn.net/Coastline98/article/details/125463916 lvs四层负载均衡
 */
public class LVSLearning {
    /**
     1.lvs是什么？
        LVS是Linux Virtual Server的简写，意即Linux虚拟服务器，是一个虚拟的服务器集群系统。本项目在1998年5月由章文嵩博士成立，是中国国内最早出现的自由软件项目之一。在linux内存2.6中，它已经成为内核的一部分，在此之前的内核版本则需要重新编译内核

     2.lvs的作用
         LVS主要用于多服务器的负载均衡。它工作在传输层，可以实现高性能，高可用的服务器集群技术。
         它廉价，可把许多低性能的服务器组合在一起形成一个超级服务器。
         它易用，配置非常简单，且有多种负载均衡的方法。它稳定可靠，即使在集群的服务器中某台服务器无法正常工作，也不影响整体效果。
         另外可扩展性也非常好。
         因为lvs工作在传输层，所以相对于其他的负载均衡的解决办法（DNS域名轮流解析、应用层负载的调度、客户端的调度等，它的效率是非常高的）

     3.简述LVS三种工作模式，简述他们的区别?
        (1)基于NAT的LVS模式负载均衡:
             详见LVS-NAT模式.png
             所谓NAT模式，即网络地址转换模式，分发器有着一个公网IP地址，该公网IP地址对外提供服务，当客户端的请求数据发送到分发器后，由分发器将公网地址转换成私网地址，根据一定的算法，分发给后台的服务器组进行处理。后台的服务器组在处理完成后，
            将响应数据包发送给分发器，由分发器将私网IP地址转化成公网IP地址后，再反馈给客户端。
            在NAT工作模式下，由于分发器要同时处理数据包的处理和响应，因此分发器的性能称为整个架构的瓶颈。如果后台真实服务器数量过多，则分发器的工作量就会越大，并且分发器还要维护NAT表，消耗大量的内存资源，因此这个模式尽管很适合公司网络，
            缺点: 能够支持的真实服务器数量不多。
        (2)基于TUN的LVS负载均衡:
            详见LVS-TUN模式.png
             Tunnel模式即隧道模式，所谓Tunnel模式，即分发器只负责接收客户端发送过来的数据包，然后将该数据包封装后按照算法发送给真实服务器，真实服务器在处理完该数据包后，不需要将该数据包发送给分发器，而是直接将该数据包发送给客户端。
            一台负载均衡能为超过100台的物理服务器服务，负载均衡器不再是系统的瓶颈, 也正因为每个真实都有自己的公网IP地址
             在Tunnel模式下，每个真实服务器都会有自己的公网IP地址，该公网IP地址一方面用于接收从分发器发送的客户端请求，一方面给客户端发送其响应报文。也正因为每个真实都有自己的公网IP地址，因此真实服务器不用拘泥于网络架构，可以分散存储，起到容灾备份的作用。
            缺点：但是，这种方式需要所有的服务器支持"IP Tunneling"(IP Encapsulation)协议。需要大量的公网ip。成本较高。安全性较差，采用共用ip地址，节点完全暴露。
        (3)基于DR的LVS负载均衡: (这样是用的最多的一种模式)
            详见LVS-DR模式.png
             DR模式，即Direct Routing，直接路由模式。在此模式下，分发器有接收客户端请求的公网IP地址，各个真实服务器也要有自己的环回地址，该地址与分发器公网IP地址完全相同，并且该地址还需要能够正常访问公网。这样，当客户端的请求发送到分发器后，分发器只需要将该请求
            数据包修改MAC地址后，发送给后端真实服务器即可，由于目的MAC地址和IP地址都是自己，因此后端真实服务器会对该数据包进行正常响应。在响应完成后，真实服务器会将该数据包直接从环回地址处发送出去，也不需要经过分发器。
            在DR模式下，网络层面需要解决两个问题，一个是解决IP地址冲突的问题，由于在一个局域网中存在多个相同的公网IP地址，因此必须设置后端真实服务器不对ARP请求报文进行响应；另一个是后端真实服务器的转发问题，必须设置真实服务器使用自己的真实网卡转发环回网卡的数据包。
            缺点：要求负载均衡器的网卡必须与物理网卡在一个网段段上。节点安全性较差采用公用ip地址，节点完全暴露

     4.lvs常见算法
        (1)轮询调度RR：
            将外部请求按照顺序轮流分配到真实的服务器上
        (2)加权轮询WRR：
            根据真实服务器的不同处理能力来调度访问请求
        (3)最少链接数LC：
            调度器通过最少连接调度算法动态的将网络请求调度到以建立的连接数最少的服务器上（两台服务器配置差不多时使用）
        (4)加权最少连接WLC：
            优化负载的性能，较高权重的服务器将承受较大比例的活动连接负载（两台服务器 有一台比较弱时 可以使用）
        (5)基于局部性的最少连接 LBLC:
            针对目标的IP地址的负载均衡，应用于cache集群上。会根据请求的目标IP地址找出该目标IP地址最近使用的服务器，如果该服务器是可用的没有超载，则将请求发送到服务器上，若服务器不存在，或服务器处于一半的工作负载，则用最少连接数选出一个服务器
        (6)带复制的基于局部性的最少连接（locality-Based Least Connections with Replication）
            也是针对目标IP地址的负载均衡，用于cache集群。它与LBLC算法不同之处是它要维护的是从一个目标IP地址到一组服务器的映射，而LBLC算法维护的是从一个目标IP地址到一台服务器的映射。
        (7)目标地址散列（Destination IP Hashing）
            目标地址散列调度算法根据请求的目标IP地址，作为散列键（Hash KEY）从静态分配的散列表中找出对应的服务器，若服务器是可用的且未超载，则请求发送到该服务器，否则返回空。
        (8)源地址散列
            根据请求的源IP地址，作为散列键从静态分配的散列表中找出对应的服务器，若服务器是可用的且未超载，则请求发送到该服务器，否则返回空。
        (9)源IP端口散列
            通过hash函数将来自用一个源IP地址和源端口号的请求映射到后端同一台服务器上。（适合按服务的类型分类）
        (10)随机
            随机地将请求分发到不同的服务器上

     5.典型的负载均衡集群中，包括三层次的组件
        前端至少一个负载均衡调度器（LB）负责相应并发来自客户端的访问请求；后端有大量真实服务器构成服务池（server pool）提供实际的应用服务透明性 一致性和伸缩性
        (1)第一层：
            负载调度器，这是访问整个集群系统唯一入口，对外使用所有服务器共有VIP，也称为集群IP，通常会配置主备两台调度器实现热备份。
        (2)第二层：
            服务器池，集群符提供的应用服务，由服务器池承担。每个节点具有独立的真实IP，只处理调度器分发过来的客户机请求。当某个节点失效时，负载调度器的容错机制会将它隔离，等排出错误后，在从新纳入服务器池
        (3)第三层：
            共享存储，为服务池中的所有节点提供稳定的，一致的文件存取服务，保证整个集群的统一性

     6.lvs四层负载均衡
        lvs是一款用于四层负载均衡的工具。所谓的四层负载均衡，对应的是网络七层协议，常见的如HTTP协议是建立在七层协议上的，而lvs作用于四层协议上，也即：传输层，网络层，数据链路层和物理层。这里的传输层主要协议有TCP和UDP协议，
        也就是说lvs主要支持的方式是TCP和UDP。也正是因为lvs是处于四层负载均衡上的，因而其处理请求的能力比常见的服务器要高非常多，比如nginx的请求处理就是建立在网络七层上的，lvs的负载均衡能力是nginx的十倍以上。在特大型网站中，
        应用服务器是可以横向扩容的，而nginx是不支持横向扩容的

     12.LVS和Nginx实现负载均衡功能的比较 https://blog.csdn.net/weixin_44687753/article/details/88900343
        12.1 lvs的优势
            (1)抗负载能力强，因为lvs工作方式的逻辑是非常之简单，而且工作在网络4层仅做请求分发之用，没有流量，所以在效率上基本不需要太过考虑。在我手里的 lvs，仅仅出过一次问题：在并发最高的一小段时间内均衡器出现丢包现象，
                据分析为网络问题，即网卡或linux2.4内核的承载能力已到上限，内存和 cpu方面基本无消耗
            (2)配置性低，这通常是一大劣势，但同时也是一大优势，因为没有太多可配置的选项，所以除了增减服务器，并不需要经常去触碰它，大大减少了人为出错的几率
            (3)工作稳定，因为其本身抗负载能力很强，所以稳定性高也是顺理成章，另外各种lvs都有完整的双机热备方案，所以一点不用担心均衡器本身会出什么问题，节点出现故障的话，lvs会自动判别，所以系统整体是非常稳定的
            (4)无流量，上面已经有所提及了。lvs仅仅分发请求，而流量并不从它本身出去，所以可以利用它这点来做一些线路分流之用。没有流量同时也保住了均衡器的IO性能不会受到大流量的影响
            (5)基本上能支持所有应用，因为lvs工作在4层，所以它可以对几乎所有应用做负载均衡，包括http、数据库、聊天室等等
        12.2 nginx和lvs作对比的结果
            (1)nginx工作在网络的第7层，所以它可以针对http应用本身来做分流策略，比如针对域名、目录结构等，相比之下lvs并不具备这样的功能，所以nginx单凭这点可以利用的场合就远多于lvs了；但nginx有用的这些功能使其可调整度
                要高于lvs，所以经常要去触碰，由lvs的第2条优点来看，触碰多了，人为出现问题的几率也就会大。
            (2)nginx对网络的依赖较小，理论上只要ping得通，网页访问正常，nginx就能连得通，nginx同时还能区分内外网，如果是同时拥有内外网的节点，就相当于单机拥有了备份线路；lvs就比较依赖于网络环境，
                目前来看服务器在同一网段内并且lvs使用direct方式分流，效果较能得到保证。另外注意，lvs需要向托管商至少申请多于一个ip来做visual ip。
            (3)nginx安装和配置比较简单，测试起来也很方便，因为它基本能把错误用日志打印出来。lvs的安装和配置、测试就要花比较长的时间，因为同上所述，lvs对网络依赖性比较大，很多时候不能配置成功都是因为网络问题而不是配置问题，出了问题要解决也相应的会麻烦的多。
            (4)nginx也同样能承受很高负载且稳定，但负载度和稳定度差lvs还有几个等级：nginx处理所有流量所以受限于机器IO和配置；本身的bug也还是难以避免的；nginx没有现成的双机热备方案，所以跑在单机上还是风险比较大，单机上的事情全都很难说。
            (5)nginx可以检测到服务器内部的故障，比如根据服务器处理网页返回的状态码、超时等等，并且会把返回错误的请求重新提交到另一个节点。目前lvs中ldirectd也能支持针对服务器内部的情况来监控，但lvs的原理使其不能重发请求。比如用户正在上传一个文件，而处理该
                上传的节点刚好在上传过程中出现故障，nginx会把上传切到另一台服务器重新处理，而lvs就直接断掉了。
        12.3 两者配合使用
             nginx用来做http的反向代理，能够upsteam实现http请求的多种方式的均衡转发。由于采用的是异步转发可以做到如果一个服务器请求失败，立即切换到其他服务器，直到请求成功或者最后一台服务器失败为止。这可以最大程度的提高系统的请求成功率。
             lvs采用的是同步请求转发的策略。这里说一下同步转发和异步转发的区别。同步转发是在lvs服务器接收到请求之后，立即redirect到一个后端服务器，由客户端直接和后端服务器建立连接。异步转发是nginx在保持客户端连接的同时，发起一个相同内容的新请求到后端，等后端返回结果后，由nginx返回给客户端。
             进一步来说：当做为负载均衡服务器的nginx和lvs处理相同的请求时，所有的请求和响应流量都会经过nginx；但是使用lvs时，仅请求流量经过lvs的网络，响应流量由后端服务器的网络返回。
             也就是，当作为后端的服务器规模庞大时，nginx的网络带宽就成了一个巨大的瓶颈。
             但是仅仅使用lvs作为负载均衡的话，一旦后端接受到请求的服务器出了问题，那么这次请求就失败了。但是如果在lvs的后端在添加一层nginx（多个），每个nginx后端再有几台应用服务器，那么结合两者的优势，既能避免单nginx的流量集中瓶颈，又能避免单lvs时一锤子买卖的问题。
     */
}
