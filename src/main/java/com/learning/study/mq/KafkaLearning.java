package com.learning.study.mq;

/**
 * https://blog.csdn.net/tototuzuoquan/article/details/116573246
 */
public class KafkaLearning {
    /**
     1、Kafka 都有哪些特点？
        高吞吐量、低延迟：kafka每秒可以处理几十万条消息，它的延迟最低只有几毫秒，每个topic可以分多个partition, consumer group 对partition进行consume操作。
             •可扩展性：kafka集群支持热扩展
             •持久性、可靠性：消息被持久化到本地磁盘，并且支持数据备份防止数据丢失
             •容错性：允许集群中节点失败（若副本数量为n,则允许n-1个节点失败）
             •高并发：支持数千个客户端同时读写

     2、请简述下你在哪些场景下会选择 Kafka？
         •日志收集：一个公司可以用Kafka可以收集各种服务的log，通过kafka以统一接口服务的方式开放给各种consumer，例如hadoop、HBase、Solr等。
         •消息系统：解耦和生产者和消费者、缓存消息等。
         •用户活动跟踪：Kafka经常被用来记录web用户或者app用户的各种活动，如浏览网页、搜索、点击等活动，这些活动信息被各个服务器发布到kafka的topic中，然后订阅者通过订阅这些topic来做实时的监控分析，或者装载到hadoop、数据仓库中做离线分析和挖掘。
         •运营指标：Kafka也经常用来记录运营监控数据。包括收集各种分布式应用的数据，生产各种操作的集中反馈，比如报警和报告。
         •流式处理：比如spark streaming和 Flink

     3.Kafka 的设计架构
         Kafka 架构分为以下几个部分
             •Producer ：消息生产者，就是向 kafka broker 发消息的客户端。
             •Consumer ：消息消费者，向 kafka broker 取消息的客户端。
             •Topic ：可以理解为一个队列，一个 Topic 又分为一个或多个分区，
             •Consumer Group：这是 kafka 用来实现一个 topic 消息的广播（发给所有的 consumer）和单播（发给任意一个 consumer）的手段。一个 topic 可以有多个 Consumer Group。
             •Broker ：一台 kafka 服务器就是一个 broker。一个集群由多个 broker 组成。一个 broker 可以容纳多个 topic。
             •Partition：为了实现扩展性，一个非常大的 topic 可以分布到多个 broker上，每个 partition 是一个有序的队列。partition 中的每条消息都会被分配一个有序的id（offset）。将消息发给 consumer，kafka 只保证按一个 partition 中的消息的顺序，不保证一个 topic 的整体（多个 partition 间）的顺序。
             •Offset：kafka 的存储文件都是按照 offset.kafka 来命名，用 offset 做名字的好处是方便查找。例如你想找位于 2049 的位置，只要找到 2048.kafka 的文件即可。当然 the first offset 就是 00000000000.kafka。

     4.Kafka 分区的目的？
        分区对于 Kafka 集群的好处是：实现负载均衡。分区对于消费者来说，可以提高并发度，提高效率。

     5.Kafka 是如何做到消息的有序性？
        kafka 中的每个 partition 中的消息在写入时都是有序的，而且单独一个 partition 只能由一个消费者去消费，可以在里面保证消息的顺序性。但是分区之间的消息是不保证有序的。

     6.Kafka 是如何保证数据可靠性和一致性
        6.1 数据可靠性
            Kafka 作为一个商业级消息中间件，消息可靠性的重要性可想而知。本文从 Producter 往 Broker 发送消息、Topic 分区副本以及 Leader 选举几个角度介绍数据的可靠性。
            6.1.1 Topic 分区副本
                在 Kafka 0.8.0 之前，Kafka 是没有副本的概念的，那时候人们只会用 Kafka 存储一些不重要的数据，因为没有副本，数据很可能会丢失。但是随着业务的发展，支持副本的功能越来越强烈，所以为了保证数据的可靠性，Kafka 从 0.8.0 版本开始引入了分区副本（详情请参见 KAFKA-50）。
                也就是说每个分区可以人为的配置几个副本（比如创建主题的时候指定 replication-factor，也可以在 Broker 级别进行配置 default.replication.factor），一般会设置为3。
                Kafka 可以保证单个分区里的事件是有序的，分区可以在线（可用），也可以离线（不可用）。在众多的分区副本里面有一个副本是 Leader，其余的副本是 follower，所有的读写操作都是经过 Leader 进行的，同时 follower 会定期地去 leader 上的复制数据。当 Leader 挂了的时候，
                其中一个 follower 会重新成为新的 Leader。通过分区副本，引入了数据冗余，同时也提供了 Kafka 的数据可靠性。
                Kafka 的分区多副本架构是 Kafka 可靠性保证的核心，把消息写入多个副本可以使 Kafka 在发生崩溃时仍能保证消息的持久性。
            6.1.2 Producer 往 Broker 发送消息
                如果我们要往 Kafka 对应的主题发送消息，我们需要通过 Producer 完成。前面我们讲过 Kafka 主题对应了多个分区，每个分区下面又对应了多个副本；为了让用户设置数据可靠性， Kafka 在 Producer 里面提供了消息确认机制。也就是说我们可以通过配置来决定消息发送到对应分区的
                几个副本才算消息发送成功。可以在定义 Producer 时通过 acks 参数指定（在 0.8.2.X 版本之前是通过 request.required.acks 参数设置的，详见 KAFKA-3043）。这个参数支持以下三种值：
                (1)acks = 0：
                    意味着如果生产者能够通过网络把消息发送出去，那么就认为消息已成功写入 Kafka 。在这种情况下还是有可能发生错误，比如发送的对象无能被序列化或者网卡发生故障，但如果是分区离线或整个集群长时间不可用，
                    那就不会收到任何错误。在 acks=0 模式下的运行速度是非常快的（这就是为什么很多基准测试都是基于这个模式），你可以得到惊人的吞吐量和带宽利用率，不过如果选择了这种模式， 一定会丢失一些消息。
                (2)acks = 1：
                    意味若 Leader 在收到消息并把它写入到分区数据文件（不一定同步到磁盘上）时会返回确认或错误响应。在这个模式下，如果发生正常的 Leader 选举，生产者会在选举时收到一个 LeaderNotAvailableException 异常，
                    如果生产者能恰当地处理这个错误，它会重试发送悄息，最终消息会安全到达新的 Leader 那里。不过在这个模式下仍然有可能丢失数据，比如消息已经成功写入 Leader，但在消息被复制到 follower 副本之前 Leader发生崩溃。
                (3)acks = all（这个和 request.required.acks = -1 含义一样）：
                    意味着 Leader 在返回确认或错误响应之前，会等待所有同步副本都收到悄息。如果和 min.insync.replicas 参数结合起来，就可以决定在返回确认前至少有多少个副本能够收到悄息，生产者会一直重试直到消息被成功提交。
                    不过这也是最慢的做法，因为生产者在继续发送其他消息之前需要等待所有副本都收到当前的消息。
                根据实际的应用场景，我们设置不同的 acks，以此保证数据的可靠性。
                另外，Producer 发送消息还可以选择同步（默认，通过 producer.type=sync 配置） 或者异步（producer.type=async）模式。如果设置成异步，虽然会极大的提高消息发送的性能，但是这样会增加丢失数据的风险。
                如果需要确保消息的可靠性，必须将 producer.type 设置为 sync。
            6.1.3 同步发送、异步发送、异步回调和分区路由发送
                java代码同步异步:
                    (1)同步发送
                        kafkaTemplate.send("topic","111111111").get();
                        在这里，send()方法先返回了一个Future对象，然后调用Future对象的get()方法等待kafka响应。如果服务器返回错误，get()方法会抛出异常。如果没有发生错误，我们会得到一个RecordMetadata对象，可以用它获取消息的偏移量。同步发送需要等待kafka服务器的响应吞吐相对较低
                    (2)异步发送
                        kafkaTemplate.send("test","111111111");
                        异步发送相比同步发送能处理更多的消息耗时更少，大多数情况下我们都不需要等待响应，所以大多数情况下我们都会使用异步发送。但是在遇到消息发送失败时我们不能准确的处理，所以我们需要一个异步发送消息失败的回调。
                    (3)异步回调
                         @Component
                         public class KafkaSendResultHandler implements ProducerListener {
                             @Override
                             public void onSuccess(ProducerRecord producerRecord,RecordMetadata recordMetadata) {
                                 log.info("Message send success : " + producerRecord.toString());
                             }
                             @Override
                             public void onError(ProducerRecord producerRecord, Exception exception) {
                                 log.info("Message send error : " + producerRecord.toString());
                             }
                         }

                         @Service
                         public class KafkaServer {
                             @Autowired
                             private KafkaTemplate kafkaTemplate;
                             @Autowired
                             private KafkaSendResultHandler producerListener;

                             public void send(){
                                 kafkaTemplate.setProducerListener(producerListener);
                                 kafkaTemplate.send("topic","11111");
                             }
                         }
                         在这里我们实现了ProducerListener 接口重写了onSuccess()和onError方法，然后在kafkaServer中我们增加了 kafkaTemplate.setProducerListener(producerListener);
                    (4)分区路由发送
                         当我们的分区数有多个时并且对数据顺序有严格时我们需要保证同样的一条数据发送到同一个分区上，针对这种情况spring kafka提供了对应的发送接口，下面是代码：
                         // 自定义路由key,唯一
                         String routeKey = ""; //key会在kafka进行hash运算, 最后到达某个分区
                         kafkaTemplate.send(new ProducerRecord("topic",routeKey, "111"));
                         在这里我们直接调用send() 方法里面传入ProducerRecord对象其中routeKey 就是路由到不同分区的参数。
            6.1.4 Leader 选举
                 在介绍 Leader 选举之前，让我们先来了解一下 ISR（in-sync replicas）列表。每个分区的 leader 会维护一个 ISR 列表，ISR 列表里面就是 follower 副本的 Borker 编号，只有跟得上 Leader 的 follower
                 副本才能加入到 ISR 里面，这个是通过 replica.lag.time.max.ms 参数配置的，具体可以参见《图文了解 Kafka 的副本复制机制》。只有 ISR 里的成员才有被选为 leader 的可能。
                 所以当 Leader 挂掉了，而且 unclean.leader.election.enable=false 的情况下，Kafka 会从 ISR 列表中选择第一个 follower 作为新的 Leader，因为这个分区拥有最新的已经 committed 的消息。通过这个可以保证已经 committed 的消息的数据可靠性。
             综上所述，为了保证数据的可靠性，我们最少需要配置一下几个参数：
                 (1)producer 级别：acks=all（或者 request.required.acks=-1），同时发生模式为同步 producer.type=sync
                 (2)topic 级别：设置 replication.factor>=3，并且 min.insync.replicas>=2；
                 (3)broker 级别：关闭不完全的 Leader 选举，即 unclean.leader.election.enable=false；
        6.2 数据一致性
             这里介绍的数据一致性主要是说不论是老的 Leader 还是新选举的 Leader，Consumer 都能读到一样的数据。那么 Kafka 是如何实现的呢？
             见图kafka数据一致性.jpg
             假设分区的副本为3，其中副本0是 Leader，副本1和副本2是 follower，并且在 ISR 列表里面。虽然副本0已经写入了 Message4，但是 Consumer 只能读取到 Message2。因为所有的 ISR 都同步了 Message2，只有 High Water Mark 以上的消息才支持 Consumer 读取，
             而 High Water Mark 取决于 ISR 列表里面偏移量最小的分区，对应于上图的副本2，这个很类似于木桶原理。
             这样做的原因是还没有被足够多副本复制的消息被认为是“不安全”的，如果 Leader 发生崩溃，另一个副本成为新 Leader，那么这些消息很可能丢失了。如果我们允许消费者读取这些消息，可能就会破坏一致性。试想，一个消费者从当前 Leader（副本0） 读取并处理了
             Message4，这个时候 Leader 挂掉了，选举了副本1为新的 Leader，这时候另一个消费者再去从新的 Leader 读取消息，发现这个消息其实并不存在，这就导致了数据不一致性问题。
             当然，引入了 High Water Mark 机制，会导致 Broker 间的消息复制因为某些原因变慢，那么消息到达消费者的时间也会随之变长（因为我们会先等待消息复制完毕）。
             延迟时间可以通过参数 replica.lag.time.max.ms 参数配置，它指定了副本在复制消息时可被允许的最大延迟时间。

     7.ISR、OSR、AR 是什么？
         ISR：In-Sync Replicas 副本同步队列
         OSR：Out-of-Sync Replicas
         AR：Assigned Replicas 所有副本
         ISR是由leader维护，follower从leader同步数据有一些延迟（具体可以参见 图文了解 Kafka 的副本复制机制），超过相应的阈值会把 follower 剔除出 ISR, 存入OSR（Out-of-Sync Replicas ）列表，新加入的follower也会先存放在OSR中。AR=ISR+OSR。

     8.Kafka 的每个分区只能被一个消费者线程
         Kafka通过消费者组机制同时实现了发布/订阅模型和点对点模型。多个组的消费者消费同一个分区属于多订阅者的模式，自然没有什么问题；而在单个组内某分区只交由一个消费者处理的做法则属于点对点模式。
         其实这就是设计上的一种取舍，如果Kafka真的允许组内多个消费者消费同一个分区，也不是什么灾难性的事情，只是没什么意义，而且还会重复消费消息
         假设1个partition能够被同组的多个consumer消费，因为consumer是通过pull的模式从partition拉取消息的，pull的时候就要决定从哪里pull，也就是index的值，不做中心化维护index的值的话，consumer
         就很容易pull到重复的消息重复消费，对index做中心化处理的话，就会增加通信成本，consumer每次pull的时候还得通信获取最新的index的值，再加上consumer消费失败，不commit成功的话，index的值维护
         起来就会异常复杂。
         整体上利大于弊呐，于是就1个partition只能被同组的一个consumer，如果需要多个consumer，就分多个partition

     9.如何做到多个线程同时消费一个分区？(解决办法: 不同消费组)
         多个Kafka消费者要想同时消费相同Topic下的相同Partition的数据，则需要将这些Kafka消费者放到不同的消费者组中。

     10.Kafka事务处理
         我们使用kafkaTemplate.send向kafka发送数据，但是发送数据之后方法内部抛出了异常。假如我们的代码含义是下面的这样的
         用户订单支付，向kafka发送数据，为用户增加积分
         然后把用户的订单支付结果存入数据库
         订单支付未成功，可能用户余额不足，抛出异常。但是向kafka发送的数据已经发出去了，这显然不是我们希望看到的。我们期望的结果是：订单支付成功和用户积分增加成功，要么都成功，要么都失败。
         下面是带事务处理的kafka生产者代码
             //带事务处理的发送方式
             public void rightSend(){
                 Order order = new Order();
                 order.setCurrencyType("RMB");
                 order.setCount(18);
                 order.setStatus("success");
                 // 声明事务：operations函数报错，消息就不会发出去。
                 kafkaTemplate.executeInTransaction(operations -> {
                 //数据发往kafka
                 operations.send("order-test",order);
                 //模拟后续业务处理发生了异常
                 throw new RuntimeException("fail");
                 });
             }
         注意： Spring提供了万能的@Transactional注解，是可以用来管理kafka事务的，但是需要针对kafka做额外的配置管理。
         加之通常情况下，spring的注解用于数据库事务处理，如果再结合数据库多数据源、分布式事务相关的处理，很有可能会造成不可预知的问题。所以我建议使用上面这个方式。

     11.数据传输的事务有几种？
         数据传输的事务定义通常有以下三种级别：
             （1）最多一次: 消息不会被重复发送，最多被传输一次，但也有可能一次不传输
             （2）最少一次: 消息不会被漏发送，最少被传输一次，但也有可能被重复传输.
             （3）精确的一次（Exactly once）: 不会漏传输也不会重复传输,每个消息都传输被

     12.Kafka 消费者是否可以消费指定分区消息？
         Kafa consumer消费消息时，向broker发出fetch请求去消费特定分区的消息，consumer指定消息在日志中的偏移量（offset），就可以消费从这个位置开始的消息，customer拥有了offset的控制权，可以向后回滚去重新消费之前的消息，这是很有意义的
             @KafkaListener(id = "thing2", topicPartitions =
             { @TopicPartition(topic = "topic1", partitions = { "0", "1" }),
             @TopicPartition(topic = "topic2", partitions = "0",
             partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "100"))
             })
             public void listen(ConsumerRecord<?, ?> record) {
             ...
         上面例子意思是 监听topic1的0,1分区；监听topic2的第0分区,并且第0分区从offset为100的开始消费;

     13.Kafka消息是采用Pull模式，还是Push模式？
         Kafka最初考虑的问题是，customer应该从brokes拉取消息还是brokers将消息推送到consumer，也就是pull还push。在这方面，Kafka遵循了一种大部分消息系统共同的传统的设计：producer将消息推送到broker，consumer从broker拉取消息。
         一些消息系统比如Scribe和Apache Flume采用了push模式，将消息推送到下游的consumer。这样做有好处也有坏处：由broker决定消息推送的速率，对于不同消费速率的consumer就不太好处理了。消息系统都致力于让consumer以最大的速率最
         快速的消费消息，但不幸的是，push模式下，当broker推送的速率远大于consumer消费的速率时，consumer恐怕就要崩溃了。最终Kafka还是选取了传统的pull模式。
         Pull模式的另外一个好处是consumer可以自主决定是否批量的从broker拉取数据。Push模式必须在不知道下游consumer消费能力和消费策略的情况下决定是立即推送每条消息还是缓存之后批量推送。如果为了避免consumer崩溃而采用较低的
         推送速率，将可能导致一次只推送较少的消息而造成浪费。Pull模式下，consumer就可以根据自己的消费能力去决定这些策略。
         Pull有个缺点是，如果broker没有可供消费的消息，将导致consumer不断在循环中轮询，直到新消息到t达。为了避免这点，Kafka有个参数可以让consumer阻塞知道新消息到达(当然也可以阻塞知道消息的数量达到某个特定的量这样就可以批量发

     14.Kafka 高效文件存储设计特点
         •Kafka把topic中一个parition大文件分成多个小文件段，通过多个小文件段，就容易定期清除或删除已经消费完文件，减少磁盘占用。
         •通过索引信息可以快速定位message和确定response的最大大小。
         •通过index元数据全部映射到memory，可以避免segment file的IO磁盘操作。
         •通过索引文件稀疏存储，可以大幅降低index文件元数据占用空间大小

     15.Kafka创建Topic时如何将分区放置到不同的Broker中
         因为:
             所有主题的第一个分区都是存放在第一个Broker上，这样会造成第一个Broker上的分区总数多于其他的Broker，这样就失去了负载均衡的目的；
             如果主题的分区数多于Broker的个数，多于的分区都是倾向于将分区发放置在前几个Broker上，同样导致负载不均衡。
         所以:
             副本因子不能大于 Broker 的个数；
             •第一个分区（编号为0）的第一个副本放置位置是随机从 brokerList 选择的；
             •其他分区的第一个副本放置位置相对于第0个分区依次往后移。也就是如果我们有5个 Broker，5个分区，假设第一个分区放在第四个 Broker 上，那么第二个分区将会放在第五个 Broker 上；第三个分区将会放在第一个 Broker 上；第四个分区将会放在第二个 Broker 上，依次类推；
             •剩余的副本相对于第一个副本放置位置其实是由 nextReplicaShift 决定的，而这个数也是随机产生的

     16.Kafka新建的分区会在哪个目录下创建
         在启动 Kafka 集群之前，我们需要配置好 log.dirs 参数，其值是 Kafka 数据的存放目录，这个参数可以配置多个目录，目录之间使用逗号分隔，通常这些目录是分布在不同的磁盘上用于提高读写性能。
         当然我们也可以配置 log.dir 参数，含义一样。只需要设置其中一个即可。
         如果 log.dirs 参数只配置了一个目录，那么分配到各个 Broker 上的分区肯定只能在这个目录下创建文件夹用于存放数据。
         但是如果 log.dirs 参数配置了多个目录，那么 Kafka 会在哪个文件夹中创建分区目录呢？答案是：Kafka 会在含有分区目录最少的文件夹中创建新的分区目录，分区目录名为 Topic名+分区ID。
         注意，是分区文件夹总数最少的目录，而不是磁盘使用量最少的目录！也就是说，如果你给 log.dirs 参数新增了一个新的磁盘，新的分区目录肯定是先在这个新的磁盘上创建直到这个新的磁盘目录拥有的分区目录不是最少为止。

     17.谈一谈 Kafka 的再均衡
         在Kafka中，当有新消费者加入或者订阅的topic数发生变化时，会触发Rebalance(再均衡：在同一个消费者组当中，分区的所有权从一个消费者转移到另外一个消费者)机制，Rebalance顾名思义就是重新均衡消费者消费。Rebalance的过程如下：
         第一步：所有成员都向coordinator发送请求，请求入组。一旦所有成员都发送了请求，coordinator会从中选择一个consumer担任leader的角色，并把组成员信息以及订阅信息发给leader。
         第二步：leader开始分配消费方案，指明具体哪个consumer负责消费哪些topic的哪些partition。一旦完成分配，leader会将这个方案发给coordinator。coordinator接收到分配方案之后会把方案发给各个consumer，这样组内的所有成员就都知道自己应该消费哪些分区了。
         所以对于Rebalance来说，Coordinator起着至关重要的作用

     18.consumer分区分配策略
         用过 Kafka 的同学用过都知道，每个 Topic 一般会有很多个 partitions。为了使得我们能够及时消费消息，我们也可能会启动多个 Consumer 去消费，而每个 Consumer 又会启动一个或多个streams去分别消费 Topic 里面的数据。我们又知道，Kafka 存在 Consumer Group 的概念，
         也就是 group.id 一样的 Consumer，这些 Consumer 属于同一个Consumer Group，组内的所有消费者协调在一起来消费订阅主题(subscribed topics)的所有分区(partition)。当然，每个分区只能由同一个消费组内的一个consumer来消费。那么问题来了，同一个 Consumer Group
         里面的 Consumer 是如何知道该消费哪些分区里面的数据呢？
         在 Kafka 内部存在两种默认的分区分配策略：Range 和 RoundRobin。当以下事件发生时，Kafka 将会进行一次分区分配：
             (1)同一个 Consumer Group 内新增消费者
             (2)消费者离开当前所属的Consumer Group，包括shuts down 或 crashes
             (3)订阅的主题新增分区
         将分区的所有权从一个消费者移到另一个消费者称为重新平衡（rebalance），如何rebalance就涉及到本文提到的分区分配策略。下面我们将详细介绍 Kafka 内置的两种分区分配策略。
         本文假设我们有个名为 T1 的主题，其包含了10个分区，然后我们有两个消费者（C1，C2）来消费这10个分区里面的数据，而且 C1 的 num.streams = 1，C2 的 num.streams = 2。
         18.1 Range strategy(范围分区)
             Range策略是对每个主题而言的，首先对同一个主题里面的分区按照序号进行排序，并对消费者按照字母顺序进行排序。在我们的例子里面，排完序的分区将会是0, 1, 2, 3, 4, 5, 6, 7, 8, 9；消费者线程排完序将会是C1-0, C2-0, C2-1。
             然后将partitions的个数除于消费者线程的总数来决定每个消费者线程消费几个分区。如果除不尽，那么前面几个消费者线程将会多消费一个分区。在我们的例子里面，我们有10个分区，3个消费者线程， 10 / 3 = 3，而且除不尽，那么消费
             者线程 C1-0 将会多消费一个分区，所以最后分区分配的结果看起来是这样的：
                 C1-0 将消费 0, 1, 2, 3 分区
                 C2-0 将消费 4, 5, 6 分区
                 C2-1 将消费 7, 8, 9 分区
             假如我们有11个分区，那么最后分区分配的结果看起来是这样的：
                 C1-0 将消费 0, 1, 2, 3 分区
                 C2-0 将消费 4, 5, 6, 7 分区
                 C2-1 将消费 8, 9, 10 分区
             假如我们有2个主题(T1和T2)，分别有10个分区，那么最后分区分配的结果看起来是这样的：
                 C1-0 将消费 T1主题的 0, 1, 2, 3 分区以及 T2主题的 0, 1, 2, 3分区
                 C2-0 将消费 T1主题的 4, 5, 6 分区以及 T2主题的 4, 5, 6分区
                 C2-1 将消费 T1主题的 7, 8, 9 分区以及 T2主题的 7, 8, 9分区
             可以看出，C1-0 消费者线程比其他消费者线程多消费了2个分区，这就是Range strategy的一个很明显的弊端。
         18.2 RoundRobin strategy(轮询分区)
             使用RoundRobin策略有两个前提条件必须满足：
                 (1)同一个Consumer Group里面的所有消费者的num.streams必须相等；
                 (2)每个消费者订阅的主题必须相同。
             所以这里假设前面提到的2个消费者的num.streams = 2。RoundRobin策略的工作原理：将所有主题的分区组成 TopicAndPartition 列表，然后对 TopicAndPartition 列表按照 hashCode 进行排序
             在我们的例子里面，加入按照 hashCode 排序完的topic-partitions组依次为T1-5, T1-3, T1-0, T1-8, T1-2, T1-1, T1-4, T1-7, T1-6, T1-9，我们的消费者线程排序为C1-0, C1-1, C2-0, C2-1，最后分区分配的结果为：
                 C1-0 将消费 T1-5, T1-2, T1-6 分区；
                 C1-1 将消费 T1-3, T1-1, T1-9 分区；
                 C2-0 将消费 T1-0, T1-4 分区；
                 C2-1 将消费 T1-8, T1-7 分区；
             多个主题的分区分配和单个主题类似，这里就不在介绍了。
             根据上面的详细介绍相信大家已经对Kafka的分区分配策略原理很清楚了。不过遗憾的是，目前我们还不能自定义分区分配策略，只能通过partition.assignment.strategy参数选择 range 或 roundrobin。partition.assignment.strategy参数默认的值是range。
         18.3 StickyAssignor(粘滞分配策略)
             特性：
                 1. 分区的分配尽可能的均匀；
                 2. 分区的分配尽可能的和上次分配保持一致；
                 当两者发生冲突时，优先满足第一个目标
             假设消费组有3个消费者：C0,C1,C2，它们分别订阅了2个Topic(T1,T2),并且每个
             主题有4个分区(P1,P2,P3,P4),也就是说，整个消费组订阅了8个分区：T1P1、T1P2、
             T1P3、T1P4、T2P1、T2P2、T2P3、T2P4， 那么最终的分配场景结果为 ：
                 C0: T1P1, T1P4, T2P3
                 C1: T1P2, T2P1, T2P4
                 C2: T1P3, T2P2
             这种分配方式有点类似于轮询策略，但实际上不然，假设C1消费者此时宕机，会导致
             重新分区，如果是轮询，那么结果应该是：
                 C0: T1P1, T1P3, T2P1, T2P3
                 C2: T1P2, T1P4, T2P2, T2P4
             如果是stickyAssignor， 它会满足分区分配尽可能和上次分配保持相同, 那么结果是：
                 C0: T1P1, T1P4, T2P3, T1P2
                 C2: T1P3, T2P2, T2P1, T2P4
             减少了不必要的分区移动

     19.producer的分区分配策略
         我们向topic发送消息的时候是要把messages封装成一个ProducerRecord对象的
         可以看到再new一个ProducerRecord对象时可分为三种情况：
             1.指明 partition 的情况下，直接将指明的值直接作为 partiton 值；
             2.没有指明 partition 值但有 key 的情况下，将 key 的 hash 值与 topic 的 partition 数进行取余得到 partition 值；（具体实现可参考：默认分区器org.apache.kafka.clients.producer.internals.DefaultPartitioner中的 partition() 方法）
             3.既没有 partition 值又没有 key 值的情况下，第一次调用时随机生成一个整数（后面每次调用在这个整数上自增），将这个值与 topic 可用的 partition 总数取余得到 partition值，也就是常说的 round-robin 算法。这也是默认的分区分配策略，能够保证负载均衡
     */
}































