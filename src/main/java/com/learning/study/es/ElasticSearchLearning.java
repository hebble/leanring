package com.learning.study.es;

/**
 * https://blog.csdn.net/a745233700/article/details/115585342
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

     */
}


















