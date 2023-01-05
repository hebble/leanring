package com.learning.study.es;

/**
 * https://blog.csdn.net/a745233700/article/details/115585342 ElasticSearch搜索引擎常见面试题总结
 * https://blog.csdn.net/leveretz/article/details/128012607 es使用教程之_score(评分)介绍
 */
public class ElasticSearchLearning {
    /**
     1.ElasticSearch基础：
        1.1 什么是Elasticsearch：
            Elasticsearch 是基于 Lucene 的 Restful 的分布式实时全文搜索引擎，每个字段都被索引并可被搜索，可以快速存储、搜索、分析海量的数据。
            全文检索是指对每一个词建立一个索引，指明该词在文章中出现的次数和位置。当查询时，根据事先建立的索引进行查找，并将查找的结果反馈给用户的检索方式。这个过程类似于通过字典中的检索字表查字的过程。
        1.2 Elasticsearch 的基本概念：
             （1）index 索引：索引类似于mysql 中的数据库，Elasticesearch 中的索引是存在数据的地方，包含了一堆有相似结构的文档数据。
             （2）type 类型：类型是用来定义数据结构，可以认为是 mysql 中的一张表，type 是 index 中的一个逻辑数据分类
             （3）document 文档：类似于 MySQL 中的一行，不同之处在于 ES 中的每个文档可以有不同的字段，但是对于通用字段应该具有相同的数据类型，文档是es中的最小数据单元，可以认为一个文档就是一条记录。
             （4）Field 字段：Field是Elasticsearch的最小单位，一个document里面有多个field
             （5）shard 分片：单台机器无法存储大量数据，es可以将一个索引中的数据切分为多个shard，分布在多台服务器上存储。有了shard就可以横向扩展，存储更多数据，让搜索和分析等操作分布到多台服务器上去执行，提升吞吐量和性能。
             （6）replica 副本：任何服务器随时可能故障或宕机，此时 shard 可能会丢失，通过创建 replica 副本，可以在 shard 故障时提供备用服务，保证数据不丢失，另外 replica 还可以提升搜索操作的吞吐量。
            shard 分片数量在建立索引时设置，设置后不能修改，默认5个；replica 副本数量默认1个，可随时修改数量；
        1.3 什么是倒排索引：
            在搜索引擎中，每个文档都有对应的文档 ID，文档内容可以表示为一系列关键词的集合，例如，某个文档经过分词，提取了 20 个关键词，而通过倒排索引，可以记录每个关键词在文档中出现的次数和出现位置。
            也就是说，倒排索引是 关键词到文档 ID 的映射，每个关键词都对应着一系列的文件，这些文件中都出现了该关键词。
             要注意倒排索引的两个细节：
                 (1)倒排索引中的所有词项对应一个或多个文档
                 (2)倒排索引中的词项 根据字典顺序升序排列
        1.4 doc_values 的作用：
            倒排索引虽然可以提高搜索性能，但也存在缺陷，比如我们需要对数据做排序或聚合等操作时，lucene 会提取所有出现在文档集合的排序字段，然后构建一个排好序的文档集合，而这个步骤是基于内存的，如果排序数据量巨大的话，容易造成内存溢出和性能缓慢。
            doc_values 就是 es 在构建倒排索引的同时，会对开启 doc_values 的字段构建一个有序的 “document文档 ==> field value” 的列式存储映射，可以看作是以文档维度，实现了根据指定字段进行排序和聚合的功能，降低对内存的依赖。另外 doc_values
            保存在操作系统的磁盘中，当 doc_values 大于节点的可用内存，ES可以从操作系统页缓存中加载或弹出，从而避免发生内存溢出的异常，但如果 docValues 远小于节点的可用内存，操作系统就自然将所有 doc_values 存于内存中（堆外内存），有助于快速访问。
        1.5 text 和 keyword类型的区别：
            两个类型的区别主要是分词：keyword 类型是不会分词的，直接根据字符串内容建立倒排索引，所以keyword类型的字段只能通过精确值搜索到；Text 类型在存入 Elasticsearch 的时候，会先分词，然后根据分词后的内容建立倒排索引
        1.6 query 和 filter 的区别？
             （1）query：查询操作不仅仅会进行查询，还会计算分值，用于确定相关度；
             （2）filter：查询操作仅判断是否满足查询条件，不会计算任何分值，也不会关心返回的排序问题，同时，filter 查询的结果可以被缓存，提高性能。

     2.常用的存储mapping配置项 与 doc_values详细介绍
        2.1 ES的数据存储结构：
            ES底层使用 Lucene 存储数据，Lucene 的索引包含以下部分：
                (1)inverted index：倒排索引。
                (2)bkd tree: Block k-d tree，用于在高维空间内做索引，如地理坐标的索引。
                (3)column store：doc values，列式存储，批量读取连续的数据以提高排序和聚合的效率。
                (4)document store：Store Fileds，行式存储文档，用于控制 doc 原始数据的存储，其中占比最大的是 source 字段。
                (5)term vectors：用于存储各个词在文档中出现的位置等信息。
        2.2 ES常见配置项说明：
            在很多场合下，我们并不需要存储上述全部信息，因此可以通过设置 mappings 里面的属性来控制哪些字段是我们需要存储的、哪些是不需要存储的。而 ES 的 mapping 中有很多设置选项，
            这些选项如果设置不当，有的可能浪费存储空间，有的可能导致无法使用 Aggregation，有的可能导致不能检索。下面就简单介绍下 ES 中常见的存储与检索的 mapping 配置项：
                 (1) _all 提供跨字段全文检索 默认关闭
                     a.会占用额外空间，把 mapping 中的所有字段通过空格拼接起来做索引，在跨字段全文检索才需要打开；
                     b.在 v6.0+已被弃用，v7.0会正式移除，可以使用 [copy_to] 来自定义组合字段
                 (2) _source	存储 post 提交到ES的原始 json 内容 默认开启
                     a.会占用很多存储空间。数据压缩存储，读取会有额外解压开销。
                     b.不需要读取原始字段内容可以考虑关闭，但关闭后无法 reindex
                 (3) store	是否单独存储该字段 默认关闭
                    会占用额外存储空间，与 source 独立，同时开启 store 和 source 则会将该字段原始内容保存两份，不同字段单独存储，不同字段的数据在磁盘上不连续，若读取多个字段则需要查询多次，
                    如需读取多个字段，需权衡比较 source 与 store 效率
                 (4) doc_values	支持排序、聚合 默认开启
                    会占用额外存储空间，与 source 独立，同时开启 doc_values 和 _source 则会将该字段原始内容保存两份。doc_values 数据在磁盘上采用列式存储，关闭后无法使用排序和聚合
                 (5) index	是否加入倒排索引	默认开启
                    关闭后无法对其进行搜索，但字段仍会存储到 _source 和 doc_values，字段可以被排序和聚合	开启
                (6)enabled	是否对该字段进行处理	默认开启
                    关闭后，只在 _source中存储，类似 index 与 doc_values 的总开关
            在ES的 mapping 设置里，all，source 是 mapping 的元数据字段（Meta-Fields），store、doc_values、enabled、index 是 mapping 参数。
        2.3 _all：(在 v6.0+已被弃用，v7.0会正式移除，可以使用 [copy_to] 来自定义组合字段)
             PUT mytest/_mapping
             {
                 "properties": {
                     "e":{
                     "copy_to": ["a", "b"],
                     "type":"keyword"
                     }
                 }
             }
            all 字段的作用是提供跨字段查询的支持，把 mapping 中的所有字段通过空格拼接起来做索引。ES在查询的过程中，需要指定在哪一个field里面查询。
                 {
                     “name”: “smith”,
                    “email”: "John@example.com"
                 }
             用户在查询时，想查询叫做 John 的人，但不知道 John 出现在 name 字段中还是在 email 字段中，由于ES是为每一个字段单独建立索引，所以用户需要以 John 为关键词发起两次查询，分别查询name字段和email字段。
             如果开启了 all 字段，则ES会在索引过程中创建一个虚拟的字段 all，其值为文档中各个字段拼接起来所组成的一个很长的字符串（例如上面的例子，all 字段的内容为字符串 “smith John@example.com”）。
            随后，该字段将被分词打散，与其他字段一样被收入倒排索引中。由于 all 字段包含了所有字段的信息，因此可以实现跨字段的查询，用户不用关心要查询的关键词在哪个字段中。
             由于该字段的内容都来自 source 字段，因此默认情况下，该字段的内容并不会被保存，可以通过设置 store 属性来强制保存 all 字段。开启 all 字段，会带来额外的CPU开销和存储，如果没有使用到，可以关闭 all 字段。
        2.4 _source：
             source 字段用于存储 post 到 ES 的原始 json 文档。为什么要存储原始文档呢？因为 ES 采用倒排索引对文本进行搜索，而倒排索引无法存储原始输入文本。一段文本交给ES后，首先会被分析器(analyzer)打散成单词，
            为了保证搜索的准确性，在打散的过程中，会去除文本中的标点符号，统一文本的大小写，甚至对于英文等主流语言，会把发生形式变化的单词恢复成原型或词根，然后再根据统一规整之后的单词建立倒排索引，经过如此一番
            处理，原文已经面目全非。因此需要有一个地方来存储原始的信息，以便在搜到这个文档时能够把原文返回给查询者。
             那么一定要存储原始文档吗？不一定！如果没有取出整个原始 json 结构体的需求，可以在 mapping 中关闭 source 字段或者只在 source 中存储部分字段（使用store）。 但是这样做有些负面影响：
                 （1）不能获取到原文
                 （2）无法reindex：如果存储了 source，当 index 发生损坏，或需要改变 mapping 结构时，由于存在原始数据，ES可以通过原始数据自动重建index，如果不存 source 则无法实现
                 （3）无法在查询中使用script：因为 script 需要访问 source 中的字段
        2.5 store：
            store 决定一个字段是否要被单独存储。大家可能会有疑问，source 里面不是已经存储了原始的文档嘛，为什么还需要一个额外的 store 属性呢？原因如下：
             （1）如果禁用了 source 保存，可以通过指定 store 属性来单独保存某个或某几个字段，而不是将整个输入文档保存到 source 中。
             （2）如果 source 中有长度很长的文本（如一篇文章）和较短的文本（如文章标题），当只需要取出标题时，如果使用 source 字段，ES需要读取整个 source 字段，然后返回其中的 title，由此会引来额外的IO开销，降低效率。
             此时可以选择将 title 的 store 设置为true，在 source 字段外单独存储一份。读取时不必在读取整 source 字段了。但是需要注意，应该避免使用 store 查询多个字段，因为 store 的存储在磁盘上不连续，ES在读取不同的
             store 字段时，每个字段的读取均需要在磁盘上进行查询操作，而使用 source 字段可以一次性连续读取多个字段。
        2.6 doc_values：
            倒排索引可以提供全文检索能力，但是无法提供对排序和数据聚合的支持。doc_values 本质上是一个序列化的列式存储结构，适用于聚合（aggregations）、排序（Sorting）、脚本（scripts access to field）等操作。
            默认情况下，ES几乎会为所有类型的字段存储doc_value，但是 text 或 text_annotated 等可分词字段不支持 doc values 。如果不需要对某个字段进行排序或者聚合，则可以关闭该字段的doc_value存储。
        2.7 index：
            控制倒排索引，用于标识指定字段是否需要被索引。默认情况下是开启的，如果关闭了 index，则该字段的内容不会被 analyze 分词，也不会存入倒排索引，即意味着该字段无法被搜索。
        2.8 enabled：
             这是一个 index 和 doc_value 的总开关，如果 enabled 设置为false，则这个字段将会仅存在于 source 中，其对应的 index 和 doc_value 都不会被创建。
             这意味着，该字段将不可以被搜索、排序或者聚合，但可以通过 source 获取其原始值。
        2.9 term_vector：
            在对文本进行 analyze 的过程中，可以保留有关分词结果的相关信息，包括单词列表、单词之间的先后顺序、单词在原文中的位置等信息。查询结果返回的高亮信息就可以利用其中的数据来返回。
            默认情况下，term_vector是关闭的，如有需要（如加速highlight结果）可以开启该字段的存储。
         PUT mytest2
         {
             "mappings": {
                 "properties": {
                     "a":{
                         "index": true,
                         "store": true,
                         "doc_values": true,
                         "term_vector": "no",
                         "copy_to": ["b", "c"]
                     }
                 },
                 "_source": {
                    "enabled": true
                 },
                 "_all": {
                    "enabled": true
                 }
             }
         }

     3.ES的写入流程
        3.1 ES写数据的整体流程：
            见图es写数据整体流程.png
             （1）客户端选择 ES 的某个 node 发送请求过去，这个 node 就是协调节点 coordinating node
             （2）coordinating node 对 document 进行路由，将请求转发给对应的 node（有 primary shard）
             （3）实际的 node 上的 primary shard 处理请求，然后将数据同步到 replica node
             （4）coordinating node 等到 primary node 和所有 replica node 都执行成功之后，最后返回响应结果给客户端。
        3.2 ES主分片写数据的详细流程：
            见图ES主分片写数据的详细流程
             （1）主分片先将数据写入ES的 memory buffer，然后定时（默认1s）将 memory buffer 中的数据写入一个新的 segment 文件中，并进入操作系统缓存 Filesystem cache（同时清空 memory buffer），
                这个过程就叫做 refresh；每个 segment 文件实际上是一些倒排索引的集合， 只有经历了 refresh 操作之后，这些数据才能变成可检索的。
                ES 的近实时性：数据存在 memory buffer 时是搜索不到的，只有数据被 refresh 到  Filesystem cache 之后才能被搜索到，而 refresh 是每秒一次， 所以称 es 是近实时的；
                可以手动调用 es 的 api 触发一次 refresh 操作，让数据马上可以被搜索到；
             （2）由于 memory Buffer 和 Filesystem Cache 都是基于内存，假设服务器宕机，那么数据就会丢失，所以 ES 通过 translog 日志文件来保证数据的可靠性，在数据写入 memory buffer 的同时，
                将数据也写入 translog 日志文件中，当机器宕机重启时，es 会自动读取 translog 日志文件中的数据，恢复到 memory buffer 和 Filesystem cache 中去。
                ES 数据丢失的问题：translog 也是先写入 Filesystem cache，然后默认每隔 5 秒刷一次到磁盘中，所以默认情况下，可能有 5 秒的数据会仅仅停留在 memory buffer 或者 translog 文件的
                Filesystem cache中，而不在磁盘上，如果此时机器宕机，会丢失 5 秒钟的数据。也可以将 translog 设置成每次写操作必须是直接 fsync 到磁盘，但是性能会差很多。
             （3）flush 操作：不断重复上面的步骤，translog 会变得越来越大，不过 translog 文件默认每30分钟或者 阈值超过 512M 时，就会触发 commit 操作，即 flush操作，将 memory buffer 中所有的数据写入
                新的 segment 文件中， 并将内存中所有的 segment 文件全部落盘，最后清空 translog 事务日志。
                     ① 将 memory buffer 中的数据 refresh 到 Filesystem Cache 中去，清空 buffer；
                     ② 创建一个新的 commit point（提交点），同时强行将 Filesystem Cache 中目前所有的数据都 fsync 到磁盘文件中；
                     ③ 删除旧的 translog 日志文件并创建一个新的 translog 日志文件，此时 commit 操作完成
        3.3 ES的更新和删除流程
             删除和更新都是写操作，但是由于 Elasticsearch 中的文档是不可变的，因此不能被删除或者改动以展示其变更；所以 ES 利用 .del 文件 标记文档是否被删除，磁盘上的每个段都有一个相应的.del 文件
             （1）如果是删除操作，文档其实并没有真的被删除，而是在 .del 文件中被标记为 deleted 状态。该文档依然能匹配查询，但是会在结果中被过滤掉。
             （2）如果是更新操作，就是将旧的 doc 标识为 deleted 状态，然后创建一个新的 doc。
            memory buffer 每 refresh 一次，就会产生一个 segment 文件 ，所以默认情况下是 1s 生成一个 segment 文件，这样下来 segment 文件会越来越多，此时会定期执行 merge。每次 merge 的时候，会将多个
            segment 文件合并成一个，同时这里会将标识为 deleted 的 doc 给物理删除掉，不写入到新的 segment 中，然后将新的 segment 文件写入磁盘，这里会写一个 commit point ，标识所有新的 segment 文件，
            然后打开 segment 文件供搜索使用，同时删除旧的 segment 文件

     4.ES的搜索流程：
        搜索被执行成一个两阶段过程，即 Query Then Fetch：
         4.1 Query阶段：
            客户端发送请求到 coordinate node，协调节点将搜索请求广播到所有的 primary shard 或 replica，每个分片在本地执行搜索并构建一个匹配文档的大小为 from + size(比如from=50,size=10, 那么大小为60) 的优先队列。接着每个分片返回各自
            优先队列中 所有 docId 和 打分值 给协调节点，由协调节点进行数据的合并、排序、分页等操作，产出最终结果。
        4.2 Fetch阶段:
            协调节点根据 Query阶段产生的结果，去各个节点上查询 docId 实际的 document 内容，最后由协调节点返回结果给客户端。
                 coordinate node 对 doc id 进行哈希路由，将请求转发到对应的 node，此时会使用 round-robin 随机轮询算法，在 primary shard 以及其所有 replica 中随机选择一个，让读请求负载均衡。
                 接收请求的 node 返回 document 给 coordinate node 。
                 coordinate node 返回 document 给客户端。
            Query Then Fetch 的搜索类型在文档相关性打分的时候参考的是本分片的数据，这样在文档数量较少的时候可能不够准确，DFS Query Then Fetch 增加了一个预查询的处理，询问 Term 和 Document frequency，这个评分更准确，但是性能会变差。

     5.ES在高并发下如何保证读写一致性？
         （1）对于更新操作：可以通过版本号使用乐观并发控制，以确保新版本不会被旧版本覆盖
             每个文档都有一个_version 版本号，这个版本号在文档被改变时加一。Elasticsearch使用这个 _version 保证所有修改都被正确排序，当一个旧版本出现在新版本之后，它会被简单的忽略。
             利用_version的这一优点确保数据不会因为修改冲突而丢失，比如指定文档的version来做更改，如果那个版本号不是现在的，我们的请求就失败了。
        （2）对于写操作，一致性级别支持 quorum/one/all，默认为 quorum，即只有当大多数分片可用时才允许写操作。但即使大多数可用，也可能存在因为网络等原因导致写入副本失败，这样该副本被认为故障，副本将会在一个不同的节点上重建。
             one：写操作只要有一个primary shard是active活跃可用的，就可以执行
             all：写操作必须所有的primary shard和replica shard都是活跃可用的，才可以执行
             quorum：默认值，要求ES中大部分的shard是活跃可用的，才可以执行写操作
        （3）对于读操作，可以设置 replication 为 sync(默认)，这使得操作在主分片和副本分片都完成后才会返回；如果设置replication 为 async 时，也可以通过设置搜索请求参数 _preference 为 primary 来查询主分片，确保文档是最新版本。

     6.ES集群如何选举Master节点：
        6.1 Elasticsearch 的分布式原理：
            Elasticsearch 会对存储的数据进行切分，划分到不同的分片上，同时每一个分片会生成多个副本，从而保证分布式环境的高可用。ES集群中的节点是对等的，节点间会选出集群的 Master，由 Master 会负责维护集群状态信息，并同步给其他节点。
            Elasticsearch 的性能会不会很低：不会，ES只有建立 index 和 type 时需要经过 Master，而数据的写入有一个简单的 Routing 规则，可以路由到集群中的任意节点，所以数据写入压力是分散在整个集群的。
        6.2 ES集群 如何 选举 Master：
            Elasticsearch 的选主是 ZenDiscovery 模块负责的，主要包含Ping（节点之间通过这个RPC来发现彼此）和 Unicast（单播模块包含一个主机列表以控制哪些节点需要ping通）这两部分；
             （1）确认候选主节点的最少投票通过数量（elasticsearch.yml 设置的值 discovery.zen.minimum_master_nodes）
             （2）选举时，集群中每个节点对所有 master候选节点（node.master: true）根据 nodeId 进行字典排序，然后选出第一个节点（第0位），暂且认为它是master节点。
             （3）如果对某个节点的投票数达到阈值，并且该节点自己也选举自己，那这个节点就是master；否则重新选举一直到满足上述条件。
            补充：master节点的职责主要包括集群、节点和索引的管理，不负责文档级别的管理；data节点可以关闭http功能。
        6.3 Elasticsearch是如何避免脑裂现象：
             （1）当集群中 master 候选节点数量不小于3个时（node.master: true），可以通过设置最少投票通过数量（discovery.zen.minimum_master_nodes），设置超过所有候选节点一半以上来解决脑裂问题，即设置为 (N/2)+1；
             （2）当集群 master 候选节点 只有两个时，这种情况是不合理的，最好把另外一个node.master改成false。如果我们不改节点设置，还是套上面的(N/2)+1公式，此时discovery.zen.minimum_master_nodes应该设置为2。这就出现一个问题，两个master备选节点，只要有一个挂，就选不出master了

     7.建立索引阶段性能提升方法：
         （1）如果是大批量导入，可以设置 index.number_of_replicas: 0 关闭副本，等数据导入完成之后再开启副本
         （2）使用批量请求并调整其大小：每次批量数据 5–15 MB 大是个不错的起始点。
         （3）如果搜索结果不需要近实时性，可以把每个索引的 index.refresh_interval 改到30s
         （4）增加 index.translog.flush_threshold_size 设置，从默认的 512 MB 到更大一些的值，比如 1 GB
         （5）使用 SSD 存储介质
         （6）段和合并：Elasticsearch 默认值是 20 MB/s。但如果用的是 SSD，可以考虑提高到 100–200 MB/s。如果你在做批量导入，完全不在意搜索，你可以彻底关掉合并限流。

     8.ES的深度分页与滚动搜索scroll
         （1）深度分页：
             深度分页其实就是搜索的深浅度，比如第1页，第2页，第10页，第20页，是比较浅的；第10000页，第20000页就是很深了。搜索得太深，就会造成性能问题，会耗费内存和占用cpu。而且es为了性能，
             他不支持超过一万条数据以上的分页查询。那么如何解决深度分页带来的问题，我们应该避免深度分页操作（限制分页页数），比如最多只能提供100页的展示，从第101页开始就没了，毕竟用户也不会搜的那么深。
         （2）滚动搜索：
             一次性查询1万+数据，往往会造成性能影响，因为数据量太多了。这个时候可以使用滚动搜索，也就是 scroll。 滚动搜索可以先查询出一些数据，然后再紧接着依次往下查询。在第一次查询的时候
             会有一个滚动id，相当于一个锚标记 ，随后再次滚动搜索会需要上一次搜索滚动id，根据这个进行下一次的搜索请求。每次搜索都是基于一个历史的数据快照，查询数据的期间，如果有数据变更，那么和搜索是没有关系的。

     9.倒排索引的数据结构
        倒排索引的底层实现是基于：FST（Finite State Transducer）数据结构。
             lucene从4+版本后开始大量使用的数据结构是FST。FST有两个优点：
             1、 空间占用小。通过对词典中单词前缀和后缀的重复利用，压缩了存储空间；
             2、 查询速度快。O(len(str))的查询时间复杂度。

     10._score(评分)介绍 https://blog.csdn.net/leveretz/article/details/128012607
        使用ES时，对于查询出的文档无疑会有文档相似度之别。而理想的排序是和查询条件相关性越高排序越靠前，而这个排序的依据就是_score
        10.1 词频/逆向文档频率（TF/IDF）
            当匹配到一组文档后，需要根据相关度排序这些文档，不是所有的文档都包含所有词，有些词比其他的词更重要。一个文档的相关度评分部分取决于每个查询词在文档中的 权重 。
             (1)字段长度准则：这个准则很简单，字段内容的长度越长，相关性越低。我们在上面的两个例子中都能看到，同样包含了“He is”这个关键字，但是”He is passionate.”的相关性评分高于”He is a big data engineer.”，这就是因为字段长度准则影响了它们的相关性评分；
             (2)检索词频率准则：检索关键字出现频率越高，相关性也越高。这个例子中没有比较明显的体现出来，你可以自己试验一下；
             (3)逆向文档频率准则：每个检索关键字在Index中出现的频率越高，相关性越低。

            10.1.1 词频
                词在文档中出现的频度是多少？ 频度越高，权重 越高 。 5 次提到同一词的字段比只提到 1 次的更相关。词频的计算方式如下：
                    tf(t in d) = √frequency      词 t 在文档 d 的词频（ tf ）是该词在文档中出现次数的平方根。
                如果不在意词在某个字段中出现的频次，而只在意是否出现过，则可以在字段映射中禁用词频统计：
                 PUT /my_index
                 {
                     "mappings": {
                         "doc": {
                             "properties": {
                                 "text": {
                                     "type": "string",
                                     "index_options": "docs"
                                 }
                             }
                         }
                     }
                 }
                将参数 index_options 设置为 docs 可以禁用词频统计及词频位置，这个映射的字段不会计算词的出现次数，对于短语或近似查询也不可用。要求精确查询的 not_analyzed 字符串字段会默认使用该设置。
            10.1.2 逆向文档频率
                词在集合所有文档里出现的频率是多少？频次越高，权重 越低 。 常用词如 and 或 the 对相关度贡献很少，因为它们在多数文档中都会出现，一些不常见词如 elastic 或 hippopotamus 可以帮助我们
                快速缩小范围找到感兴趣的文档。逆向文档频率的计算公式如下：
                    idf(t) = 1 + log ( numDocs / (docFreq + 1))  词 t 的逆向文档频率（ idf ）是：索引中文档数量除以所有包含该词的文档数，然后求其对数
            10.1.3 文档长度归一值
                字段的长度是多少？ 字段越短，字段的权重 越高 。如果词出现在类似标题 title 这样的字段，要比它出现在内容 body 这样的字段中的相关度更高。字段长度的归一值公式如下：
                    norm(d) = 1 / √numTerms  字段长度归一值（ norm ）是字段中词数平方根的倒数
                字段长度的归一值对全文搜索非常重要， 许多其他字段不需要有归一值。无论文档是否包括这个字段，索引中每个文档的每个 string 字段都大约占用 1 个 byte 的空间。对于 not_analyzed 字符串字段的归一值默认是禁用的，而对于 analyzed 字段也可以通过修改字段映射禁用归一值：
                     PUT /my_index
                         {
                             "mappings": {
                                 "doc": {
                                     "properties": {
                                         "text": {
                                             "type": "string",
                                             "norms": { "enabled": false }
                                     }
                                 }
                             }
                         }
                     }
                对于有些应用场景如日志，归一值不是很有用，要关心的只是字段是否包含特殊的错误码或者特定的浏览器唯一标识符。字段的长度对结果没有影响，禁用归一值可以节省大量内存空间。
        10.2 文档评分计算
            转自官方文档 Lucene的使用评分函数
            评分计算公式
                score(q,d)  =
                    queryNorm(q)            //归一化因子
                    · coord(q,d)              //协调因子
                    · ∑ (
                         tf(t in d)          //词频
                         · idf(t)²             //逆向文档频率
                         · t.getBoost()        //权重
                         · norm(t,d)           //字段长度归一值
                    ) (t in q)
            下面简要介绍公式中新提及的三个参数，具体信息可以点击上方官方文档原文：
                 queryNorm 查询归化因子：
                    会被应用到每个文档，不能被更改，总而言之，可以被忽略。
                 coord 协调因子：
                    可以为那些查询词包含度高的文档提供奖励，文档里出现的查询词越多，它越有机会成为好的匹配结果。
                    协调因子将评分与文档里匹配词的数量相乘，然后除以查询里所有词的数量，如果使用协调因子，评分会变成：
                         文档里有 fox → 评分： 1.5 * 1 / 3 = 0.5
                         文档里有 quick fox → 评分： 3.0 * 2 / 3 = 2.0
                         文档里有 quick brown fox → 评分： 4.5 * 3 / 3 = 4.5
                         协调因子能使包含所有三个词的文档比只包含两个词的文档评分要高出很多。
                 Boost 权重：
                    在查询中设置关键字的权重可以灵活的找到更匹配的文档。
        10.3 实例测试
             // 准备
             PUT {{host}}:{{port}}/demo
             {
                 "mappings":{
                     "article":{
                         "properties":{
                             "content":{
                             "type":"text"
                             }
                         }
                     }
                 }
             }
             //导入数据
             [
                 {
                    "content": "测试语句1"
                 },
                 {
                    "content": "测试语句2"
                 },
                 {
                    "content": "测试语句3，字段长度不同"
                 }
             ]
             //查询
             POST {{host}}:{{port}}/demo/article/_search
             {
                 "query":{
                     "match":{
                        "content":"测"
                     }
                 }
             }
             //测试结果：
             {
                 "took": 0,
                 "timed_out": false,
                 "_shards": {
                     "total": 5,
                     "successful": 5,
                     "skipped": 0,
                     "failed": 0
                },
                 "hits": {
                     "total": 3,
                     "max_score": 0.2824934,
                     "hits": [
                         {
                             "_index": "demo",
                             "_type": "article",
                             "_id": "AWEIQ90700f4t28Wzjdj",
                             "_score": 0.2824934,
                             "_source": {
                                "content": "测试语句2"
                             }
                         },
                         {
                             "_index": "demo",
                             "_type": "article",
                             "_id": "AWEIQ71f00f4t28WzjZT",
                             "_score": 0.21247853,
                             "_source": {
                                "content": "测试语句1"
                             }
                         },
                         {
                             "_index": "demo",
                             "_type": "article",
                             "_id": "AWEIRAEw00f4t28Wzjkd",
                             "_score": 0.1293895,
                             "_source": {
                                "content": "测试语句3，字段长度不同"
                             }
                         }
                     ]
                 }
             }
            奇怪的是，按照语句1和语句2的分数居然不同！因为他们两个文档的关键参数，词频，字段长度，逆向文档频率均相同，为什么算出来的分不同呢？
            原因主要是因为 每个分片会根据 该分片内的所有文档计算一个本地 IDF 。而文档落在不同的分片就会导致逆向文档频率不同，算出来的分数也不同。
            参见官网 被破坏的相关度
            当文档数量比较大，分片分布均匀后，这个问题基本不会影响很大。那么在我们这个demo中使用添加 ?search_type=dfs_query_then_fetch来查询所有的idf。
             POST {{host}}:{{port}}/demo/article/_search?search_type=dfs_query_then_fetch
             {
                 "query":{
                     "match":{
                        "content":"测"
                     }
                 }
             }
        10.4 其他更改评分的方法
            由于其他几个方法官网介绍的比较详尽，所以这里就不多做介绍，直接贴上官网链接。而使用脚本评分，官网介绍有些细节不够完善，因此在此多加介绍：
                 按受欢迎度提升权重
                 过滤集提升权重
                 随机评分
                 越近越好
                 脚本评分 ()
            脚本评分主要应用在提供的评分满足不了需求，需要通过脚本自定义评分标准。比如虽然提供了前缀分词，但是前缀分词后，返回匹配的结果评分都是1，无法进一步区分。而我们可以通过脚本在使用tf/idf得出分数后，再加上前缀匹配后的额外分值，达到搜索和前缀匹配的目的。
             那么在增加一组数据
                 {
                    "content":"语句测试4"
                 }
            从测试结果中看到，虽然语句4顺序不同，但是根据评分算法，依旧还是同分。
            //添加脚本评分, 将前缀匹配得分为1, 否则为0 , 分数相加
                 POST {{host}}:{{port}}/demo/article/_search?search_type=dfs_query_then_fetch
                 {
                     "query": {
                         "function_score": {
                             "query": {
                                 "match": {
                                    "content": "测"
                                  }
                             },
                             "script_score": {
                                 "script": {
                                     "lang": "painless",
                                     "source": "if(doc['content.keyword'].value.startsWith(params.keyword))return 1; return 0;",                 //此处更改为content.keyword
                                     "params":{ "keyword":"测" }
                                }
                             },
                             "boost_mode": "sum" //score_mode计算functions中的分数形式，加减乘除，boost_mode计算最外层的分数形式，加减乘除。所以最后总分是tf/idf分数加上脚本得分。
                         }
                     }
                 }
        10.5 painless
             painless是一种新支持的脚本语言，语言格式和java十分类似。可以参考以下文档：
                 painless语言介绍
                 painless api
                 painless 实例
            score_mode计算functions中的分数形式，加减乘除，boost_mode计算最外层的分数形式，加减乘除。所以最后总分是tf/idf分数加上脚本得分。

     11.match VS match_phrase
        比如查询 He is
            对于match的结果，我们可以可以看到，结果的Document中description这个field可以包含“He is”，“He”或者“is”
            match_phrased的结果中的description字段，必须包含“He is”这一个词组

     12.ES优化 https://baijiahao.baidu.com/s?id=1730939254719695179&wfr=spider&for=pc
        ES性能优化是没有什么捷径的，我们不要期待着随手调一个参数，就可以万能的应对所有的性能慢的场景。也许有的场景是你换个参数，或者调整一下语法，就可以搞定，但是绝对不是所有场景都可以这样。往ES里写的数据，实际上都写到磁盘文件里去了，
        磁盘文件里的数据操作系统会自动将里面的数据缓存到os cache里面去，ES的搜索引擎严重依赖于底层的filesystem cache，你如果给filesystem cache更多的内存，尽量让内存可以容纳所有的indx segment file索引数据文件，那么你搜索的时候就基
        本都是走内存的，性能会非常高。性能差距可以有很大，基于我们做的200万和2000万数据量的测试和压测，如果走磁盘一般肯定上秒，搜索性能绝对是秒级别的，1秒，5秒，10秒。但是如果是走filesystem cache，是走纯内存的，那么一般来说性能比走
        磁盘要高一个数量级，基本上就是毫秒级的，从几毫秒到几百毫秒不等。
        ES内存有个算法可以帮助我们更好的理解文件缓存，例如ES节点有3台机器，每台机器，看起来内存很多，64G，总内存，64 * 3 = 192g，每台机器给es jvm heap是32G，那么剩下来留给filesystem cache的就是每台机器才32g，总共集群里给filesystem
        cache的就是32 * 3 = 96g内存，如果你此时，你整个，磁盘上索引数据文件，在3台机器上，一共占用了1T的磁盘容量，你的es数据量是1t，每台机器的数据量是300g，你觉得你的性能能好吗？filesystem cache的内存才100g，十分之一的数据可以放内存，
        其他的都在磁盘，然后你执行搜索操作，大部分操作都是走磁盘，性能肯定差。
        归根结底，你要让es性能要好，最佳的情况下，就是你的机器的内存，至少可以容纳你的总数据量的一半。 最佳的情况下，我们自己的生产环境实践经验，所以说我们当时的策略，是仅仅在es中就存少量的数据，就是你要用来搜索的那些索引，内存留给filesystem
        cache的，就100G，那么你就控制在100gb以内，相当于是，你的数据几乎全部走内存来搜索，性能非常之高，一般可以在1秒以内。

        12.1 常规优化
             (1)不要返回数据量非常大的结果集
             (2)避免出现大文档，即单条索引记录的体积不要过大
             (3)默认情况下， http.max_content_length 设置为100mb，限制单个索引记录的大小不能超过100mb。
             (4)使用更高性能的查询API。比如多用filter少用query。
             (5)使用足够的最小数字类型有利于节约磁盘空间: 数字数据选择的字段类型会对磁盘使用产生重大影响。 特别是，整数应使用整数类型（字节、短整型、整数或长整型）存储，浮点数应存储在 scaled_float 中（如果合适）或适合用例的最小类型：使用
                float over double，或 half_float over float 将有助于节省存储空间。
             (6)document模型设计：es里面的复杂的关联查询，复杂的查询语法，尽量别用，一旦用了性能一般都不太好，搜索尽可能少的字段。document模型设计是非常重要的，很多操作，不要在搜索的时候才想去执行各种复杂的乱七八糟的操作。es能支持的操作
                就是那么多，不要考虑用es做一些它不好操作的事情。如果真的有那种操作，尽量在document模型设计的时候，写入的时候就完成。另外对于一些太复杂的操作，比如join，nested，parent-child搜索都要尽量避免，性能都很差的。很多复杂的乱七
                八糟的一些操作，如何执行，在写入数据的时候，就设计好模型，加几个字段，把处理好的数据写入加的字段里面，自己用java程序封装，es能做的，用es来做，搜索出来的数据，在java程序里面去做，比如说我们，基于es，用java封装一些特别复杂的操作
        12.2 分片优化
             Elasticsearch中的数据组织成索引。每一个索引由一个或多个分片组成。每个分片是Luncene索引的一个实例，你可以把实例理解成自管理的搜索引擎，用于在Elasticsearch集群中对一部分数据进行索引和处理查询。当数据写入分片时，它会定期地发布到磁盘
            上的新的不可变的Lucene段中，此时它可用于查询。随着分段数（segment）的增长，这些segment被定期地整合到较大的segments。
             当索引越来越大，单个 shard 也很巨大，查询速度也越来越慢。这时候，是选择分索引还是更多的shards？更多的 shards 会带来额外的索引压力，即 IO 压力。我们应尽量选择分索引。比如按照每个大分类一个索引，或者主要的大城市一个索引。然后将他们进行合并查询。
            分片是Elasticsearch在集群周围分发数据的单位。Elasticsearch在重新平衡数据时 （例如 发生故障后） 移动分片的速度取决于分片的大小和数量以及网络和磁盘性能。在优化过程中，应避免有非常大的分片，因为大的分片可能会对集群从故障中恢复的能力产生负面影响。
            对于多大的分片没有固定的限制，但是分片大小为50GB通常被界定为适用于各种用例的限制。
        12.3 索引优化
            索引速度提高与否？主要是看瓶颈在什么地方，若是 Read DB（产生DOC）的速度比较慢，那瓶颈不在 ElasticSearch 时，优化就没那么大的动力。实际上 Elasticsearch 的索引速度还是非常快的。
            若是 Elasticsearch 普通索引IO 压力已经见顶，这时候bulk也无法提供帮助，SSD 应该是很好的选择。在 create doc 速度能跟上的时候，bulk是可以提高速度的。
            (1)SSD选择：
                SSD是经济压力能承受情况下的不二选择。减少碎片也可以提高索引速度，每天进行优化还是很有必要的。
            (2)Replica设置：
                在初次索引的时候，把 replica 设置为 0，也能提高索引速度，一般我们在进行大量数据的同步任务和加载的时候，可以先设置index.refresh_interval=-1，index.number_of_replicas=0，关闭自动刷新并将索引的副本数设置为0。待完成数据同步后，再调整回正常值。
                另外设置正确的副本数能够提高吞吐量，正确的副本数量是多少？如果您的集群有 num_nodes 个节点、num_primaries 主分片，并且您希望最多同时处理 max_failures 个节点故障，那么适合您的副本数为 max(max_failures, ceil(num_nodes / num_primaries) - 1)。
            (3)足够的内存：
                分配足够的内存给文件系统缓存，文件系统缓存将用于缓冲 I/O 操作。应该确保将运行 Elasticsearch 的计算机的至少一半内存提供给文件系统缓存
            (4)使用自动生成ID：
                在索引具有显式 id 的文档时，Elasticsearch 需要检查具有相同 id 的文档是否已存在于同一个分片中，这是一项代价高昂的操作，并且随着索引的增长而变得更加昂贵。通过使用自动生成的 id，Elasticsearch 可以跳过此检查，从而加快索引速度
            (5)索引缓冲区：
                索引缓冲大小index_buffer_size，索引缓冲区index.memory.index_buffer_size默认值是 10%，例如，如果 JVM 10GB 的内存，它将为索引缓冲区提供 1GB。
            (6)使用跨集群复制，避免争抢资源
            (7)禁用你不需要的特性：
                不作为查询条件的属性，可以添加在mapping中声明："index": false；text类型字段如果你只匹配而不关注匹配的分数，可以将类型声明为match_only_text，此字段类型通过删除评分和位置信息来节省大量空间。
            (8)不要使用默认的动态字符串映射:
                默认的动态字符串映射会将字符串字段同时作为text和keyword进行索引。 如果您只需要其中之一，这是一种浪费。 通常，id 字段只需要作为关键字索引，而 body 字段只需要作为文本字段索引。
            (9)禁用 _source: _source:
                字段存储文档的原始 JSON 正文。 如果您不需要访问它，您可以禁用它。 但是，需要访问 _source 的 API（例如 update 和 reindex）将不起作用。
            (10)压缩器best_compression:
                _source 和 stored 字段可以很容易地占用不可忽略的磁盘空间量。 可以使用 best_compression 编解码器更积极地压缩它们。
        12.4 查询优化
            (1)Routing关键：
                查询关键是什么，routing，routing，还是 routing。为了提高查询速度，减少慢查询，结合自己的业务实践，使用多个集群，每个集群使用不同的 routing。比如，用户是一个routing维度
            (2)预处理：
                预处理索引数据，减少查询过程中的计算消耗，优先考虑将索引的mapping中的标识符字段（如id字段）设置为keyword类型，numeric 类型适合范围查询range queries，keyword 类型适合等值查询term queries。
            (3)预热文件系统缓存：
                如果重新启动运行 Elasticsearch 的机器，文件系统缓存将是空的，因此操作系统将索引的热点区域加载到内存中需要一些时间，以便快速搜索操作。 您可以使用 index.store.preload 设置根据文件扩展名明确告诉操作系统哪些文件应该立即加载到内存中
            (4)设置索引存储时的排序方式加快连接查询性能
            (5)数据预热：
                对于那些你觉得比较热的，经常会有人访问的数据，最好做一个专门的缓存预热子系统，就是对热数据，每隔一段时间，你就提前访问一下，让数据进入filesystem cache里面去。这样期待下次别人访问的时候，一定性能会好一些
            (6)冷热分离：
                将冷数据写入一个索引中，然后热数据写入另外一个索引中，这样可以确保热数据在被预热之后，尽量都让他们留在filesystem os cache里，别让冷数据给冲刷掉。假设你有6台机器，2个索引，一个放冷数据，一个放热数据，每个索引3个shard，3台机器放热
                数据index；另外3台机器放冷数据index，然后这样的话，你大量的时候是在访问热数据index，热数据可能就占总数据量的10%，此时数据量很少，几乎全都保留在filesystem cache里面了，就可以确保热数据的访问性能是很高的。对于冷数据而言，是在别的
                index里的，跟热数据index都不再相同的机器上，大家互相之间都没什么联系了。如果有人访问冷数据，可能大量数据是在磁盘上的，此时性能差点，就10%的人去访问冷数据；90%的人在访问热数据
        12.5 分页优化
                因此我们应尽量避免深度分页/默认深度分页性能很惨，可以实现类似于app里的推荐商品不断下拉出来一页一页的。scroll会一次性给你生成所有数据的一个快照，然后每次翻页就是通过游标移动，获取下一页下一页这样子，性能会比上面说的那种分页性能也高很多很多

     13.Elasticsearch的路由（Routing）特性 https://blog.csdn.net/wwd0501/article/details/78109617
        13.1 Elasticsearch路由机制介绍
            Elasticsearch的路由机制与其分片机制有着直接的关系。Elasticsearch的路由机制即是通过哈希算法，将具有相同哈希值的文档放置到同一个主分片中。这个和通过哈希算法来进行负载均衡几乎是一样的
            而Elasticsearch也有一个默认的路由算法：它会将文档的ID值作为依据将其哈希到相应的主分片上，这种算法基本上会保持所有数据在所有分片上的一个平均分布，而不会产生数据热点
            而我们为什么会需要自定义的Routing模式呢？首先默认的Routing模式在很多情况下都是能满足我们的需求的——平均的数据分布、对我们来说是透明的、多数时候性能也不是问题。但是在我们更深入地理解我们的数据的特征之后，使用自定义的Routing模式可能会给我们带来更好的性能。
            假设你有一个100个分片的索引。当一个请求在集群上执行时会发生什么呢？
                (1)这个搜索的请求会被发送到一个节点
                (2)接收到这个请求的节点，将这个查询广播到这个索引的每个分片上（可能是主分片，也可能是复制分片）
                (3)每个分片执行这个搜索查询并返回结果
                (4)结果在通道节点上合并、排序并返回给用户
            因为默认情况下，Elasticsearch使用文档的ID（类似于关系数据库中的自增ID，当然，如果不指定ID的话，Elasticsearch使用的是随机值）将文档平均的分布于所有的分片上，这导致了Elasticsearch不能确定文档的位置，所以它必须将这个请求广播到所有的100个分片上去执行。
            这同时也解释了为什么主分片的数量在索引创建的时候是固定下来的，并且永远不能改变。因为如果分片的数量改变了，所有先前的路由值就会变成非法了，文档相当于丢失了。
            而自定义的Routing模式，可以使我们的查询更具目的性。我们不必盲目地去广播查询请求，取而代之的是：我们要告诉Elasticsearch我们的数据在哪个分片上。
            原来的查询语句：“请告诉我，USER1的文档数量一共有多少”
            使用自定义Routing（在USESR　ID上）后的查询语句：“请告诉我，USER1的文档数量一共有多少，它就在第三个分片上，其它的分片就不要去扫描了”
        13.2 指定个性化路由
            所有的文档API（get，index，delete，update和mget）都能接收一个routing参数，可以用来形成个性化文档分片映射。一个个性化的routing值可以确保相关的文档存储到同样的分片上——比如，所有属于同一个用户的文档。
            直接在请求的URL中指定routing参数:
                 curl -XPOST 'http://localhost:9200/store/order?routing=user123' -d '
                 {
                     "productName": "sample",
                     "customerID": "user123"
                 }'
            这样我们就按照用户的customerID的值将具有相同customerID的文档置于同一分片上了。
            查询时也可以指定多个
                curl -XGET 'http://localhost:9200/forum/posts/?routing=Admin,Moderator' -d '{}'
        13.3 路由机制的总结
            实际上，如果不明确指明使用路由机制，实际上路由机制也是在发挥作用的，只是默认的路由值是文档的id而已。而个性化路由的需求主要是和业务相关的。默认的路由（如果是自动的生成的id）直观上会把所有的文档随机分配到
            一个分片上，而个性化的路由值就是和业务相关的了。这也会造成一些潜在的问题，比如user123本身的文档就非常多，有数十万个，而其他大多数的用户只有几个文档，这样的话就会导致user123所在的分片较大，出现数据偏移
            的情况，特别是多个这样的用户处于同一分片的时候，现象会更明显。具体的使用还是要结合实际的应用场景来选择的
        13.4 优化路由的单点查询问题
            自定义路由可能会导致索引分配不均，大量的索引路由到一个分片上，导致这个分片的索引和查询性能降低。为了解决这个问题，可以设置 routing_partition_size 参数。（注意这是一个索引级别的设置，只能在创建索引的
            时候设置。）这样routing将路由到一组分片，然后_id字段在决定文档保存到那一个分片上。由于这个原因。routing_partition_size的值必须是一个大于1但是小于number_of_shards设置的分片数量的一个整数。具体公式如下：
                shard_num = (hash(_routing) + hash(_id) % routing_partition_size) % num_primary_shards
            注意：索引一旦设置了routing_partition_size 后，join field将不能被创建了，同时索引中的所有mapping必须设置_routing为required的。
            查看每个分片文档数量
                GET _cat/shards/test07
                    test07 2 p STARTED 0  261b 192.168.0.16 testshshs_core
                    test07 1 p STARTED 1 3.2kb 192.168.0.15 testshshs_first
                    test07 0 p STARTED 1 3.2kb 192.168.0.16 testshshs_core
            注意：网上说自定义路由选取的字段特别重要，需要考虑是否达到预期效果，不要是数据过于集中，可能会适得其反，这需要根据场景实际测试。
     */
}


















