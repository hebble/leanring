package com.learning.study.mysql;

/**
 *
 https://zhuanlan.zhihu.com/p/340593296
 https://blog.csdn.net/a745233700/article/details/114242960
 */
public class MysqlLearning {
    /**
     * 1、数据库的常用范式：
     *      第一范式（1NF）：指表的列不可再分，数据库中表的每一列都是不可分割的基本数据项，同一列中不能有多个值；
     *      第二范式（2NF）：在 1NF 的基础上，还包含两部分的内容：一是表必须有一个主键；二是表中非主键列必须完全依赖于主键，不能只依赖于主键的一部分；
     *      第三范式（3NF）：在 2NF 的基础上，消除非主键列对主键的传递依赖，非主键列必须直接依赖于主键。
     *      BC范式（BCNF）：在 3NF 的基础上，消除主属性对于码部分的传递依赖
     *
     *2.SQL语句的执行过程
     *      2.1、客户端的数据库驱动与数据库连接池：
     *          （1）客户端与数据库进行通信前，通过数据库驱动与MySQL建立连接，建立完成之后，就发送SQL语句
     *          （2）为了减少频繁创建和销毁连接造成系统性能的下降，通过数据库连接池维护一定数量的连接线程，当需要进行连接时，就直接从连接池中获取，使用完毕之后，再归还给连接池。常见的数据库连接池有 Druid、C3P0、DBCP
     *      2.2、MySQL架构的Server层的执行过程：
     *          （1）连接器：主要负责跟客户端建立连接、获取权限、维持和管理连接
     *          （2）查询缓存：优先在缓存中进行查询，如果查到了则直接返回，如果缓存中查询不到，在去数据库中查询。
     *                  MySQL缓存是默认关闭的，也就是说不推荐使用缓存，并且在MySQL8.0 版本已经将查询缓存的整块功能删掉了
     *          （3）解析器/分析器：分析器的工作主要是对要执行的SQL语句进行词法解析、语法解析，最终得到抽象语法树，然后再使用预处理器对抽象语法树进行语义校验，判断抽象语法树中的表是否存在，如果存在的话，在接着判断select投影列字段是否在表中存在等。
     *          （4）优化器：主要将SQL经过词法解析、语法解析后得到的语法树，通过数据字典和统计信息的内容，再经过一系列运算 ，最终得出一个执行计划，包括选择使用哪个索引
     *          （5）执行器：根据一系列的执行计划去调用存储引擎提供的API接口去调用操作数据，完成SQL的执行。
     *      2.3、Innodb存储引擎的执行过程
     *          （1）首先MySQL执行器根据 执行计划 调用存储引擎的API查询数据
     *          （2）存储引擎先从缓存池buffer pool中查询数据，如果没有就会去磁盘中查询，如果查询到了就将其放到缓存池中
     *          （3）在数据加载到 Buffer Pool 的同时，会将这条数据的原始记录保存到 undo 日志文件中
     *          （4）innodb 会在 Buffer Pool 中执行更新操作
     *          （5）更新后的数据会记录在 redo log buffer 中
     *          （6）提交事务在提交的同时会做以下三件事
     *          （7）（第一件事）将redo log buffer中的数据刷入到redo log文件中
     *          （8）（第二件事）将本次操作记录写入到 bin log文件中
     *          （9）（第三件事）将bin log文件名字和更新内容在 bin log 中的位置记录到redo log中，同时在 redo log 最后添加 commit 标记
     *          （10）使用一个后台线程，它会在某个时机将我们Buffer Pool中的更新后的数据刷到 MySQL 数据库中，这样就将内存和数据库的数据保持统一了
     *      备注: buffer pool 和 查询缓存的区别：
     *          （1）查询缓存：查询缓存位于Server层，MySQL Server首选会从查询缓存中查看是否曾经执行过这个SQL，如果曾经执行过的话，之前执行的查询结果会以Key-Value的形式保存在查询缓存中。key是SQL语句，value是查询结果。我们将这个过程称为查询缓存！
     *          （2）Buffer Pool位于存储引擎层。Buffer Pool就是MySQL存储引擎为了加速数据的读取速度而设计的缓冲机制
     *
     * 3.常用的存储引擎？InnoDB与MyISAM的区别？
     *      存储引擎是对底层物理数据执行实际操作的组件，为Server服务层提供各种操作数据的API。常用的存储引擎有InnoDB、MyISAM、Memory。这里我们主要介绍InnoDB 与 MyISAM 的区别：
     *      （1）事务：MyISAM不支持事务，InnoDB支持事务
     *      （2）锁级别：MyISAM只支持表级锁，InnoDB支持行级锁和表级锁，默认使用行级锁，但是行锁只有通过索引查询数据才会使用，否则将使用表锁。行级锁在每次获取锁和释放锁的操作需要消耗比表锁更多的资源。使用行锁可能会存在死锁的情况，但是表级锁不存在死锁
     *      （3）主键和外键：MyISAM 允许没有任何索引和主键的表存在，不支持外键。InnoDB的主键不能为空且支持主键自增长，如果没有设定主键或者非空唯一索引，就会自动生成一个6字节的主键，支持外键完整性约束
     *      （4）索引结构：MyISAM 和 InnoDB 都是使用B+树索引，MyISAM的主键索引和辅助索引的Data域都是保存行数据记录的地址。但是InnoDB的主键索引的Data域保存的不是行数据记录的地址，而是保存该行的所有数据内容，而辅助索引的Data域保存的则是主索引的值。
     *      （5）全文索引：MyISAM支持全文索引，InnoDB在5.6版本之前不支持全文索引，5.6版本及之后的版本开始支持全文索引
     *      （6）表的具体行数：
     *          ① MyISAM：保存有表的总行数，如果使用 select count() from table 会直接取出出该值，不需要进行全表扫描。
     *          ② InnoDB：没有保存表的总行数，如果使用 select count() from table 需要会遍历整个表，消耗相当大。
     *      （7）存储结构：
     *          ① MyISAM会在磁盘上存储成三个文件：.frm文件存储表定义，.MYD文件存储数据，.MYI文件存储索引。
     *          ② InnoDB：把数据和索引存放在表空间里面，所有的表都保存在同一个数据文件中，InnoDB表的大小只受限于操作系统文件的大小，一般为2GB。
     *      （8）存储空间：
     *          ① MyISAM：可被压缩，存储空间较小。支持三种不同的存储格式：静态表(默认，但是注意数据末尾不能有空格，会被去掉)、动态表、压缩表。
     *          ② InnoDB：需要更多的内存和存储，它会在主内存中建立其专用的缓冲池用于高速缓冲数据和索引。
     *      （9）适用场景：
     *          ① 如果需要提供回滚、崩溃恢复能力的ACID事务能力，并要求实现行锁级别并发控制，InnoDB是一个好的选择；
     *          ② 如果数据表主要用来查询记录，读操作远远多于写操作且不需要数据库事务的支持，则MyISAM引擎能提供较高的处理效率；
     *
     *      备注：在mysql8.0版本中已经废弃了MyISAM存储引擎
     *
     * 4.数据库中的锁机制？
     *      当数据库中多个事务并发存取同一数据的时候，若对并发操作不加控制就可能会读取和存储不正确的数据，破坏数据库的一致性。MySQL锁机制的基本工作原理就是，事务在修改数据库之前，需要先获得相应的锁，获得锁的事务才可以修改数据；在该事务操作期间，这部分的数据是锁定，其他事务如果需要修改数据，需要等待当前事务提交或回滚后释放锁。
     *      按照不同的分类方式，锁的种类可以分为以下几种：
     *          按锁的粒度划分：表级锁、行级锁、页级锁；
     *          按锁的类型划分：共享（锁S锁）、排他锁（X锁）；
     *          按锁的使用策略划分：乐观锁、悲观锁；
     *      4.1表级锁、行级锁、页级锁
     *          表级锁：最大粒度的锁级别，发生锁冲突的概率最高，并发度最低，但开销小，加锁快，不会出现死锁；
     *          行级锁：最小粒度的锁级别，发生锁冲突的概率最小，并发度最高，但开销大，加锁慢，会发生死锁；
     *          页级锁：锁粒度界于表级锁和行级锁之间，对表级锁和行级锁的折中，并发度一般。开销和加锁时间也界于表锁和行锁之间，会出现死锁；
     *         InnoDB存储引擎支持行级锁和表级锁，默认情况下使用行级锁，但只有通过索引进行查询数据，才使用行级锁，否就使用表级锁。
     *      4.2InnoDB的行锁有两种类型：
     *          共享锁（S锁、读锁）：多个事务可以对同一数据行共享一把S锁，但只能进行读不能修改；
     *          排它锁（X锁、写锁）：一个事务获取排它锁之后，可以对锁定范围内的数据行执行写操作，在锁定期间，其他事务不能再获取这部分数据行的锁（共享锁、排它锁），只允许获取到排它锁的事务进行更新数据。
     *          对于update，delete，insert 操作，InnoDB会自动给涉及的数据行加排他锁；对于普通SELECT语句，InnoDB不会加任何锁。
     *
     * 5.MySQL索引的实现原理
     *      索引本质上就是一种通过减少查询需要遍历行数，加快查询性能的数据结构，避免数据库进行全表扫描，好比书的目录，让你更快的找到内容。
     *
     * 6.索引的使用场景
     *      （1）在哪些列上面创建索引：
     *          WHERE子句中经常出现的列上面创建索引，加快条件的判断速度。
     *          按范围存取的列或者在group by或order by中使用的列，因为索引已经排序，这样可以利用索引加快排序查询时间。
     *          经常用于连接的列上，这些列主要是一些外键，可以加快连接的速度；
     *          作为主键的列上，强制该列的唯一性和组织表中数据的排列结构；
     *      （2）不在哪些列建索引？
     *          区分度不高的列。由于这些列的取值很少，例如性别，在查询的结果中，结果集的数据行占了表中数据行的很大比例，即需要在表中搜索的数据行的比例很大。增加索引，并不能明显加快检索速度。
     *          在查询中很少的列不应该创建索引。由于这些列很少使用到，但增加了索引，反而降低了系统的维护速度和增大了空间需求。
     *          当添加索引造成修改成本的提高 远远大于 检索性能的提高时，不应该创建索引。当增加索引时，会提高检索性能，但是会降低修改性能。当减少索引时，会提高修改性能，降低检索性能。
     *          定义为text, image和bit数据类型的列不应该增加索引。这些列的数据量要么相当大，要么取值很少。
     *
     * 7.索引的分类
     *      （1）普通索引、唯一索引、主键索引、全文索引、组合索引。
     *          普通索引：最基本的索引，没有任何限制
     *          唯一索引：但索引列的值必须唯一，允许有空值，可以有多个NULL值。如果是组合索引，则列值的组合必须唯一。
     *          主键索引：一种特殊的唯一索引，不允许有空值。
     *          全文索引：全文索引仅可用于 MyISAM 表，并只支持从CHAR、VARCHAR或TEXT类型，用于替代效率较低的like 模糊匹配操作，而且可以通过多字段组合的全文索引一次性全模糊匹配多个字段。
     *          组合索引：主要是为了提高mysql效率，创建组合索引时应该将最常用作限制条件的列放在最左边，依次递减。
     *      （2）聚簇索引与非聚簇索引：
     *          如果按数据存储的物理顺序与索引值的顺序分类，可以将索引分为聚簇索引与非聚簇索引两类：
     *          聚簇索引：表中数据存储的物理顺序与索引值的顺序一致，一个基本表最多只能有一个聚簇索引，更新聚簇索引列上的数据时，往往导致表中记录的物理顺序的变更，代价较大，因此对于经常更新的列不宜建立聚簇索引
     *          非聚簇索引：表中数据的物理顺序与索引值的顺序不一致的索引组织，一个基本表可以有多个非聚簇索引。
     *
     * 8.索引失效的情况
 *             1.未遵循最佳左前缀规则导致索引失效
     *         2.计算、函数、类型转换(自动或手动)导致索引失效
     *         3.范围条件右边列索引可能失效(当Mysql发现通过索引扫描的行记录数超过全表的10%-30%时，优化器可能会放弃走索引，自动变成全表扫描)
     *            索引最多用于一个范围列，如果查询条件中有两个范围列则无法全用到索引。
     *         4.不等于(!=)会导致索引失效
     *         5.is null可以用到索引，is not null不能用到索引
     *         6.like以通配符%开头索引失效
     *         7.or 前的列建立了索引 or后面的列没有建立索引 会导致索引失效
     *         8.不同字符集进行比较前需要进行转换，会导致索引失效
     *
     * 9.索引的数据结构？
     *      索引的数据结构主要有B+树和哈希表，对应的索引分别为B+树索引和哈希索引。InnoDB引擎的索引类型有B+树索引和哈希索引，默认的索引类型为B+树索引。
     *      (1)B+树索引
     *          在B+树中，所有的记录节点都是按照键值大小的顺序放在叶子节点上, 因为B+树具有有序性，并且所有的数据都存放在叶子节点，所以查找的效率非常高，并且支持排序和范围查找。
     *          B+树的索引又可以分为主索引和辅助索引。其中主索引为聚簇索引，辅助索引为非聚簇索引。
     *          聚簇索引
     *              聚簇索引是以主键作为B+ 树索引的键值所构成的B+树索引，聚簇索引的叶子节点存储着完整的数据记录；
     *          非聚簇索引
     *              非聚簇索引是以非主键的列作为B+树索引的键值所构成的B+树索引，非聚簇索引的叶子节点存储着主键值
     *              所以使用非聚簇索引进行查询时，会先找到主键值，然后到根据聚簇索引找到主键对应的数据域。
     *       (2)哈希索引
     *          哈希索引是基于哈希表实现的，对于每一行数据，存储引擎会对索引列通过哈希算法进行哈希计算得到哈希码，并且哈希算法要尽量保证不同的列值计算出的哈希码值是不同的，
     *          将哈希码的值作为哈希表的key值，将指向数据行的指针作为哈希表的value值。这样查找一个数据的时间复杂度就是o(1)，一般多用于精确查找。
     *
     * 10.Hash索引和B+树的区别？
     *      因为两者数据结构上的差异导致它们的使用场景也不同，哈希索引一般多用于精确的等值查找，B+索引则多用于除了精确的等值查找外的其他查找。在大多数情况下，会选择使用B+树索引。
     *      (1)哈希索引不支持排序，因为哈希表是无序的。
     *      (2)哈希索引不支持范围查找。
     *      (3)哈希索引不支持模糊查询及多列索引的最左前缀匹配。
     *      (4)因为哈希表中会存在哈希冲突，所以哈希索引的性能是不稳定的，而B+树索引的性能是相对稳定的，每次查询都是从根节点到叶子节点
     *
     * 11.B树和B+树的区别？
     *     B树和B+树最主要的区别主要有两点：
     *          (1)B树中的内部节点和叶子节点均存放键和值，而B+树的内部节点只有键没有值，叶子节点存放所有的键和值。
     *          (2)B＋树的叶子节点是通过相连在一起的，方便顺序检索。
     *
     * 12.数据库为什么使用B+树而不是B树？
     *      B树适用于随机检索，而B+树适用于随机检索和顺序检索
     *      B+树的空间利用率更高，因为B树每个节点要存储键和值，而B+树的内部节点只存储键，这样B+树的一个节点就可以存储更多的索引，从而使树的高度变低，减少了I/O次数，使得数据检索速度更快。
     *      B+树的叶子节点都是连接在一起的，所以范围查找，顺序查找更加方便
     *      B+树的性能更加稳定，因为在B+树中，每次查询都是从根节点到叶子节点，而在B树中，要查询的值可能不在叶子节点，在内部节点就已经找到。
     *
     *      那在什么情况适合使用B树呢，因为B树的内部节点也可以存储值，所以可以把一些频繁访问的值放在距离根节点比较近的地方，这样就可以提高查询效率。综上所述，B+树的性能更加适合作为数据库的索引。
     *
     * 13.什么是聚簇索引，什么是非聚簇索引？
     *      聚簇索引和非聚簇索引最主要的区别是数据和索引是否分开存储。
     *      聚簇索引:
     *          将数据和索引放到一起存储，索引结构的叶子节点保留了数据行。
     *      非聚簇索引：
     *          将数据进和索引分开存储，索引叶子节点存储的是指向数据行的地址。
     *
     * 14.非聚簇索引一定会进行回表查询吗？
     *      答案是不一定的，这里涉及到一个索引覆盖的问题，如果查询的数据在辅助索引上完全能获取到便不需要回表查询。
     *      例如有一张表存储着个人信息包括id、name、age等字段。假设聚簇索引是以ID为键值构建的索引，非聚簇索引是以name为键值构建的索引，select id,name from user where name = 'zhangsan';
     *      这个查询便不需要进行回表查询因为，通过非聚簇索引已经能全部检索出数据，这就是索引覆盖的情况。如果查询语句是这样，select id,name,age from user where name = 'zhangsan';
     *      则需要进行回表查询，因为通过非聚簇索引不能检索出age的值。那应该如何解决那呢？只需要将索引覆盖即可，建立age和name的联合索引再使用select id,name,age from user where name = 'zhangsan';进行查询即可。
     *
     *      所以通过索引覆盖能解决非聚簇索引回表查询的问题。
     *
     * 15.索引的使用场景有哪些？
     *      对于中大型表建立索引非常有效，对于非常小的表，一般全部表扫描速度更快些。
     *      对于超大型的表，建立和维护索引的代价也会变高，这时可以考虑分区技术。
     *      如何表的增删改非常多，而查询需求非常少的话，那就没有必要建立索引了，因为维护索引也是需要代价的。
     *      一般不会出现再where条件中的字段就没有必要建立索引了。
     *      多个字段经常被查询的话可以考虑联合索引。
     *      字段多且字段值没有重复的时候考虑唯一索引。
     *      字段多且有重复的时候考虑普通索引。
     *
     * 16.什么是前缀索引？
     *      前缀索引是指对文本或者字符串的前几个字符建立索引，这样索引的长度更短，查询速度更快。
     *      使用场景：前缀的区分度比较高的情况下。
     *      ALTER TABLE table_name ADD KEY(column_name(prefix_length));
     *
     *      这里面有个prefix_length参数很难确定，这个参数就是前缀长度的意思。通常可以使用以下方法进行确定，先计算全列的区分度
     *          SELECT COUNT(DISTINCT column_name) / COUNT(*) FROM table_name;
     *      然后在计算前缀长度为多少时和全列的区分度最相似。
     *          SELECT COUNT(DISTINCT LEFT(column_name, prefix_length)) / COUNT(*) FROM table_name;
     *      不断地调整prefix_length的值，直到和全列计算出区分度相近。
     *
     * 17.什么是最左匹配原则？
     *      最左匹配原则：从最左边为起点开始连续匹配，遇到范围查询（<、>、between、like）会停止匹配。
     *      例如建立索引(a,b,c)，大家可以猜测以下几种情况是否用到了索引。
     *      第一种
     *          select * from table_name where a = 1 and b = 2 and c = 3
     *          select * from table_name where b = 2 and a = 1 and c = 3
     *          上面两次查询过程中所有值都用到了索引，where后面字段调换不会影响查询结果，因为MySQL中的优化器会自动优化查询顺序。
     *      第二种
     *          select * from table_name where a = 1
     *          select * from table_name where a = 1 and b = 2
     *          select * from table_name where a = 1 and b = 2 and c = 3
     *          答案是三个查询语句都用到了索引，因为三个语句都是从最左开始匹配的。
     *      第三种
     *          select * from table_name where b = 1
     *          select * from table_name where b = 1 and c = 2
     *          答案是这两个查询语句都没有用到索引，因为不是从最左边开始匹配的
     *      第四种
     *          select * from table_name where a = 1 and c = 2
     *          这个查询语句只有a列用到了索引，c列没有用到索引，因为中间跳过了b列，不是从最左开始连续匹配的。
     *      第五种
     *          select * from table_name where a = 1 and b < 3 and c < 1
     *          这个查询中只有a列和b列使用到了索引，而c列没有使用索引，因为根据最左匹配查询原则，遇到范围查询会停止。
     *      第六种
     *          select * from table_name where a like 'ab%';
     *          select * from table_name where a like '%ab'
     *          select * from table_name where a like '%ab%'
     *          对于列为字符串的情况，只有前缀匹配可以使用索引，中缀匹配和后缀匹配只能进行全表扫描。
     *
     * 18.SQL优化与索引优化
     *      (1)索引优化
     *          1.like语句的前导模糊查询不使用索引
     *          2.负向条件查询不能使用索引
     *              负向条件有：!=、<>、not in、not exists、not like 等
     *              status != 2 可以优化为  status in (0,3,4)
     *          3.范围条件右边的列不能使用索引（范围列可以用到索引）：
     *              范围条件有：<、<=、>、>=、between等
     *              索引最多用于一个范围列，如果查询条件中有两个范围列则无法全用到索引
     *              假如有联合索引 (emp_no 、title、from_date )，那么下面的 SQL 中 emp_no 可以用到索引，而title 和 from_date 则使用不到索引。
     *              select * from employees.titles where emp_no < 10010' and title='Senior Engineer'and from_date between '1986-01-01' and '1986-12-31'
     *          4.在索引列做任何操作（计算、函数、表达式）会导致索引失效而转向全表扫描：
     *          5.where 子句中索引列使用参数，也会导致索引失效：
     *              select id from t where num=@num                                   -- 不能使用索引
     *              select id from t with(index(索引名)) where num=@num   --可以改为强制查询使用索引：
     *          6.强制类型转换会导致全表扫描
     *              select * from user where phone=13800001234;      -- 不能使用索引
     *              select * from user where phone='13800001234';     -- 可以使用索引
     *          7.is null, is not null 在无法使用索引，不过在mysql的高版本已经做了优化，允许使用索引
     *          8.使用组合索引时，要符合最左前缀原则：
     *          9.利用覆盖索引来进行查询操作，避免回表，减少select * 的使用
     *              覆盖索引：被查询列要被所建的索引覆盖，被查询列的数据能从索引中直接取得，不用通过行定位符 再到 row 上获取，加速查询速度。
     *              Select uid, login_time from user where login_name=? and passwd=?
     *              可以建立(login_name, passwd, login_time)的联合索引，由于 login_time 已经建立在索引中了，被查询的 uid 和 login_time 就不用去 row 上获取数据了，从而加速查询。
     *          10.利用索引下推减少回表的次数
     *          11.使用前缀索引
     *              短索引不仅可以提高查询性能而且可以节省磁盘空间和I/O操作，减少索引文件的维护开销，但缺点是不能用于 ORDER BY 和 GROUP BY 操作，也不能用于覆盖索引
     *          12.order by、group by后面的列如果有索引，可以利用索引的有序性可以消除排序带来的CPU开销
     *              （1）order by 最后的字段是组合索引的一部分，并且放在索引组合顺序的最后，避免出现file_sort 的情况，影响查询性能。例如对于语句 where a= ? and b= ? order by c，可以建立联合索引(a,b,c)。
     *              （2）如果索引中有范围查找，那么索引有序性无法利用，如 WHERE  a > 10 ORDER BY b; 索引(a,b)无法排序。
     *              （3）如果是前缀索引，是不能消除排序的
     *              （4）order by排序字段顺序，即asc/desc升降要跟索引保持一致，充分利用索引的有序性来消除排序带来的CPU开销
     *          13.SQL 性能优化 explain 中的 type：至少要达到 range 级别，要求是 ref 级别，如果可以是 consts 最好。
     *              consts：单表中最多只有一个匹配行（主键或者唯一索引），在优化阶段即可读取到数据。
     *              ref：使用普通的索引
     *              range：对索引进行范围检索。
     *              当 type=index 时，索引物理文件全扫，速度非常慢。
     *          14.业务上具有唯一特性的字段，即使是多个字段的组合，也必须建成唯一索引，防止脏数据产生：
     *              不要以为唯一索引影响了 insert 速度，这个速度损耗可以忽略，但提高查找速度是明显的。另外，即使在应用层做了非常完善的校验控制，只要没有唯一索引，根据墨菲定律，必然。
     *          15.更新十分频繁、数据区分度不高的列不宜建立索引
     *              数据更新会变更 B+ 树，在更新频繁的字段建立索引会大大降低数据库性能。类似于“性别”这种区分度不大的属性，建立索引是没有什么意义的，不能有效过滤数据，性能与全表扫描类似。一般区分度在80%以上的时候就可以建立索引，区分度可以使用 count(distinct(列名))/count(*) 来计算。
     *      (2)SQL语句优化
     *          1.减少请求的数据量
     *              只返回必要的列，用具体的字段列表代替 select * 语句
     *              只返回必要的行，使用 Limit 语句来限制返回的数据
     *          2.优化深度分页的场景：利用延迟关联或者子查询
     *              对于 limit m, n 的分页查询，越往后面翻页（即m越大的情况下）SQL的耗时会越来越长，对于这种应该先取出主键id，然后通过主键id跟原表进行Join关联查询
     *              因为MySQL 并不是跳过 offset 行，而是取 offset+N 行，然后放弃前 offset 行，返回 N 行，那当 offset 特别大的时候，效率就非常的低下，要么控制返回的总页数，要么对超过特定阈值的页数进行 SQL 改写。
     *              延迟关联示例如下，先快速定位需要获取的 id 段，然后再关联：
     *                  # 延迟关联：通过使用覆盖索引查询返回需要的主键,再根据主键关联原表获得需要的数据
     *                  # 覆盖索引：select的数据列只用从索引中就能够得到，不用回表查询
     *                  select a.* from 表1 a,(select id from 表1 where 条件 limit 100000,20) b where a.id=b.id；
     *              但对于深度分页的情况，最好还是将上次遍历到的最末尾的数据ID传给数据库，然后直接定位到该ID处 再 往后面遍历数据
     *           3.分解大连接查询：
     *              将一个大连接查询分解成对每一个表进行一次单表查询，然后在应用程序中进行关联，这样做的好处有：
     *                  （1）减少锁竞争；
     *                  （2）让缓存更高效。对于连接查询，如果其中一个表发生变化，那么整个查询缓存就无法使用。而分解后的多个查询，即使其中一个表发生变化，对其它表的查询缓存依然可以使用。
     *                  （3）分解成多个单表查询，这些单表查询的缓存结果更可能被其它查询使用到，从而减少冗余记录的查询。
     *                  （4）在应用层进行连接，可以更容易对数据库进行拆分，从而更容易做到高性能和可伸缩。
     *                  （5）查询本身效率也可能会有所提升。比如使用 IN() 代替连接查询，可以让 MySQL 按照 ID 顺序进行查询，这可能比随机的连接要更高效。
     *            4.避免使用select的内联子查询：
     *                在select后面有子查询的情况称为内联子查询，SQL返回多少行，子查询就需要执行过多少次，严重影响SQL性能。
     *            5.尽量使用Join代替子查询
     *                 由于MySQL的优化器对于子查询的处理能力比较弱，所以不建议使用子查询，可以改写成Inner Join，之所以 join 连接效率更高，是因为 MySQL不需要在内存中创建临时表
     *            6.多张大数据量的表进行JOIN连接查询，最好先过滤在JOIN:
     *                  在多个表进行 join 连接查询的时候，最好先在一个表上先过滤好数据，然后再用过滤好的结果集与另外的表 Join，这样可以尽可能多的减少不必要的 IO 操作，大大节省 IO 操作所消耗的时间
     *            7.避免在使用or来连接查询条件：
     *                  如果一个字段有索引，一个字段没有索引，将导致引擎放弃使用索引而进行全表扫描。
     *            8.union、in、or 都能够命中索引，但推荐使用 in：
     *                  对于上面三种关键词：union all 分两步执行，而 in 和 or 只用了一步，效率高一点。用 or 的执行时间比 in 时间长。因为使用 or 条件查询，会先判断一个条件进行筛选，
     *                  再判断 or 中另外的条件再筛选，而 in 查询直接一次在 in 的集合里筛选，并且or 查询优化耗费的 CPU 比 in 多，所以推荐使用in
     *            9.对于连续的数值，能用 between 就不要用 in
     *            10.小表驱动大表，即小的数据集驱动大的数据集
     *                  in 和 exists 都可以用于子查询，那么 MySQL 中 in 和 exists 有什么区别呢？
     *                      （1）使用exists时会先进行外表查询，将查询到的每行数据带入到内表查询中看是否满足条件；使用in一般会先进行内表查询获取结果集，然后对外表查询匹配结果集，返回数据。
     *                      （2）in在内表查询或者外表查询过程中都会用到索引；exists仅在内表查询时会用到索引
     *                      （3）一般来说，当子查询的结果集比较大，外表较小使用exist效率更高；当子查询的结果集较小，外表较大时，使用in效率更高。
     *                      （4）对于 not in 和 not exists，not exists 效率比 not in 的效率高，与子查询的结果集无关，因为 not in 对于内外表都进行了全表扫描，没有使用到索引。not exists的子查询中可以用到表上的索引。
     *            11.使用union all 替换 union
     *                  当SQL语句需要union两个查询结果集合时，这两个结果集合会以union all的方式被合并，然后再输出最终结果前进行排序。如果用union all替代union，这样排序就不是不要了，效率就会因此得到提高.。
     *                  需要注意的是，UNION ALL 将重复输出两个结果集合中相同记录。
     *            12.优化Group by，使用where子句替换Having子句：
     *                  避免使用having子句，having只会在检索出所有记录之后才会对结果集进行过滤，这个处理需要排序分组，如果能通过where子句提前过滤查询的数目，就可以减少这方面的开销。
     *                  on、where、having这三个都可以加条件的子句，on是最先执行，where次之，having最后。
     *                  提高GROUP BY 语句的效率, 可以通过将不需要的记录在GROUP BY 之前过滤掉。
     *                      低效: SELECT JOB, AVG(SAL) FROM EMP GROUP by JOB HAVING JOB = ‘PRESIDENT' OR JOB = ‘MANAGER'
     *                      高效: SELECT JOB, AVG(SAL) FROM EMP WHERE JOB = ‘PRESIDENT' OR JOB = ‘MANAGER' GROUP by JOB
     *            13.尽量使用数字型字段：
     *                  若只含数值信息的字段尽量不要设计为字符型，这会降低查询和连接的性能。引擎在处理查询和连接时会逐个比较字符串中每一个字符，而对于数字型而言只需要比较一次就够了。
     *            14.写出统一的SQL语句
     *                  对于以下两句SQL语句，很多人都认为是相同的。不过数据库查询优化器则认为是不同的，虽然只是大小写不同，但必须进行两次解析，生成2个执行计划。所以应该保证相同的查询语句在任何地方都一致，多一个空格都不行。
     *            15.使用复合索引须遵守最左前缀原则：
     *                  复合索引必须使用到最左边字段作为条件时才能保证系统使用该索引，否则该索引将不会被使用，并且应尽可能的让字段顺序与索引顺序相一致。
     *            16.当需要全表删除且无需回滚时，使用Truncate替代delete
     *            17.使用表的别名
     *                  当在SQL语句中连接多个表时, 使用表的别名并把别名前缀用于每个Column上，这样可以减少解析的时间并减少那些由Column歧义引起的语法错误
     *            18.避免使用耗费资源的操作
     *                  带有DISTINCT,UNION,MINUS,INTERSECT,ORDER BY的SQL语句，会启动SQL引擎执行耗费资源的排序功能，DISTINCT需要一次排序操作，而其他的至少需要执行两次排序。通常。带有UNION, MINUS ,
     *                  INTERSECT的SQL语句都可以用其他方式重写，如果你的数据库的SORT_AREA_SIZE调配得好, 使用UNION , MINUS, INTERSECT也是可以考虑的, 毕竟它们的可读性很强。
     *            19.尽量避免使用游标，因为游标的效率较差。与临时表一样，游标并不是不可使用。对小型数据集使用 FAST_FORWARD 游标通常要优于其他逐行处理方法，尤其是在必须引用几个表才能获得所需的数据时。在结果集中包括“合计”的例程通常要比使用游标执行的速度快。
     *            20.尽量避免大事务操作，提高系统并发能力
     *            21.在运行代码中，尽量使用PreparedStatement来查询，不要用Statement。
     *
     *      19.数据库参数优化
     *          MySQL属于 IO 密集型的应用程序，主要职责就是数据的管理及存储工作。而我们知道，从内存中读取一个数据库的时间是微秒级别，而从一块普通硬盘上读取一个IO是在毫秒级别，二者相差3个数量级。所以，要优化数据库，
     *          首先第一步需要优化的就是 IO，尽可能将磁盘IO转化为内存IO。所以对于MySQL数据库的参数优化上，主要针对减少磁盘IO的参数做优化：比如使用 query_cache_size 调整查询缓存的大小，
     *          使用 innodb_buffer_pool_size 调整缓冲区的大小
     *
     *      20.explain的执行计划
     *          执行计划是SQL语句经过查询分析器后得到的 抽象语法树 和 相关表的统计信息 作出的一个查询方案，这个方案是由查询优化器自动分析产生的。由于是动态数据采样统计分析出来的结果，所以可能会存在分析错误的情况，
     *          也就是存在执行计划并不是最优的情况。通过explain关键字知道MySQL是如何执行SQL查询语句的，分析select 语句的性能瓶颈，从而改进我们的查询，explain的结果如下
     *          id | select_type | table | type | possible_keys | key | key_len |ref | rows | extra
     *          重要的有id、type、key、key_len、rows、extra：
     *
     *          （1）id：id列可以理解为SQL执行顺序的标识，有几个select 就有几个id
     *              id值不同：id值越大优先级越高，越先被执行；
     *              id值相同：从上往下依次执行；
     *              id列为null：表示这是一个结果集，不需要使用它来进行查询
     *          （2）select_type：查询的类型，主要用于区分普通查询、联合查询、子查询等复杂的查询；
     *          （3）table：表示 explain 的一行正在访问哪个表
     *          （4）type：访问类型，即MySQL决定如何查找表中的行。依次从好到差：
     *              system > const > eq_ref > ref > fulltext > ref_or_null > index_merge > unique_subquery > index_subquery > range > index > ALL，
     *              除了all之外，其他的 type 类型都可以使用到索引，除了 index_merge 之外，其他的type只可以用到一个索引。一般要求type为 ref 级别，范围查找需要达到 range 级别。
     *              system：表中只有一条数据匹配（等于系统表），可以看成 const 类型的特例
     *                  const：通过索引一次就找到了，表示使用主键索引或者唯一索引
     *                  eq_ref：主键或者唯一索引中的字段被用于连接使用，只会返回一行匹配的数据
     *                  ref：普通索引扫描，可能返回多个符合查询条件的行。
     *                  fulltext：全文索引检索，全文索引的优先级很高，若全文索引和普通索引同时存在时，mysql不管代价，优先选择使用全文索引。
     *                  ref_or_null：与ref方法类似，只是增加了null值的比较。
     *                  index_merge：表示查询使用了两个以上的索引，索引合并的优化方法，最后取交集或者并集，常见and ，or的条件使用了不同的索引。
     *                  unique_subquery：用于where中的in形式子查询，子查询返回不重复值唯一值；
     *                  index_subquery：用于in形式子查询使用到了辅助索引或者in常数列表，子查询可能返回重复值，可以使用索引将子查询去重。
     *                  range：索引范围扫描，常见于使用>,<,between ,in ,like等运算符的查询中。
     *                  index：索引全表扫描，把索引树从头到尾扫描一遍；
     *                  all：遍历全表以找到匹配的行（Index与ALL虽然都是读全表，但index是从索引中读取，而ALL是从硬盘读取）
     *                  NULL：MySQL在优化过程中分解语句，执行时甚至不用访问表或索引
     *
     *          （5）possible_keys：查询时可能使用到的索引
     *          （6）key：实际使用哪个索引来优化对该表的访问
     *          （7）key_len：实际上用于优化查询的索引长度，即索引中使用的字节数。通过这个值，可以计算出一个多列索引里实际使用了索引的哪写字段。
     *          （8）ref：显示哪个字段或者常量与key一起被使用
     *          （9）rows：根据表统计信息及索引选用情况，大致估算此处查询需要读取的行数，不是精确值。
     *          （10）extra：其他的一些额外信息
     *              using index：使用覆盖索引
     *              using index condition：查询的列未被索引覆盖，where筛选条件使用了索引
     *              using temporary：用临时表保存中间结果，常用于 group by 和 order by 操作中，通常是因为 group by 的列上没有索引，也有可能是因为同时有group by和order by，但group by和order by的列又不一样，一般看到它说明查询需要优化了
     *              using filesort：MySQL有两种方式对查询结果进行排序，一种是使用索引，另一种是filesort（基于快排实现的外部排序，性能比较差），当数据量很大时，这将是一个CPU密集型的过程，所以可以通过建立合适的索引来优化排序的性能
     */

