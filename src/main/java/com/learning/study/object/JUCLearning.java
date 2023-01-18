package com.learning.study.object;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

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

    /**
     6.AQS抽象队列同步器
         6.1.AQS 的工作原理
            6.1.1 什么是 AQS
                AQS，Abstract Queued Synchronizer，抽象队列同步器，是 J.U.C 中实现锁及同步组件的基础。工作原理就是如果被请求的共享资源空闲，则将当前请求资源的线程设置为有效的工作线程，并且将共享资源设置为锁定状态，如果被请求的共享资源被占用，
                那么就将获取不到锁的线程加入到等待队列中。这时，就需要一套线程阻塞等待以及被唤醒时的锁分配机制，而 AQS 是通过 CLH 队列实现锁分配的机制。
                AQS分为两种模式：独占模式 EXCLUSIVE 和 共享模式 SHARED，像 ReentrantLock、CyclicBarrier 是基于独占模式模式实现的，Semaphore，CountDownLatch 等是基于共享模式
            6.1.2 CLH 同步队列的模型
                CLH 队列是由内部类 Node 构成的同步队列，是一个双向队列（不存在队列实例，仅存在节点之间的关联关系），将请求共享资源的线程封装成 Node 节点来实现锁的分配；同时利用内部类 ConditionObject 构建等待队列，当调用 ConditionObject 的
                await() 方法后，线程将会加入等待队列中，当调用 ConditionObject 的 signal() 方法后，线程将从等待队列转移动同步队列中进行锁竞争。AQS 中只能存在一个同步队列，但可拥有多个等待队列。AQS 的 CLH 同步队列的模型如下图：
                详见AQS 的 CLH 同步队列的模型.png

                AQS 有三个主要变量，分别是 head、tail、state，其中 head 指向同步队列的头部，注意 head 为空结点，不存储信息。而 tail 则是同步队列的队尾，同步队列采用的是双向链表的结构是为了方便对队列进行查找操作。当 Node 节点被设置为 head 后，
                其 thread 信息和前驱结点将被清空，因为该线程已获取到同步状态，正在执行了，也就没有必要存储相关信息了，head 只保存后继结点的指针即可，便于 head 结点释放同步状态后唤醒后继结点。
                队列的入队和出队操作都是无锁操作，基于 CAS+自旋锁 实现，AQS 维护了一个 volatile 修饰的 int 类型的 state 同步状态，volatile 保证线程之间的可见性，并通过 CAS 对该同步状态进行原子操作、实现对其值的修改。当 state=0 时，表示没有
                任何线程占有共享资源的锁，当 state=1 时，则说明当前有线程正在使用共享变量，其他线程必须加入同步队列进行等待；

        6.2.AQS 的设计模式
            6.2.1 QS 的模板方法模式
                AQS 的基于模板方法模式设计的，在 AQS 抽象类中已经实现了线程在等待队列的维护方式（如获取资源失败入队/唤醒出队等），而对于具体共享资源 state 的获取与释放（也就是锁的获取和释放）则交由具体的同步器来实现
                具体的同步器需要实现以下几种方法：
                     isHeldExclusively()：该线程是否正在独占资源，只有用到 condition 才需要去实现它
                     tryAcquire(int)：独占模式，尝试获取资源，成功则返回 true，失败则返回 false
                     tryRelease(int)：独占方式，尝试释放资源，成功则返回 true，失败则返回 false
                     tryAcquireShared(int)：共享方式，尝试获取资源。负数表示失败；0表示成功，但没有剩余可用资源；正数表示成功，且有剩余资源
                     tryReleaseShared(int)：共享方式，尝试释放资源，如果释放后允许唤醒后续等待结点返回true，否则返回false
            6.2.2 JUC 中提供的同步器
                 闭锁 CountDownLatch：用于让主线程等待一组事件全部发生后继续执行。
                 栅栏 CyclicBarrier：用于等待其它线程，且会阻塞自己当前线程，所有线程必须全部到达栅栏位置后，才能继续执行；且在所有线程到达栅栏处之后，可以触发执行另外一个预先设置的线程。
                 信号量 Semaphore：用于控制访问资源的线程个数，常常用于实现资源池，如数据库连接池，线程池。在 Semaphore 中，acquire 方法用于获取资源，有的话，继续执行，没有资源的话将阻塞直到有其它线程调用 release 方法释放资源；
                 交换器 Exchanger：用于线程之间进行数据交换；当两个线程都到达共同的同步点（都执行到exchanger.exchange 的时刻）时，发生数据交换，否则会等待直到其它线程到达；

            CountDownLatch 和 CyclicBarrier 的区别？
             两者都可以用来表示代码运行到某个点上，二者的区别在于：
                 (1)CyclicBarrier 的某个线程运行到某个位置之后就停止运行，直到所有的线程都到达了这个点，所有线程才重新运行；CountDownLatch 的某线程运行到某个位置之后，只是给计数值-1而已，该线程继续运行；
                 (2)CyclicBarrier 可重用，CountDownLatch 不可重用，计数值 为 0 时该 CountDownLatch 就不可再用了。

        6.3.CountDownLatch
            6.3.1 什么是 CountDownLatch?
                CountDownLatch，闭锁，就是一个基于 AQS 共享模式的同步计数器，它内部的方法都是围绕 AQS 实现的。主要作用是使一个或一组线程在其他线程执行完毕之前，一直处于等待状态，直到其他线程执行完成后再继续执行。
                CountDownLatch 利用 AQS 的 state 变量充当计数器（由 volatile 修饰并使用 CAS 进行更新的），计数器的初始值就是线程的数量，每当一个线程执行完成，计数器的值就会减一，当计数器的值为 0 时，表示所有的
                线程都已经完成任务了，那么接下来就唤醒在 CountDownLatch 上等待的线程执行后面的任务。
                那么当计数器的值为 0 时，主线程是如何被唤醒的呢？这就要从 CountDownLatch 的工作流程来说明了，CountDownLatch 的工作流程可以看成在一开始只在 CLH 队列中放入一个主线程，然后不停的唤醒，唤醒之后如果
                发现 state 还是不为0，则继续等待。而主线程什么时候会被唤醒呢？当每个子线程执行完毕的时候，会调用 countDown() 并基于 CAS 将计数器 state 的值减一，减一成功释放资源后，就会调用 unparkSuccessor()
                唤醒主线程，当所有的子线程都执行完了，也就是 state 为 0 时，这时候主线程被唤醒之后就可以继续执行了。
                state 被减成了 0 之后，就无法继续使用这个 CountDownLatch 了，需要重新 new 一个，因为 state 的数量只有在初始化 CountDownLatch 的时候才可以设置，这也是 CountDownLatch 不可重用的原因。
            6.3.2 CountDownLatch 的源码简单说明
                从代码层面上来看，CountDownLatch 基于内部类 Sync 实现，而 Sync 继承自 AQS。CountDownLatch 最主要有两个方法：await() 和 countDown()
                     await()：调用该方法的线程会被挂起，直到 CountDownLatch 计数器的值为 0 才继续执行，底层使用的是 AQS 的 tryAcquireShared()
                     countDown()：用于减少计数器的数量，如果计数减为 0 的话，就会唤醒主线程，底层使用的是 AQS 的 releaseShared()

        6.4.CyclicBarrier
            6.4.1 什么是CyclicBarrier
                CyclicBarrier，循环栅栏，通过 CyclicBarrier 可以实现一组线程之间的相互等待，当所有线程都到达屏障点之后再执行后续的操作。通过 await() 方法可以实现等待，当最后一个线程执行完，会使得所有在相应 CyclicBarrier 实例上的等待的线程被唤醒，而最后一个线程自身不会被暂停。
                CyclicBarrier 没有像 CountDownLatch 和 ReentrantLock 使用 AQS 的 state 变量，它是直接借助 ReentrantLock 加上 Condition 等待唤醒的功能进而实现的。在构建 CyclicBarrier 的时候，传入的值会赋值给 CyclicBarrier 内部维护的变量 count，同时也会赋值给 parties 变量（这是可以复用的关键）。
                线程调用 await() 表示线程已经到达栅栏，每次调用 await() 时，会将 count 减一，操作 count 值是直接使用 ReentrantLock 来保证线程安全性的，如果 count 不为 0，则添加到 condition 队列中，如果 count 等于 0，则把节点从 condition 队列中移除并添加到 AQS 队列中进行全部唤醒，并且将 parties 的值重新赋值给 count 从而实现复用。
            6.4.2 CyclicBarrier 的源码分析:
                成员变量:
                     //同步操作锁
                     private final ReentrantLock lock = new ReentrantLock();
                     //线程拦截器
                     private final Condition trip = lock.newCondition();
                     //每次拦截的线程数
                     private final int parties;
                     //换代前执行的任务
                     private final Runnable barrierCommand;
                     //表示栅栏的当前代
                     private Generation generation = new Generation();
                     //计数器
                     private int count;
                 CyclicBarrier 是通过独占锁实现的，底层包含了 “ReentrantLock 对象 lock” 和 “Condition 对象 trip”，通过条件队列 trip 来对线程进行阻塞的，并且其内部维护了两个 int 型的变量 parties 和 count：
                 parties 表示每次拦截的线程数，该值在构造时进行赋值，用于实现 CyclicBarrier 的复用；
                 count 是内部计数器，它的初始值和 parties 相同，以后随着每次 await 方法的调用而减 1，直到减为 0 就将所有线程唤醒。
                 CyclicBarrier 有一个静态内部类 Generation，该类的对象代表栅栏的当前代，利用它可以实现循环等待，当 count 减为 0 会将所有阻塞的线程唤醒，并设置成下一代。
                 barrierCommand 表示换代前执行的任务，在唤醒所有线程前可以通过 barrierCommand 来执行指定的任务
            6.4.3 代码场景:
         */
        public void test1() throws Exception {
            String[] arr = {"小王","小张","小李","小赵","小丽"};
            CyclicBarrier cyclicBarrier = new CyclicBarrier(5,new Runnable() {
                @Override
                public void run() {
                    System.out.println("5人已来,可以开席了");
                }
            });
            for (int i = 0; i < 10; i++) {
                final int index = i;
                Thread.sleep(1000);
                new Thread(()->{
                    try {
                        System.out.println(arr[index] + "要来了");
                        cyclicBarrier.await();
                        System.out.println(arr[index] + "开始吃了");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },"线程" + i).start();
            }
        }
        /**
                 结果输出:
                     小王要来了
                     小李要来了
                     小赵要来了
                     小张要来了
                     小丽要来了
                     5人已来,可以开席了
                     小丽开始吃了
                     小王开始吃了
                     小赵开始吃了
                     小张开始吃了
                     小李开始吃了

        6.5.Semaphore
            6.5.1 什么是 Semaphore
                Semaphore 信号量，主要用于控制并发访问共享资源的线程数量，底层基于 AQS 共享模式，并依赖 AQS 的变量 state 作为许可证 permit，通过控制许可证的数量，来保证线程之间的配合。线程使用 acquire()
                获取访问许可，只有拿到 “许可证” 后才能继续运行，当 Semaphore 的 permit 不为 0 的时候，对请求资源的线程放行，同时 permit 的值减1，当 permit 的值为 0 时，那么请求资源的线程会被阻塞直到其他
                线程释放访问许可，当线程对共享资源操作完成后，使用 release() 归还访问许可。不同于 CyclicBarrier 和 ReentrantLock，Semaphore 不会使用到 AQS 的 Condition 条件队列，都是在 CLH 同步队列中
                操作，只是当前线程会被 park。另外 Semaphore 是不可重入的。
            6.5.2 Semaphore 的公平和非公平两种模式
                 Semaphore 通过自定义两种不同的同步器（FairSync 和 NonfairSync）提供了公平和非公平两种工作模式，两种模式下分别提供了限时/不限时、响应中断/不响应中断的获取资源的方法（限时获取总是及时响应中断的），而所有的释放资源的 release() 操作是统一的。
                 公平模式：遵循 FIFO，调用 acquire() 方法获取许可证的顺序时，先判断同步队列中是不是存在其他的等待线程，如果存在就将请求线程封装成 Node 结点加入同步队列，从而保证每个线程获取同步状态都是按照先到先得的顺序执行的，否则对 state 值进行减操作并返回剩下的信号量
                 非公平模式：是抢占式的，通过竞争的方式获取，不管同步队列中是否存在等待线程，有可能一个新的获取线程恰好在一个许可证释放时得到了这个许可证，而前面还有等待的线程。
            6.5.3 代码场景:
         */
        public void test2() {
            Semaphore semaphore = new Semaphore(10);  //停车场同时容纳的车辆10
            //模拟100辆车进入停车场
            for (int i = 0; i < 100; i++) {
                new Thread(() -> {
                    try {
                        System.out.println("====" + Thread.currentThread().getName() + "来到停车场");
                        if (semaphore.availablePermits() == 0) {
                            System.out.println("车位不足，请耐心等待");
                        }
                        semaphore.acquire();//获取令牌尝试进入停车场
                        System.out.println(Thread.currentThread().getName() + "成功进入停车场");
                        Thread.sleep(new Random().nextInt(10000));//模拟车辆在停车场停留的时间
                        System.out.println(Thread.currentThread().getName() + "驶出停车场");
                        semaphore.release();//释放令牌，腾出停车场车位
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, i + "号车").start();
            }
        }

    /**
     7.synchronized锁机制原理 与 Lock锁机制
        7.1 synchronized锁机制
            7.1.1 synchronized 的作用
                 synchronized 通过当前线程持有对象锁，从而拥有访问权限，而其他没有持有当前对象锁的线程无法拥有访问权限，保证在同一时刻，只有一个线程可以执行某个方法或者某个代码块，从而保证线程安全。synchronized 可以保证线程的可见性，
                 synchronized 属于隐式锁，锁的持有与释放都是隐式的，我们无需干预。synchronized最主要的三种应用方式：
                     (1)修饰实例方法：作用于当前实例加锁，进入同步代码前要获得当前实例的锁
                     (2)修饰静态方法：作用于当前类对象加锁，进入同步代码前要获得当前类对象的锁
                     (3)修饰代码块：指定加锁对象，进入同步代码库前要获得给定对象的锁
            7.1.2 synchronized 底层语义原理
                synchronized 锁机制在 Java 虚拟机中的同步是基于进入和退出监视器锁对象 monitor 实现的（无论是显示同步还是隐式同步都是如此），每个对象的对象头都关联着一个 monitor 对象，当一个 monitor 被某个线程持有后，它便处于锁定状态。
                在 HotSpot 虚拟机中，monitor 是由 ObjectMonitor 实现的，每个等待锁的线程都会被封装成 ObjectWaiter 对象，ObjectMonitor 中有两个集合，_WaitSet 和 _EntryList，用来保存 ObjectWaiter 对象列表 ，owner 区域指向持有
                ObjectMonitor 对象的线程。当多个线程同时访问一段同步代码时，首先会进入 _EntryList 集合尝试获取 moniter，当线程获取到对象的 monitor 后进入 _Owner 区域并把 _owner 变量设置为当前线程，同时 monitor 中的计数器 count 加1；
                若线程调用 wait() 方法，将释放当前持有的 monitor，count自减1，owner 变量恢复为 null，同时该线程进入 _WaitSet 集合中等待被唤醒。若当前线程执行完毕也将释放 monitor 并复位变量的值，以便其他线程获取 monitor。
                     _EntryList：存储处于 Blocked 状态的 ObjectWaiter 对象列表。
                     _WaitSet：存储处于 wait 状态的 ObjectWaiter 对象列表。
            7.1.3 synchronized 的显式同步与隐式同步
                synchronized 分为显式同步（同步代码块）和隐式同步（同步方法），显式同步指的是有明确的 monitorenter 和 monitorexit 指令，而隐式同步并不是由 monitorenter 和 monitorexit 指令来实现同步的，
                而是由方法调用指令读取运行时常量池中方法的 ACC_SYNCHRONIZED 标志来隐式实现的。
                (1)synchronized 代码块底层原理
                    synchronized 同步语句块的实现是显式同步的，通过 monitorenter 和 monitorexit 指令实现，其中 monitorenter 指令指向同步代码块的开始位置，monitorexit 指令则指明同步代码块的结束位置，当执行 monitorenter 指令时，
                    当前线程将尝试获取 objectref（即对象锁）所对应的 monitor 的持有权：
                         a.当对象锁的 monitor 的进入计数器为 0，那线程可以成功取得 monitor，并将计数器值设置为 1，取锁成功。
                         b.如果当前线程已经拥有对象锁的 monitor 的持有权，那它可以重入这个 monitor，重入时计数器的值也会加1。
                         c.若其他线程已经拥有对象锁的 monitor 的所有权，那当前线程将被阻塞，直到正在执行线程执行完毕，即monitorexit 指令被执行，执行线程将释放 monitor 并设置计数器值为0，其他线程将有机会持有 monitor。
                    编译器会确保无论方法通过何种方式完成，无论是正常结束还是异常结束，代码中调用过的每条 monitorenter 指令都有执行其对应 monitorexit 指令。为了保证在方法异常完成时，monitorenter 和 monitorexit 指令依然可以正确配对执行，
                    编译器会自动产生一个异常处理器，这个异常处理器可处理所有的异常，它的目的就是用来执行 monitorexit 指令。
                    详见synchronized 代码块底层原理.png
                (2)synchronized 方法底层原理
                     synchronized 同步方法的实现是隐式的，无需通过字节码指令来控制，它是在方法调用和返回操作之中实现。JVM 可以通过方法常量池中的方法表结构（method_info Structure）中的 ACC_SYNCHRONIZED 访问标志 判断一个方法是否同步方法。
                    当方法调用时，调用指令将会检查方法的 ACC_SYNCHRONIZED 访问标志是否被设置，如果设置了，标识该方法是一个同步方法，执行线程将先持有 monitor， 然后再执行方法，最后再方法完成(无论是正常完成还是非正常完成)时释放 monitor。
                    在方法执行期间，执行线程持有了 monitor，其他任何线程都无法再获得同一个 monitor。
                     如果一个同步方法执行期间抛出了异常，并且在方法内部无法处理此异常，那这个同步方法所持有的 monitor 将在异常抛到同步方法之外时自动释放。
                    详见synchronized 方法底层原理.png
        7.2 JVM 对 synchronized 锁的优化
            在早期版本中，synchronized 属于重量级锁，效率低下，因为监视器锁 monitor 是依赖于操作系统的 Mutex 互斥量来实现的，操作系统实现线程之间的切换时需要从用户态转换到核心态，这个状态之间的转换需要相对比较长的时间，时间成本相对较高。
            在 JDK6 之后，synchronized 在 JVM 层面做了优化，减少锁的获取和释放所带来的性能消耗，主要优化方向有以下几点：
            7.2.1 锁升级：偏向锁->轻量级锁->自旋锁->重量级锁
                锁的状态总共有四种，无锁状态、偏向锁、轻量级锁和重量级锁。随着锁的竞争，锁可以从偏向锁升级到轻量级锁，再升级的重量级锁，但是锁的升级是单向的，只能从低到高升级，不会出现锁的降级。重量级锁基于从操作系统的互斥量实现的，
                而偏向锁与轻量级锁不同，他们是通过 CAS 并配合 Mark Word 一起实现的。
                7.2.1.1 synchronized 的 Mark word 标志位:
                    synchronized 使用的锁对象是存储在 Java 对象头里的，那么 Java 对象头是什么呢？对象实例分为：
                         (1)对象头
                             Mark Word(hashcode、分代年龄、锁标记位)
                             指向类的指针(元数据指针)
                             数组长度(数组对象才有)
                         (2)实例数据
                         (3)对齐填充
                    详见对象实例结构.png
                    其中，Mark Word 记录了对象的 hashcode、分代年龄、锁标记位相关的信息，由于对象头的信息是与对象自身定义的数据没有关系的额外存储成本，因此考虑到 JVM 的空间效率，Mark Word 被设计成为一个非固定的数据结构，以便存储
                    更多有效的数据，它会根据对象本身的状态复用自己的存储空间，在 32位 JVM 中的长度是 32 位，具体信息如下图所示：
                    详见Mark word数据结构.png
                7.2.1.2 锁升级过程:
                     （1）偏向锁：如果一个线程获得了锁，那么进入偏向模式，当这个线程再次请求锁的时候，只需去对象头的 Mark Word 中判断偏向线程ID是否指向它自己，无需再进入 monitor 中去竞争对象，这样就省去了大量锁申请的操作，
                        适用于连续多次都是同一个线程申请相同的锁的场景。偏向锁只有初始化的时候需要一次 CAS 操作，但如果出现其他线程竞争锁资源，那么偏向锁就会被撤销，并升级为轻量级锁。
                     （2）轻量级锁：不需要申请互斥量，允许短时间内的锁竞争，每次申请、释放锁都至少需要一次 CAS，适用于多个线程交替执行同步代码块的场景
                     （3）自旋锁：自旋锁假设在不久将来，当前的线程可以获得锁，因此在轻量级锁升级成为重量级锁之前，虚拟机会让当前想要获取锁的线程做几个空循环，在经过若干次循环后，如果得到锁，就顺利进入临界区，如果还不能获得锁，那就会将线程在操作系统层面挂起。
                        这种方式确实可以提升效率的，但是当线程越来越多竞争很激烈时，占用 CPU 的时间变长会导致性能急剧下降，因此 JVM 对于自旋锁有一定的次数限制，可能是50或者100次循环后就放弃，直接挂起线程，让出CPU资源。
                     （4）自适应自旋锁：自适应自旋解决的是 “锁竞争时间不确定” 的问题，自适应意味着自旋的时间不再固定了，而是由前一次在同一个锁上的自旋时间及锁的拥有者的状态来决定。
                        如果在同一个锁对象上，自旋等待刚刚成功获得过锁，并且持有锁的线程正在运行中，那么虚拟机就会认为这次自旋也很有可能再次成功，进而它将允许自旋等待持续相对更长的时间，比如100个循环。
                        相反的，如果对于某个锁，自旋很少成功获得过，那在以后要获取这个锁时将可能减少自旋时间甚至省略自旋过程，以避免浪费处理器资源。
                        但自旋锁带来的副作用就是不公平的锁机制：处于阻塞状态的线程，并没有办法立刻竞争被释放的锁。然而，处于自旋状态的线程，则很有可能优先获得这把锁。
                     （5）重量级锁：适用于多个线程同时执行同步代码块的场景，且锁竞争时间长。在这个状态下，未抢到锁的线程都会进入到 Monitor 中并阻塞在 _EntryList 集合中（具体的争夺锁的原理见该部分的第2点：synchronized 底层语义原理）。
                    详见synchronized锁升级过程.png
            7.2.2 锁消除
                消除锁属于编译器对锁的优化，JIT 编译时(可以简单理解为当某段代码即将第一次被执行时进行编译，又称即时编译)会使用逃逸分析技术，通过对运行上下文的扫描，去除不可能存在共享资源竞争的锁，通过这种方式消除没有必要的锁，可以节省毫无意义的请求锁时间。
            7.2.3 锁粗化
                JIT 编译器动态编译时，如果发现几个相邻的同步块使用的是同一个锁实例，那么 JIT 编译器将会把这几个同步块合并为一个大的同步块，从而避免一个线程“反复申请、释放同一个锁“所带来的性能开销。
        7.3 偏向锁的废除
             在 JDK6 中引入的偏向锁能够减少竞争锁定的开销，使得 JVM 的性能得到了显著改善，但是 JDK15 却将决定将偏向锁禁用，并在以后删除它，这是为什么呢？主要有以下几个原因：
                 (1)为了支持偏向锁使得代码复杂度大幅度提升，并且对 HotSpot 的其他组件产生了影响，这种复杂性已成为理解代码的障碍，也阻碍了对同步系统进行重构
                 (2)在更高的 JDK 版本中针对多线程场景推出了性能更高的并发数据结构，所以过去看到的性能提升，在现在看来已经不那么明显了。
                 (3)围绕线程池队列和工作线程构建的应用程序，性能通常在禁用偏向锁的情况下变得更好。
        7.4 Lock 锁机制
            讲到 Synchronized 锁机制，肯定离不开的话题就是 Lock 的锁机制，那这里我们就简单介绍下 Lock 锁机制。
            7.4.1 Lock 锁是什么
                Lock 锁其实指的是 JDK5 之后在 JUC 中引入的 Lock 接口，该接口中只有6个方法的声明，对于实现该接口的所有锁可以称为 Lock 锁。Lock 锁是显式锁，锁的持有与释放都必须手动编写，当前线程使用 lock() 方法与 unlock() 对临界区进行加锁与释放锁，
                当前线程获取到锁之后，其他线程由于无法持有锁将无法进入临界区，直到当前线程释放锁，unlock() 操作必须在 finally 代码块中，这样可以确保即使临界区执行抛出异常，线程最终也能正常释放锁。
            7.4.2 ReentrantLock 重入锁
                ReentrantLock 重入锁是基于 AQS 框架并实现了 Lock 接口，支持一个线程对资源重复加锁，作用与 synchronized 锁机制相当，但比 synchronized 更加灵活，同时也支持公平锁和非公平锁。
            7.4.3 ReentrantLock 与 synchronized 的区别
                 （1）使用的区别：synchronized 是 Java 的关键字，是隐式锁，依赖于 JVM 实现，当 synchronized 方法或者代码块执行完之后，JVM 会自动让线程释放对锁的占用；ReentrantLock 依赖于 API，是显式锁，需要 lock() 和 unlock() 方法配合
                    try/finally 语句块来完成。在发生异常时，JVM 会自动释放 synchronized 锁，因此不会导致死锁；而 ReentrantLock 在发生异常时，如果没有主动通过 unLock() 去释放锁，则很可能造成死锁现象，这也是 unLock() 语句必须写在 finally 语句块的原因。
                 （2）功能的区别：ReentrantLock 相比于 synchronzied 更加灵活， 除了拥有 synchronzied 的所有功能外，还提供了其他特性：
                     ReentrantLock 可以实现公平锁，而 synchronized 不能保证公平性。
                     ReentrantLock 可以知道有没有成功获取锁（tryLock），而 synchronized 不支持该功能
                     ReentrantLock 可以让等待锁的线程响应中断，而使用 synchronized 时，等待的线程不能够响应中断，会一直等待下去；
                     ReentrantLock 可以基于 Condition 实现多条件的等待唤醒机制，而如果使用 synchronized，则只能有一个等待队列
                 （3）性能的区别：在 JDK6 以前，如果竞争资源不激烈，两者的性能是差不多的，而当竞争资源非常激烈时，此时 ReentrantLock 的性能要远远优于 synchronizsed。但是在 JDK6 及以后的版本，JVM 对 synchronized 进行了优化，所以两者的性能变得差不多了
                    总的来说，synchronizsed 和 ReentrantLock 都是可重入锁，在使用选择上需要根据具体场景而定，大部分情况下依然建议使用 synchronized 关键字，原因之一是使用方便语义清晰，二是性能上虚拟机已为我们自动优化。如果确实需要使用到 ReentrantLock
                    提供的多样化特性时，我们可以选择ReentrantLock
            7.4.4  “可重入锁”概念
                “可重入锁”概念是：自己可以再次获取自己的内部锁。比如一个线程获得了某个对象的锁，此时这个对象锁还没有释放，当其再次想要获取这个对象的锁的时候还是可以获取的，如果不可重入的话，就会造成死锁。同一个线程每次获取锁，锁的计数器都自增1，
                所以要等到锁的计数器下降为0时才能释放锁。
        7.5 ReadWriteLock 读写锁
            ReentrantLock 某些时候有局限，如果使用 ReentrantLock，主要是为了防止线程A在写数据、线程B在读数据造成的数据不一致，但如果线程C在读数据、线程D也在读数据，由于读数据是不会改变数据内容的，所以就没有必要加锁，但如果使用了 ReentrantLock，
            那么还是加锁了，反而降低了程序的性能，因此诞生了读写锁 ReadWriteLock。ReadWriteLock 是一个接口，而 ReentrantReadWriteLock 是 ReadWriteLock 接口的具体实现，实现了读写的分离，读锁是共享的，写锁是独占的，读和读之间不会互斥，
            读和写、写和写之间才会互斥，提升了读写的性能。

     8.sychronized和 ReenteredLock区别
        (1)底层实现上来说
             synchronized 是JVM层面的锁，是Java关键字，通过monitor对象来完成（monitorenter与monitorexit），对象只有在同步块或同步方法中才能调用wait/notify方法，ReentrantLock 是从jdk1.5以来（java.util.concurrent.locks.Lock）提供的API层面的锁。
             synchronized 的实现涉及到锁的升级，具体为无锁、偏向锁、自旋锁、向OS申请重量级锁，
             ReentrantLock 实现则是通过利用CAS（CompareAndSwap）自旋机制保证线程操作的原子性和volatile保证数据可见性以实现锁的功能。
        (2)是否可手动释放
            synchronized 不需要用户去手动释放锁，synchronized 代码行完后系统会自动让线程释放对锁的占用； ReentrantLock则需要用户去手动释放锁，如果没有手动释放锁，就可能导致死锁现象。一般通过lock()和unlock()方法配合try/finally语句块来完成，使用释放更加灵活。
        (3)是否可中断
            synchronized是不可中断类型的锁，除非加锁的代码中出现异常或正常执行完成； ReentrantLock则可以中断，可通过trylock(long timeout,TimeUnit unit)设置超时方法或者将lockInterruptibly()放到代码块中，调用interrupt方法进行中断。
        (4)是否公平锁
            synchronized为非公平锁 ReentrantLock则即可以选公平锁也可以选非公平锁，通过构造方法new ReentrantLock时传入boolean值进行选择，为空默认false非公平锁，true为公平锁。

     9.JDK锁
        (1)乐观锁: java中的乐观锁---CAS
        (2)悲观锁: java中的悲观锁  synchronized修饰的方法和方法块  比如我们尝试用的hashtable,以及StringBuffer他们的方法都被synchronized修饰,ReentrantLock不仅悲观还重入(也属于重入锁)
        (3)自旋锁: 自旋锁就是在获取锁的时候，如果锁被其他线程获取,该线程就会一直循环等待,一直尝试着去获取锁,直到目标达成。而不像普通的锁那样，如果获取不到锁就进入阻塞
               自旋默认的次数: 10次
        (4)可重入锁(递归锁): 可重入锁使一种技术,任意线程在获取到 锁之后能够再次 获取 该锁而不会被锁阻塞
               原理 : 通过组合自定义同步器来实现锁的获取和释放
               再次获取锁: 识别获取 锁的线程是否为当前占据锁的线程,如果是,则再次成功获取,获取锁后,进行计数自增
               释放锁:  释放锁 进行计数自减
               java中的可重入锁: ReentrantLock、synchronized修饰的方法或代码段
        (5)读写锁: 读写锁使一种技术,通过ReentrantReadWriteLock类来实现的,为了提高性能,Java提供了读写锁,读的地方使用 读锁,写的地方使用写锁,在没有写锁的情况下,读锁是无阻塞的,多个读锁不互斥,读锁与写锁互斥,这是由jvm来控制的
        (6)公平锁: 公平锁使一种思想,多个线程按照顺序来获取锁 ,并发环境中,每个线程会去查看锁的维护队列,如果队列为空,就占有锁,如果队列不为空,就加入等待队列的末尾,按照FIFO原则获取锁
        (7)非公平锁: 非公平锁也是一种思想,线程尝试获取锁,如果获取不到,按照公平锁的方式,多个线程获取锁不是按照 先到先得的顺序.是无序的,有可能后到了先获取到锁
               synchronized是非公平锁
        (8)共享锁: 共享锁是一种思想,可以多个线程获取读锁,以共享的方式持有锁,和乐观锁还有读写锁同义
        (9)独占锁: 独占锁是一种思想,只能有一个线程获取锁,以独有的方式持有锁,悲观锁和互斥锁同义
               synchronized，ReentrantLock
        (10)重量级锁: synchronized 就是重量级锁,为了优化重量级锁,引入了轻量级锁和偏向锁
        (11)轻量级锁: jdk6是加入的一种锁的优化机制,轻量级锁是在没有多线程竞争的情况下使用的CAS操作去消除同步使用的互斥量
        (12)偏向锁: 偏向锁 是JDK6时加入的一种锁优化机制： 在无竞争的情况下把整个同步都消除掉,连CAS操作都不去做了。偏是指偏心，它的意思是这个锁会偏向于第一个获得它的线程，如果在接下来的执行过程中，该锁一直没有被其他的线程获取，则持有偏向锁的线程将永远不需要再进行同步。持有偏向锁的线程以后每次进入这个锁相关的同步块时，虚拟机都可以不再进行任何同步操作.
        (13)分段锁: 是一种机制,是不是想到了ConcurrentHashMap了,默认情况下ConcurrentHashMap被细分为16个段(Segment)每次上锁只是锁的每个segment. segment通过继承ReentrntLock来进行加锁,只要保证每个segment是线程安全的,是不是就保证了全局的线程安全.
        (14)互斥锁: 互斥锁和悲观锁还有独占锁同义,某个资源,只能被一个线程访问,其他线程不能访问.例如上文提到的读写锁中的写锁,写与写之间是互斥的,写与读之间也是互斥的
        (15)同步锁:

     10.volatile关键字(可以解决变量的可见性,有序性,不可解决原子性)
        volatile是一个"变量修饰符"，它只能修饰"成员变量"，它能强制线程每次从主内存获取值，并能保证此变量不会被编译器优化。
        volatile能解决变量的可见性、有序性；
        volatile不能解决变量的原子性
          (1)可见性： 当一个线程修改了声明为volatile变量的值，新值对于其他要读该变量的线程来说是立即可见的。
          (2)有序性： volatile变量的所谓有序性也就是被声明为volatile的变量的临界区代码的执行是有顺序的，即禁止指令重排序。
          (3)受限原子性： volatile变量不可保证原子性

        10.1 voliate可见性底层实现原理
            实际上voliate的可见性实现借助了CPU的lock指令，即在写voliate变量的时候，在该指令前加一个lock指令，这个指令有两个作用：
                (1)写volatile时处理器会将缓存写回到主内存。
                (2)一个处理器的缓存写回到主内存会导致其他处理器的缓存失效。（即其他线程缓存该变量地址失效，下次读取时会自动从主存中读取
            注意：基于 CPU 缓存一致性协议，JVM 实现了 volatile 的可见性，但由于总线嗅探机制，会不断的监听总线，如果大量使用 volatile 会引起总线风暴。所以，volatile 的使用要适合具体场景。
        10.2 voliate有序性底层实现原理
            volatile有序性的保证就是通过禁止指令重排序来实现的。指令重排序包括编译器和处理器重排序，JMM会分别限制这两种指令重排序。
            那么禁止指令重排序又是如何实现的呢？答案是加内存屏障。
            内存屏障指令，它是一个 CPU 指令, 告诉编译器和处理器，重排序时不能把后面的指令重排序到内存屏障之前的位置，从而避免多线程环境下出现乱序执行现象
        10.3 voliate为什么不保证原子性
            首先说明i++的操作本身就不是原子性的，而是分为三步
                (1)线程读取i
                (2)i自增，temp = i + 1
                (3)刷回主存，i = temp
            线程A: i=5->temp=6->i=temp=6
            线程B: i=5->temp=6,由于voliate,i=6->i=temp=6
     */
}
