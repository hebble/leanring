package com.learning.study.object;

/**
 * https://blog.csdn.net/a745233700/category_9280527.html Java核心编程技术
 */
public class JavaIOLearning {
    /**
     1.Reactor 网络模型 https://blog.csdn.net/a745233700/article/details/122660246
        1.1 什么是 Reactor 模型? (非阻塞同步网络模型)
            Reactor 模式也叫做反应器设计模式，是一种为处理服务请求并发提交到一个或者多个服务处理器的事件设计模式。当请求抵达后，通过服务处理器将这些请求采用多路分离的方式分发给相应的请求处理器。
            Reactor 模式主要由 Reactor 和处理器 Handler 这两个核心部分组成，如下图所示，它俩负责的事情如下：
                 Reactor：负责监听和分发事件，事件类型包含连接事件、读写事件；
                 Handler ：负责处理事件，如 read -> 业务逻辑 （decode + compute + encode）-> send；
            在绝大多数场景下，处理一个网络请求有如下几个步骤：
                 (1)read：从 socket 读取数据。
                 (2)decode：解码，网络上的数据都是以 byte 的形式进行传输的，要想获取真正的请求，必需解码
                 (3)compute：计算，也就是业务处理。
                 (4)encode：编码，网络上的数据都是以 byte 的形式进行传输的，也就是 socket 只接收 byte，所以必需编码。
                 (5)send：发送应答数据
            对于Reactor模式来说，每当有一个 Event 输入到 Server 端时，Service Handler 会将其转发（dispatch）相对应的 Handler 进行处理。Reactor 模型中定义的三种角色：
                 Reactor：派发器，负责监听和分配事件，并将事件分派给对应的 Handler。新的事件包含连接建立就绪、读就绪、写就绪等。
                 Acceptor：请求连接器，处理客户端新连接。Reactor 接收到 client 端的连接事件后，会将其转发给 Acceptor，由 Acceptor 接收 Client 的连接，创建对应的 Handler，并向 Reactor 注册此 Handler。
                 Handler：请求处理器，负责事件的处理，将自身与事件绑定，执行非阻塞读/写任务，完成 channel 的读入，完成处理业务逻辑后，负责将结果写出 channel。可用资源池来管理。
        1.2 Reactor 模型的分类
            Reactor 模型中的 Reactor 可以是单个也可以是多个，Handler 同样可以是单线程也可以是多线程，所以组合的模式大致有如下三种：
                 (1)单 Reactor 单线程模型
                 (2)单 Reactor 多线程模型
                 (3)主从 Reactor 单线程模型
                 (4)主从 Reactor 多线程模型
            其中第三种的主从Reactor单线程模型没什么实际意义，所以下文就着重介绍其他三种模型

            1.2.1 单 Reactor 单线程模型
                详见单 Reactor 单线程模型.png
                处理流程:
                     （1）Reactor 线程通过 select 监听事件，收到事件后通过 Dispatch 进行分发
                     （2）如果是连接建立事件，则将事件分发给 Acceptor，Acceptor 会通过 accept() 方法获取连接，并创建一个Handler 对象来处理后续的响应事件
                     （3）如果是IO读写事件，则 Reactor 会将该事件交由当前连接的 Handler 来处理
                     （4）Handler 会完成 read -> 业务处理 -> send 的完整业务流程
                优缺点:
                    单 Reactor 单线程模型的优点在于将所有处理逻辑放在一个线程中实现，没有多线程、进程通信、竞争的问题。但该模型在性能与可靠性方面存在比较严重的问题：
                     (1)性能：只在代码上进行组件的区分，整体操作还是单线程，无法充分利用 CPU 资源，并且 Handler 业务处理部分没有异步，一个 Reactor 既要负责处理连接请求，又要负责处理读写请求，一般来
                        说处理连接请求是很快的，但处理读写请求时涉及到业务逻辑处理，相对慢很多。所以 Reactor 在处理读写请求时，其他请求只能等着，容易造成系统的性能瓶颈
                     (2)可靠性：一旦 Reactor 线程意外中断或者进入死循环，会导致整个系统通信模块不可用，不能接收和处理外部消息，造成节点故障
                        所以该单Reactor单进程模型不适用于计算密集型的场景，只适用于业务处理非常快速的场景。Redis的线程模型就是基于单 Reactor 单线程模型实现的，因为 Redis 业务处理主要是在内存中完成，
                        操作的速度是很快的，性能瓶颈不在 CPU 上，所以 Redis 对于命令的处理是单进程的。
            1.2.2 单 Reactor 多线程模型
                详见单 Reactor 多线程模型.png
                为了解决单Reactor单线程模型存在的性能问题，就演进出了单 Reactor 多线程模型，该模型在事件处理器部分采用了多线程（线程池）
                处理流程:
                     （1）Reactor 线程通过 select 监听事件，收到事件后通过 Dispatch 进行分发
                     （2）如果是连接建立事件，则将事件分发给 Acceptor，Acceptor 会通过 accept() 方法获取连接，并创建一个Handler 对象来处理后续的响应事件
                     （3）如果是IO读写事件，则 Reactor 会将该事件交由当前连接对应的 Handler 来处理
                     （4）与单Reactor单线程不同的是，Handler 不再做具体业务处理，只负责接收和响应事件，通过 read 接收数据后，将数据发送给后面的 Worker 线程池进行业务处理。
                     （5）Worker 线程池再分配线程进行业务处理，完成后将响应结果发给 Handler 进行处理。
                     （6）Handler 收到响应结果后通过 send 将响应结果返回给 Client。
                优缺点:
                     相对于第一种模型来说，在处理业务逻辑，也就是获取到 IO读写事件之后，交由线程池来处理，Handler 收到响应后通过 send 将响应结果返回给客户端。这样可以降低 Reactor
                     的性能开销，从而更专注的做事件分发工作了，提升整个应用的吞吐，并且 Handler 使用了多线程模式，可以充分利用 CPU 的性能。但是这个模型存在的问题：
                     （1）Handler 使用多线程模式，自然带来了多线程竞争资源的开销，同时涉及共享数据的互斥和保护机制，实现比较复杂
                     （2）单个 Reactor 承担所有事件的监听、分发和响应，对于高并发场景，容易造成性能瓶颈。
            1.2.3 主从 Reactor 多线程模型
                详见主从 Reactor 多线程模型.png
                单Reactor多线程模型解决了 Handler 单线程的性能问题，但是 Reactor 还是单线程的，对于高并发场景还是会有性能瓶颈，所以需要将 Reactor 调整为多线程模式，也就是接下来要介绍的主从 Reactor 多线程模型。主从 Reactor 多线程模型将 Reactor 分成两部分：
                     （1）MainReactor：只负责处理连接建立事件，通过 select 监听 server socket，将建立的 socketChannel 指定注册给 subReactor，通常一个线程就可以了
                     （2）SubReactor：负责读写事件，维护自己的 selector，基于 MainReactor 注册的 SocketChannel 进行多路分离 IO 读写事件，读写网络数据，并将业务处理交由 worker 线程池来完成。SubReactor 的个数一般和 CPU 个数相同
                处理流程:
                    （1）主线程中的 MainReactor 对象通过 select 监听事件，接收到事件后通过 Dispatch 进行分发，如果事件类型为连接建立事件则分发给 Acceptor 进行连接建立
                        连接建立：
                             a.从主线程池中随机选择一个 Reactor 线程作为 Acceptor 线程，用于绑定监听端口，接收客户端连接
                             b.Acceptor 线程接收客户端连接请求之后创建新的 SocketChannel，将其注册到主线程池的其它 Reactor 线程上，由其负责接入认证、IP黑白名单过滤、握手等操作。
                             c.步骤 b完成之后，业务层的链路正式建立，将 SocketChannel 从主线程池的 Reactor 线程的多路复用器上摘除，重新注册到 SubReactor 线程池的线程上，并创建一个 Handler 用于处理各种连接事件
                    （2）如果接收到的不是连接建立事件，则分发给 SubReactor，SubReactor 调用当前连接对应的 Handler 进行处理
                    （3）Handler 通过 read 读取数据后，将数据分发给 Worker 线程池进行业务处理，Worker 线程池则分配线程进行业务处理，完成后将响应结果发给 Handler
                    （4）Handler 收到响应结果后通过 send 将响应结果返回给 Client
                优缺点:
                     主从 Reactor 多线程模型的优点在于主线程和子线程分工明确，主线程只负责接收新连接，子线程负责完成后续的业务处理，同时主线程和子线程的交互也很简单，子线程接收主线程的连接后，只管业务处理即可，无须关注主线程，可以直接在子线程将处理结果发送给客户端。
                     该 Reactor 模型适用于高并发场景，并且 Netty 网络通信框架也是采用这种实现
        1.3 Reactor 优缺点
             （1）响应快，不必为单个同步时间所阻塞，虽然 Reactor 本身依然是同步的；
             （2）可以最大程度的避免复杂的多线程及同步问题，并且避免了多线程/进程的切换开销
             （3）可扩展性，可以方便地通过增加 Reactor 实例个数来充分利用 CPU 资源；
             （4）可复用性，Reactor 模型本身与具体事件处理逻辑无关，具有很高的复用性。

     2.Proactor 网络模型 https://blog.csdn.net/a745233700/article/details/122390285
        前面我们介绍了 Reactor 网络模型（文章地址：https://blog.csdn.net/a745233700/article/details/122660246），知道了 Reactor 是非阻塞同步网络模型，而 Proactor 是异步网络模型。
        2.1 阻塞型IO和非阻塞型IO
            (1)对于阻塞IO
                当用户程序执行 read，线程会被阻塞，一直等内核数据准备好，并把数据从内核缓冲区拷贝到应用程序的缓冲区中，当拷贝过程完成，read 才会返回
                详见阻塞IO.png
                阻塞等待的是「内核数据准备好」和「数据从内核态拷贝到用户态」这两个过程
            (2)非阻塞IO
                非阻塞的read请求在数据未准备好的情况下立即返回，可以继续往下执行，此时应用程序不断轮询内核，直到数据准备好，内核将数据拷贝到应用程序缓冲区，read 调用就可以获取到结果
                详见非阻塞IO.png
                这里最后一次 read 调用，获取数据的过程，是一个同步的过程，是需要等待的过程。这里的同步指的是内核态的数据拷贝到用户程序的缓存区这个过程。
            因此，无论 read 和 send 是阻塞 I/O，还是非阻塞 I/O 都是同步调用。因为在 read 调用时，内核将数据从内核空间拷贝到用户空间的过程都是需要等待的，也就是说这个过程是同步的，如果内核实现的拷贝效率不高，read 调用就会在这个同步过程中等待比较长的时间。
        2.2 异步IO
            指的是「内核数据准备好」和「数据从内核态拷贝到用户态」这两个过程都不用等待。当我们发起 aio_read （异步 I/O） 之后，就立即返回，内核自动将数据从内核空间拷贝到用户空间，这个拷贝过程同样是异步的，内核自动完成的，
            和前面的同步操作不一样，应用程序并不需要主动发起拷贝动作
            详见异步IO.png
        2.3 Reactor和Proactor的区别
            Proactor 正是使用了异步 I/O 技术，所以被称为异步网络模型。现在我们再来理解 Reactor 和 Proactor 的区别，就比较清晰了
                (1)Reactor 是同步非阻塞网络模型，感知的是就绪可读写事件。在每次感知到有事件发生（比如可读就绪事件）后，就需要应用进程主动调用 read 方法来完成数据的读取，也就是要应用进程主动将 socket 接收缓存中的数据读到应用进程内存中，这个过程是同步的，读取完数据后应用进程才能处理数据。
                (2)Proactor 是异步网络模式， 感知的是已完成的读写事件。在发起异步读写请求时，需要传入数据缓冲区的地址（用来存放结果数据）等信息，这样系统内核才可以自动帮我们把数据的读写工作完成，这里的读写工作全程由操作系统来做，并不需要像 Reactor 那样还需要应用进程主动发起 read/write
                    来读写数据，操作系统完成读写工作后，就会通知应用进程直接处理数据。
        2.4 Proactor 的执行流程
            无论是 Reactor，还是 Proactor，都是一种基于「事件分发」的网络编程模式，区别在于 Reactor 模式是基于「待完成」的 I/O 事件，而 Proactor 模式则是基于「已完成」的 I/O 事件
            详见Proactor模式.png
            介绍一下 Proactor 模式的工作流程：
                 (1)Proactor Initiator 负责创建 Proactor 和 Handler 对象，并将 Proactor 和 Handler 都通过  Asynchronous Operation Processor 注册到内核；
                 (2)Asynchronous Operation Processor 负责处理注册请求，并处理 I/O 操作；
                 (3)Asynchronous Operation Processor 完成 I/O 操作后通知 Proactor；
                 (4)Proactor 根据不同的事件类型回调不同的 Handler 进行业务处理；
                 (5)Handler 完成业务处理；
            需要注意的是：Proactor关注的不是就绪事件，而是完成事件，这是区分Reactor模式的关键点。
        2.5 Proactor 模型处理读取操作的主要流程
            下面就简单介绍下 Proactor 模型处理读取操作的主要流程：
                 （1）应用程序初始化一个异步读取操作，然后注册相应的事件处理器，此时事件处理器不关注读取就绪事件，而是关注读取完成事件，这是区别于Reactor的关键。
                 （2）事件分离器等待读取操作完成事件
                 （3）在事件分离器等待读取操作完成的时候，操作系统调用内核线程完成读取操作，并将读取的内容放入用户传递过来的缓存区中。这也是区别于Reactor的一点，Proactor中，应用程序需要传递缓存区。
                 （4）事件分离器捕获到读取完成事件后，激活应用程序注册的事件处理器，事件处理器直接从缓存区读取数据，而不需要进行实际的读取操作。
             异步IO都是操作系统负责将数据读写到应用传递进来的缓冲区供应用程序操作。
             Proactor中写入操作和读取操作基本一致，只不过监听的事件是写入完成事件而已。
        2.6 Proactor 的缺点
            Proactor 性能确实非常强大，效率也高，但是同样存在以下缺点：
                 （1）内存的使用：缓冲区在读或写操作的时间段内必须保持住，可能造成持续的不确定性，并且每个并发操作都要求有独立的缓存，相比Reactor模型，在Socket已经准备好读或写前，是不要求开辟缓存的；
                 （2）操作系统的支持：Windows 下通过一套完整的支持 socket 的异步编程接口，也就是通过 IOCP 实现了真正的异步，但 Linux 系统下的异步 IO 还不完善，aio 系列函数是由 POSIX 定义的异步操作接口，
                    不是真正的操作系统级别支持的，而是在用户空间模拟出来的异步，并且仅仅支持基于本地文件的 aio 异步操作，网络编程中的 socket 是不支持的。因此，Linux 系统下高并发网络编程都是以 Reactor 模型为主

     3.序列化与反序列化
        3.1 什么是序列化
             两个服务之间要传输一个数据对象，就需要将对象转换成二进制流，通过网络传输到对方服务，再转换成对象，供服务方法调用。这个编码和解码的过程称之为序列化和反序列化。所以序列化就是把 Java 对象变成二进制形式，本质上就是一个byte[]数组。
             将对象序列化之后，就可以写入磁盘进行保存或者通过网络中输出给远程服务了。反之，反序列化可以从网络或者磁盘中读取的字节数组，反序列化成对象，在程序中使用。
        3.2 序列化优点
             (1)永久性保存对象：将对象转为字节流存储到硬盘上，即使 JVM 停机，字节流还会在硬盘上等待，等待下一次 JVM 启动时，反序列化为原来的对象，并且序列化的二进制序列能够减少存储空间
             (2)方便网络传输：序列化成字节流形式的对象可以方便网络传输（二进制形式），节约网络带宽
             (3)通过序列化可以在进程间传递对象
        3.3 序列化的几种方式：
             3.3.1 Java 原生序列化
                 Java 默认通过 Serializable 接口实现序列化，只要实现了该接口，该类就会自动实现序列化与反序列化，该接口没有任何方法，只起标识作用。Java序列化保留了对象类的元数据（如类、成员变量、继承类信息等），以及对象数据等，兼容性最好，但不支持跨语言，而且性能一般。
                 实现 Serializable 接口的类在每次运行时，编译器会根据类的内部实现，包括类名、接口名、方法和属性等自动生成一个 serialVersionUID，serialVersionUID 主要用于验证对象在反序列化过程中，序列化对象是否加载了与序列化兼容的类，如果是具有相同类名的不同版本号的类，
                 在反序列化中是无法获取对象的，显式地定义 serialVersionUID 有两种用途：
                    在某些场合，希望类的不同版本对序列化兼容，因此需要确保类的不同版本具有相同的 serialVersionUID；
                    在某些场合，不希望类的不同版本对序列化兼容，因此需要确保类的不同版本具有不同的 serialVersionUID；
                 如果源码改变，那么重新编译后的 serialVersionUID 可能会发生变化，因此建议一定要显示定义 serialVersionUID 的属性值。
            3.3.2 Hessian 序列化:
                 Hessian 序列化是一种支持动态类型、跨语言、基于对象传输的网络协议。Java 对象序列化的二进制流可以被其他语言反序列化。 Hessian 协议具有如下特性：
                     自描述序列化类型。不依赖外部描述文件或接口定义， 用一个字节表示常用
                     基础类型，极大缩短二进制流
                     语言无关，支持脚本语言
                     协议简单，比 Java 原生序列化高效
                 Hessian 2.0 中序列化二进制流大小是 Java 序列化的 50%，序列化耗时是 Java 序列化的 30%，反序列化耗时是 Java 反序列化的20% 。
                 Hessian 会把复杂对象所有属性存储在一个 Map 中进行序列化。所以在父类、子类存在同名成员变量的情况下， Hessian 序列化时，先序列化子类 ，然后序列化父类，因此反序列化结果会导致子类同名成员变量被父类的值覆盖。
            3.3.3 Json 序列化
                 JSON 是一种轻量级的数据交换格式。JSON 序列化就是将数据对象转换为 JSON 字符串，在序列化过程中抛弃了类型信息，所以反序列化时需要提供类型信息才能准确地反序列化，相比前两种方式，JSON 可读性比较好，方便调试。
        3.4 为什么不建议使用Java序列化
             目前主流框架很少使用到 Java 序列化，比如 SpringCloud 使用的 Json 序列化，Dubbo 虽然兼容 Java 序列化，但默认使用的是 Hessian 序列化。这是为什么呢？主要是因为 JDK 默认的序列化方式存在以下一些缺陷：无法跨语言、
             易被攻击、序列化的流太大、序列化性能太差等
            3.4.1 无法跨语言
                Java 序列化只支持 Java 语言实现的框架，其它语言大部分都没有使用 Java 的序列化框架，也没有实现 Java 序列化这套协议，因此，两个不同语言编写的应用程序之间通信，无法使用 Java 序列化实现应用服务间传输对象的序列化和反序列化。
            3.4.2 易被攻击
                 对象是通过在 ObjectInputStream 上调用 readObject() 方法进行反序列化的，它可以将类路径上几乎所有实现了 Serializable 接口的对象都实例化。这意味着，在反序列化字节流的过程中，该方法可以执行任意类型的代码，这是非常危险的。
                 对于需要长时间进行反序列化的对象，不需要执行任何代码，也可以发起一次攻击。攻击者可以创建循环对象链，然后将序列化后的对象传输到程序中反序列化，这种情况会导致 hashCode 方法被调用次数呈次方爆发式增长, 从而引发栈溢出异常。
                 序列化通常会通过网络传输对象，而对象中往往有敏感数据，所以序列化常常成为黑客的攻击点，攻击者巧妙地利用反序列化过程构造恶意代码，使得程序在反序列化的过程中执行任意代码。 Java 工程中广泛使用的 Apache Commons Collections、Jackson、fastjson 等都出现过反序列化漏洞。
                 如何防范这种黑客攻击呢？有些对象的敏感属性不需要进行序列化传输，可以加 transient 关键字，避免把此属性信息转化为序列化的二进制流，除此之外，声明为 static 类型的成员变量也不能要序列化。如果一定要传递对象的敏感属性，可以使用对称与非对称加密方式独立传输，再使用某个
                 方法把属性还原到对象中。
            3.4.3 序列化后的流太大
                 序列化后的二进制流大小能体现序列化的性能。序列化后的二进制数组越大，占用的存储空间就越多，存储硬件的成本就越高。如果我们是进行网络传输，则占用的带宽就更多，这时就会影响到系统的吞吐量。
            3.4.4 序列化性能太差
                序列化的速度也是体现序列化性能的重要指标，如果序列化的速度慢，网络通信效率就低，从而增加系统的响应时间
     */
}
