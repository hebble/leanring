package com.learning.study.third;

public class ZookeeperLearning {
    /**
     * 1. ZooKeeper 是什么？
     *      ZooKeeper 是一个分布式的，开放源码的分布式应用程序协调服务，是 Google的 Chubby 一个开源的实现，它是集群的管理者
     *      客户端的读请求可以被集群中的任意一台机器处理，如果读请求在节点上注册了监听器，这个监听器也是由所连接的 zookeeper 机器来处理。对于写请求，这些请求会同时发给其他 zookeeper 机器并且达成一致后，请求才会返回成功。
     *      因此，随着 zookeeper 的集群机器增多，读请求的吞吐会提高但是写请求的吞吐会下降。
     *      有序性是 zookeeper 中非常重要的一个特性，所有的更新都是全局有序的，每个更新都有一个唯一的时间戳，这个时间戳称为 zxid（Zookeeper Transaction Id）。而读请求只会相对于更新有序，也就是读请求的返回结果中会带有这个 zookeeper 最新的 zxid。
     *
     *  2.ZooKeeper 提供了什么？
     *      1、文件系统
     *      2、通知机制
     *
     *  3.Zookeeper 文件系统
     *      Zookeeper 提供一个多层级的节点命名空间（节点称为 znode）。与文件系统不同的是，这些节点都可以设置关联的数据，而文件系统中只有文件节点可以存放数据而目录节点不行。Zookeeper 为了保证高吞吐和低延迟，在内存中维护了这个树状的目录结构，这种特性
     * 使得 Zookeeper 不能用于存放大量的数据，每个节点的存放数据上限为 1M。
     *
     * 4.四种类型的 znode
     *      1、PERSISTENT-持久化目录节点
     *          客户端与 zookeeper 断开连接后，该节点依旧存在
     *      2、PERSISTENT_SEQUENTIAL-持久化顺序编号目录节点
     *          客户端与 zookeeper 断开连接后，该节点依旧存在，只是 Zookeeper 给该节点名称进行顺序编号
     *      3、EPHEMERAL-临时目录节点
     *          客户端与 zookeeper 断开连接后，该节点被删除
     *      4、EPHEMERAL_SEQUENTIAL-临时顺序编号目录节点
     *          客户端与 zookeeper 断开连接后，该节点被删除，只是 Zookeeper 给该节点名称进行顺序编号
     *
     * 5.Zookeeper 通知机制
     *      client 端会对某个 znode 建立一个 watcher 事件，当该 znode 发生变化时，这些client 会收到 zk 的通知，然后 client 可以根据 znode 变化来做出业务上的改变等。
     *
     * 6.Zookeeper 做了什么？
     *      1、命名服务: 命名服务是指通过指定的名字来获取资源或者服务的地址，利用 zk 创建一个全局的路径，即是唯一的路径，这个路径就可以作为一个名字，指向集群中的集群，提供的服务的地址，或者一个远程的对象等等。
     *      2、配置管理: 程序分布式的部署在不同的机器上，将程序的配置信息放在 zk 的 znode 下，当有配置发生改变时，也就是 znode 发生变化时，可以通过改变 zk 中某个目录节点的内容，利用 watcher 通知给各个客户端，从而更改配置。
     *      3、集群管理: 所谓集群管理无在乎两点：是否有机器退出和加入、选举 master。
     *      4、分布式锁:
     *          有了 zookeeper 的一致性文件系统，锁的问题变得容易。锁服务可以分为两类，一个是保持独占，另一个是控制时序。
     *          对于第一类，我们将 zookeeper 上的一个 znode 看作是一把锁，通过 createznode 的方式来实现。所有客户端都去创建 /distribute_lock 节点，最终成功创建的那个客户端也即拥有了这把锁。用完删除掉自己创建的 distribute_lock 节点就释放出锁。
     *          对于第二类，/distribute_lock 已经预先存在，所有客户端在它下面创建临时顺序编号目录节点，和选master 一样，编号最小的获得锁，用完删除，依次方便。
     *      5、队列管理
     *          第一类，在约定目录下创建临时目录节点，监听节点数目是否是我们要求的数目。
     *          第二类，和分布式锁服务中的控制时序场景基本原理一致，入列有编号，出列按编号。
     *
     * 7. Zookeeper 工作原理
     *      Zookeeper 的核心是原子广播，这个机制保证了各个 Server 之间的同步。实现这个机制的协议叫做 Zab 协议。Zab 协议有两种模式，它们分别是恢复模式（选主）和广播模式（同步）。
     *      当服务启动或者在领导者崩溃后，Zab 就进入了恢复模式，当领导者被选举出来，且大多数 Server 完成了和 leader 的状态同步以后，恢复模式就结束了。状态同步保证了leader 和 Server 具有相同的系统状态。
     *
     * 8. zookeeper 是如何保证事务的顺序一致性的？
     *      zookeeper 采用了递增的事务 Id 来标识，所有的 proposal（提议）都在被提出的时候加上了 zxid，zxid 实际上是一个 64 位的数字，高 32 位是 epoch（时期; 纪元; 世; 新时代）用来标识 leader 是否发生改变，如果有新的 leader 产生出来，epoch 会自增，低
     *      32 位用来递增计数。当新产生 proposal 的时候，会依据数据库的两阶段过程，首先会向其他的 server 发出事务执行请求，如果超过半数的机器都能执行并且能够成功，那么就会开始执行。
     *
     * 9.Zookeeper 下 Server 工作状态
     *      每个 Server 在工作过程中有三种状态：
     *          LOOKING：当前 Server 不知道 leader 是谁，正在搜寻
     *          LEADING：当前 Server 即为选举出来的 leader
     *          FOLLOWING：leader 已经选举出来，当前 Server 与之同步
     *
     *
     */
}