    /**
     *  20.MySQL的主从复制
     *      20.1.MySQL主从复制的原理
     *          Slave从Master获取binlog二进制日志文件，然后再将日志文件解析成相应的SQL语句在从服务器上重新执行一遍主服务器的操作，通过这种方式来保证数据的一致性。由于主从复制的过程是异步复制的，
     *          因此Slave和Master之间的数据有可能存在延迟的现象，只能保证数据最终的一致性。在master和slave之间实现整个复制过程主要由三个线程来完成：
     *              （1）Slave SQL thread线程：创建用于读取relay log中继日志并执行日志中包含的更新，位于slave端
     *              （2）Slave I/O thread线程：读取 master 服务器Binlog Dump线程发送的内容并保存到slave服务器的relay log中继日志中，位于slave端：
     *              （3）Binlog dump thread线程(也称为IO线程)：将bin-log二进制日志中的内容发送到slave服务器，位于master端
     *          注意：如果一台主服务器配两台从服务器那主服务器上就会有两个Binlog dump 线程，而每个从服务器上各自有两个线程；
     *      20.2.主从复制流程
     *          （1）master服务器在执行SQL语句之后，记录在binlog二进制文件中；
     *          （2）slave端的IO线程连接上master端，并请求从指定bin log日志文件的指定pos节点位置（或者从最开始的日志）开始复制之后的日志内容。
     *          （3）master端在接收到来自slave端的IO线程请求后，通知负责复制进程的IO线程，根据slave端IO线程的请求信息，读取指定binlog日志指定pos节点位置之后的日志信息，然后返回给slave端的IO线程。该返回信息中除了binlog日志所包含的信息之外，还包括本次返回的信息在master端的binlog文件名以及在该binlog日志中的pos节点位置。
     *          （4）slave端的IO线程在接收到master端IO返回的信息后，将接收到的binlog日志内容依次写入到slave端的relay log文件的最末端，并将读取到的master端的binlog文件名和pos节点位置记录到master-info文件中（该文件存slave端），以便在下一次同步的候能够告诉master从哪个位置开始进行数据同步；
     *          （5）slave端的SQL线程在检测到relay log文件中新增内容后，就马上解析该relay log文件中的内容，然后还原成在master端真实执行的那些SQL语句，再按顺序依次执行这些SQL语句，从而到达master端和slave端的数据一致性；
     *      20.3 主从复制的好处
     *          （1）读写分离，通过动态增加从服务器来提高数据库的性能，在主服务器上执行写入和更新，在从服务器上执行读功能。
     *          （2）提高数据安全，因为数据已复制到从服务器，从服务器可以终止复制进程，所以，可以在从服务器上备份而不破坏主服务器相应数据。
     *          （3）在主服务器上生成实时数据，而在从服务器上分析这些数据，从而提高主服务器的性能。
     *      20.4 MySQL支持的复制类型及其优缺点
     *          binlog日志文件有两种格式，一种是Statement-Based（基于语句的复制），另一种是Row-Based（基于行的复制）。默认格式为Statement-Based，
     *          如果想改变其格式在开启服务的时候使用 -binlog-format 选项，其具体命令如下：
     *              mysqld_safe –user=msyql –binlog-format=格式 &
     *          （1）基于语句的复制（Statement-Based）：在主服务器上执行的SQL语句，在从服务器上执行同样的语句。效率比较高。 一旦发现没法精确复制时，会自动选着基于行的复制。
     *              优点：
     *                  a.因为记录的SQL语句，所以占用更少的存储空间。binlog日志包含了描述数据库操作的事件，但这些事件包含的情况只是对数据库进行改变的操作，例如 insert、update、create、delete等操作。相反对于select、desc等类似的操作并不会去记录。
     *                  b.binlog日志文件记录了所有的改变数据库的语句，所以此文件可以作为数据库的审核依据。
     *              缺点：
     *              a.不安全，不是所有的改变数据的语句都会被记录。对于非确定性的行为不会被记录。例如：对于 delete 或者 update 语句，如果使用了 limit 但是并没有 order by ，这就属于非确定性的语句，就不会被记录。
     *              b.对于没有索引条件的update，insert……select 语句，必须锁定更多的数据，降低了数据库的性能。
 *             （2）基于行的复制（Row-Based）：把改变的内容复制过去，而不是把命令在从服务器上执行一遍，从mysql5.0开始支持；
     *              优点：
     *                  a.所有的改变都会被复制，这是最安全的复制方式；
     *                  b.对于 update、insert……select等语句锁定更少的行；
     *              缺点：
     *                  a.不能通过binlog日志文件查看什么语句执行了，也无从知道在从服务器上接收到什么语句，我们只能看到什么数据改变。
     *                  b.因为记录的是数据，所以说binlog日志文件占用的存储空间要比Statement-based大。
     *                  c.对于数据量大的操作其花费的时间有更长。
 *              （3）混合类型的复制：默认采用基于语句的复制，一旦发现基于语句的无法精确的复制时，就会采用基于行的复制
     * 21.读写分离
     *      实现原理:
     *          读写分离解决的是，数据库的写操作，影响了查询的效率，适用于读远大于写的场景。读写分离的实现基础是主从复制，主数据库利用主从复制将自身数据的改变同步到从数据库集群中，然后主数据库负责处理写操作
     *          （当然也可以执行读操作），从数据库负责处理读操作，不能执行写操作。并可以根据压力情况，部署多个从数据库提高读操作的速度，减少主数据库的压力，提高系统总体的性能。
     *      读写分离提高性能的原因:
     *          （1）增加物理服务器，负荷分摊；
     *          （2）主从只负责各自的写和读，极大程度的缓解X锁和S锁争用；
     *          （3）从库可配置MyISAM引擎，提升查询性能以及节约系统开销；
     *          （4）主从复制另外一大功能是增加冗余，提高可用性，当一台数据库服务器宕机后能通过调整另外一台从库来以最快的速度恢复服务。
     *       Mysql读写分写的实现方式：
     *          （1）基于程序代码内部实现：在代码中根据select 、insert进行路由分类。优点是性能较好，因为程序在代码中实现，不需要增加额外的硬件开支，缺点是需要开发人员来实现，运维人员无从下手。
     *          （2）基于中间代理层实现：代理一般介于应用服务器和数据库服务器之间，代理数据库服务器接收到应用服务器的请求后根据判断后转发到后端数据库，有以下代表性的代理层。
     *
     * 22.分库分表：垂直分表、垂直分库、水平分表、水平分库
     *      读写分离解决的是数据库读写操作的压力，但是没有分散数据库的存储压力，利用分库分表可以解决数据库的储存瓶颈，并提升数据库的查询效率。
     *      22.1 垂直拆分：
     *          a.垂直分表：将一个表按照字段分成多个表，每个表存储其中一部分字段。一般会将常用的字段放到一个表中，将不常用的字段放到另一个表中。
     *              优点：
     *                  避免IO竞争减少锁表的概率。因为大的字段效率更低，第一，大字段占用的空间更大，单页内存储的行数变少，会使得IO操作增多；第二数据量大，需要的读取时间长。
     *                  可以更好地提升热门数据的查询效率。
     *          b.垂直分库：按照业务模块的不同，将表拆分到不同的数据库中，适合业务之间的耦合度非常低、业务逻辑清晰的系统。
     *              优点：
     *                  降低业务中的耦合，方便对不同的业务进行分级管理
     *                  可以提升IO、数据库连接数、解决单机硬件存储资源的瓶颈问题
     *          垂直拆分（分库、分表）的缺点：
     *              主键出现冗余，需要管理冗余列
     *              事务的处理变得复杂
     *              仍然存在单表数据量过大的问题
     *        22.2 水平拆分:
     *          a.水平分表：在同一个数据库内，把同一个表的数据按照一定规则拆分到多个表中
     *              优点：
     *                  解决了单表数据量过大的问题
 *                      避免IO竞争并减少锁表的概率
     *          b.水平分库：把同一个表的数据按照一定规则拆分到不同的数据库中，不同的数据库可以放到不同的服务器上。
     *              优点：
     *                  解决了单库大数据量的瓶颈问题
     *                  IO冲突减少，锁的竞争减少，某个数据库出现问题不影响其他数据库，提高了系统的稳定性和可用性
     *          水平拆分（分表、分库）的缺点：
     *                  分片事务一致性难以解决
     *                  跨节点JOIN性能差，逻辑会变得复杂
     *                  数据扩展难度大，不易维护
     *
     * 23.分库分表存在的问题的解决：
     *      （1）事务的问题：
     *          a.方案一：使用分布式事务：
     *              优点：由数据库管理，简单有效。
     *              缺点：性能代价高，特别是shard越来越多。
     *          b.方案二：程序与数据库共同控制实现，原理就是将一个跨多个数据库的分布式事务分解成多个仅存在于单一数据库上面的小事务，并交由应用程序来总体控制各个小事务。
     *              优点：性能上有优势；
     *              缺点：需要在应用程序在事务上做灵活控制。如果使用了spring的事务管理，改动起来会面临一定的困难。
 *          （2）跨节点 Join 的问题：
     *          解决该问题的普遍做法是分两次查询实现：在第一次查询的结果集中找出关联数据的id，根据这些id发起第二次请求得到关联数据。
     *      （3）跨节点count，order by，group by，分页和聚合函数问题：
     *          由于这类问题都需要基于全部数据集合进行计算。多数的代理都不会自动处理合并工作，解决方案：与解决跨节点join问题的类似，分别在各个节点上得到结果后在应用程序端进行合并。
     *          和 join 不同的是每个结点的查询可以并行执行，因此速度要比单一大表快很多。但如果结果集很大，对应用程序内存的消耗是一个问题。
     *
     *  24.分库分表后，ID键如何处理？
     *      分库分表后不能每个表的ID都是从1开始，所以需要一个全局ID，设置全局ID主要有以下几种方法：
     *      （1）UUID：
     *          优点：本地生成ID，不需要远程调用，全局唯一不重复。
     *          缺点：占用空间大，不适合作为索引。
     *      （2）数据库自增ID：在分库分表表后使用数据库自增ID，需要一个专门用于生成主键的库，每次服务接收到请求，先向这个库中插入一条没有意义的数据，获取一个数据库自增的ID，利用这个ID去分库分表中写数据。
     *          优点：简单易实现。
     *          缺点：在高并发下存在瓶颈。
     *      （3）Redis生成ID：
     *          优点：不依赖数据库，性能比较好。
     *          缺点：引入新的组件会使得系统复杂度增加
     *      （4）Twitter的snowflake算法：是一个64位的long型的ID，其中有1bit是不用的，41bit作为毫秒数，10bit作为工作机器ID，12bit作为序列号。
     *          1bit：第一个bit默认为0，因为二进制中第一个bit为1的话为负数，但是ID不能为负数.
     *          41bit：表示的是时间戳，单位是毫秒。
     *          10bit：记录工作机器ID，其中5个bit表示机房ID，5个bit表示机器ID。
     *          12bit：用来记录同一毫秒内产生的不同ID。
     *      （5）美团的Leaf分布式ID生成系统，美团点评分布式ID生成系统：
     *          在复杂分布式系统中，往往需要对大量的数据和消息进行唯一标识。如在美团点评的金融、支付、餐饮、酒店、猫眼电影等产品的系统中，数据日渐增长，对数据分库分表后需要有一个唯一ID来标识一条数据或消息，
     *          数据库的自增ID显然不能满足需求；特别一点的如订单、骑手、优惠券也都需要有唯一ID做标识。此时一个能够生成全局唯一ID的系统是非常必要的。概括下来，那业务系统对ID号的要求有哪些呢？
     *              a.全局唯一性：不能出现重复的ID号，既然是唯一标识，这是最基本的要求。
     *              b.趋势递增：在MySQL InnoDB引擎中使用的是聚集索引，由于多数RDBMS使用B-tree的数据结构来存储索引数据，在主键的选择上面我们应该尽量使用有序的主键保证写入性能。
     *              c.单调递增：保证下一个ID一定大于上一个ID，例如事务版本号、IM增量消息、排序等特殊需求。
     *              d.信息安全：如果ID是连续的，恶意用户的扒取工作就非常容易做了，直接按照顺序下载指定URL即可；如果是订单号就更危险了，竞对可以直接知道我们一天的单量。所以在一些应用场景下，会需要ID无规则、不规则。
     *          上述123对应三类不同的场景，3和4需求还是互斥的，无法使用同一个方案满足。
     *          同时除了对ID号码自身的要求，业务还对ID号生成系统的可用性要求极高，想象一下，如果ID生成系统瘫痪，整个美团点评支付、优惠券发券、骑手派单等关键动作都无法执行，这就会带来一场灾难。
     *
     *  25.分区
     *      分区就是将表的数据按照特定规则存放在不同的区域，也就是将表的数据文件分割成多个小块，在查询数据的时候，只要知道数据数据存储在哪些区域，然后直接在对应的区域进行查询，不需要对表数据进行全部的查询，提高查询的性能。
     *      同时，如果表数据特别大，一个磁盘磁盘放不下时，我们也可以将数据分配到不同的磁盘去，解决存储瓶颈的问题，利用多个磁盘，也能够提高磁盘的IO效率，提高数据库的性能。在使用分区表时，
     *      需要注意分区字段必须放在主键或者唯一索引中、每个表最大分区数为1024；常见的分区类型有：Range分区、List分区、Hash分区、Key分区
     *      （1）Range分区：按照连续的区间范围进行分区
     *      （2）List分区：按照给定的集合中的值进行选择分区。
     *      （3）Hash分区：基于用户定义的表达式的返回值进行分区，该表达式使用将要插入到表中的这些行的列值进行计算。这个函数可以包含MySQL中有效的、产生非负整数值的任何表达式。
     *      （4）Key分区：类似于按照HASH分区，区别在于Key分区只支持计算一列或多列，且key分区的哈希函数是由 MySQL 服务器提供。
     *      表分区的优点：
                (1)可伸缩性：
                    将分区分在不同磁盘，可以解决单磁盘容量瓶颈问题，存储更多的数据，也能解决单磁盘的IO瓶颈问题。
                (2)提升数据库的性能：
                    减少数据库检索时需要遍历的数据量，在查询时只需要在数据对应的分区进行查询。
                    避免Innodb的单个索引的互斥访问限制
                    对于聚合函数，例如sum()和count()，可以在每个分区进行并行处理，最终只需要统计所有分区得到的结果
                (3)方便对数据进行运维管理：
                    方便管理，对于失去保存意义的数据，通过删除对应的分区，达到快速删除的作用。比如删除某一时间的历史数据，直接执行truncate，或者直接drop整个分区，这比detele删除效率更高；
                    在某些场景下，单个分区表的备份很恢复会更有效率。

        26.主键一般用自增ID还是UUID？
         （1）自增ID：
             使用自增ID的好处：
                 字段长度较 UUID 会小很多。
                 数据库自动编号，按顺序存放，利于检索
                 无需担心主键重复问题
             使用自增ID的缺点：
                 因为是自增，在某些业务场景下，容易被其他人查到业务量。
                 发生数据迁移时，或者表合并时会非常麻烦
                 在高并发的场景下，竞争自增锁会降低数据库的吞吐能力
        （2）UUID：通用唯一标识码，UUID是基于当前时间、计数器和硬件标识等数据计算生成的。
             使用UUID的优点：
                 唯一标识，不用考虑重复问题，在数据拆分、合并时也能达到全局的唯一性。
                 可以在应用层生成，提高数据库的吞吐能力。
                 无需担心业务量泄露的问题。
             使用UUID的缺点：
                 因为UUID是随机生成的，所以会发生随机IO，影响插入速度，并且会造成硬盘的使用率较低。
                 UUID占用空间较大，建立的索引越多，造成的影响越大。
                 UUID之间比较大小较自增ID慢不少，影响查询速度。
         一般情况下，MySQL推荐使用自增ID，因为在MySQL的 InnoDB 存储引擎中，主键索引是聚簇索引，主键索引的B+树的叶子节点按照顺序存储了主键值及数据，如果主键索引是自增ID，只需要按顺序往后排列即可，
         如果是UUID，ID是随机生成的，在数据插入时会造成大量的数据移动，产生大量的内存碎片，造成插入性能的下降。
     */

