package com.learning.study.spring;

/**
 * https://blog.csdn.net/a745233700/article/details/80959716/
 * https://zhuanlan.zhihu.com/p/499197180
 */
public class SpringLearning {

    /**
    1.谈谈你对Spring的理解？
       Spring框架是一个轻量级的开源框架, 平时接触到最多的还是IoC和AOP两个特性。IoC指的是控制反转，把对象的创建和依赖关系的维护交给Spring容器去管理。
       Spring通过工厂模式、反射机制等技术管理对象的作用域和生命周期。AoP一般称为面向切面编程，是面向对象的一种补充，将程序中独立于其他功能的方法抽取出来，使Java开发模块化，仅需专注于主业务即可

    2.Spring Bean的生命周期
       Spring启动，查找并加载需要被Spring管理的bean，进行Bean的实例化
       Bean实例化后对将Bean的引入和值注入到Bean的属性中

       如果Bean实现了BeanNameAware接口的话，Spring将Bean的Id传递给setBeanName()方法
       如果Bean实现了BeanFactoryAware接口的话，Spring将调用setBeanFactory()方法，将BeanFactory容器实例传入
       如果Bean实现了ApplicationContextAware接口的话，Spring将调用Bean的setApplicationContext()方法，将bean所在应用上下文引用传入进来。

       如果Bean实现了BeanPostProcessor接口，Spring就将调用他们的postProcessBeforeInitialization()方法。
       如果Bean 实现了InitializingBean接口，Spring将调用他们的afterPropertiesSet()方法。类似的，如果bean使用init-method声明了初始化方法，该方法也会被调用
       如果Bean 实现了BeanPostProcessor接口，Spring就将调用他们的postProcessAfterInitialization()方法。

       此时，Bean已经准备就绪，可以被应用程序使用了。他们将一直驻留在应用上下文中，直到应用上下文被销毁。
       如果bean实现了DisposableBean接口，Spring将调用它的destory()接口方法，同样，如果bean使用了destory-method 声明销毁方法，该方法也会被调用。

       总体的生命周期流程(记):
           (1)加载spring bean,bean实例化
           (2)bean的属性赋值
           (3)BeanNameAware.setBeanName()
               (4)BeanClassLoaderAware.setBeanClassLoader()
           (5)BeanFactoryAware.setBeanFactory()
               (6)EnvironmentAware.setEnvironment()
               (7)EmbeddedValueResolverAware.setEmbeddedValueResolver()
               (8)ResourceLoaderAware.setResourceLoader()
               (9)ApplicationEventPublisherAware.setApplicationEventPublisher()
               (10)MessageSourceAware.setMessageSource()
           (11)ApplicationContextAware.setApplicationContext()
               (12)ServletContextAware.setServletContext()
           (13)BeanPostProcessor.postProcessBeforeInitialization()
           (14)InitializingBean.afterPropertiesSet()
           (15)调用自定义初始化方法
           (16)BeanPostProcessor.postProcessAfterInitialization()
           (17)bean可以使用了
           (18)容器关闭
           (19)DisposableBean.dispose()
           (20)自定义销毁方法
           (21)结束

    3.详细讲解一下核心容器（spring context应用上下文) 模块
       这是基本的 Spring 模块，提供 spring 框架的基础功能, BeanFactory 是任何以 spring 为基础的应用的核心.它使 Spring 成为一个容器 。BeanFactory是工厂模式的一个实现，提供了控制反转功能，用来把应用的配置和依赖从真正的应用代码中分离。
       最常用的就是org.springframework.beans.factory.xml.XmlBeanFactory ，它根据XML文件中的定义加载beans。该容器从XML 文件读取配置元数据并用它去创建一个完全配置的系统或应用。

    4.Spring的IoC理解
       什么是IOC:
           IOC，Inversion of Control，控制反转，指将对象的控制权转移给Spring框架，由 Spring 来负责控制对象的生命周期（比如创建、销毁）和对象间的依赖关系。
       什么是DI:
           IoC 的一个重点就是在程序运行时，动态的向某个对象提供它所需要的其他对象，这一点是通过DI（Dependency Injection，依赖注入）来实现的,而 Spring 的 DI 具体就是通过反射实现注入的
       IoC的原理:
           Spring 的 IoC 的实现原理就是工厂模式加反射机制

    5.Spring的AOP理解
       AOP，一般称为面向切面，作为面向对象的一种补充，用于将那些与业务无关，但却对多个对象产生影响的公共行为和逻辑，抽取并封装为一个可重用的模块，这个模块被命名为“切面”（Aspect），减少系统中的重复代码,降低了模块间的耦合度，提高系统的可维护性
       可用于权限认证、日志、事务处理
       AOP实现的关键在于 代理模式，AOP代理主要分为静态代理和动态代理。静态代理的代表为AspectJ；动态代理则以Spring AOP为代表。
           （1）AspectJ是静态代理，也称为编译时增强，AOP框架会在编译阶段生成AOP代理类，并将AspectJ(切面)织入到Java字节码中，运行的时候就是增强之后的AOP对象。
           （2）Spring AOP使用的动态代理，所谓的动态代理就是说AOP框架不会去修改字节码，而是每次运行时在内存中临时为方法生成一个AOP对象，这个AOP对象包含了目标对象的全部方法，并且在特定的切点做了增强处理，并回调原对象的方法。

       Spring AOP中的动态代理主要有两种方式，JDK动态代理和CGLIB动态代理：
           (1)JDK动态代理只提供接口的代理，不支持类的代理，要求被代理类实现接口。JDK动态代理的核心是InvocationHandler接口和Proxy类
             在获取代理对象时，使用Proxy类来动态创建目标类的代理类（即最终真正的代理类，这个类继承自Proxy并实现了我们定义的接口），
             当代理对象调用真实对象的方法时， InvocationHandler 通过invoke()方法反射来调用目标类中的代码，动态地将横切逻辑和业务编织在一起；
           (2)如果被代理类没有实现接口，那么Spring AOP会选择使用CGLIB来动态代理目标类。CGLIB（Code Generation Library），是一个代码生成的类库，
             可以在运行时动态的生成指定类的一个子类对象，并覆盖其中特定方法并添加增强代码，从而实现AOP。CGLIB是通过继承的方式做的动态代理，因此
             如果某个类被标记为final，那么它是无法使用CGLIB做动态代理的。
           (3)区别：JDK代理只能对实现接口的类生成代理；CGlib是针对类实现代理，对指定的类生成一个子类，并覆盖其中的方法，这种通过继承类的实现方式，不能代理final修饰的类。

       静态代理与动态代理区别:
           在于生成AOP代理对象的时机不同，相对来说AspectJ的静态代理方式具有更好的性能，但是AspectJ需要特定的编译器进行处理，而Spring AOP则无需特定的编译器处理

    6.BeanFactory和ApplicationContext有什么区别？
        BeanFactory和ApplicationContext是Spring的两大核心接口，都可以当做Spring的容器。
          （1）BeanFactory是Spring里面最底层的接口，是IoC的核心，定义了IoC的基本功能，包含了各种Bean的定义、加载、实例化，依赖注入和生命周期管理。ApplicationContext接口作为BeanFactory的子类，除了提供BeanFactory所具有的功能外，还提供了更完整的框架功能：
               继承MessageSource，因此支持国际化。
               资源文件访问，如URL和文件（ResourceLoader）。
               载入多个（有继承关系）上下文（即同时加载多个配置文件） ，使得每一个上下文都专注于一个特定的层次，比如应用的web层。
               提供在监听器中注册bean的事件。
          （2）BeanFactroy采用的是延迟加载形式来注入Bean的，只有在使用到某个Bean时(调用getBean())，才对该Bean进行加载实例化。
               ApplicationContext，它是在容器启动时，一次性创建了所有的Bean
          （3）BeanFactory和ApplicationContext都支持BeanPostProcessor、BeanFactoryPostProcessor的使用，但两者之间的区别是：BeanFactory需要手动注册，而ApplicationContext则是自动注册。
          （4）BeanFactory通常以编程的方式被创建，ApplicationContext还能以声明的方式创建，如使用ContextLoader。

    7.Spring中bean的作用域
       （1）singleton：默认作用域，单例bean，每个容器中只有一个bean的实例。
       （2）prototype：为每一个bean请求创建一个实例。
       （3）request：为每一个request请求创建一个实例，在请求完成以后，bean会失效并被垃圾回收器回收。
       （4）session：与request范围类似，同一个session会话共享一个实例，不同会话使用不同的实例。
       （5）global-session：全局作用域，所有会话共享一个实例。如果想要声明让所有会话共享的存储变量的话，那么这全局变量需要存储在global-session中。

    8.Spring框架中的Bean是线程安全的么？如果线程不安全，那么如何处理？
       Spring容器本身并没有提供Bean的线程安全策略，因此可以说Spring容器中的Bean本身不具备线程安全的特性，但是具体情况还是要结合Bean的作用域来讨论。
          （1）对于prototype作用域的Bean，每次都创建一个新对象，也就是线程之间不存在Bean共享，因此不会有线程安全问题。
          （2)对于singleton作用域的Bean，所有的线程都共享一个单例实例的Bean，因此是存在线程安全问题的
               有状态Bean(Stateful Bean) ：就是有实例变量的对象，可以保存数据，是非线程安全的。
               无状态Bean(Stateless Bean)：就是没有实例变量的对象，不能保存数据，是不变类，是线程安全的。

           对于有状态的bean（比如Model和View），就需要自行保证线程安全，最浅显的解决办法就是将有状态的bean的作用域由“singleton”改为“prototype”。
           也可以采用ThreadLocal解决线程安全问题，为每个线程提供一个独立的变量副本，不同线程只操作自己线程的副本变量。

    9.Spring基于xml注入bean的几种方式
       set()方法注入；
       构造器注入：①通过index设置参数的位置；②通过type设置参数类型；
       静态工厂注入；
       实例工厂；

    10.Spring如何解决循环依赖问题：
       循环依赖问题在Spring中主要有三种情况：
           （1）通过构造方法进行依赖注入时产生的循环依赖问题。
           （2）通过setter方法进行依赖注入且是在多例（原型）模式下产生的循环依赖问题。
           （3）通过setter方法进行依赖注入且是在单例模式下产生的循环依赖问题。
       在Spring中，只有第（3）种方式的循环依赖问题被解决了，其他两种方式在遇到循环依赖问题时都会产生异常。其实也很好解释：
               第（1）种构造方法注入的情况下，在new对象的时候就会堵塞住了，其实也就是”先有鸡还是先有蛋“的历史难题。
               第（2）种setter方法（多例）的情况下，每一次getBean()时，都会产生一个新的Bean，如此反复下去就会有无穷无尽的Bean产生了，最终就会导致OOM问题的出现。
       Spring在单例模式下的setter方法依赖注入引起的循环依赖问题，主要是通过二级缓存和三级缓存来解决的，其中三级缓存是主要功臣。
       解决的核心原理就是：在对象实例化之后，依赖注入之前，Spring提前暴露的Bean实例的引用在第三级缓存中进行存储。

    11.Spring三大缓存介绍
       Spring中有三个缓存，用于存储单例的Bean实例，这三个缓存是彼此互斥的，不会针对同一个Bean的实例同时存储。如果调用getBean，则需要从三个缓存中依次获取指定的Bean实例。 读取顺序依次是一级缓存 ==> 二级缓存 ==> 三级缓存。
           11.1 一级缓存：Map<String, Object> singletonObjects：
               （1）第一级缓存的作用：
                   用于存储单例模式下创建的Bean实例（已经创建完毕）。
                   该缓存是对外使用的，指的就是使用Spring框架的程序员。
               （2）存储什么数据？
                   K：bean的名称
                   V：bean的实例对象（有代理对象则指的是代理对象，已经创建完毕）
           11.2 第二级缓存：Map<String, Object> earlySingletonObjects：
               （1）第二级缓存的作用：
                   用于存储单例模式下创建的Bean实例（该Bean被提前暴露的引用，该Bean还在创建中）。
                   该缓存是对内使用的，指的就是Spring框架内部逻辑使用该缓存。
                   为了解决第一个classA引用最终如何替换为代理对象的问题（如果有代理对象）
           11.3 第三级缓存：Map<String, ObjectFactory<?>> singletonFactories：
               （1）第三级缓存的作用：
                   通过ObjectFactory对象来存储单例模式下提前暴露的Bean实例的引用（正在创建中）。
                   该缓存是对内使用的，指的就是Spring框架内部逻辑使用该缓存。
                   此缓存是解决循环依赖最大的功臣
               （2）存储什么数据？
                   K：bean的名称
                   V：ObjectFactory，该对象持有提前暴露的bean的引用
               （3）为什么第三级缓存要使用ObjectFactory
                   如果仅仅是解决循环依赖问题，使用二级缓存就可以了，但是如果对象实现了AOP，那么注入到其他bean的时候，并不是最终的代理对象，而是原始的。这时就需要通过三级缓存的ObjectFactory才能提前产生最终的需要代理的对象。
               （4）什么时候将Bean的引用提前暴露给第三级缓存的ObjectFactory持有？
                   时机就是在第一步实例化之后，第二步依赖注入之前，完成此操作。
           11.4 循环依赖的对象在三级缓存中的迁移过程
                (1)A 创建过程中需要 B， 于是 A 将自己放到三级缓存里面，去实例化 B
                (2)B 实例化的时候发现需要 A，于是 B 先查一级缓存，没有，再查二级缓存，还是没有，再查三级缓存, 找到了A，然后把三级缓存中的 A 放到二级缓存，并删除三级缓存中的 A
                (3)B 顺利初始化完毕，将自己放到一级缓存中(此时 B 中的 A 还是创建中状态，并没有完全初始化)，删除三级缓存中的 B
                (4)然后接着回来创建 A，此时 B 已经完成创建，直接从一级缓存中拿到 B，完成 A 的创建，并将 A 添加到单例池，删除二级缓存中的 A

    12.解决构造函数相互注入造成的循环依赖
       前面说Spring可以自动解决单例模式下通过setter()方法进行依赖注入产生的循环依赖问题。而对于通过构造方法进行依赖注入时产生的循环依赖问题没办法自动解决，那针对这种情况，我们可以使用@Lazy注解来解决。
       也就是说，对于类A和类B都是通过构造器注入的情况，可以在A或者B的构造函数的形参上加个@Lazy注解实现延迟加载。
       @Lazy实现原理是，当实例化对象时，如果发现参数或者属性有@Lazy注解修饰，那么就不直接创建所依赖的对象了，而是使用动态代理创建一个代理类。
       比如，类A的创建：A a=new A(B)，需要依赖对象B，发现构造函数的形参上有@Lazy注解，那么就不直接创建B了，而是使用动态代理创建了一个代理类B1，此时A跟B就不是相互依赖了，变成了A依赖一个代理类B1，B依赖A。
       但因为在注入依赖时，类A并没有完全的初始化完，实际上注入的是一个代理对象，只有当他首次被使用的时候才会被完全的初始化。

    13.Spring的自动装配
       在spring中，使用autowire来配置自动装载模式，对象无需自己查找或创建与其关联的其他对象，由容器负责把需要相互协作的对象引用赋予各个对象。
           （1）在Spring框架xml配置中共有5种自动装配：
               no：默认的方式是不进行自动装配的，通过手工设置ref属性来进行装配bean。
               byName：通过bean的名称进行自动装配，如果一个bean的 property 与另一bean 的name 相同，就进行自动装配。
               byType：通过参数的数据类型进行自动装配。
               constructor：利用构造函数进行装配，并且构造函数的参数通过byType进行装配。
               autodetect：自动探测，如果有构造方法，通过 construct的方式自动装配，否则使用 byType的方式自动装配。
            (2)使用@Autowired、@Resource注解来自动装配指定的bean
               在使用@Autowired时，首先在容器中查询对应类型的bean：如果查询结果刚好为一个，就将该bean装配给@Autowired指定的数据；如果查询的结果不止一个，那么@Autowired会根据名称来查找；如果上述查找的结果为空，那么会抛出异常。
            注：@Autowired和@Resource之间的区别：
               (1) @Autowired默认是按照类型装配注入的，默认情况下它要求依赖对象必须存在（可以设置它required属性为false）。
               (2) @Resource默认是按照名称来装配注入的，只有当找不到与名称匹配的bean才会按照类型来装配注入。

    14.Spring事务的实现方式和实现原理
       Spring事务的本质其实就是数据库对事务的支持，没有数据库的事务支持，spring是无法提供事务管理功能的。真正的数据库层的事务提交和回滚是通过binlog或者redo log实现的。

       14.1 Spring事务的种类：
           spring支持编程式事务管理和声明式事务管理两种方式：
               a.编程式事务管理使用TransactionTemplate。
               b.声明式事务管理建立在AOP之上的。其本质是通过AOP功能，对方法前后进行拦截，将事务处理的功能编织到拦截的方法中，也就是在目标方法开始之前启动一个事务，在执行完目标方法之后根据执行情况提交或者回滚事务。
       14.2 spring的事务传播机制：
           spring事务的传播机制说的是，当多个事务同时存在的时候，spring如何处理这些事务的行为。事务传播机制实际上是使用简单的ThreadLocal实现的，所以，如果调用的方法是在新线程调用的，事务传播实际上是会失效的。
           spring事务的传播机制共7中,可以分为3组+1个特殊来分析或者记忆
               (1)REQUIRE组
                   1.REQUIRED:当前存在事务则使用当前的事务, 当前不存在事务则创建一个新的事务(多用于CRUD)
                   2.REQUIRES_NEW:创建新事务, 如果已经存在事务, 则把已存在的事务挂起(相当于两个事务之间没有关系)(多用于日志)
               (2)SUPPORT组
                   1.SUPPORTS:支持事务. 如果当前存在事务则加入该事务, 如果不存在事务则以无事务状态执行(多用于查询)
                   2.NOT_SUPPORTED:不支持事务. 在无事务状态下执行,如果已经存在事务则挂起已存在的事务
               (3)Exception组
                   1.MANDATORY:必须在事务中执行, 如果当前不存在事务, 则抛出异常
                   2.NEVER: 不可在事务中执行, 如果当前存在事务, 则抛出异常
               (4)NESTED:嵌套事务. 如果当前存在事务, 则嵌套执行, 如果当前不存在事务, 则开启新事务
                   嵌套相当于包含关系
           注意: 在同一个类中，一个方法调用另外一个有注解（比如@Async，@Transational）的方法，注解是不会生效的
           原因：spring 在扫描bean的时候会扫描方法上是否包含@Transactional注解，如果包含，spring会为这个bean动态地生成一个子类（即代理类，proxy），代理类是继承原来那个bean的。
               此时，当这个有注解的方法被调用的时候，实际上是由代理类来调用的，代理类在调用之前就会启动transaction。然而，如果这个有注解的方法是被同一个类中的其他方法调用的，
               那么该方法的调用并没有通过代理类，而是直接通过原来的那个bean，所以就不会启动transaction，我们看到的现象就是@Transactional注解无效。
       14.3 实例:
           A: EmployeeService.addEmpByRequired()
           B: DepartmentService.addDeptByRequired()
           A调用B
           (1)A添加@Transactional(propagation = Propagation.REQUIRED)
               a.无论int i =1/0 这个异常出现在哪里(A或B中)，添加员工和添加部门都会回滚。
               b.值得一提的是，如果异常在addDept中，但是在addEmpByRequired把 addDept方法 try，catch了，则会抛出异常：Transaction rolled back because it has been marked as rollback-only 。
               c.如果在addDeptByRequired上添加@Transactional(propagation = Propagation.REQUIRED)，在addEmpByRequired不添加事务，则addDeptByRequired是一个事务，addEmpByRequired并不是一个事务。
                   因为addDeptByRequired开启了一个事务，但是addEmpByRequired并不存在一个事务中。
           (2)A添加@Transactional(propagation = Propagation.REQUIRED)
             B添加@Transactional(propagation = Propagation.NESTED)
               如果B中有异常, B回滚, A捕捉了异常, A不会回滚
       14.4 REQUIRED和NESTED的区别
            REQUIRED中A->B, A捕捉了B的异常, try，catch了，则会抛出异常：Transaction rolled back because it has been marked as rollback-only 。 (事务只能回滚)
            NESTED中A->B,  A捕捉了B的异常, try，catch了, 则不会抛出异常, A也不会回滚
       14.4 Spring中的隔离级别：
           (1)ISOLATION_DEFAULT：这是个 PlatfromTransactionManager 默认的隔离级别，使用数据库默认的事务隔离级别。
           (2)ISOLATION_READ_UNCOMMITTED：读未提交，允许事务在执行过程中，读取其他事务未提交的数据。这种隔离级别会产生脏读，不可重复读和幻像读
           (3)ISOLATION_READ_COMMITTED：读已提交，允许事务在执行过程中，读取其他事务已经提交的数据。这种事务隔离级别可以避免脏读出现，但是可能会出现不可重复读和幻像读。
           (4)ISOLATION_REPEATABLE_READ：可重复读，在同一个事务内，任意时刻的查询结果都是一致的。可能出现幻像读。
           (5)ISOLATION_SERIALIZABLE：所有事务逐个依次执行。除了防止脏读、不可重复读外，还避免了幻像读。

           读取未提交 - 脏读问题
               age:10 -> A事务begin,B事务begin -> A修改age=20 -> B事务提取到age=20, 处理了业务逻辑 -> A事务回滚了, 20就是脏数据 -> 读到了脏数据
           读取已提交 - 不可重复读问题
               解决脏读: age:10 -> A事务begin,B事务begin -> A修改age=20 -> B事务提取到age=10, 处理了业务逻辑 -> A事务回滚了 -> 没有读到脏数据
               不可重复读: age:10 -> A事务begin,B事务begin -> A修改age=20 -> B事务提取到age=10 -> A事务commit -> B再次读取age=20, 处理业务逻辑 -> 出现了多次读取不一样
           可重复读 - 幻读问题
               解决不可重复读: age:10 -> A事务begin,B事务begin -> A修改age=20 -> B事务提取到age=10 -> A事务commit -> B再次读取age=10, 处理业务逻辑 -> 多次读取都一样
               幻读: age:10 -> A事务begin,B事务begin -> B事务修改所有age=20 -> A事务插入一条数据 -> A事务commit -> B事务提交 -> 这时发现有一条数据没有更新到, 出现了幻觉
       14.5 事务的ACID是指什么？
               原子性(Atomic)：事务中各项操作，要么全做要么全不做，任何一项操作的失败都会导致整个事务的失败；
               一致性(Consistent)：事务结束后系统状态是一致的；
               隔离性(Isolated)：并发执行的事务彼此无法看到对方的中间状态；
               持久性(Durable)：事务完成后所做的改动都会被持久化，即使发生灾难性的失败，通过日志和同步备份可以在故障发生后重建数据。

    15.Spring 框架中都用到了哪些设计模式？
       （1）工厂模式：Spring使用工厂模式，通过BeanFactory和ApplicationContext来创建对象
       （2）单例模式：Bean默认为单例模式
       （3）策略模式：例如Resource的实现类，针对不同的资源文件，实现了不同方式的资源获取策略
       （4）代理模式：Spring的AOP功能用到了JDK的动态代理和CGLIB字节码生成技术
       （5）模板方法：可以将相同部分的代码放在父类中，而将不同的代码放入不同的子类中，用来解决代码重复的问题。比如RestTemplate, JmsTemplate, JpaTemplate
       （6）适配器模式：Spring AOP的增强或通知（Advice）使用到了适配器模式，Spring MVC中也是用到了适配器模式适配Controller
       （7）观察者模式：Spring事件驱动模型就是观察者模式的一个经典应用。
       （8）桥接模式：可以根据客户的需求能够动态切换不同的数据源。比如我们的项目需要连接多个数据库，客户在每次访问中根据需要会去访问不同的数据库

    16.单例模式:
        16.1 懒汉式
              public class LazySingleton {
                // 被volatile修饰的变量可以确保多个线程能正常处理
                private volatile static LazySingleton instance = null;

                private LazySingleton() {}

                public static LazySingleton getInstance() {
                    // 第一层判断，如果实例已经创建，跳过
                    if(instance == null) {
                        synchronized(LazySingleton.class) {
                            // 第二层判断，如果实例创建，跳过
                            if(instance == null) {
                                instance = new LazySingleton();
                                             }* 			}
                     }
                    return instance;* 	}
              }
        16.2 饿汉式(spring 缺省模式)
            public class Singleton {
                  private static Singleton instance = new Singleton();
                  private Singleton (){}
                  public static Singleton getInstance() {
                  return instance;
                  }
            }
        16.3 完美模式(静态内部类)
            Holder模式借用了饿汉模式的优势, 就是在加载类(内部类)的同时对instance对象进行初始化, 由于自始至终类只会加载一次, 所以即使在多线程的情况下, 也能够保持单例的性质
            该方法可以实现延迟加载，又可以保证线程安全，不影响系统性能
            public class Singleton {
                private Singleton() {}

                // 静态内部类
                private static class Holder {
                    private final static Singleton instance = new Singleton();
                     }

                public static Singleton getInstance() {
                    return Holder.instance;
                 }
            }

           注意: 全部该类涉及的类(包括内部类和从其余包导入的类)都会在类加载的过程当中加载到,有当某个类初始化以后，才会调用类的静态代码块, 只有当咱们有对类的引用的时候，才会将类初始化
           类初始化过程
              (1)父类静态变量、静态代码块初始化顺序级别一致，谁在前，就先初始化谁(只初始化一次)
              (2)子类静态变量、静态代码块初始化顺序级别一致，谁在前，就先初始化谁(只初始化一次)
              (3)父类成员变量(被子类重写用子类的)、构造代码块初始化顺序级别一致，谁在前，就先初始化谁
              (4)父类构造方法
              (5)子类成员变量(被子类重写用子类的)、构造代码块初始化顺序级别一致，谁在前，就先初始化谁
              (6)子类构造方法
           java中的静态属性和静态方法是可以被继承的，但是不能被子类重写

    17.Spring框架中有哪些不同类型的事件？
       Spring 提供了以下5种标准的事件：
           （1）上下文更新事件（ContextRefreshedEvent）：在调用ConfigurableApplicationContext 接口中的refresh()方法时被触发。
           （2）上下文开始事件（ContextStartedEvent）：当容器调用ConfigurableApplicationContext的Start()方法开始/重新开始容器时触发该事件。
           （3）上下文停止事件（ContextStoppedEvent）：当容器调用ConfigurableApplicationContext的Stop()方法停止容器时触发该事件。
           （4）上下文关闭事件（ContextClosedEvent）：当ApplicationContext被关闭时触发该事件。容器被关闭时，其管理的所有单例Bean都被销毁。
           （5）请求处理事件（RequestHandledEvent）：在Web应用中，当一个http请求（request）结束触发该事件。
       如果一个bean实现了ApplicationListener接口，当一个ApplicationEvent 被发布以后，bean会自动被通知。
     */


















}
