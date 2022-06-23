package com.learning.study.object;

public class LockLearning {

    /**
     * ============== synchronized和Lock
     * 1.synchronized(悲观锁、同步锁)
     *      synchronized被称为“重量级的锁”方式，也是“悲观锁”——效率比较低。
     *     1.1synchronized有几种使用方式：
     *      a).同步代码块【常用】
     *      b).同步方法【常用】
     * 2.Lock锁也称同步锁
     * public void lock():加同步锁。
     * public void unlock():释放同步锁。
     */
    public void synchronized和Lock() {}

    /**
     * ============ sychronized和 ReenteredLock区别
     * 1. 底层实现上来说
     * synchronized 是JVM层面的锁，是Java关键字，通过monitor对象来完成（monitorenter与monitorexit），对象只有在同步块或同步方法中才能调用wait/notify方法，ReentrantLock 是从jdk1.5以来（java.util.concurrent.locks.Lock）提供的API层面的锁。
     * synchronized 的实现涉及到锁的升级，具体为无锁、偏向锁、自旋锁、向OS申请重量级锁，
     * ReentrantLock 实现则是通过利用CAS（CompareAndSwap）自旋机制保证线程操作的原子性和volatile保证数据可见性以实现锁的功能。
     *
     * 2. 是否可手动释放
     * synchronized 不需要用户去手动释放锁，synchronized 代码执行完后系统会自动让线程释放对锁的占用； ReentrantLock则需要用户去手动释放锁，如果没有手动释放锁，就可能导致死锁现象。一般通过lock()和unlock()方法配合try/finally语句块来完成，使用释放更加灵活。
     *
     * 3. 是否可中断
     * synchronized是不可中断类型的锁，除非加锁的代码中出现异常或正常执行完成； ReentrantLock则可以中断，可通过trylock(long timeout,TimeUnit unit)设置超时方法或者将lockInterruptibly()放到代码块中，调用interrupt方法进行中断。
     *
     * 4. 是否公平锁
     * synchronized为非公平锁 ReentrantLock则即可以选公平锁也可以选非公平锁，通过构造方法new ReentrantLock时传入boolean值进行选择，为空默认false非公平锁，true为公平锁。
     */
    public void sychronized和ReenteredLock区别() {}

    /**
     * =========== volatile关键字
     * volatile是一个"变量修饰符"，它只能修饰"成员变量"，它能强制线程每次从主内存获取值，并能保证此变量不会被编译器优化。
     * volatile能解决变量的可见性、有序性；
     * volatile不能解决变量的原子性
     *
     * 可见性： 当一个线程修改了声明为volatile变量的值，新值对于其他要读该变量的线程来说是立即可见的。
     * 有序性： volatile变量的所谓有序性也就是被声明为volatile的变量的临界区代码的执行是有顺序的，即禁止指令重排序。
     * 受限原子性： volatile变量不可保证原子性
     *
     * 1.1 voliate可见性底层实现原理
     *      实际上voliate的可见性实现借助了CPU的lock指令，即在写voliate变量的时候，在该指令前加一个lock指令，这个指令有两个作用：
     * 1）写volatile时处理器会将缓存写回到主内存。
     * 2）一个处理器的缓存写回到主内存会导致其他处理器的缓存失效。（即其他线程缓存该变量地址失效，下次读取时会自动从主存中读取）
     * 注意：基于 CPU 缓存一致性协议，JVM 实现了 volatile 的可见性，但由于总线嗅探机制，会不断的监听总线，如果大量使用 volatile 会引起总线风暴。所以，volatile 的使用要适合具体场景。
     *
     * 1.2 voliate有序性底层实现原理
     * volatile有序性的保证就是通过禁止指令重排序来实现的。指令重排序包括编译器和处理器重排序，JMM会分别限制这两种指令重排序。
     * 那么禁止指令重排序又是如何实现的呢？答案是加内存屏障。
     *
     * 1.3 voliate为什么不保证原子性
     * 首先说明i++的操作本身就不是原子性的，而是分为三步
     *
     * 1、线程读取i
     * 2、i自增，temp = i + 1
     * 3、刷回主存，i = temp
     *
     * 线程A: i=5->temp=6->i=temp=6
     * 线程B: i=5->temp=6,由于voliate,i=6->i=temp=6
     */
    public void volatile关键字() {}

    /**
     * ============ Synchronized底层实现原理
     * Synchronized 是通过对象内部的一个叫做监视器锁（monitor）来实现的，监视器锁本质又是依赖于底层的操作系统的 Mutex Lock（互斥锁）来实现的。
     * 而操作系统实现线程之间的切换需要从用户态转换到核心态，这个成本非常高，状态之间的转换需要相对比较长的时间，这就是为什么 Synchronized 效
     * 率低的原因。因此，这种依赖于操作系统 Mutex Lock 所实现的锁我们称之为 “重量级锁”。
     *
     * Synchronized底层实现原理
     * 同步代码块通过monitorenter和monitorexit执行来进行加锁。当线程执行到monitorenter的时候要先获得锁，才能执行后面的方法。
     * 当线程执行到monitorexit的时候则要释放锁。每个对象自身维护着一个被加锁次数的计数器，当计数器不为0时，只有获得锁的线程才能再次获得锁。
     */
    public void Synchronized底层实现原理() {}

