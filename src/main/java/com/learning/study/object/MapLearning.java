package com.learning.study.object;

public class MapLearning {
    /**
     * 1.map数据结构
     * 在JDK1.7中，底层数据结构是数组+链表
     * 在JDK1.8中，底层数据结构是数组+链表+红黑树
     */

    /**
     * 2.JDK1.8中什么情况下，链表会转变为红黑树？
     * 当数组长度大于64且链表节点个数大于8时才会转变为红黑树，如果链表节点个数小于6则又会退化为链表
     * 在链表转换为红黑树前面会进行判断，如果数组长度小于64，则会优先选择扩容。
     */

    /**
     * 3.存储数据的过程
     * 概述：当我们往HashMap中存储数据时，首先会利用hash(key)方法 计算出key的hash值，再利用该 hash值 与 HashMap数组的长度-1 进行 与运算，从而得到该key在数组中对应的 下标位置。
     * 如果该位置上目前还没有存储数据，则直接将该key-value键值对数据存入到数组中的这个位置；如果该位置目前已经有了数据，则把新的数据存入到一个链表中；当链表的长度超过阈值(JDK 8中该值为8)时，
     * 会将链表转换为红黑树(转换为红黑树还需要满足其他的条件，链表长度达到阈值只是其中的一个条件)。通过这样的机制来解决存值时可能产生的哈希冲突问题，并可以大大提高我们的查找效率。
     */
    /**
     * 4.HashMap怎么设定初始容量大小？
     * 一般如果new HashMap（）不传值，默认大小是16，负载因子是0.75。如果自己传入初始大小k，初始化大小为大于k的2的整数次方，例如如果传10，大小为16。
     */
}
