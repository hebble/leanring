package com.learning.study.object;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * https://blog.csdn.net/a745233700/article/details/122660332 Java IO篇：什么是零拷贝？
 */
public class 零拷贝 {
    /**
     1.传统的IO模式
        传统的IO模式，主要包括 read 和 write 过程：
             read：把数据从磁盘读取到内核缓冲区，再拷贝到用户缓冲区
             write：先把数据写入到 socket缓冲区，最后写入网卡设备
        详见传统IO模式read和write流程图.png
         （1）用户空间的应用程序通过read()函数，向操作系统发起IO调用，上下文从用户态到切换到内核态，然后再通过 DMA 控制器将数据从磁盘文件中读取到内核缓冲区
         （2）接着CPU将内核空间缓冲区的数据拷贝到用户空间的数据缓冲区，然后read系统调用返回，而系统调用的返回又会导致上下文从内核态切换到用户态
         （3）用户空间的应用程序通过write()函数向操作系统发起IO调用，上下文再次从用户态切换到内核态；接着CPU将数据从用户缓冲区复制到内核空间的 socket 缓冲区（也是内核缓冲区，只不过是给socket使用），然后write系统调用返回，再次触发上下文切换
         （4）最后异步传输socket缓冲区的数据到网卡，也就是说write系统调用的返回并不保证数据被传输到网卡

        在传统的数据 IO 模式中，读取一个磁盘文件，并发送到远程端的服务，就共有四次用户空间与内核空间的上下文切换，四次数据复制，包括两次 CPU 数据复制，两次 DMA 数据复制。但两次 CPU 数据复制才是最消耗资源和时间的，这个过程还需要内核态和用户态
        之间的来回切换，而CPU资源十分宝贵，要拷贝大量的数据，还要处理大量的任务，如果能把 CPU 的这两次拷贝给去除掉，既能节省CPU资源，还可以避免内核态和用户态之间的切换。而零拷贝技术就是为了解决这个问题

        DMA（Direct Memory Access，直接内存访问）：DMA 本质上是一块主板上独立的芯片，允许外设设备直接与内存存储器进行数据传输，并且不需要CPU参与的技术

     2.什么是零拷贝?
         零拷贝指在进行数据 IO 时，数据在用户态下经历了零次 CPU 拷贝，并非不拷贝数据。通过减少数据传输过程中 内核缓冲区和用户进程缓冲区 间不必要的CPU数据拷贝 与 用户态和内核态的上下文切换次数，降低 CPU 在这两方面的开销，释放 CPU 执行其他任务，
         更有效的利用系统资源，提高传输效率，同时还减少了内存的占用，也提升应用程序的性能。
         由于零拷贝在内核空间中完成所有的内存拷贝，可以最大化使用 socket 缓冲区的可用空间，从而提高了一次系统调用中处理的数据量，进一步降低了上下文切换次数。零拷贝技术基于 PageCache，而 PageCache 缓存了最近访问过的数据，提升了访问缓存数据的
        性能，同时，为了解决机械磁盘寻址慢的问题，它还协助 IO 调度算法实现了 IO 合并与预读（这也是顺序读比随机读性能好的原因），这进一步提升了零拷贝的性能。

     3.Linux 中的零拷贝方式
        3.1 mmap + write 实现的零拷贝
            详见mmap + write 实现的零拷贝流程图.png
             （1）用户应用程序通过 mmap() 向操作系统发起 IO调用，上下文从用户态切换到内核态；然后通过 DMA 将数据从磁盘中复制到内核空间缓冲区
             （2）mmap 系统调用返回，上下文从内核态切换回用户态（这里不需要将数据从内核空间复制到用户空间，因为用户空间和内核空间共享了这个缓冲区）
             （3）用户应用程序通过 write() 向操作系统发起 IO调用，上下文再次从用户态切换到内核态。接着 CPU 将数据从内核空间缓冲区复制到内核空间 socket 缓冲区；write 系统调用返回，导致内核空间到用户空间的上下文切换
             （4）DMA 异步将 socket 缓冲区中的数据拷贝到网卡
             mmap 的零拷贝 I/O 进行了4次用户空间与内核空间的上下文切换，以及3次数据拷贝；其中3次数据拷贝中包括了2次 DMA 拷贝和1次 CPU 拷贝。所以 mmap 通过内存地址映射的方式，节省了数据IO过程中的一次CPU数据拷贝以及一半的内存空间
        3.2 sendfile 实现的零拷贝
            详见sendfile 实现的零拷贝流程图.png
             （1）用户应用程序发出 sendfile 系统调用，上下文从用户态切换到内核态；然后通过 DMA 控制器将数据从磁盘中复制到内核缓冲区中
             （2）然后CPU将数据从内核空间缓冲区复制到 socket 缓冲区
             （3）sendfile 系统调用返回，上下文从内核态切换到用户态
             （4）DMA 异步将内核空间 socket 缓冲区中的数据传递到网卡
            通过 sendfile 实现的零拷贝I/O使用了2次用户空间与内核空间的上下文切换，以及3次数据的拷贝。其中3次数据拷贝中包括了2次DMA拷贝和1次CPU拷贝。那能不能将CPU拷贝的次数减少到0次呢？答案肯定是有的，那就是 带 DMA 收集拷贝功能的 sendfile
        3.3 带 DMA 收集拷贝功能的 sendfile 实现的零拷贝
            Linux 2.4 版本之后，对 sendfile 做了升级优化，引入了 SG-DMA技术，其实就是对DMA拷贝加入了 scatter/gather 操作，它可以直接从内核空间缓冲区中将数据读取到网卡，无需将内核空间缓冲区的数据再复制一份到 socket 缓冲区，从而省去了一次 CPU拷贝
            详见带 DMA 收集拷贝功能的 sendfile 实现的零拷贝.png
             （1）用户应用程序发出 sendfile 系统调用，上下文从用户态切换到内核态；然后通过 DMA 控制器将数据从磁盘中复制到内核缓冲区中
             （2）接下来不需要CPU将数据复制到 socket 缓冲区，而是将相应的文件描述符信息复制到 socket 缓冲区，该描述符包含了两种的信息：①内核缓冲区的内存地址、②内核缓冲区的偏移量
             （3）sendfile 系统调用返回，上下文从内核态切换到用户态
             （4）DMA 根据 socket 缓冲区中描述符提供的地址和偏移量直接将内核缓冲区中的数据复制到网卡
            带有 DMA 收集拷贝功能的 sendfile 实现的 I/O 使用了2次用户空间与内核空间的上下文切换，以及2次数据的拷贝，而且这2次的数据拷贝都是非CPU拷贝，这样就实现了最理想的零拷贝I/O传输了，不需要任何一次的CPU拷贝，以及最少的上下文切换

        备注：需要注意的是，零拷贝有一个缺点，就是不允许进程对文件内容作一些加工再发送，比如数据压缩后再发送。

     4.零拷贝技术的应用场景
        4.1 Java 的 NIO
            （1）mmap + write 的零拷贝方式
                FileChannel 的 map() 方法产生的 MappedByteBuffer：FileChannel 提供了 map() 方法，该方法可以在一个打开的文件和 MappedByteBuffer 之间建立一个虚拟内存映射，MappedByteBuffer 继承于 ByteBuffer；该缓冲器的内存是一个文件的
                内存映射区域。map() 方法底层是通过 mmap 实现的，因此将文件内存从磁盘读取到内核缓冲区后，用户空间和内核空间共享该缓冲区。mmap的小demo如下：
     */
            public void test1 () {
                try {
                    FileChannel readChannel = FileChannel.open(Paths.get("./jay.txt"), StandardOpenOption.READ);
                    MappedByteBuffer data = readChannel.map(FileChannel.MapMode.READ_ONLY, 0, 1024 * 1024 * 40);
                    FileChannel writeChannel = FileChannel.open(Paths.get("./siting.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                    //数据传输
                    writeChannel.write(data);
                    readChannel.close();
                    writeChannel.close();
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
    /**
            (2)sendfile 的零拷贝方式
                FileChannel 的 transferTo、transferFrom 如果操作系统底层支持的话，transferTo、transferFrom也会使用 sendfile 零拷贝技术来实现数据的传输
     */
            public void test2() {
                try {
                    FileChannel readChannel = FileChannel.open(Paths.get("./jay.txt"), StandardOpenOption.READ);
                    long len = readChannel.size();
                    long position = readChannel.position();

                    FileChannel writeChannel = FileChannel.open(Paths.get("./siting.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                    //数据传输
                    readChannel.transferTo(position, len, writeChannel);
                    readChannel.close();
                    writeChannel.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
    /**
        4.2 Netty 框架
            Netty 的零拷贝主要体现在下面五个方面：
             （1）在网络通信上，Netty 的接收和发送 ByteBuffer 采用直接内存，使用堆外直接内存进行 Socket 读写，不需要进行字节缓冲区的二次拷贝。如果使用传统的堆内存进行 Socket 读写，JVM 会将堆内存 Buffer 拷贝一份到直接内存中（为什么拷贝？因为 JVM 会发生 GC 垃圾回收，数据的内存地址会发生变化，直接将堆内的内存地址传给内核，内存地址一旦变了就内核读不到数据了），然后才写入 Socket 中。相比于堆外直接内存，消息在发送过程中多了一次缓冲区的内存拷贝。
             （2）在文件传输上，Netty 的通过 FileRegion 包装的 FileChannel.tranferTo 实现文件传输，它可以直接将文件缓冲区的数据发送到目标 Channel，避免了传统通过循环 write 方式导致的内存拷贝问题。
             （3）在缓存操作上，Netty 提供了CompositeByteBuf 类，它可以将多个 ByteBuf 合并为一个逻辑上的 ByteBuf，避免了各个 ByteBuf 之间的拷贝。
             （4）通过 wrap 操作，我们可以将byte[]数组、ByteBuf、ByteBuffer等包装成一个Netty ByteBuf对象，进而避免了拷贝操作。
             （5）ByteBuf 支持 slice 操作，因此可以将 ByteBuf 分解为多个共享同一个存储区域的 ByteBuf，避免了内存的拷贝
        4.3 kafka
            Kafka 的索引文件使用的是 mmap + write 方式，数据文件使用的是 sendfile 方式
     */
}