    /**
     * =========== Synchronized锁的升级过程
     * Java SE 1.6 为了减少获得锁和释放锁带来的性能消耗，引入了 “偏向锁” 和 “轻量级锁”：锁一共有 4 种状态，级别从低到高依次是：无锁状态、偏向锁状态、轻量级锁状态和重量级锁状态。锁可以升级但不能降级。
     * 偏向锁：大多数情况下，锁不仅不存在多线程竞争，而且总是由同一线程多次获得，为了让线程获得锁的代价更低而引入了偏向锁。当一个线程访问同步块并获取锁时，会在对象头和栈帧中记录存储锁偏向的线程ID，以后该线程在进入同步块时先判断对象头的Mark Word里是否存储着指向当前线程的偏向锁，如果存在就直接获取锁。
     * 轻量级锁：当其他线程尝试竞争偏向锁时，锁升级为轻量级锁。线程在执行同步块之前，JVM会先在当前线程的栈帧中创建用于存储锁记录的空间，并将对象头中的MarkWord替换为指向锁记录的指针。如果成功，当前线程获得锁，如果失败，标识其他线程竞争锁，当前线程便尝试使用自旋来获取锁。
     * 重量级锁：锁在原地循环等待的时候，是会消耗CPU资源的。所以自旋必须要有一定的条件控制，否则如果一个线程执行同步代码块的时间很长，那么等待锁的线程会不断的循环反而会消耗CPU资源。默认情况下锁自旋的次数是10 次，可以使用-XX:PreBlockSpin参数来设置自旋锁等待的次数。10次后如果还没获取锁，则升级为重量级锁。
     */
    public void Synchronized锁的升级过程() {}

    /**
     * 1.乐观锁: java中的乐观锁---CAS
     * 2.悲观锁: java中的悲观锁  synchronized修饰的方法和方法块  比如我们尝试用的hashtable,以及StringBuffer他们的方法都被synchronized修饰,ReentrantLock不仅悲观还重入(也属于重入锁)
     * 3.自旋锁: 自旋锁就是在获取锁的时候，如果锁被其他线程获取,该线程就会一直循环等待,一直尝试着去获取锁,直到目标达成。而不像普通的锁那样，如果获取不到锁就进入阻塞
     *      自旋默认的次数: 10次
     * 4.可重入锁(递归锁): 可重入锁使一种技术,任意线程在获取到 锁之后能够再次 获取 该锁而不会被锁阻塞
     *      原理 : 通过组合自定义同步器来实现锁的获取和释放
     *      再次获取锁: 识别获取 锁的线程是否为当前占据锁的线程,如果是,则再次成功获取,获取锁后,进行计数自增
     *      释放锁:  释放锁 进行计数自减
     *      java中的可重入锁: ReentrantLock、synchronized修饰的方法或代码段
     * 5.读写锁: 读写锁使一种技术,通过ReentrantReadWriteLock类来实现的,为了提高性能,Java提供了读写锁,读的地方使用 读锁,写的地方使用写锁,在没有写锁的情况下,读锁是无阻塞的,多个读锁不互斥,读锁与写锁互斥,这是由jvm来控制的
     * 6.公平锁: 公平锁使一种思想,多个线程按照顺序来获取锁 ,并发环境中,每个线程会去查看锁的维护队列,如果队列为空,就占有锁,如果队列不为空,就加入等待队列的末尾,按照FIFO原则获取锁
     * 7.非公平锁: 非公平锁也是一种思想,线程尝试获取锁,如果获取不到,按照公平锁的方式,多个线程获取锁不是按照 先到先得的顺序.是无序的,有可能后到了先获取到锁
     *      synchronized是非公平锁
     * 8.共享锁: 共享锁是一种思想,可以多个线程获取读锁,以共享的方式持有锁,和乐观锁还有读写锁同义
     * 9.独占锁: 独占锁是一种思想,只能有一个线程获取锁,以独有的方式持有锁,悲观锁和互斥锁同义
     *      synchronized，ReentrantLock
     * 10.重量级锁: synchronized 就是重量级锁,为了优化重量级锁,引入了轻量级锁和偏向锁
     * 11.轻量级锁: jdk6是加入的一种锁的优化机制,轻量级锁是在没有多线程竞争的情况下使用的CAS操作去消除同步使用的互斥量
     * 12.偏向锁: 偏向锁 是JDK6时加入的一种锁优化机制： 在无竞争的情况下把整个同步都消除掉,连CAS操作都不去做了。偏是指偏心，它的意思是这个锁会偏向于第一个获得它的线程，如果在接下来的执行过程中，该锁一直没有被其他的线程获取，则持有偏向锁的线程将永远不需要再进行同步。持有偏向锁的线程以后每次进入这个锁相关的同步块时，虚拟机都可以不再进行任何同步操作.
     * 13.分段锁: 是一种机制,是不是想到了ConcurrentHashMap了,默认情况下ConcurrentHashMap被细分为16个段(Segment)每次上锁只是锁的每个segment. segment通过继承ReentrntLock来进行加锁,只要保证每个segment是线程安全的,是不是就保证了全局的线程安全.
     * 14.互斥锁: 互斥锁和悲观锁还有独占锁同义,某个资源,只能被一个线程访问,其他线程不能访问.例如上文提到的读写锁中的写锁,写与写之间是互斥的,写与读之间也是互斥的
     * 15.同步锁:
     */
    public void JDK锁() {}
}
