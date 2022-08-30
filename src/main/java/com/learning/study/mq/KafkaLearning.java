package com.learning.study.mq;

/**
 * https://mp.weixin.qq.com/s?__biz=MzA5MTc0NTMwNQ==&mid=2650716970&idx=1&sn=3875dd83ca35c683bfa42135c55a03ab&chksm=887da65cbf0a2f4aeae51f4d41fa8dec9c66af17fbc423eb5a1b0d35d20348880c8b2539ddbf&scene=21#wechat_redirect
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
                         String routeKey = "";
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

     */
}