    /**
       27.视图View
            视图是从一个或者多个表（或视图）导出的表，其内容由查询定义。视图是一个虚拟表，数据库中只存储视图的定义，不存储视图对应的数据，在对视图的数据进行操作时，系统根据视图的定义去操作相应的基本表。
            可以说，视图是在基本表之上建立的表，它的结构和内容都来自基本表，依据基本表存在而存在。一个视图可以对应一个基本表，也可以对应多个基本表。视图是基本表的抽象和在逻辑意义上建立的新关系。
            （1）视图的优点：
                简化了操作，把经常使用的数据定义为视图
                安全性，用户只能查询和修改能看到的数据
                逻辑上的独立性，屏蔽了真实表的结构带来的影响
            （2）视图的缺点：
                性能差，数据库必须把对视图的查询转化成对基本表的查询，如果这个视图是由一个复杂的多表查询所定义，那么，即使是视图的一个简单查询，数据库也要把它变成一个复杂的结合体，需要花费一定的时间。

       28.存储过程Procedure
            SQL语句需要先编译然后执行，而存储过程就是一组为了完成特定功能的SQL语句集，经过编译后存储在数据库中，用户通过制定存储过程的名字并给定参数来调用它。
            用程序也可以实现操作数据库的复杂逻辑，那为什么需要存储过程呢？主要是因为使用程序调用API执行，其效率相对较慢，应用程序需通过引擎把SQL语句交给MYSQL引擎来执行，那还不如直接让MySQL负责它最精通最能够完成的工作。
            存储过程的优点：
                （1）标准组件式编程：存储过程创建后，可以在程序中被多次调用，而不必重新编写该存储过程的SQL语句。并且DBA可以随时对存储过程进行修改，对应用程序源代码毫无影响。
                （2）更快的执行速度：如果某一操作包含大量的Transaction-SQL代码或分别被多次执行，那么存储过程要比批处理的执行速度快很多。因为存储过程是预编译的，在首次运行一个存储过程时查询，优化器对其进行分析优化，并且给出最终被存储在系统表中的执行计划。而批处理的Transaction-SQL语句在每次运行时都要进行编译和优化，速度相对要慢一些。
                （3）增强SQL语言的功能和灵活性：存储过程可以用控制语句编写，有很强的灵活性，可以完成复杂的判杂的断和较复运算。
                （4）减少网络流量：针对同一个数据库对象的操作（如查询、修改），如果这一操作所涉及的Transaction-SQL语句被组织进存储过程，那么当在客户计算机上调用该存储过程时，网络中传送的只是该调用语句，从而大大减少网络流量并降低了网络负载。
                （5）作为一种安全机制来充分利用：通过对执行某一存储过程的权限进行限制，能够实现对相应的数据的访问权限的限制，避免了非授权用户对数据的访问，保证了数据的安全。

       29.触发器Trigger
            触发器是与表有关的数据库对象，当触发器所在表上出现指定事件并满足定义条件的时候，将执行触发器中定义的语句集合。触发器的特性可以应用在数据库端确保数据的完整性。触发器是一个特殊的存储过程，
            不同的是存储过程要用call来调用，而触发器不需要使用call，也不需要手工调用，它在插入，删除或修改特定表中的数据时触发执行，它比数据库本身标准的功能有更精细和更复杂的数据控制能力。

       30.游标Cursor：
            游标，就是游动的标识，可以充当指针的作用，使用游标可以遍历查询数据库返回的结果集中的所有记录，但是每次只能提取一条记录，即每次只能指向并取出一行的数据，以便进行相应的操作。当你没有使用游标的时候，
            相当于别人一下给你所有的东西让你拿走；用了游标之后，相当于别人一件一件的给你，这时你可以先看看这个东西好不好，再自己进行选择。

     */
}















































