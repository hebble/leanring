package com.learning.study.object;

/**
 * https://blog.csdn.net/a745233700/category_9280527.html java基础篇
 */
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
             finalize是Object类的一个方法，供垃圾收集时的其他资源回收，

     2.反射机制详解 https://blog.csdn.net/a745233700/article/details/82893076
        2.1 什么是反射?
             （1）Java反射机制的核心是在程序运行时动态加载类并获取类的详细信息，从而操作类或对象的属性和方法。本质是JVM得到class对象之后，再通过class对象进行反编译，从而获取对象的各种信息。
             （2）Java属于先编译再运行的语言，程序中对象的类型在编译期就确定下来了，而当程序在运行时可能需要动态加载某些类，这些类因为之前用不到，所以没有被加载到JVM。通过反射，可以在运行时动态地创建对象并调用其属性，
                不需要提前在编译期知道运行的对象是谁。
        2.2 反射的原理：
            见反射原理图.png
            下图是类的正常加载过程、反射原理与class对象：
            Class对象的由来是将.class文件读入内存，并为之创建一个Class对象。
        2.3 反射的优缺点：
             优点：
                在运行时获得类的各种内容，进行反编译，对于Java这种先编译再运行的语言，能够让我们很方便的创建灵活的代码，这些代码可以在运行时装配，无需在组件之间进行源代码的链接，更加容易实现面向对象。
             缺点：
                （1）反射会消耗一定的系统资源，因此，如果不需要动态地创建一个对象，那么就不需要用反射；
                （2）反射调用方法时可以忽略权限检查，因此可能会破坏封装性而导致安全问题。
        2.4 反射的用途：
             (1)反编译：.class-->.java
             (2)通过反射机制访问java对象的属性，方法，构造方法等
             (3)当我们在使用IDE,比如Ecplise时，当我们输入一个对象或者类，并想调用他的属性和方法是，一按点号，编译器就会自动列出他的属性或者方法，这里就是用到反射。
             (4)反射最重要的用途就是开发各种通用框架。比如很多框架（Spring）都是配置化的（比如通过XML文件配置Bean），为了保证框架的通用性，他们可能需要根据配置文件加载不同的类或者对象，调用不同的方法，这个时候就必须使用到反射了，运行时动态加载需要的加载的对象。
             (5)例如，在使用Strut2框架的开发过程中，我们一般会在struts.xml里去配置Action，
                加载数据库驱动的，用到的也是反射。
                Class.forName("com.mysql.jdbc.Driver"); // 动态加载mysql驱动
        2.5 反射机制常用的类
             Java.lang.Class;
             Java.lang.reflect.Constructor;
             Java.lang.reflect.Field;
             Java.lang.reflect.Method;
             Java.lang.reflect.Modifier;

        2.6 反射的基本使用
            2.6.1 获得Class：主要有三种方法：
                 （1）Object-->getClass
                 （2）任何数据类型（包括基本的数据类型）都有一个“静态”的class属性
                 （3）通过class类的静态方法：forName(String className)（最常用）
                注意，在运行期间，一个类，只有一个Class对象产生，所以打印结果都是true；
                三种方式中，常用第三种，第一种对象都有了还要反射干什么，第二种需要导入类包，依赖太强，不导包就抛编译错误。一般都使用第三种，一个字符串可以传入也可以写在配置文件中等多种方法。
            2.6.2 判断是否为某个类的示例
                一般的，我们使用instanceof 关键字来判断是否为某个类的实例。同时我们也可以借助反射中Class对象的isInstance()方法来判断时候为某个类的实例，他是一个native方法。
                public native boolean isInstance(Object obj);
            2.6.3 创建实例：通过反射来生成对象主要有两种方法：
                （1）使用Class对象的newInstance()方法来创建Class对象对应类的实例。
                     Class<?> c = String.class;
                     Object str = c.newInstance();
                （2）先通过Class对象获取指定的Constructor对象，再调用Constructor对象的newInstance()方法来创建对象，这种方法可以用指定的构造器构造类的实例。
                     //获取String的Class对象
                     Class<?> str = String.class;
                     //通过Class对象获取指定的Constructor构造器对象
                     Constructor constructor=c.getConstructor(String.class);
                     //根据构造器创建实例：
                     Object obj = constructor.newInstance(“hello reflection”);

     3.hashCode()与equals() https://blog.csdn.net/a745233700/article/details/83186808
        3.1 什么是hashCode：
            hashCode就是对象的散列码，是根据对象的某些信息推导出的一个整数值，默认情况下表示是对象的存储地址。通过散列码，可以提高检索的效率，主要用于在散列存储结构中快速确定对象的存储地址，如Hashtable、hashMap中。
            为什么说hashcode可以提高检索效率呢？我们先看一个例子，如果想判断一个集合是否包含某个对象，最简单的做法是怎样的呢？逐一取出集合中的每个元素与要查找的对象进行比较，当发现该元素与要查找的对象进行equals()比较的结果为true时，
            则停止继续查找并返回true，否则，返回false。如果一个集合中有很多个元素，比如有一万个元素，并且没有包含要查找的对象时，则意味着你的程序需要从集合中取出一万个元素进行逐一比较才能得到结论，这样做的效率是非常低的。
            这时，可以采用哈希算法（散列算法）来提高从集合中查找元素的效率，将数据按特定算法直接分配到不同区域上。将集合分成若干个存储区域，每个对象可以计算出一个哈希码，可以将哈希码分组（使用不同的hash函数来计算的），
            每组分别对应某个存储区域，根据一个对象的哈希码就可以确定该对象应该存储在哪个区域，大大减少查询匹配元素的数量。

            比如HashSet就是采用哈希算法存取对象的集合，它内部采用对某个数字n进行取余的方式对哈希码进行分组和划分对象的存储区域，当从HashSet集合中查找某个对象时，Java系统首先调用对象的hashCode()方法获得该对象的哈希码，
            然后根据哈希吗找到相应的存储区域，最后取得该存储区域内的每个元素与该对象进行equals()比较，这样就不用遍历集合中的所有元素就可以得到结论。

            下面通过String类的hashCode()计算一组散列码：
                 public class HashCodeTest {
                     public static void main(String[] args) {
                         int hash= 0;
                         String s= "ok";
                         StringBuilder sb = new StringBuilder(s);

                         System.out.println(s.hashCode() + "  " + sb.hashCode());

                         String t = new String("ok");
                         StringBuilder tb =new StringBuilder(s);
                         System.out.println(t.hashCode() + "  " + tb.hashCode());
                     }
                 }
             运行结果：
                 3548  1829164700
                 3548  2018699554
            我们可以看出，字符串s与t拥有相同的散列码，这是因为字符串的散列码是由内容导出的。而字符串缓冲sb与tb却有着不同的散列码，这是因为StringBuilder没有重写hashCode()方法，它的散列码是由Object类默认的hashCode()计算出来的对象存储地址，所以散列码自然也就不同了。
        3.2 如何重写hashCode()方法
            java 7还提供了另外一个方法java.util.Objects.hash(Object... objects)，当我们需要组合多个散列值时可以调用该方法。进一步简化上述的代码：
                 import java.util.Objects;
                 public  class Model {
                     private   String name;
                     private double salary;
                     private int sex;

                     @Override
                     public int hashCode() {
                         return Objects.hash(name,salary,sex);
                     }
                 }
         3.3 equals()与hashCode()的联系：
             Java的超类Object类已经定义了equals()和hashCode()方法，在Obeject类中，equals()比较的是两个对象的内存地址是否相等，而hashCode()返回的是对象的内存地址。所以hashCode主要是用于查找使用的，而equals()是用于
            比较两个对象是否相等的。但有时候我们根据特定的需求，可能要重写这两个方法，在重写这两个方法的时候，主要注意保持一下几个特性：
                 （1）如果两个对象的equals()结果为true，那么这两个对象的hashCode一定相同；
                 （2）两个对象的hashCode()结果相同，并不能代表两个对象的equals()一定为true，只能够说明这两个对象在一个散列存储结构中。
                 （3）如果对象的equals()被重写，那么对象的hashCode()也要重写。
            注意: 根据第一条特性:
                 重写equals()的同时要重写hashCode()方法, 否则hashCode()默认返回对象地址, 这样hashCode就不相等了
        3.4 由hashCode()造成的内存泄露问题
             import java.util.HashSet;
                 public class Demo {
                     public static void main(String[] args){
                     HashSet<RectObject> set = new HashSet<RectObject>();
                     RectObject r1 = new RectObject(3,3);
                     RectObject r2 = new RectObject(5,5);
                     RectObject r3 = new RectObject(3,5);
                     set.add(r1);
                     set.add(r2);
                     set.add(r3);
                     r3.y = 7;
                     System.out.println("删除前的大小size:"+set.size());//3
                     set.remove(r3);
                     System.out.println("删除后的大小size:"+set.size());//3
                 }
             }
             运行结果：
                 删除前的大小size:3
                 删除后的大小size:3
             在这里，我们发现了一个问题，当我们调用了remove删除r3对象，以为删除了r3,但事实上并没有删除，这就叫做内存泄露，就是不用的对象但是他还在内存中。所以我们多次这样操作之后，内存就爆了
             上面的这个内存泄露告诉我一个信息：如果我们将对象的属性值参与了hashCode的运算中，在进行删除的时候，就不能对其属性值进行修改，否则会导致内存泄露问题。
        3.5 基本数据类型和String类型的hashCode()方法和equals()方法
             （1）hashCode()：八种基本类型的hashCode()很简单就是直接返回他们的数值大小，String对象是通过一个复杂的计算方式，但是这种计算方式能够保证，如果这个字符串的值相等的话，他们的hashCode就是相等的。
             （2）equals()：8种基本类型的封装类的equals方法就是直接比较数值，String类型的equals方法是比较字符串的值的。
        3.6 hashcode 在 JVM 发生 GC 前后的值是否发生改变？
             回答评论区一个小伙伴的问题：对象在 GC 后存储位置会发生改变，那这个对象的 hashcode 会不会发生变化？如果在 GC 前用户线程获取到对象的hashcode，然后就 GC 了，GC 之后根据 hashcode 再找对象时会不会找不到？
             答案当时是不会的！

             前面提到，在不重写 hashcode() 的时候，hashcode 是根据对象的内存地址映射生成的。而且 java.lang.Object 的对 hashCode() 方法有三项约定：
                 第一，当一个对象 equals() 方法所使用的字段不变时，多次调用 hashCode() 方法的值应保持不变。
                 第二，如果两个对象 equals(Object o) 方法是相等的，则 hashCode() 方法值必须相等。
                 第三，如果两个对象 equals(Object o) 方法是不相等，则 hashCode() 方法值不要求相等，但在这种情况下尽量确保 hashCode 不同，以提升性能。
             而我们知道，JVM 进行 GC 操作时，无论是标记复制算法还是标记整理算法，对象的内存地址都是会变的，但 hashcode 又要求保持不变，JVM到底是如何实现这一功能的呢？
                 当hashCode方法未被调用时，对象头中用来存储 hashcode 的位置为0，但是当 hashCode() 方法首次被调用时，才会计算对应的hashcode值，并存储到 object header 中。当再次被调用时，则直接从对象头中获取计算好hashcode就可以了。
                 上述方式就保证了即使GC发生，对象存储地址发生了变化，也不影响 hashcode 的值。比如在GC发生前调用了 hashCode() 方法，hashcode 值已经被存储，即使地址变了也没关系；在GC发生后调用hashCode方法更是如此。

     */
}




































