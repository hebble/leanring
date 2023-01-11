package com.learning.study.object;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JUCLearning {
    /**
    1.Atomic原子类与CAS原理
         1.1 Atomic 原子类的原理
             Atomic 原子操作类是基于无锁 CAS + volatile 实现的，并且类中的所有方法都使用 final 修饰，进一步保证线程安全。而 CAS 算法的具体实现方式在于 Unsafe 类中，Unsafe 类的所有方法都是 native 修饰的，也就是说所有方法都是直接调用操作系统底层资源进行执行相应任务。
             Atomic 使用乐观策略，每次操作时都假设没有冲突发生，并采用 volatile 配合 CAS 去修改内存中的变量，如果失败则重试，直到成功为止。
             乐观锁：乐观锁认为竞争不总是发生，因此每次操作共享资源时都不需要加锁，并将“比较-替换”这两个动作作为一个原子操作去修改内存中的变量，一旦发生冲突就重试，直到成功为止。无锁策略就是一种乐观策略，采用 volatile + CAS 来保证线程执行的安全性。
             悲观锁：悲观锁认为每次访问共享资源总会发生冲突，因此每次对共享资源进行操作时，都会去事先申请一个独占的锁，比如 synchronized 和 ReentronLock 都是独占锁。
         1.2 什么是 CAS：Compare And Swap
            1.2.1 CAS 的算法核心思想：
                 执行函数：CAS(V,E,U)，其包含3个参数：内存值V，旧预期值E，要修改的值U。
                     (1)当且仅当 预期值E 和 内存值V 相同时，才会将内存值修改为U并返回true；
                     (2)若V值和E值不同，则说明已经有其他线程做了更新，则当前线程不执行更新操作，但可以选择重新读取该变量再尝试再次修改该变量，也可以放弃操作。
                 CAS 一定要 volatile 变量配合，这样才能保证每次拿到的变量是主内存中最新的那个值，否则旧的预期值E对某条线程来说，永远是一个不会变的值E，只要某次CAS操作失败，永远都不可能成功。由于 CAS 无锁操作中没有锁的存在，因此不可能出现死锁的情况，也就是天生免疫死锁。
            1.2.2 CPU 指令对 CAS 的支持
                 由于 CAS 的步骤很多，那会不会存在一种情况：假设某个线程在判断 V 和 E 相同后，正要赋值时，切换了线程，更改了值，从而造成了数据不一致呢？答案是否定的，因为 CAS 是一种系统原语，原语属于操作系统用语范畴，是由若干条指令组成的，用于完成某个功能的一个过程，
                 并且原语的执行必须是连续的，在执行过程中不允许被中断，也就是说CAS是一条CPU的原子指令，不会造成所谓的数据不一致问题。
            1.2.3 CAS 的 ABA 问题及其解决方案：
                 假设这样一种场景，当第一个线程执行 CAS(V,E,U) 操作，在获取到当前变量V，准备修改为新值U前，另外两个线程已连续修改了两次变量V的值，使得该值又恢复为旧值，这样的话，我们就无法正确判断这个变量是否已被修改过。
                 解决方法：使用带版本的标志或者时间戳解决ABA问题，在更新数据时，只有要更新的数据和版本标识符合期望值，才允许替换。
         1.3 Unsafe 类
             Atomic 中 CAS 操作的执行依赖于 Unsafe 类的方法，Unsafe 类中的所有方法都是 native 修饰的，也就是说所有方法都直接调用操作系统底层资源执行相应任务。Unsafe类提供了很多功能，这里我们主要介绍 Unsafe 的 CAS，
             对其他功能感兴趣的读者可以去阅读这篇文章：https://blog.csdn.net/javazejian/article/details/72772470
             Unsafe 类存在于 sun.misc 包中，其内部方法操作可以像C的指针一样直接操作内存，单从名称看来就可以知道该类是非安全的，因为 Unsafe 拥有着类似于C的指针操作，因此总是不应该首先使用 Unsafe 类，Java 官方也不建议直接使用的 Unsafe 类
             无锁操作 CAS 是一些CPU直接支持的指令，在 Java 中无锁操作 CAS 基于以下3个方法实现，在稍后讲解Atomic系列内部方法就是基于下述方法的实现的。
                 //第一个参数o为给定对象，offset为对象内存的偏移量，通过这个偏移量迅速定位字段并设置或获取该字段的值，
                 //expected表示期望值，x表示要设置的值，下面3个方法都通过CAS原子指令执行操作。
                 public final native boolean compareAndSwapObject(Object o, long offset,Object expected, Object x);
                 public final native boolean compareAndSwapInt(Object o, long offset,int expected,int x);
                 public final native boolean compareAndSwapLong(Object o, long offset,long expected,long x);
             同时 Unsafe 类中在 JDK8 新增的几个方法，它们的实现是基于上述的CAS方法
                具体方法省略
         1.4 原子操作类 Atomic
             原子更新基本类型主要包括3个类：
                 AtomicBoolean：原子更新布尔类型
                 AtomicInteger：原子更新整型
                 AtomicLong：原子更新长整型
             这3个类的实现原理和使用方式几乎是一样的，这里我们以 AtomicInteger 为例进行分析，AtomicInteger 主要是针对 int 类型的数据执行原子操作，它提供了原子自增方法、原子自减方法以及原子赋值方法等，鉴于AtomicInteger的源码不多，我们直接看源码：
                 public class AtomicInteger extends Number implements java.io.Serializable {
                     private static final long serialVersionUID = 6214790243416807050L;
                     // 获取指针类Unsafe
                     private static final Unsafe unsafe = Unsafe.getUnsafe();
                     //下述变量value在AtomicInteger实例对象内的内存偏移量
                     private static final long valueOffset;

                     static {
                         try {
                             //通过unsafe类的objectFieldOffset()方法，获取value变量在对象内存中的偏移
                             //通过该偏移量valueOffset，unsafe类的内部方法可以获取到变量value对其进行取值或赋值操作
                             valueOffset = unsafe.objectFieldOffset
                             (AtomicInteger.class.getDeclaredField("value"));
                         } catch (Exception ex) { throw new Error(ex); }
                     }
                     //当前AtomicInteger封装的int变量value
                     private volatile int value;

                     public AtomicInteger(int initialValue) {
                        value = initialValue;
                     }
                     public AtomicInteger() {
                     }
                     //获取当前最新值，
                     public final int get() {
                        return value;
                     }
                     //设置当前值，具备volatile效果，方法用final修饰是为了更进一步的保证线程安全。
                     public final void set(int newValue) {
                        value = newValue;
                     }
                     //最终会设置成newValue，使用该方法后可能导致其他线程在之后的一小段时间内可以获取到旧值，有点类似于延迟加载
                     public final void lazySet(int newValue) {
                        unsafe.putOrderedInt(this, valueOffset, newValue);
                     }
                     //设置新值并获取旧值，底层调用的是CAS操作即unsafe.compareAndSwapInt()方法
                     public final int getAndSet(int newValue) {
                        return unsafe.getAndSetInt(this, valueOffset, newValue);
                     }
                     //如果当前值为expect，则设置为update(当前值指的是value变量)
                     public final boolean compareAndSet(int expect, int update) {
                        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
                     }
                     //当前值加1返回旧值，底层CAS操作
                     public final int getAndIncrement() {
                        return unsafe.getAndAddInt(this, valueOffset, 1);
                     }
                     //当前值减1，返回旧值，底层CAS操作
                     public final int getAndDecrement() {
                        return unsafe.getAndAddInt(this, valueOffset, -1);
                     }
                     //当前值增加delta，返回旧值，底层CAS操作
                     public final int getAndAdd(int delta) {
                        return unsafe.getAndAddInt(this, valueOffset, delta);
                     }
                     //当前值加1，返回新值，底层CAS操作
                     public final int incrementAndGet() {
                        return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
                     }
                     //当前值减1，返回新值，底层CAS操作
                     public final int decrementAndGet() {
                        return unsafe.getAndAddInt(this, valueOffset, -1) - 1;
                     }
                     //当前值增加delta，返回新值，底层CAS操作
                     public final int addAndGet(int delta) {
                        return unsafe.getAndAddInt(this, valueOffset, delta) + delta;
                     }
                     //省略一些不常用的方法....
                 }
             可以发现 AtomicInteger 原子类的内部几乎是基于 Unsafe 类中的 CAS 相关操作的方法实现的，这也同时证明 AtomicInteger 是基于无锁实现的，这里重点分析自增操作实现过程，其他方法自增实现原理一样。

    2.ThreadLocal 原理总结
         2.1 什么是 ThreadLocal
             ThreadLocal 提供了线程内部的局部变量，当在多线程环境中使用 ThreadLocal 维护变量时，会为每个线程生成该变量的副本，每个线程只操作自己线程中的变量副本，不同线程间的数据相互隔离、互不影响，从而保证了线程的安全。
             ThreadLocal 适用于无状态，副本变量独立后不影响业务逻辑的高并发场景，如果业务逻辑强依赖于变量副本，则不适合用 ThreadLocal 解决，需要另寻解决方案。
         2.2 ThreadLocal 的数据结构
             在 JDK8 中，每个线程 Thread 内部都维护了一个 ThreadLocalMap 的数据结构，ThreadLocalMap 中有一个由内部类 Entry 组成的 table 数组，Entry 的 key 就是线程的本地化对象 ThreadLocal，而 value 则存放了当前线程所操作
             的变量副本。每个 ThreadLocal 只能保存一个副本 value，并且各个线程的数据互不干扰，如果想要一个线程保存多个副本变量，就需要创建多个ThreadLocal。
         2.3 ThreadLocal 的核心方法
             ThreadLocal 对外暴露的方法有4个：
                 initialValue()方法：返回为当前线程初始副本变量值。
                 get()方法：获取当前线程的副本变量值。
                 set()方法：保存当前线程的副本变量值。
                 remove()方法：移除当前前程的副本变量值
         2.4 ThreadLocal 的哈希冲突的解决方法：线性探测
             和 HashMap 不同，ThreadLocalMap 结构中没有 next 引用，也就是说 ThreadLocalMap 中解决哈希冲突的方式并非链表的方式，而是采用线性探测的方式，当发生哈希冲突时就将步长加1或减1，寻找下一个相邻的位置。
         2.5 ThreadLocal 的内存泄露
             在使用 ThreadLocal 时，当使用完变量后，必须手动调用 remove() 方法删除 entry 对象，否则会造成 value 的内存泄露，严格来说，ThreadLocal 是没有内存泄漏问题，有的话，那也是忘记执行 remove() 引起的，这是使用不规范导致的。
             不过有些人认为 ThreadLocal 的内存泄漏是跟 Entry 中使用弱引用 key 有关，这个结论是不对的。ThreadLocal 造成内存泄露的根本原因并不是 key 使用弱引用，因为即使 key 使用强引用，也会造成 Entry 对象的内存泄露，内存泄露的根
             本原因在于 ThreadLocalMap 的生命周期与当前线程 CurrentThread 的生命周期相同，且 ThreadLocal 使用完没有进行手动删除导致的。下面我们就针对两种情况进行分析：
            2.5.1 如果 key 使用强引用
                 如果在业务代码中使用完 ThreadLocal，则此时 Stack 中的 ThreadLocalRef 就会被回收了。
                 但是此时 ThreadLocalMap 中的 Entry 中的 Key 是强引用 ThreadLocal 的，会造成 ThreadLocal 实例无法回收。
                 如果我们没有删除 Entry 并且 CurrentThread 依然运行的情况下，强引用链如下图红色，会导致Entry内存泄漏。
                 所以结论就是：强引用无法避免内存泄漏。
            2.5.2 如果 key 使用弱引用
                 如果在业务代码中使用完 ThreadLocal，则此时 Stack 中的 ThreadLocalRef 就会被回收了。
                 但是此时 ThreadLocalMap 中的 Entry 中的 Key 是弱引用 ThreadLocal 的，会造成 ThreadLocal 实例被回收，此时 Entry 中的 key = null。
                 但是当我们没有手动删除 Entry 以及 CurrentThread 依然运行的时候，还是存在强引用链，但因为 ThreadLocalRef 已经被回收了，那么此时的 value 就无法访问到了，导致value内存泄漏
                 所以结论就是：弱引用也无法避免内存泄漏
            2.5.3 内存泄露的原因：
                 从上面的分析知道内存泄漏跟强弱引用无关，内存泄漏的前提有两个：
                     (1)ThreadLocalRef 用完后 Entry 没有手动删除。
                     (2)ThreadLocalRef 用完后 CurrentThread 依然在运行。
                 第一点表明当我们在使用完 ThreadLocal 后，调用其对应的 remove() 方法删除对应的 Entry 就可以避免内存泄漏
                 第二点是由于 ThreadLocalMap 是 CurrentThread 的一个属性，被当前线程引用，生命周期跟 CurrentThread 一样，如果当前线程结束 ThreadLocalMap 被回收，自然里面的 Entry 也被回收了，但问题是此时的线程不一定会被回收，比如线程是从线程池中获取的，用完后就放回池子里了
                 所以，我们可以得出在这小节开头的结论：ThreadLocal 内存泄漏根源是 ThreadLocalMap 的生命周期跟 Thread 一样，如果用完 ThreadLocal 没有手动删除就会内存泄漏。
            2.5.4 为什么使用弱引用：
                 前面讲到 ThreadLocal 的内存泄露与强弱引用无关，那么为什么还要用弱引用呢？
                 （1）Entry 中的 key（Threadlocal）是弱引用，目的是将 ThreadLocal 对象的生命周期跟线程周期解绑，用 WeakReference 弱引用关联的对象，只能生存到下一次垃圾回收之前，GC发生时，不管内存够不够，都会被回收。
                 （2）当我们使用完 ThreadLocal，而 Thread 仍然运行时，即使忘记调用 remove() 方法， 弱引用也会比强引用多一层保障：当 GC 发生时，弱引用的 ThreadLocal 被收回，那么 key 就为 null 了。而 ThreadLocalMap 中的 set()、get() 方法，
                     会针对 key == null (也就是 ThreadLocal 为 null) 的情况进行处理，如果 key == null，则系统认为 value 也应该是无效了应该设置为 null，也就是说对应的 value 会在下次调用 ThreadLocal 的 set()、get() 方法时，执行底层 ThreadLocalMap
                     中的 expungeStaleEntry() 方法进行清除无用的 value，从而避免内存泄露。
         2.6 ThreadLocal 的应用场景：
             （1）Hibernate 的 session 获取：每个线程访问数据库都应当是一个独立的 session 会话，如果多个线程共享同一个 session 会话，有可能其他线程关闭连接了，当前线程再执行提交时就会出现会话已关闭的异常，导致系统异常。使用 ThreadLocal 的方式能避免线程争抢session，提高并发安全性。
             （2）Spring 的事务管理：事务需要保证一组操作同时成功或失败，意味着一个事务的所有操作需要在同一个数据库连接上，Spring 采用 Threadlocal 的方式，来保证单个线程中的数据库操作使用的是同一个数据库连接，同时采用这种方式可以使业务层使用事务时不需要感知并管理 connection 对象，
                 通过传播级别，巧妙地管理多个事务配置之间的切换，挂起和恢复。
         2.7 如果想共享线程的 ThreadLocal 数据怎么办 ？
             使用 InheritableThreadLocal 可以实现多个线程访问 ThreadLocal 的值，我们在主线程中创建一个 InheritableThreadLocal 的实例，然后在子线程中得到这个InheritableThreadLocal实例设置的值。
     */
    private void test() {
        final ThreadLocal threadLocal = new InheritableThreadLocal();
        threadLocal.set("主线程的ThreadLocal的值");
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                log.info( "我是子线程，我要获取其他线程的ThreadLocal的值 ==> " + threadLocal.get());
            }
        };
        t.start();
    }
    /**
         2.8 为什么一般用 ThreadLocal 都要用 static?
            ThreadLocal 能实现线程的数据隔离，不在于它自己本身，而在于 Thread 的 ThreadLocalMap，所以，ThreadLocal 可以只实例化一次，只分配一块存储空间就可以了，没有必要作为成员变量多次被初始化。

    3.多线程状态
        详见多线程的六种状态.png
        (1)NEW：初始状态
        (2)RUNNABLE：运行状态（这里只按照JVM层面的标准，不看底层的实现，因为处于JVM运行状态的线程在实际上可能还要等待操作系统分配时间片，但是那些都是实现层，这里只按照JVM标准规范即可）
        (3)BLOCKED：阻塞状态，是线程在等待锁的状态
        (4)WAITING：等待状态，等待状态时线程会让出CPU、但是不会释放锁
        (5)TIMED_WAITING：等待状态，和WAITING总体上是一致的，但是区别在于在等待一定状态之后，会重新恢复到之前的状态
        (6)TERMINATED：被终止状态

    4.Thread 中wait和sleep的区别
         相同点：
         　　(1)wait和sleep方法都可以使线程进入阻塞状态
         　　(2)wait和sleep方法均是可中断方法，被中断后都会受到中断异常
         不同点：
         　　(1)所属类不同：wait是Object的方法，而sleep是Thead特有的方法
         　　(2)关于锁的释放：wait 会释放锁，sleep 睡觉了，抱着锁睡觉，不会释放！
         　　(3)使用位置不同：wait方法的执行必须在同步代码块中进行，而sleep则可以在任何位置　　
         　　(4)sleep方法短暂休眠之后会主动退出阻塞，而wait方法（没有指定等待的时间）则需要被其他线程中断后才能退出阻塞

    5.阻塞队列ArrayBlockingQueue与LinkedBlockingQueue https://blog.csdn.net/a745233700/article/details/120691533
        5.1 什么是阻塞队列 ?
             阻塞队列最大的特性在于支持阻塞添加和阻塞删除方法：
                 (1)阻塞添加：当阻塞队列已满时，队列会阻塞加入元素的线程，直到队列元素不满时才重新唤醒线程执行加入元素操作。
                 (2)阻塞删除：但阻塞队列元素为空时，删除队列元素的线程将被阻塞，直到队列不为空再执行删除操作
            Java 中的阻塞队列接口 BlockingQueue 继承自 Queue 接口，因此先来看看阻塞队列接口为我们提供的主要方法：
                 public interface BlockingQueue<E> extends Queue<E> {
                    // 将指定的元素插入到此队列的尾部（如果立即可行且不会超过该队列的容量）
                    // 在成功时返回 true，如果此队列已满，则抛IllegalStateException。
                    boolean add(E e);

                    // 将指定的元素插入到此队列的尾部（如果立即可行且不会超过该队列的容量）
                    // 如果该队列已满，则在到达指定的等待时间之前等待可用的空间,该方法可中断
                    boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException;

                    //将指定的元素插入此队列的尾部，如果该队列已满，则一直等到（阻塞）。
                    void put(E e) throws InterruptedException;

                    //获取并移除此队列的头部，如果没有元素则等待（阻塞），直到有元素将唤醒等待线程执行该操作
                    E take() throws InterruptedException;

                    //获取并移除此队列的头部，在指定的等待时间前一直等到获取元素， //超过时间方法将结束
                    E poll(long timeout, TimeUnit unit) throws InterruptedException;

                    //从此队列中移除指定元素的单个实例（如果存在）。
                    boolean remove(Object o);
                }
                //除了上述方法还有继承自Queue接口的方法
                //获取但不移除此队列的头元素,没有则跑异常NoSuchElementException
                E element();

                //获取但不移除此队列的头；如果此队列为空，则返回 null。
                E peek();

                //获取并移除此队列的头，如果此队列为空，则返回 null。
                E poll();
            这里我们把上述操作进行分类：
                （1）插入方法：
                     add(E e)：添加成功返回 true，失败抛 IllegalStateException 异常
                     offer(E e)：成功返回 true，如果此队列已满，则返回 false
                     put(E e)：将元素插入此队列的尾部，如果该队列已满，则一直阻塞
                （2）删除方法
                     remove(Object o)：移除指定元素,成功返回 true，失败返回 false
                     poll()：获取并移除此队列的头元素，若队列为空，则返回 null
                     take()：获取并移除此队列头元素，若没有元素则一直阻塞
                （3）检查方法：
                     element() ：获取但不移除此队列的头元素，没有元素则抛异常
                     peek() :获取但不移除此队列的头；若队列为空，则返回 null
        5.2 ArrayBlockingQueue
            ArrayBlockingQueue 内部通过数组对象 items 来存储所有的数据，需要注意的是ArrayBlockingQueue 通过一个 ReentrantLock 来同时控制添加线程与移除线程的并发访问，这点与 LinkedBlockingQueue 区别很大
            (稍后会分析)。而对于 notEmpty 条件对象则是用于存放等待或唤醒调用 take() 方法的线程，告诉他们队列已有元素，可以执行获取操作。同理 notFull 条件对象是用于等待或唤醒调用 put() 方法的线程，告诉它们
            队列未满，可以执行添加元素的操作。takeIndex 代表的是下一个方法(take，poll，peek，remove)被调用时获取数组元素的索引，putIndex 则代表下一个方法（put, offer, or add）被调用时元素添加到数组中的索引。
        5.3 LinkedBlockingQueue
            每个添加到 LinkedBlockingQueue 队列中的数据都将被封装成 Node 节点，添加的链表队列中，其中 head 和 last 分别指向队列的头结点和尾结点。与 ArrayBlockingQueue 不同的是，LinkedBlockingQueue 内部
            分别使用了 takeLock 和 putLock 对并发进行控制，也就是说，添加和删除操作并不是互斥操作，可以同时进行，可以大大提高吞吐量。这里再次强调如果没有给 LinkedBlockingQueue 指定容量大小，其默认值将是
            Integer.MAX_VALUE，如果存在添加速度大于删除速度时候，有可能会内存溢出。至于 LinkedBlockingQueue 的实现原理图与 ArrayBlockingQueue 是类似的，除了对添加和移除方法使用单独的锁控制外，两者都使用了
            不同的 Condition 条件对象作为等待队列，用于挂起 take 线程和 put 线程。
        5.4 ArrayBlockingQueue 和 LinkedBlockingQueue 迥异
            通过上述的分析，对于 ArrayBlockingQueue 和 LinkedBlockingQueue 的基本使用以及内部实现原理我们已较为熟悉了，这里我们就对它们两间的区别来个小结：
                 （1）队列大小有所不同，ArrayBlockingQueue 是有界的初始化必须指定大小，而LinkedBlockingQueue 可以是有界的也可以是无界的(默认是 Integer.MAX_VALUE)，对于后者而言，当添加速度大于移除速度时，在无界的情况下，可能会造成内存溢出等问题
                 （2）数据存储容器不同，ArrayBlockingQueue 采用的是数组作为数据存储容器，而LinkedBlockingQueue 采用的则是以 Node 节点作为连接对象的链表
                 （3）创建与销毁对象的开销不同，ArrayBlockingQueue 采用数组作为存储容器，在插入或删除元素时不会产生或销毁任何额外的对象实例，而 LinkedBlockingQueue 则会生成一个额外的 Node 对象。在长时间内需要高效并发地处理大批量数据的时，对于GC可能存在较大影响。
                 （4）队列添加或移除的锁不一样，ArrayBlockingQueue 的锁是没有分离的，添加操作和移除操作采用同一个 ReenterLock 锁，而 LinkedBlockingQueue 的锁是分离的，添加采用的是 putLock，移除采用的是 takeLock，这样能大大提高队列的吞吐量，
                    也意味着在高并发的情况下生产者和消费者可以并行地操作队列中的数据，以此来提高整个队列的并发性能。
        5.5 为什么juc中ArrayBlockingQueue用一个锁（两个condition），而LinkedBlockingQueue用两个锁（两个condition）实现。
             LinkedBlockingQueue的较大一部分时间需要构造节点，导致较长的等待。所以同时存取有较大优化。
             而ArrayBlockingQueue的不用构造节点，加锁和解锁的时间可能占比较大。
             转成双锁之后，对比原来的存取操作，需要多竞争两次。一次是Atomic变量的cas操作，另一次是获得另一把锁的通知操作。可能这部分的损耗，已经比并发存取带来收益更大
     */


}
