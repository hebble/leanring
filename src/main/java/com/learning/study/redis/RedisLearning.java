package com.learning.study.redis;

public class RedisLearning {
    /**
     * 1.redis概念
     * 与MySQL数据库不同的是，Redis的数据是存在内存中的。它的读写速度非常快，每秒可以处理超过10万次读写操作。因此redis被广泛应用于缓存，另外，Redis也经常用来做分布式锁。除此之外，Redis支持事务、持久化、LUA 脚本、LRU 驱动事件、多种集群方案。
     *
     * 2.redis数据结构
     *      String（字符串）
     *      Hash（哈希）
     *      List（列表）
     *      Set（集合）
     *      zset（有序集合）
     * 3.Redis为什么这么快？
     *      基于内存存储实现
     *      高效的数据结构
     *      合理的数据编码
     *      合理的线程模型
     *          I/O 多路复用(I/O ：网络 I/O, 多路 ：多个网络连接, 复用：复用同一个线程。)
     *          单线程模型(Redis是单线程模型的，而单线程避免了CPU不必要的上下文切换和竞争锁的消耗)
     *      虚拟内存机制
     *          虚拟内存机制就是暂时把不经常访问的数据(冷数据)从内存交换到磁盘中，从而腾出宝贵的内存空间用于其它需要访问的数据(热数据)。通过VM功能可以实现冷热数据分离，使热数据仍在内存中、冷数据保存到磁盘。这样就可以避免因为内存不足而造成访问速度下降的问题。
     */

    /**
     * 4. 什么是缓存击穿、缓存穿透、缓存雪崩？
     *
     */
}
