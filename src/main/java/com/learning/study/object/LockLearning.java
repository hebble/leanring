package com.learning.study.object;

public class LockLearning {
    //TODO 1.synchronized(悲观锁、同步锁)
    /**
     * 1.synchronized(悲观锁、同步锁)
     *      synchronized被称为“重量级的锁”方式，也是“悲观锁”——效率比较低。
     *     1.1synchronized有几种使用方式：
     *      a).同步代码块【常用】
     *      b).同步方法【常用】
     * 2.Lock锁也称同步锁
     * public void lock():加同步锁。
     * public void unlock():释放同步锁。
     */

    /**
     * sychronized和 ReenteredLock区别
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

    /**
     * volatile关键字
     * volatile是一个"变量修饰符"，它只能修饰"成员变量"，它能强制线程每次从主内存获取值，并能保证此变量不会被编译器优化。
     * volatile能解决变量的可见性、有序性；
     * volatile不能解决变量的原子性
     */


}
