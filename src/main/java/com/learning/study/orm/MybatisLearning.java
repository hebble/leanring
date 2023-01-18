package com.learning.study.orm;

public class MybatisLearning {
    /**
      1.什么是Mybatis？
           Mybatis是一个半ORM（对象关系映射）框架，它内部封装了JDBC，加载驱动、创建连接、创建statement等繁杂的过程，开发者开发时只需要关注如何编写SQL语句，可以严格控制sql执行性能，灵活度高
           称Mybatis是半自动ORM映射工具，是因为在查询关联对象或关联集合对象时，需要手动编写sql来完成
           由于MyBatis专注于SQL本身，灵活度高，所以比较适合对性能的要求很高，或者需求变化较多的项目，如互联网项目。

      2.MyBatis的实现逻辑
           (1)在 MyBatis 的初始化过程中，会生成一个 Configuration 全局配置对象，里面包含了所有初始化过程中生成对象
           (2)根据 Configuration 创建一个 SqlSessionFactory 对象，用于创建 SqlSession “会话”
           (3)通过 SqlSession 可以获取到 Mapper 接口对应的动态代理对象，去执行数据库的相关操作
           (4)动态代理对象执行数据库的操作，由 SqlSession 执行相应的方法，在他的内部调用 Executor 执行器去执行数据库的相关操作
           (5)在 Executor 执行器中，会进行相应的处理，将数据库执行结果返回

      3.Mybaits的优缺点
           （1）优点：
               a.与JDBC相比，减少了50%以上的代码量，消除了JDBC大量冗余的代码，不需要手动开关连接；
               b.基于SQL语句编程，相当灵活，不会对应用程序或者数据库的现有设计造成任何影响，SQL写在XML里，解除sql与程序代码的耦合，便于统一管理；提供XML标签，支持编写动态SQL语句，并可重用。
               c.很好的与各种数据库兼容（因为MyBatis使用JDBC来连接数据库，所以只要JDBC支持的数据库MyBatis都支持）。
               d.能够与Spring很好的集成；
               e.提供映射标签，支持对象与数据库的ORM字段关系映射；提供对象关系映射标签，支持对象关系组件维护。
           （2）缺点：
               a.SQL语句的编写工作量较大，尤其当字段多、关联表多时，对开发人员编写SQL语句的功底有一定要求。
               b.SQL语句依赖于数据库，导致数据库移植性差，不能随意更换数据库。

      4.#{}和${}的区别是什么？
           ${}是字符串替换，#{}是预处理；使用#{}可以有效的防止SQL注入，提高系统安全性。
           #{}匹配的是一个占位符，相当于 JDBC 中的一个?，会对一些敏感字符进行过滤，编译过后会对传递的值加上双引号，因此可以防止 SQL 注入问题。
           Mybatis在处理${}时，就是把${}直接替换成变量的值。而Mybatis在处理#{}时，会对sql语句进行预处理，将sql中的#{}替换为?号，调用PreparedStatement的set方法来赋值；

      5.Mapper接口的工作原理
            Mapper 接口的工作原理是JDK动态代理，Mybatis运行时会使用JDK动态代理为Mapper接口生成代理对象 MappedProxy，代理对象会拦截接口方法，根据类的全限定名+方法名，
            唯一定位到一个MapperStatement并调用执行器执行所代表的sql，然后将sql执行结果返回。
            Mapper接口里的方法，是不能重载的，因为是使用 全限名+方法名 的保存和寻找策略。
            当调用接口方法时，通过 “接口全限名+方法名”拼接字符串作为key值，可唯一定位一个MapperStatement，因为在Mybatis中，每一个SQL标签，都会被解析为一个MapperStatement对象。

      6.Mybatis的Xml映射文件中，不同的Xml映射文件，id是否可以重复？
           不同的Xml映射文件，如果配置了namespace，那么id可以重复；如果没有配置namespace，那么id不能重复；原因就是namespace+id是作为Map的key使用的，如果没有namespace，就剩下id，
           那么，id重复会导致数据互相覆盖。有了namespace，自然id就可以重复，namespace不同，namespace+id自然也就不同。

           备注：在旧版本的Mybatis中，namespace是可选的，不过新版本的namespace已经是必填项了。

      7.Mybatis是如何进行分页的？分页插件的原理是什么？
            Mybatis使用RowBounds对象进行分页，它是针对ResultSet结果集执行的内存分页，而非物理分页。可以在sql内直接书写带有物理分页的参数来完成物理分页功能，也可以使用分页插件来完成物理分页。
            分页插件的基本原理是使用Mybatis提供的插件接口，实现自定义插件，在插件的拦截方法内拦截待执行的sql，然后重写sql，根据dialect方言，添加对应的物理分页语句和物理分页参数。

      8.Mybatis是否支持延迟加载？如果支持，它的实现原理是什么？
             Mybatis仅支持association关联对象和collection关联集合对象的延迟加载，association指的就是一对一，collection指的就是一对多查询。在Mybatis配置文件中，可以配置是否启用延迟加载lazyLoadingEnabled=true|false。
             延迟加载的基本原理是，使用CGLIB创建目标对象的代理对象，当调用目标方法时，进入拦截器方法，比如调用a.getB().getName()，拦截器invoke()方法发现a.getB()是null值，那么就会单独发送事先保存好的查询关联B对象的sql，把B查询上来，然后调用a.setB(b)，于是a的对象b属性就有值了，接着完成a.getB().getName()方法的调用。

      9.Mybatis的一级、二级缓存
           （1）一级缓存: 基于 PerpetualCache 的 HashMap 本地缓存，其存储作用域为 Session，当 Session flush 或 close 之后，该 Session 中的所有 Cache 就将清空，默认打开一级缓存。
           （2）二级缓存与一级缓存其机制相同，默认也是采用 PerpetualCache，HashMap 存储，不同在于其存储作用域为 Mapper(Namespace)，并且可自定义存储源，如 Ehcache。默认不打开二级缓存，要开启二级缓存，使用二级缓存属性类需要实现Serializable序列化接口(可用来保存对象的状态),可在它的映射文件中配置 ；
           （3）对于缓存数据更新机制，当某一个作用域(一级缓存 Session/二级缓存Namespaces)的进行了C/U/D 操作后，默认该作用域下所有 select 中的缓存将被 clear 掉并重新更新，如果开启了二级缓存，则只根据配置判断是否刷新。

      10.如何获取自动生成的(主)键值?
           1.在 <insert /> 标签中添加useGeneratedKeys="true"等属性
           2.在 <insert /> 标签内添加<selectKey />标签
               <selectKey keyProperty="id" keuColumn="id" resulyType="long" order="AFTER">
                   select last_insert_id()
               </selectKey>

      11.MyBatis与Hibernate有哪些不同？
             （1）Mybatis和hibernate不同，它不完全是一个ORM框架，因为MyBatis需要程序员自己编写Sql语句。
             （2）Mybatis直接编写原生态sql，可以严格控制sql执行性能，灵活度高，非常适合对关系数据模型要求不高的软件开发，因为这类软件需求变化频繁，一但需求变化要求迅速输出成果。但是灵活的前提是mybatis无法做到数据库无关性，如果需要实现支持多种数据库的软件，则需要自定义多套sql映射文件，工作量大。
             （3）Hibernate对象/关系映射能力强，数据库无关性好，对于关系模型要求高的软件，如果用hibernate开发可以节省很多代码，提高效率。
     */
}





































