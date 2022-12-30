package com.learning.study.redis;

import com.learning.study.test.User;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisLearning {
    /**
     * 1.redis概念
     * 与MySQL数据库不同的是，Redis的数据是存在内存中的。它的读写速度非常快，每秒可以处理超过10万次读写操作。因此redis被广泛应用于缓存，另外，Redis也经常用来做分布式锁。除此之外，Redis支持事务、持久化、LUA 脚本、LRU 驱动事件、多种集群方案。
     *
     * 2.redis数据结构
     *      String（字符串）
     *      Hash（哈希）
     *      List（列表）
     *      Set（集合）
     *      zset（有序集合）
     * 3.Redis为什么这么快？
     *      基于内存存储实现
     *      高效的数据结构
     *      合理的数据编码
     *      合理的线程模型
     *          I/O 多路复用(I/O ：网络 I/O, 多路 ：多个网络连接, 复用：复用同一个线程。)
     *          单线程模型(Redis是单线程模型的，而单线程避免了CPU不必要的上下文切换和竞争锁的消耗)
     *      虚拟内存机制
     *          虚拟内存机制就是暂时把不经常访问的数据(冷数据)从内存交换到磁盘中，从而腾出宝贵的内存空间用于其它需要访问的数据(热数据)。通过VM功能可以实现冷热数据分离，使热数据仍在内存中、冷数据保存到磁盘。这样就可以避免因为内存不足而造成访问速度下降的问题。
     */

    /**
     * 4. 什么是缓存击穿、缓存穿透、缓存雪崩？
     *  4.1缓存穿透
     *      指查询一个一定不存在的数据，由于缓存是不命中时需要从数据库查询，查不到数据则不写入缓存，这将导致这个不存在的数据每次请求都要到数据库去查询，进而给数据库带来压力。
     *      如何避免缓存穿透呢？
     *          1.如果是非法请求，我们在API入口，对参数进行校验，过滤非法值。
     *          2.如果查询数据库为空，我们可以给缓存设置个空值，或者默认值。但是如有有写请求进来的话，需要更新缓存哈，以保证缓存一致性，同时，最后给缓存设置适当的过期时间。（业务上比较常用，简单有效）
     *          3.使用布隆过滤器快速判断数据是否存在。即一个查询请求过来时，先通过布隆过滤器判断值是否存在，存在才继续往下查。
     *  4.2缓存雪奔问题
     *      指缓存中数据大批量到过期时间，而查询数据量巨大，请求都直接访问数据库，引起数据库压力过大甚至down机。
     *      解决办法:
     *          1.可通过均匀设置过期时间解决, 如采用一个较大固定值+一个较小的随机值，5小时+0到1800秒酱紫。
     *          2.Redis 故障宕机也可能引起缓存雪奔。这就需要构造Redis高可用集群啦
     *  4.3缓存击穿问题
     *      指热点key在某个时间点过期的时候，而恰好在这个时间点对这个Key有大量的并发请求过来，从而大量的请求打到db。有些文章认为它俩区别，是区别在于击穿针对某一热点key缓存，雪奔则是很多key。
     *      解决办法:
     *          1.使用互斥锁方案。缓存失效时，不是立即去加载db数据，而是先使用某些带成功返回的原子操作命令，如(Redis的setnx）去操作，成功的时候，再去加载db数据库数据和设置缓存。否则就去重试获取缓存。
     *          2. “永不过期”，是指没有设置过期时间，但是热点数据快要过期时，异步线程去更新和设置过期时间。
     */

    /**
     * 5. 什么是热Key问题，如何解决热key问题
     *      在Redis中，我们把访问频率高的key，称为热点key。如果某一热点key的请求到服务器主机时，由于请求量特别大，可能会导致主机资源不足，甚至宕机，从而影响正常的服务。
     *      如何解决热key问题？
     *          Redis集群扩容：增加分片副本，均衡读流量；
     *          将热key分散到不同的服务器中；
     *          使用二级缓存，即JVM本地缓存,减少Redis的读请求
     */

    /**
     * 6. Redis的过期策略
     *      定时过期:
     *          每个设置过期时间的key都需要创建一个定时器，到过期时间就会立即对key进行清除。
     *          该策略可以立即清除过期的数据，对内存很友好；但是会占用大量的CPU资源去处理过期的数据，从而影响缓存的响应时间和吞吐量。
     *      惰性过期:
     *          只有当访问一个key时，才会判断该key是否已过期，过期则清除。该策略可以最大化地节省CPU资源，却对内存非常不友好。极端情况可能出现大量的过期key没有再次被访问，从而不会被清除，占用大量内存。
     *      Redis中同时使用了惰性过期和定期过期两种过期策略。
     *
     *   但是呀，如果定期删除漏掉了很多过期的key，然后也没走惰性删除。就会有很多过期key积在内存内存，直接会导致内存爆的。或者有些时候，业务量大起来了，redis的key被大量使用，内存直接不够了，
     *   运维小哥哥也忘记加大内存了。难道redis直接这样挂掉？不会的！Redis用8种内存淘汰策略保护自己~
     *      volatile-lru：当内存不足以容纳新写入数据时，从设置了过期时间的key中使用LRU（最近最少使用）算法进行淘汰；
     *      allkeys-lru：当内存不足以容纳新写入数据时，从所有key中使用LRU（最近最少使用）算法进行淘汰。
     *      volatile-lfu：4.0版本新增，当内存不足以容纳新写入数据时，在过期的key中，使用LFU(最不经常使用,看频率)算法进行删除key。
     *      allkeys-lfu：4.0版本新增，当内存不足以容纳新写入数据时，从所有key中使用LFU(最不经常使用,看频率)算法进行淘汰；
     *      volatile-random：当内存不足以容纳新写入数据时，从设置了过期时间的key中，随机淘汰数据；。
     *      allkeys-random：当内存不足以容纳新写入数据时，从所有key中随机淘汰数据。
     *      volatile-ttl：当内存不足以容纳新写入数据时，在设置了过期时间的key中，根据过期时间进行淘汰，越早过期的优先被淘汰；
     *      noeviction：默认策略，当内存不足以容纳新写入数据时，新写入操作会报错。
     *
     *   LRU(Least Recently Used)和 LFU(Least Frequently Used)
     *      LRU(Least Recently Used)，即最近最少使用，是一种缓存置换算法。在使用内存作为缓存的时候，缓存的大小一般是固定的。当缓存被占满，这个时候继续往缓存里面添加数据，就需要淘汰一部分老的数据，
     *      释放内存空间用来存储新的数据。这个时候就可以使用LRU算法了。其核心思想是：如果一个数据在最近一段时间没有被用到，那么将来被使用到的可能性也很小，所以就可以被淘汰掉。
     *      LFU算法是Redis4.0里面新加的一种淘汰策略。它的全称是Least Frequently Used，它的核心思想是根据key的最近被访问的频率进行淘汰，很少被访问的优先被淘汰，被访问的多的则被留下来。
     *      LFU算法能更好的表示一个key被访问的热度。假如你使用的是LRU算法，一个key很久没有被访问到，只刚刚是偶尔被访问了一次，那么它就被认为是热点数据，不会被淘汰，而有些key将来是很有可能被访问到的则被淘汰了。
     *      如果使用LFU算法则不会出现这种情况，因为使用一次并不会使一个key成为热点数据。LFU原理使用计数器来对key进行排序，每次key被访问的时候，计数器增大。计数器越大，可以约等于访问越频繁。具有相同引用计数的数据块则按照时间排序。
     */

    /**
     * 7. Redis 的持久化机制有哪些？
     *      Redis提供了RDB和AOF两种持久化机制
     *      RDB 的优点: 适合大规模的数据恢复场景，如备份，全量复制等
     *      RDB缺点: 没办法做到实时持久化/秒级持久化。
     *              新老版本存在RDB格式兼容问题
     *      AOF的优点: 数据的一致性和完整性更高
     *      AOF的缺点: AOF记录的内容越多，文件越大，数据恢复变慢。
     */

    /**
     * 8. Redis 实现高可用有三种部署模式
     *  主从模式，哨兵模式，集群模式。
     *      8.1 主从模式:
     *          主从模式中，Redis部署了多台机器，有主节点，负责读写操作，有从节点，只负责读操作。从节点的数据来自主节点，实现原理就是主从复制机制
     *          主从复制包括全量复制，增量复制两种。一般当slave第一次启动连接master，或者认为是第一次连接，就采用全量复制, slave与master全量同步之后，master上的数据，如果再次发生更新，就会触发增量复制
     *      8.2 哨兵模式:
     *          主从模式中，一旦主节点由于故障不能提供服务，需要人工将从节点晋升为主节点，同时还要通知应用方更新主节点地址。显然，多数业务场景都不能接受这种故障处理方式。Redis从2.8开始正式提供了Redis Sentinel（哨兵）架构来解决这个问题
     *          由一个或多个Sentinel实例组成的Sentinel系统，它可以监视所有的Redis主节点和从节点，并在被监视的主节点进入下线状态时，自动将下线主服务器属下的某个从节点升级为新的主节点。但是呢，一个哨兵进程对Redis节点进行监控，就可能会出现问题（单点问题），因此，可以使用多个哨兵来进行监控Redis节点，并且各个哨兵之间还会进行监控
     *
     *          简单来说，哨兵模式就三个作用：
     *              1.发送命令，等待Redis服务器（包括主服务器和从服务器）返回监控其运行状态；
     *              2.哨兵监测到主节点宕机，会自动将从节点切换成主节点，然后通过发布订阅模式通知其他的从节点，修改配置文件，让它们切换主机；
     *              3.哨兵之间还会相互监控，从而达到高可用。
     *          故障切换的过程是怎样的呢?
     *              假设主服务器宕机，哨兵1先检测到这个结果，系统并不会马上进行 failover 过程，仅仅是哨兵1主观的认为主服务器不可用，这个现象成为主观下线。当后面的哨兵也检测到主服务器不可用，并且数量达到一定值时，
     *              那么哨兵之间就会进行一次投票，投票的结果由一个哨兵发起，进行 failover 操作。切换成功后，就会通过发布订阅模式，让各个哨兵把自己监控的从服务器实现切换主机，这个过程称为客观下线。这样对于客户端而言，
     *              一切都是透明的。
     *       8.3 Cluster集群模式
     *           哨兵模式基于主从模式，实现读写分离，它还可以自动切换，系统可用性更高。但是它每个节点存储的数据是一样的，浪费内存，并且不好在线扩容。因此，Cluster集群应运而生，它在Redis3.0加入的，实现了Redis的分布式存储。对数据进行分片，
     *           也就是说每台Redis节点上存储不同的内容，来解决在线扩容的问题。并且，它也提供复制和故障转移的功能。
     *           Hash Slot插槽算法:
     *              既然是分布式存储，Cluster集群使用的分布式算法是一致性Hash嘛？并不是，而是Hash Slot插槽算法。
     *              插槽算法把整个数据库被分为16384个slot（槽），每个进入Redis的键值对，根据key进行散列，分配到这16384插槽中的一个。使用的哈希映射也比较简单，用CRC16算法计算出一个16 位的值，再对16384取模。数据库中的每个键都属于这16384个槽的其中一个，集群中的每个节点都可以处理这16384个槽。
     *              集群中的每个节点负责一部分的hash槽，比如当前集群有A、B、C个节点，每个节点上的哈希槽数 =16384/3，那么就有：
     *                  节点A: 负责0~5460号哈希槽
     *                  节点B: 负责5461~10922号哈希槽
     *                  节点C: 负责10923~16383号哈希槽
     *              Redis Cluster集群中，需要确保16384个槽对应的node都正常工作，如果某个node出现故障，它负责的slot也会失效，整个集群将不能工作。
     *              因此为了保证高可用，Cluster集群引入了主从复制，一个主节点对应一个或者多个从节点。当其它主节点 ping 一个主节点 A 时，如果半数以上的主节点与 A 通信超时，那么认为主节点 A 宕机了。如果主节点宕机时，就会启用从节点。
     *              在Redis的每一个节点上，都有两个玩意，一个是插槽（slot），它的取值范围是0~16383。另外一个是cluster，可以理解为一个集群管理的插件。
     *          主观下线： 某个节点认为另一个节点不可用，即下线状态，这个状态并不是最终的故障判定，只能代表一个节点的意见，可能存在误判情况。
     *          客观下线： 指标记一个节点真正的下线，集群内多个节点都认为该节点不可用，从而达成共识的结果。如果是持有槽的主节点故障，需要为该节点进行故障转移。
     *
     *          注意:只有持有槽的主节点才有票，从节点收集到足够的选票（大于一半），触发替换主节点操作
     */

    /**
     * 9. 使用过Redis分布式锁嘛？有哪些注意点呢？
     *        分布式锁，是控制分布式系统不同进程共同访问共享资源的一种锁的实现。秒杀下单、抢红包等等业务场景，都需要用到分布式锁，我们项目中经常使用Redis作为分布式锁。
     *        加过期时间避免死锁, 设置唯一值, 只有自己能释放锁,避免别人勿删
     *
     *        但是存在问题:
     *              要知道，lua 脚本都是⽤在 Redis 的单例上的。⼀旦 Redis 本⾝出现了问题，我们的分布式锁就没法⽤了，分布式锁没法⽤，对业务的正常
     *          运⾏会造成重⼤影响，这是我们⽆法接受的。所以，我们需要把 Redis 搞成⾼可⽤的。⼀般来讲，解决 Redis ⾼可⽤的问题，都是使⽤主从集群。
     *          但是搞主从集群，⼜会引⼊新的问题。主要问题在于，Redis 的主从数据同步有延迟。这种延迟会产⽣⼀个边界条件：当主机上的 Redis 已
     *          经被⼈建好了锁，但是锁数据还未同步到从机时，主机宕了。随后，从机提升为主机，此时从机上是没有以前主机设置好的锁数据的——锁
     *          丢了……丢了……了……
     *          到这⾥，终于可以介绍 Redission（开源 Redis 客户端）了，我们来看看它怎么是实现 Redis 分布式锁的。
     *
     *          Redission 实现分布式锁的思想很简单，⽆论是主从集群还是 Redis Cluster 集群，它会对集群中的每个 Redis，挨个去执⾏设置 Redis 锁
     * 的脚本，也就是集群中的每个 Redis 都会包含设置好的锁数据。
     *
     * 10. 使用过Redisson嘛？说说它的原理
     *      分布式锁可能存在锁过期释放，业务没执行完的问题。有些小伙伴认为，稍微把锁过期时间设置长一些就可以啦。其实我们设想一下，是否可以给获得锁的线程，开启一个定时守护线程，每隔一段时间检查锁是否还存在，存在则对锁的过期时间延长，防止锁过期提前释放。
     * 当前开源框架Redisson就解决了这个分布式锁问题。
     *      只要线程一加锁成功，就会启动一个watch dog看门狗，它是一个后台线程，会每隔10秒检查一下，如果线程1还持有锁，那么就会不断的延长锁key的生存时间。因此，Redisson就是使用Redisson解决了锁过期释放，业务没执行完问题。
     *
     * 11. 什么是Redlock算法
     *      搞多个Redis master部署，以保证它们不会同时宕掉。并且这些master节点是完全相互独立的，相互之间不存在数据同步。同时，需要确保在这多个master实例上，是与在Redis单实例，使用相同方法来获取和释放锁。
     *      我们假设当前有5个Redis master节点，在5台服务器上面运行这些Redis实例。加锁流程:
     *          1.按顺序向5个master节点请求加锁
     *          2.根据设置的超时时间来判断，是不是要跳过该master节点。
     *          3.如果大于等于三个节点加锁成功，并且使用的时间小于锁的有效期，即可认定加锁成功啦。
     *          4.如果获取锁失败，解锁！
     */

    @Autowired
    private RedissonClient redissonClient;

    public void test1() throws Exception {
        String lockKey = "锁的key值";
        //获取一个锁的key
        RLock lock = redissonClient.getLock(lockKey);
            //尝试进行获取锁,当获取锁成功则运行条件
            //当获取锁失败的时候，则有其他线程的定时任务在获取锁，则放弃本次定时任务调度
        lock.tryLock(60000, 1000, TimeUnit.MILLISECONDS);
        if (lock.tryLock()) {
            try {
               //处理业务逻辑
            }finally {
                //进行释放锁
                lock.unlock();
            }
        }else{
            log.info("ReadRecordTask::上一个任务执行尚未完成");
        }
    }

    /**
     * 12. MySQL与Redis 如何保证双写一致性
     *      1.缓存延时双删
     *      2.删除缓存重试机制
     *      3.读取biglog异步删除缓存
     *
     *      延时双删流程
     *          先删除缓存
     *          再更新数据库
     *          休眠一会（比如1秒），再次删除缓存。
     *         注意: 但是如果第二次删除缓存失败呢?
     *      删除缓存重试机制
     *          写请求更新数据库
     *          缓存因为某些原因，删除失败
     *          把删除失败的key放到消息队列
     *          消费消息队列的消息，获取要删除的key
     *          重试删除缓存操作
     *      读取biglog异步删除缓存
     *          重试删除缓存机制还可以吧，就是会造成好多业务代码入侵。其实，还可以这样优化：通过数据库的binlog来异步淘汰key。
     *          以mysql为例吧
     *              可以使用阿里的canal将binlog日志采集发送到MQ队列里面
     *              然后通过ACK机制确认处理这条更新消息，删除缓存，保证数据缓存一致性
     *
     *          springboot整合canal: https://blog.csdn.net/duxing_langzi/article/details/124349157
     */

    /**
     * 13. 为什么Redis 6.0 之后改多线程呢？
     *      使用Redis时，几乎不存在CPU成为瓶颈的情况， Redis主要受限于内存和网络
     *      redis使用多线程并非是完全摒弃单线程，redis还是使用单线程模型来处理客户端的请求，只是使用多线程来处理数据的读写和协议解析，执行命令还是使用单线程。
     *      这样做的目的是因为redis的性能瓶颈在于网络IO而非CPU，使用多线程能提升IO读写的效率，从而整体提高redis的性能。
     */

    /**
     * 14. 聊聊Redis 事务机制
     *      Redis事务就是顺序性、一次性、排他性的执行一个队列中的一系列命令。
     *      一个最简单的事务从开始到执行大概会经历以下三个阶段：
     *          1.MULTI命令开始事务。
     *          2.多个命令入队。
     *          3.EXEC执行事务
     *      总的来说，Redis事务和数据库事务有很多区别，Redis事务可以理解为一个打包的批量执行脚本，但批量指令并非原子化的操作，中间某条指令的失败不会导致前面已执行指令的回滚，也不会造成后续的指令不执行。
     *      为什么Redis的事务不支持回滚呢？ 从官网能找到答案：
     *          Redis开发者觉得没必要支持回滚，这样Redis内部能够保持更简单便捷并且性能更好。
     *          Redis开发者觉得失败的命令是由使用者编程错误造成的，而这些错误应该在开发的过程中被发现，而不应该出现在生产环境中
     *
     */
    /**
     15.布隆过滤器
        15.1 什么是布隆过滤器?
            布隆过滤器（Bloom Filter）是 1970 年由布隆提出的。它实际上是一个很长的二进制向量和一系列随机映射函数。布隆过滤器可以用于检索一个元素是否在一个集合中。它的优点是空间效率和查询时间都比一般的算法要好的多，缺点是有一定的误识别率和删除困难。
        15.2 哈希函数
            布隆过滤器离不开哈希函数，所以在这里有必要介绍下哈希函数的概念，哈希函数的性质:
                 (1)经典的哈希函数都有无限大的输入值域(无穷大)。
                 (2)经典的哈希函数的输出域都是固定的范围(有穷大，假设输出域为 S)。
                 (3)当给哈希函数传入相同的值时，返回值必一样。
                 (4)当给哈希函数传入不同的输入值时，返回值可能一样，也可能不一样。
                 (5)输入值会尽可能均匀的分布在 S 上。
            前三点都是哈希函数的基础，第四点描述了哈希函数存在哈希碰撞的现象，因为输入域无限大，输出域有穷大，这是必然的，输入域中会有不同的值对应到输入域 S 中。第五点事评价一个哈希函数优劣的关键，哈希函数越优秀，分布就越均匀且与输入值出现的规律无关。
            比如存在 “hash1”,“hash2”,“hash3” 三个输入值比较类似，经过哈希函数计算后的结果应该相差非常大，可以通过常见的 MD5 和 SHA1 算法来验证这些特性。如果一个优秀的函数能够做到不同的输入值所得到的返回值可以均匀的分布在 S 中，将其返回值对 m 取余(%m)，
            得到的返回值可以认为也会均匀的分布在 0~m-1 位置上。
        15.3 布隆过滤器特点
             因使用哈希判断，时间效率很高。空间效率也是其一大优势。
             有误判的可能，需针对具体场景使用。
             因为无法分辨哈希碰撞，所以不是很好做删除操作
        15.4 使用场景
         布隆过滤器的巨大用处就是，能够迅速判断一个元素是否在一个集合中。它的常用使用场景如下：
             (1)黑名单 : 反垃圾邮件，从数十亿个垃圾邮件列表中判断某邮箱是否垃圾邮箱（同理，垃圾短信）
             (2)URL去重 : 网页爬虫对 URL 的去重，避免爬取相同的 URL 地址
             (3)单词拼写检查
             (4)Key-Value 缓存系统的 Key 校验 (缓存穿透) : 缓存穿透，将所有可能存在的数据缓存放到布隆过滤器中，当黑客访问不存在的缓存时迅速返回避免缓存及 DB 挂掉。
             (5)ID 校验，比如订单系统查询某个订单 ID 是否存在，如果不存在就直接返回。
     */
    public void test2(User user) {
        RBloomFilter<String> bloomFilter
                = redissonClient.getBloomFilter("REDISSON_BLOOMFILTER_USER");
        //布隆过滤器计算的正确率为97%，初始化布隆过滤器容量为50000L
        bloomFilter.tryInit(500000L,0.03);
        if (bloomFilter.contains(user.getName())) {
            log.warn("用户名:" + user.getName() + "已经存在");
            return;
        }
        //业务处理
//        user.setId(null);
//        int res = userMapper.insertSelective(user);
        int res = 1;
        if (res > 0) {
            //加入布隆过滤器
            bloomFilter.add(user.getName());
        }
    }

    /**
     16.redis发布订阅模式 https://blog.csdn.net/A_art_xiang/article/details/126786205
        Redis 发布订阅 (pub/sub) 是一种消息通信模式：发送者 (pub) 发送消息，订阅者 (sub) 接收消息。
        Redis 客户端可以订阅任意数量的频道。
        16.1 发布订阅的使用
            SUBSCRIBE命令
                监听发布到给定通道的消息。
                基本语法：
                     SUBSCRIBE channel [channel ...]
                     可以同时监听多个channel通道。
            PUBLISH命令
                将消息发布到一个通道。
                基本语法：
                    PUBLISH channel message
            注意发布、订阅客户端启动顺序！
                 当先启动订阅客户端，再启动发布客户端，然后发布客户端发布的消息，订阅客户端会正常受到。
                 但是！当先启动发布客户端，然后发布客户端发布的消息，再启动订阅客户端，订阅客户端是不会受到消息的！此时消息会丢失。
            总结
                 redis的发布订阅功能，很多小伙伴都会用来做消息队列使用。
                 但是该功能风险太大，很容易造成消息丢失，所以并不建议做消息队列使用。
                 怎么说呢，redis的发布订阅功能，因为其消息丢失风险，所以大大减少了其使用场景，所以具体场景具体使用吧。
        16.2 概念
            频道:
                频道（channel）类似于一个快递柜，快递员往里面放快递，收件人去里面取快递。管道（channel）是由中间件（redis）提供的，一个redisServer中有多个channel。
            消息发布者:
                可以理解为消息的生产者，消息发布者通过中间件（redis、mq等）向某个频道（管道）发送消息。
            消息接收者:
                也可以理解为消息消费者，消息接收者通过订阅某个频道（管道）来接收发布者发布的消息。
        16.3 与springboot的整合(和mq类似) https://blog.csdn.net/congge_study/article/details/126686318
            (1)自定义RedisSubConfig
                 import org.springframework.context.annotation.Bean;
                 import org.springframework.context.annotation.Configuration;
                 import org.springframework.data.redis.connection.RedisConnectionFactory;
                 import org.springframework.data.redis.listener.ChannelTopic;
                 import org.springframework.data.redis.listener.RedisMessageListenerContainer;

                 @Configuration
                 public class RedisSubConfig {
                     @Bean
                     public RedisMessageListenerContainer container(RedisConnectionFactory factory, RedisMessageListener listener) {
                         RedisMessageListenerContainer container = new RedisMessageListenerContainer();
                         container.setConnectionFactory(factory);
                         //订阅频道redis.news 和 redis.life  这个container 可以添加多个 messageListener
                         container.addMessageListener(listener, new ChannelTopic("redis.user"));
                         //container.addMessageListener(listener, new ChannelTopic("redis.news"));
                         return container;
                    }
                 }
            (2)自定义消息监听器
                 import org.springframework.beans.factory.annotation.Autowired;
                 import org.springframework.data.redis.connection.Message;
                 import org.springframework.data.redis.connection.MessageListener;
                 import org.springframework.data.redis.core.RedisTemplate;
                 import org.springframework.stereotype.Component;

                 @Component
                 public class RedisMessageListener implements MessageListener {
                     @Autowired
                     private RedisTemplate redisTemplate;

                     @Override
                     public void onMessage(Message message, byte[] pattern) {
                         // 获取消息
                         byte[] messageBody = message.getBody();
                         // 使用值序列化器转换
                         Object msg = redisTemplate.getValueSerializer().deserialize(messageBody);
                         // 获取监听的频道
                         byte[] channelByte = message.getChannel();
                         // 使用字符串序列化器转换
                         Object channel = redisTemplate.getStringSerializer().deserialize(channelByte);
                         // 渠道名称转换
                         String patternStr = new String(pattern);
                         System.out.println(patternStr);
                         System.out.println("---频道---: " + channel);
                         System.out.println("---消息内容---: " + msg);
                     }
                 }
            (3)redistemplate的序列化配置
                省略
            (4)发送消息
                 redisTemplate.convertAndSend("redis.user", userId);

     */








}
