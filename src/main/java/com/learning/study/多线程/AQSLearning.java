package com.learning.study.多线程;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

/**
 * https://blog.csdn.net/a745233700/article/details/120668889 AQS抽象队列同步器原理
 */
public class AQSLearning {
    /**
     1.AQS 的工作原理
        1.1 什么是 AQS
            AQS，Abstract Queued Synchronizer，抽象队列同步器，是 J.U.C 中实现锁及同步组件的基础。工作原理就是如果被请求的共享资源空闲，则将当前请求资源的线程设置为有效的工作线程，并且将共享资源设置为锁定状态，如果被请求的共享资源被占用，
            那么就将获取不到锁的线程加入到等待队列中。这时，就需要一套线程阻塞等待以及被唤醒时的锁分配机制，而 AQS 是通过 CLH 队列实现锁分配的机制。
            AQS分为两种模式：独占模式 EXCLUSIVE 和 共享模式 SHARED，像 ReentrantLock、CyclicBarrier 是基于独占模式模式实现的，Semaphore，CountDownLatch 等是基于共享模式
        1.2 CLH 同步队列的模型
            CLH 队列是由内部类 Node 构成的同步队列，是一个双向队列（不存在队列实例，仅存在节点之间的关联关系），将请求共享资源的线程封装成 Node 节点来实现锁的分配；同时利用内部类 ConditionObject 构建等待队列，当调用 ConditionObject 的
            await() 方法后，线程将会加入等待队列中，当调用 ConditionObject 的 signal() 方法后，线程将从等待队列转移动同步队列中进行锁竞争。AQS 中只能存在一个同步队列，但可拥有多个等待队列。AQS 的 CLH 同步队列的模型如下图：
            详见AQS 的 CLH 同步队列的模型.png

            AQS 有三个主要变量，分别是 head、tail、state，其中 head 指向同步队列的头部，注意 head 为空结点，不存储信息。而 tail 则是同步队列的队尾，同步队列采用的是双向链表的结构是为了方便对队列进行查找操作。当 Node 节点被设置为 head 后，
            其 thread 信息和前驱结点将被清空，因为该线程已获取到同步状态，正在执行了，也就没有必要存储相关信息了，head 只保存后继结点的指针即可，便于 head 结点释放同步状态后唤醒后继结点。
            队列的入队和出队操作都是无锁操作，基于 CAS+自旋锁 实现，AQS 维护了一个 volatile 修饰的 int 类型的 state 同步状态，volatile 保证线程之间的可见性，并通过 CAS 对该同步状态进行原子操作、实现对其值的修改。当 state=0 时，表示没有
            任何线程占有共享资源的锁，当 state=1 时，则说明当前有线程正在使用共享变量，其他线程必须加入同步队列进行等待；

    2.AQS 的设计模式
        2.1 QS 的模板方法模式
            AQS 的基于模板方法模式设计的，在 AQS 抽象类中已经实现了线程在等待队列的维护方式（如获取资源失败入队/唤醒出队等），而对于具体共享资源 state 的获取与释放（也就是锁的获取和释放）则交由具体的同步器来实现
            具体的同步器需要实现以下几种方法：
                 isHeldExclusively()：该线程是否正在独占资源，只有用到 condition 才需要去实现它
                 tryAcquire(int)：独占模式，尝试获取资源，成功则返回 true，失败则返回 false
                 tryRelease(int)：独占方式，尝试释放资源，成功则返回 true，失败则返回 false
                 tryAcquireShared(int)：共享方式，尝试获取资源。负数表示失败；0表示成功，但没有剩余可用资源；正数表示成功，且有剩余资源
                 tryReleaseShared(int)：共享方式，尝试释放资源，如果释放后允许唤醒后续等待结点返回true，否则返回false
        2.2 JUC 中提供的同步器
             闭锁 CountDownLatch：用于让主线程等待一组事件全部发生后继续执行。
             栅栏 CyclicBarrier：用于等待其它线程，且会阻塞自己当前线程，所有线程必须全部到达栅栏位置后，才能继续执行；且在所有线程到达栅栏处之后，可以触发执行另外一个预先设置的线程。
             信号量 Semaphore：用于控制访问资源的线程个数，常常用于实现资源池，如数据库连接池，线程池。在 Semaphore 中，acquire 方法用于获取资源，有的话，继续执行，没有资源的话将阻塞直到有其它线程调用 release 方法释放资源；
             交换器 Exchanger：用于线程之间进行数据交换；当两个线程都到达共同的同步点（都执行到exchanger.exchange 的时刻）时，发生数据交换，否则会等待直到其它线程到达；

        CountDownLatch 和 CyclicBarrier 的区别？
         两者都可以用来表示代码运行到某个点上，二者的区别在于：
             (1)CyclicBarrier 的某个线程运行到某个位置之后就停止运行，直到所有的线程都到达了这个点，所有线程才重新运行；CountDownLatch 的某线程运行到某个位置之后，只是给计数值-1而已，该线程继续运行；
             (2)CyclicBarrier 可重用，CountDownLatch 不可重用，计数值 为 0 时该 CountDownLatch 就不可再用了。

     3.CountDownLatch
        3.1 什么是 CountDownLatch?
            CountDownLatch，闭锁，就是一个基于 AQS 共享模式的同步计数器，它内部的方法都是围绕 AQS 实现的。主要作用是使一个或一组线程在其他线程执行完毕之前，一直处于等待状态，直到其他线程执行完成后再继续执行。
            CountDownLatch 利用 AQS 的 state 变量充当计数器（由 volatile 修饰并使用 CAS 进行更新的），计数器的初始值就是线程的数量，每当一个线程执行完成，计数器的值就会减一，当计数器的值为 0 时，表示所有的
            线程都已经完成任务了，那么接下来就唤醒在 CountDownLatch 上等待的线程执行后面的任务。
            那么当计数器的值为 0 时，主线程是如何被唤醒的呢？这就要从 CountDownLatch 的工作流程来说明了，CountDownLatch 的工作流程可以看成在一开始只在 CLH 队列中放入一个主线程，然后不停的唤醒，唤醒之后如果
            发现 state 还是不为0，则继续等待。而主线程什么时候会被唤醒呢？当每个子线程执行完毕的时候，会调用 countDown() 并基于 CAS 将计数器 state 的值减一，减一成功释放资源后，就会调用 unparkSuccessor()
            唤醒主线程，当所有的子线程都执行完了，也就是 state 为 0 时，这时候主线程被唤醒之后就可以继续执行了。
            state 被减成了 0 之后，就无法继续使用这个 CountDownLatch 了，需要重新 new 一个，因为 state 的数量只有在初始化 CountDownLatch 的时候才可以设置，这也是 CountDownLatch 不可重用的原因。
        3.2 CountDownLatch 的源码简单说明
            从代码层面上来看，CountDownLatch 基于内部类 Sync 实现，而 Sync 继承自 AQS。CountDownLatch 最主要有两个方法：await() 和 countDown()
                 await()：调用该方法的线程会被挂起，直到 CountDownLatch 计数器的值为 0 才继续执行，底层使用的是 AQS 的 tryAcquireShared()
                 countDown()：用于减少计数器的数量，如果计数减为 0 的话，就会唤醒主线程，底层使用的是 AQS 的 releaseShared()

     4.CyclicBarrier
        4.1 什么是CyclicBarrier
            CyclicBarrier，循环栅栏，通过 CyclicBarrier 可以实现一组线程之间的相互等待，当所有线程都到达屏障点之后再执行后续的操作。通过 await() 方法可以实现等待，当最后一个线程执行完，会使得所有在相应 CyclicBarrier 实例上的等待的线程被唤醒，而最后一个线程自身不会被暂停。
            CyclicBarrier 没有像 CountDownLatch 和 ReentrantLock 使用 AQS 的 state 变量，它是直接借助 ReentrantLock 加上 Condition 等待唤醒的功能进而实现的。在构建 CyclicBarrier 的时候，传入的值会赋值给 CyclicBarrier 内部维护的变量 count，同时也会赋值给 parties 变量（这是可以复用的关键）。
            线程调用 await() 表示线程已经到达栅栏，每次调用 await() 时，会将 count 减一，操作 count 值是直接使用 ReentrantLock 来保证线程安全性的，如果 count 不为 0，则添加到 condition 队列中，如果 count 等于 0，则把节点从 condition 队列中移除并添加到 AQS 队列中进行全部唤醒，并且将 parties 的值重新赋值给 count 从而实现复用。
        4.2 CyclicBarrier 的源码分析:
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
        4.3 代码场景:
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

     5.Semaphore
        5.1 什么是 Semaphore
            Semaphore 信号量，主要用于控制并发访问共享资源的线程数量，底层基于 AQS 共享模式，并依赖 AQS 的变量 state 作为许可证 permit，通过控制许可证的数量，来保证线程之间的配合。线程使用 acquire()
            获取访问许可，只有拿到 “许可证” 后才能继续运行，当 Semaphore 的 permit 不为 0 的时候，对请求资源的线程放行，同时 permit 的值减1，当 permit 的值为 0 时，那么请求资源的线程会被阻塞直到其他
            线程释放访问许可，当线程对共享资源操作完成后，使用 release() 归还访问许可。不同于 CyclicBarrier 和 ReentrantLock，Semaphore 不会使用到 AQS 的 Condition 条件队列，都是在 CLH 同步队列中
            操作，只是当前线程会被 park。另外 Semaphore 是不可重入的。
        5.2 Semaphore 的公平和非公平两种模式
             Semaphore 通过自定义两种不同的同步器（FairSync 和 NonfairSync）提供了公平和非公平两种工作模式，两种模式下分别提供了限时/不限时、响应中断/不响应中断的获取资源的方法（限时获取总是及时响应中断的），而所有的释放资源的 release() 操作是统一的。
             公平模式：遵循 FIFO，调用 acquire() 方法获取许可证的顺序时，先判断同步队列中是不是存在其他的等待线程，如果存在就将请求线程封装成 Node 结点加入同步队列，从而保证每个线程获取同步状态都是按照先到先得的顺序执行的，否则对 state 值进行减操作并返回剩下的信号量
             非公平模式：是抢占式的，通过竞争的方式获取，不管同步队列中是否存在等待线程，有可能一个新的获取线程恰好在一个许可证释放时得到了这个许可证，而前面还有等待的线程。
        5.3 代码场景:
     */
    public void test2() {
        Semaphore semaphore = new Semaphore(10);  //停车场同时容纳的车辆10
        //模拟100辆车进入停车场
        for(int i=0;i<100;i++){
            new Thread(() ->  {
                try {
                    System.out.println("===="+Thread.currentThread().getName()+"来到停车场");
                    if(semaphore.availablePermits()==0){
                        System.out.println("车位不足，请耐心等待");
                    }
                    semaphore.acquire();//获取令牌尝试进入停车场
                    System.out.println(Thread.currentThread().getName()+"成功进入停车场");
                    Thread.sleep(new Random().nextInt(10000));//模拟车辆在停车场停留的时间
                    System.out.println(Thread.currentThread().getName()+"驶出停车场");
                    semaphore.release();//释放令牌，腾出停车场车位
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },i+"号车").start();
        }
    }
}


























