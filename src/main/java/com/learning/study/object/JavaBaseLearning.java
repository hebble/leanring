package com.learning.study.object;

public class JavaBaseLearning {
    /**
     1.final，finally和finalize的区别
        1.1 final
             (1)final是一个修饰词也是一个关键字。
             (2)被final修饰的类是属于最终类无法被继承
             (3)对于一个final变量，如果是基本数据类型的变量，则其数值一旦初始化以后就不能被修改。如果是引用类型的变量，则对初始化之后便不能再让其指向另一个对象，但是他指向的对象里边的内容是可变的。
             (4)被final修饰的方法无法被重写，但是可以重载。
        1.2 finally
             (1)finally是一个关键字
             (2)finally在处理异常机制的提供finally方法来执行一切操作，不管有没有异常捕捉或者抛出，finally都会执行操作，通常用于释放资源，关闭资源的操作。
             (3)finally正常情况下都会别执行，但是有两个极端情况下不会被执行
                a.如果对应的try方法快没有执行，则这个try方法快中的finally不会被执行
                b.如果try方法中jvm关机，列入system.exit(n)，finally也不会执行(电源都扒了怎么执行)
             (4)finally中如果有return关键字，则会覆盖try和catch中的return关键字，会导致return覆盖无法return，所以不推荐在finally中写return关键字
        1.3 finalize
             (1)finalize:是Object中的protected方法，子类可以覆盖方法可以实现资源的清理工作。
             (2)GC在回收对象之前都会调用该方法
             (3)finalize()方法还是存在很多问题的
                 java语言规范并不保证finalize方法会被及时的执行，更根本不会保证他们一定会被执行。
                 finalize()方法可能带来性能问题。因为JVM通常在单独的低优先级线程中完成finalize的执行。
                 finalize()方法中，可将待回收对象复制给GC Roots 可达的对象引用，从而达到对象再生的目的
                 finalize方法最多由GC执行一次（但是可以手动的调对象的finalize方法）
        总结
             final：常量用于声明属性，方法和类，分别表示属性不可交变，方法不可覆盖，类不可继承。
             finally：异常处理机构 异常处理语句结构的一部分，表示总是执行。
             finalize是Object类的一个方法，供垃圾收集时的其他资源回收，等操作
     */
}
