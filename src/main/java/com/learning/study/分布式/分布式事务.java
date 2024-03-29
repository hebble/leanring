package com.learning.study.分布式;

public class 分布式事务 {
    /**
    1.分布式事务
        分布式事务就是指事务的参与者、支持事务的服务器、资源服务器以及事务管理器分别位于不同的分布式系统的不同节点之上。以上是百度百科的解释，简单的说，就是一次大的操作由不同的小操作组成，
        这些小的操作分布在不同的服务器上，且属于不同的应用，分布式事务需要保证这些小操作要么全部成功，要么全部失败。本质上来说，分布式事务就是为了保证不同数据库的数据一致性。

    2.分布式事务的解决方案
        两阶段提交方案/XA方案
        三阶段提交协议
        本地消息表
        Raft协议

    3.XA规范
        有了分布式事务的场景，就会有解决该问题的方式规范，XA规范就是解决分布式事务的规范。分布式事务的实现方式有很多种，最具有代表性的是由Oracle Tuxedo系统提出的 XA分布式事务协议。XA协议包括两阶段提交（2PC）和三阶段提交（3PC）两种实现。
        3.1 两阶段提交方案(2PC)
            该方案基于两阶段提交协议，因此也叫做两阶段提交方案。在该分布式系统中，其中 需要一个系统担任协调器的角色，其他系统担任参与者的角色。主要分为Commit-request阶段和Commit阶段
                请求阶段：首先协调器会向所有的参与者发送准备提交或者取消提交的请求，然后会收集参与者的决策。
                提交阶段：协调者会收集所有参与者的决策信息，当且仅当所有的参与者向协调器发送确认消息时协调器才会提交请求，否则执行回滚或者取消请求。
            该方案的缺陷：
                 同步阻塞：所有的参与者都是事务同步阻塞型的。当参与者占有公共资源时，其他第三方节点访问公共资源不得不处于阻塞状态。
                 单点故障：一旦协调器发生故障，系统不可用。
                 数据不一致：当协调器发送commit之后，有的参与者收到commit消息，事务执行成功，有的没有收到，处于阻塞状态，这段时间会产生数据不一致性。
                 不确定性：当协调器发送commit之后，并且此时只有一个参与者收到了commit，那么当该参与者与协调器同时宕机之后，重新选举的协调器无法确定该条消息是否提交成功。
                 XA方案的实现方式可以使用Spring+JTA来实现，可以参考文章：Springboot+atomikos+jta实现分布式事务统一管理
        3.2 三阶段提交（3PC）
             三阶段提交是在二阶段提交上的改进版本，其在两阶段提交的基础上增加了 CanCommit阶段，并加入了超时机制。同时在协调者和参与者中都引入超时机制。三阶段将二阶段的准备阶段拆分为2个阶段，插入了一个preCommit阶段，以此来处理原先二阶段，参与者准备后，
             参与者发生崩溃或错误，导致参与者无法知晓是否提交或回滚的不确定状态所引起的延时问题。
                 阶段 1：canCommit
                     协调者向所有参与者发出包含事务内容的 canCommit 请求，询问是否可以提交事务，并等待所有参与者答复。
                     参与者收到 canCommit 请求后，如果认为可以执行事务操作，则反馈 yes 并进入预备状态，否则反馈 no。
                阶段 2：preCommit
                    阶段一中，如果所有的参与者都返回Yes的话，那么就会进入PreCommit阶段进行事务预提交。此时分布式事务协调者会向所有的参与者节点发送PreCommit请求，参与者收到后开始执行事务操作，并将Undo和Redo信息记录到事务日志中。参与者执行完事务操作后（此时
                    属于未提交事务的状态），就会向协调者反馈“Ack”表示我已经准备好提交了，并等待协调者的下一步指令。如果阶段一中有任何一个参与者节点返回的结果是No响应，或者协调者在等待参与者节点反馈的过程中因挂掉而超时（2PC中只有协调者可以超时，参与者没有超
                    时机制）。整个分布式事务就会中断，协调者就会向所有的参与者发送“abort”请求。
                阶段 3：do Commit
                    该阶段进行真正的事务提交，在阶段二中如果所有的参与者节点都可以进行PreCommit提交，那么协调者就会从“预提交状态” 转变为 “提交状态”。然后向所有的参与者节点发送"doCommit"请求，参与者节点在收到提交请求后就会各自执行事务提交操作，并向协调者
                    节点反馈“Ack”消息，协调者收到所有参与者的Ack消息后完成事务。
            相比较2PC而言，3PC对于协调者（Coordinator）和参与者（Partcipant）都设置了超时时间，而2PC只有协调者才拥有超时机制。这解决了一个什么问题呢？这个优化点，主要是避免了参与者在长时间无法与协调者节点通讯（协调者挂掉了）的情况下，无法释放资源的问题，
            因为参与者自身拥有超时机制会在超时后，自动进行本地commit从而进行释放资源。而这种机制也侧面降低了整个事务的阻塞时间和范围。
            另外，通过CanCommit、PreCommit、DoCommit三个阶段的设计，相较于2PC而言，多设置了一个缓冲阶段保证了在最后提交阶段之前各参与节点的状态是一致的。
            以上就是3PC相对于2PC的一个提高（相对缓解了2PC中的前两个问题），但是3PC依然没有完全解决数据不一致的问题。假如在 DoCommit 过程，参与者A无法接收协调者的通信，那么参与者A会自动提交，但是提交失败了，其他参与者成功了，此时数据就会不一致。

    4.TCC方案
         TCC方案分为Try Confirm Cancel三个阶段，属于补偿性分布式事务。
         Try：尝试待执行的业务
            这个过程并未执行业务，只是完成所有业务的一致性检查，并预留好执行所需的全部资源
         Confirm：执行业务
            这个过程真正开始执行业务，由于Try阶段已经完成了一致性检查，因此本过程直接执行，而不做任何检查。并且在执行的过程中，会使用到Try阶段预留的业务资源。
         Cancel：取消执行的业务
            若业务执行失败，则进入Cancel阶段，它会释放所有占用的业务资源，并回滚Confirm阶段执行的操作。

         TCC方案适用于一致性要求极高的系统中，比如金钱交易相关的系统中，不过可以看出，其基于补偿的原理，因此，需要编写大量的补偿事务的代码，比较冗余。不过现有开源的TCC框架，比如TCC-transaction。

    5.本地消息表
        本地消息表分布式事务解决方案是国外的eBay提出的一套方案。
        具体步骤见图本地消息表方式.png
        需要注意的是，该方案中，在A系统中，我们首先写入业务表，然后写入消息表，然后将消息发送到MQ中，在B系统中需要先写入消息表，这是为了保证消息重复消费，为了保证消息消费的幂等性，我们可以使用数据的唯一键来约束。
        当B系统执行成功之后，需要通知A系统执行成功，此时可以使用一个监听器，如Zookeeper，ZK监听到执行成功更新A系统成功。然后开始发送下一条消息。
        A系统中需要有一个后台线程，不断的去判断A系统的状态为待确认的消息，设置超时机制，如果超时，重新发送到MQ中。直到执行成功。
        可以看出，本地消息表方案需要写入消息表中，如果在高并发的场景下会进行大量的磁盘IO，因此该方案不适用于高并发场景。

    6.可靠消息最终一致性方案
        该方案基于本地消息表进行优化，不使用本地消息表，而是基于MQ，比如阿里的RocketMQ就支持消息事务。
        具体步骤见图可靠消息最终一致性方案.png
        在该方案中，首先A系统需要向MQ中发送prepare消息，然后执行A系统的业务，写入数据库成功之后向MQ发送confirm消息，当消息为confirm状态时，B系统就可以消费到消息，消费成功之后返回ACK确认消息给MQ。
        需要注意的是。需要保证B系统消费消息的幂等性，可以借助第三方系统。比如在redis中设置标识，标明已经消费过该消息，或者借助ZK基于分布式锁的原理，创建节点，重复消费消息，创建失败。

    7.最大努力通知方案
        最大努力通知型( Best-effort delivery)是最简单的一种柔性事务，适用于一些最终一致性时间敏感度低的业务，且被动方处理结果 不影响主动方的处理结果。典型的使用场景：如银行通知、商户通知等。
        具体步骤见图最大努力通知方案.png
        在该系统中，A系统执行完本地事务，向MQ发送消息，最大努力通知服务消费消息，比如消息服务，然后调用B系统的接口，执行B系统的本地事务，如果B系统执行成功则OK，否则不断重试，重复多次之后还是失败的话就放弃执行。

    8.Seata https://baijiahao.baidu.com/s?id=1712163514749816361&wfr=spider&for=pc
        8.1 Seata介绍
            Seata 是一款开源的分布式事务解决方案，致力于提供高性能和简单易用的分布式事务服务。Seata 将为用户提供了 AT、TCC、SAGA 和 XA 事务模式，为用户打造一站式的分布式解决方案。
             TC (Transaction Coordinator) - 事务协调者
                维护全局和分支事务的状态，驱动全局事务提交或回滚。
             TM (Transaction Manager) - 事务管理器
                定义全局事务的范围：开始全局事务、提交或回滚全局事务。
             RM (Resource Manager) - 资源管理器
                管理分支事务处理的资源，与TC交谈以注册分支事务和报告分支事务的状态，并驱动分支事务提交或回滚。
        8.2 Seata AT
            Seata 的 AT 模式（Automatic Transaction）是一种无侵入的分布式事务解决方案。
            AT 模式是一种无侵入的分布式事务解决方案。在 AT 模式下，用户只需关注自己的“业务 SQL”，用户的 “业务 SQL” 作为一阶段，Seata 框架会自动生成事务的二阶段提交和回滚操作。
            AT 模式如何做到对业务的无侵入?
                一阶段:
                    在一阶段，Seata 会拦截“业务 SQL”，首先解析 SQL 语义，找到“业务 SQL”要更新的业务数据，在业务数据被更新前，将其保存成“before image”，然后执行“业务 SQL”更新业务数据，在业务数据更新之后，
                    再将其保存成“after image”，最后生成行锁。以上操作全部在一个数据库事务内完成，这样保证了一阶段操作的原子性。
                    把业务数据在更新前后的数据镜像组织成回滚日志，将业务数据的更新和回滚日志在同一个本地事务中提交，分别插入到业务表和 UNDO_LOG 表中
                二阶段提交:
                    二阶段如果是提交的话，因为“业务 SQL”在一阶段已经提交至数据库， 所以 Seata 框架只需将一阶段保存的快照数据和行锁删掉，完成数据清理即可。
                二阶段回滚
                    二阶段如果是回滚的话，Seata 就需要回滚一阶段已经执行的“业务 SQL”，还原业务数据。回滚方式便是用“before image”还原业务数据；但在还原前要首先要校验脏写，对比“数据库当前业务数据”和 “after image”，
                    如果两份数据完全一致就说明没有脏写，可以还原业务数据，如果不一致就说明有脏写，出现脏写就需要转人工处理。
        8.3 SAGA模式
            Saga模式是SEATA提供的长事务解决方案，在Saga模式中，业务流程中每个参与者都提交本地事务，当出现某一个参与者失败则补偿前面已经成功的参与者，一阶段正向服务和二阶段补偿服务都由业务开发实现。
            详见saga模式示意图.webp
             如图：T1-T3都是正向的业务流程，都对应着一个冲正逆向操作C1-C3。
             分布式事务执行过程中，依次执行各参与者的正向操作，如果所有正向操作均执行成功，那么分布式事务提交。如果任何一个正向操作执行失败，那么分布式事务会退回去执行前面各参与者的逆向回滚操作，
            回滚已提交的参与者，使分布式事务回到初始状态。
             Saga 正向服务与补偿服务也需要业务开发者实现。因此是业务入侵的。
             Saga 模式下分布式事务通常是由事件驱动的，各个参与者之间是异步执行的，Saga 模式是一种长事务解决方案。
            Saga 模式使用场景:
                 Saga 模式适用于业务流程长且需要保证事务最终一致性的业务系统，Saga 模式一阶段就会提交本地事务，无锁、长流程情况下可以保证性能。
                 事务参与者可能是其它公司的服务或者是遗留系统的服务，无法进行改造和提供 TCC 要求的接口，可以使用 Saga 模式。
        8.4 总结 AT、TCC、Saga、XA 模式分析
             四种分布式事务模式，分别在不同的时间被提出，每种模式都有它的适用场景：
             AT 模式是无侵入的分布式事务解决方案，适用于不希望对业务进行改造的场景，几乎0学习成本。
             TCC 模式是高性能分布式事务解决方案，适用于核心系统等对性能有很高要求的场景。
             Saga 模式是长事务解决方案，适用于业务流程长且需要保证事务最终一致性的业务系统，Saga 模式一阶段就会提交本地事务，无锁，长流程情况下可以保证性能，多用于渠道层、集成层业务系统。事务参与者可能是其它
            公司的服务或者是遗留系统的服务，无法进行改造和提供 TCC 要求的接口，也可以使用 Saga 模式。
             XA模式是分布式强一致性的解决方案，但性能低而使用较少。
     */

}
