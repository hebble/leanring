package com.learning.study.object;

import lombok.extern.slf4j.Slf4j;

/**
 * https://blog.csdn.net/a745233700/category_9280527.html java基础篇
 */
@Slf4j
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

     4.HashMap 与 ConcurrentHashMap原理 https://blog.csdn.net/a745233700/article/details/119709104
         4.1 什么是HashMap?
             （1）HashMap 是基于 Map 接口的非同步实现，线程不安全，是为了快速存取而设计的；它采用 key-value 键值对的形式存放元素（并封装成 Node 对象），允许使用 null 键和 null 值，但只允许存在一个键为 null，并且存放在 Node[0] 的位置，不过允许存在多个 value 为 null 的情况。
             （2）在 JDK7 及之前的版本，HashMap 的数据结构可以看成“数组+链表”，在 JDK8 及之后的版本，数据结构可以看成"数组+链表+红黑树"，也就是说 HashMap  底层采用数组实现，数组的每个位置都存储一个单向链表，当链表的长度超过一定的阈值时，就会转换成红黑树。转换的目的是当链表中
                 元素较多时，也能保证HashMap的存取效率（备注：链表转为红黑树只有在数组的长度大于等于64才会触发）
             （3）HashMap 有两个影响性能的关键参数：“初始容量”和“加载因子”：
                 容量 capacity：就是哈希表中数组的数量，默认初始容量是16，容量必须是2的N次幂，这是为了提高计算机的执行效率。
                 加载因子 loadfactor：在 HashMap 扩容之前，容量可以达到多满的程度，默认值为 0.75
                 扩容阈值 threshold = capacity * loadfactor
             （4）采用 Fail-Fast 机制，底层通过一个 modCount 值记录修改的次数，对 HashMap 的修改操作都会增加这个值。迭代器在初始过程中会将这个值赋给 exceptedModCount ，在迭代的过程中，如果发现 modCount 和 exceptedModCount 的值不一致，代表有其他线程修改了Map，就立刻抛出异常。
        4.2 HashMap 的 put() 方法添加元素的过程
             （1）重新计算 hash 值：(让 hashCode 的高16位参与异或运算)
                 拿到 key 的 hashcode 值之后，调用 hash() 方法重新计算 hash 值，防止质量低下的 hashCode() 函数出现，从而使 hash 值的分布尽量均匀。
                 JDK8 及之后的版本，对 hash() 方法进行了优化，重新计算 hash 值时，让 hashCode 的高16位参与异或运算，目的是即使 table 数组的长度较小，在计算元素存储位置时，也能让高位也参与运算。
                    （key == null）? 0 : ( h = key.hashcode()) ^ (h >>> 16)
             （2）计算元素存放在数组中的哪个位置：
                 将重新计算出来的 hash 值与 (tablel.length-1) 进行位与&运算，得出元素应该放入数组的哪个位置。
                 为什么 HashMap 的底层数组长度总是2的n次方幂？因为当 length 为2的n次方时，h & (length - 1) 就相当于对 length 取模，而且速度比直接取模要快得多，二者等价不等效，这是HashMap在性能上的一个优化
             （3）将 key-value 添加到数组中：
                 a.如果计算出的数组位置上为空，那么直接将这个元素插入放到该位置中。
                 b.如果数组该位置上已经存在链表，则使用 equals() 比较链表上是否存在 key 相同的节点，如果为true，则替换原元素；如果不存在，则在链表的尾部插入新节点（Jdk1.7及以前的版本使用的头插法）
                 c.如果插入元素后，如果链表的节点数是否超过8个，则调用 treeifyBin() 将链表节点转为红黑树节点。
                 d.最后判断 HashMap 总容量是否超过阈值 threshold，则调用 resize() 方法进行扩容，扩容后数组的长度变成原来的2倍。
             在 HashMap 中，当发生hash冲突时，解决方式是采用拉链法，也就是将所有哈希值相同的记录都放在同一个链表中，除此之外，解决hash冲突的方式有：
                 开放地址法（线性探测再散列、二次探测再散列、伪随机探测再散列）：
                     当冲突发生时，在散列表中形成一个探测序列，沿此序列逐个单元地查找，直到找到给定的关键字，或者碰到一个开放的地址为止（即该地址单元为空）。如果是插入的情况，在探查到开放的地址，
                     则可将待插入的新结点存入该地址单元，如果是查找的情况，探查到开放的地址则表明表中无待查的关键字，即查找失败。
                 再哈希法：
                     产生冲突时，使用另外的哈希函数计算出一个新的哈希地址、直到冲突不再发生
                 建立一个公共溢出区：
                     把冲突的记录都放在另一个存储空间，不放在表里面。
        4.3 HashMap扩容的过程
             （1）重新建立一个新的数组，长度为原数组的两倍；
             （2）遍历旧数组的每个数据，重新计算每个元素在新数组中的存储位置。使用节点的hash值与旧数组长度进行位与运算，如果运算结果为0，表示元素在新数组中的位置不变；否则，则在新数组中的位置下标=原位置+原数组长度。
             （3）将旧数组上的每个数据使用尾插法逐个转移到新数组中，并重新设置扩容阈值。

             问题：为什么扩容时节点重 hash 只可能分布在原索引位置或者 原索引长度+oldCap 位置呢？换句话说，扩容时使用节点的hash值跟oldCap进行位与运算，以此决定将节点分布到原索引位置或者原索引+oldCap位置上的原理是什么呢？
                根据h & (length - 1) 就相当于对 length 取模这个原理得出的
                 假设老表的容量为16，则新表容量为16*2=32
                 老表length-1(15):
                    ...001111
                 新表length-1(31):
                     ...011111
                 新表比老表多一个第5位的1, 所以直接与length位与运算
                     ...010000
                 结果只取决于节点hash值计算后的倒数第5位

             具体实例:
                假设老表的容量为16，则新表容量为16*2=32，假设节点1的hash值为 0000 0000 0000 0000 0000 1111 0000 1010，节点2的hash值为 0000 0000 0000 0000 0000 1111 0001 1010。
                见图扩容后hashcode计算元素存放在数组位置示意图.png
                 由于老表的长度限制，节点1和节点2的索引位置只取决于节点hash值的最后4位。再看计算2，计算2为元素在新表中的索引计算，可以看出如果两个节点在老表的索引位置相同，则新表的索引位置只取决于节点hash值倒数第5位的值，而此位置的值刚好为老表的容量值16，
                 此时节点在新表的索引位置只有两种情况：原索引位置和 原索引+oldCap位置（在此例中即为10和10+16=26）。由于结果只取决于节点hash值的倒数第5位，而此位置的值刚好为老表的容量值16，因此此时新表的索引位置的计算可以替换为计算3，直接使用节点的hash值
                 与老表的容量16进行位于运算，如果结果为0则该节点在新表的索引位置为原索引位置，否则该节点在新表的索引位置为 原索引+ oldCap 位置。
        4.4 HashMap 链表转换成红黑树
             当数组中某个位置的节点达到8个时，会触发 treeifyBin() 方法将链表节点（Node）转红黑树节点（TreeNode，间接继承Node），转成红黑树节点后，其实链表的结构还存在，通过next属性维持，红黑树节点在进行操作时都会维护链表的结构，并不是转为红黑树节点后，
             链表结构就不存在了。当数组中某个位置的节点在移除后达到6个时，并且该索引位置的节点为红黑树节点，会触发 untreeify() 将红黑树节点转化成链表节点。
             HashMap 在进行插入和删除时有可能会触发红黑树的插入平衡调整（balanceInsertion方法）或删除平衡调整（balanceDeletion ）方法，调整的方式主要有以下手段：左旋转（rotateLeft方法）、右旋转（rotateRight方法）、改变节点颜色（x.red = false、x.red = true），
             进行调整的原因是为了维持红黑树的数据结构。

             当链表长过长时会转换成红黑树，那能不能使用AVL树替代呢？
                 AVL树是完全平衡二叉树，要求每个结点的左右子树的高度之差的绝对值最多为1，而红黑树通过适当的放低该条件（红黑树限制从根到叶子的最长的可能路径不多于最短的可能路径的两倍长，结果是这个树大致上是平衡的），以此来减少插入/删除时的平衡调整耗时，
                 从而获取更好的性能，虽然会导致红黑树的查询会比AVL稍慢，但相比插入/删除时获取的时间，这个付出在大多数情况下显然是值得的。
        4.5 HashMap 在 JDK7 和 JDK8 有哪些区别?
             (1)数据结构：在 JDK7 及之前的版本，HashMap 的数据结构可以看成“数组+链表”，在 JDK8 及之后的版本，数据结构可以看成"数组+链表+红黑树"，当链表的长度超过8时，链表就会转换成红黑树，从而降低时间复杂度（由O(n) 变成了 O(logN)），提高了效率
             (2)对数据重哈希：JDK8 及之后的版本，对 hash() 方法进行了优化，重新计算 hash 值时，让 hashCode 的高16位参与异或运算，目的是在 table 的 length较小的时候，在进行计算元素存储位置时，也让高位也参与运算。
             (3)在 JDK7 及之前的版本，在添加元素的时候，采用头插法，所以在扩容的时候，会导致之前元素相对位置倒置了，在多线程环境下扩容可能造成环形链表而导致死循环的问题。DK1.8之后使用的是尾插法，扩容是不会改变元素的相对位置
             (4)扩容时重新计算元素的存储位置的方式：JDK7 及之前的版本重新计算存储位置是直接使用 hash & (table.length-1)；JDK8 使用节点的hash值与旧数组长度进行位与运算，如果运算结果为0，表示元素在新数组中的位置不变；否则，则在新数组中的位置下标=原位置+原数组长度。
             (5)JDK7 是先扩容后插入，这就导致无论这次插入是否发生hash冲突都需要进行扩容，但如果这次插入并没有发生Hash冲突的话，那么就会造成一次无效扩容；JDK8是先插入再扩容的，优点是减少这一次无效的扩容，原因就是如果这次插入没有发生Hash冲突的话，那么其实就不会造成扩容
        4.6 线程不安全的体现？如何变成线程安全
             无论在 JDK7 还是 JDK8 的版本中，HashMap 都是线程不安全的，HashMap 的线程不安全主要体现在以下两个方面：
             在JDK7及以前的版本，表现为在多线程环境下进行扩容，由于采用头插法，位于同一索引位置的节点顺序会反掉，导致可能出现死循环的情况
             在JDK8及以后的版本，表现为在多线程环境下添加元素，可能会出现数据丢失的情况

             如果想使用线程安全的 Map 容器，可以使用以下几种方式：
                 （1）使用线程安全的 Hashtable，它底层的每个方法都使用了 synchronized 保证线程同步，所以每次都锁住整张表，在性能方面会相对比较低。
                 （2）使用Collections.synchronizedMap()方法来获取一个线程安全的集合，底层原理是使用synchronized来保证线程同步。
                 （3）使用 ConcurrentHashMap 集合。
        4.7 Hashtable 和 HashMap 的区别:
             (1)继承的父类：两者都实现了 Map 接口，但 HashMap 继承自 AbstractMap 类，而 Hashtable 继承自 Dictionary 类
             (2)遍历方式：HashMap 仅支持 Iterator 的遍历方式，但 Hashtable 实现了 Enumeration 接口，所以支持Iterator和Enumeration两种遍历方式
             (3)使用方式：HashMap 允许 null 键和 null 值，Hashtable 不允许 null  键和 null 值
             (4)数据结构：HashMap 底层使用“数组+链表+红黑树”，Hashtable 底层使用“数组+链表”
             (5)初始容量及扩容方式：HashMap 的默认初始容量为16，每次扩容为原来的2倍；Hashtable 默认初始容量为11，每次扩容为原来的2倍+1。
             (6)元素的hash值：HashMap的hash值是重新计算过的，Hashtable直接使用Object的hashCode；

            之所以会出现初始容量以及元素hash值计算方式的不同，是因为 HashMap 和 Hashtable 设计时的侧重点不同。Hashtable 的侧重点是哈希结果更加均匀，使得哈希冲突减少，当哈希表的大小为素数时，简单的取模哈希的结果会更加均匀。
             而 HashMap 则更加关注哈希的计算效率问题，在取模计算时，如果模数是2的幂，那么我们可以直接使用位运算来得到结果，效率要大大高于做除法。

     5.ConcurrentHashMap 原理（JDK7)
         5.1 ConcurrentHashMap实现的原理:
             见图ConcurrentHashMap数据结构图.jpg
             在 JDK7 中，ConcurrentHashMap 使用“分段锁”机制实现线程安全，数据结构可以看成是"Segment数组+HashEntry数组+链表"，一个 ConcurrentHashMap 实例中包含若干个 Segment 实例组成的数组，每个 Segment 实例又包含由若干个桶，每个桶中都是由若干个 HashEntry 对象链接起来的链表。
             因为Segment 类继承 ReentrantLock 类，所以能充当锁的角色，通过 segment 段将 ConcurrentHashMap 划分为不同的部分，就可以使用不同的锁来控制对哈希表不同部分的修改，从而允许多个写操作并发进行，默认支持 16 个线程执行并发写操作，及任意数量线程的读操作。
         5.2 ConcurrentHashMap 的 put() 方法添加元素的过程：
             （1）对 key 值进行重哈希，并使用重哈希的结果与 segmentFor() 方法， 计算出元素具体分到哪个 segment 中。插入元素前，先使用 lock() 对该 segment 加锁，之后再使用头插法插入元素。如果其他线程经过计算也是放在这个 segment 下，则需要先获取锁，如果计算得出放在其他的 segment，
                 则正常执行，不会影响效率，以此实现线程安全，这样就能够保证只要多个修改操作不发生在同一个 segment  时，它们就可以并发进行。
             （2）在将元素插入到 segment 前，会检查本次插入会不会导致 segment 中元素的数量超过阈值，如果会，那么就先对 segment 进行扩容和重哈希操作，然后再进行插入。而重哈希操作，实际上是对 ConcurrentHashMap 的某个 segment 的重哈希，因此 ConcurrentHashMap 的每个 segment 段
                 所包含的桶位也就不尽相同。
            如果元素的 hash 值与原数组长度进行位与运算，得到的结果为0，那么元素在新桶的序号就是和原桶的序号是相等的；否则元素在新桶的序号就是原桶的序号加上原数组的长度
         5.3 ConcurrentHashMap 读操作为什么不需要加锁？
             （1）在 HashEntry 类中，key，hash 和 next 域都被声明为 final 的，value 域被 volatile 所修饰，因此 HashEntry 对象几乎是不可变的，通过 HashEntry 对象的不变性来降低读操作对加锁的需求
                    next 域被声明为 final，意味着不能从hash链的中间或尾部添加或删除节点，因为这需要修改 next 引用值，因此所有的节点的修改只能从头部开始。但是对于 remove 操作，需要将要删除节点的前面所有节点整个复制一遍，最后一个节点指向要删除结点的下一个结点。
             （2）用 volatile 变量协调读写线程间的内存可见性；
             （3）若读时发生指令重排序现象（也就是读到 value 域的值为 null 的时候），则加锁重读；
        5.4 ConcurrentHashMap 的跨段操作
             ConcurrentHashMap 的跨段操作：比如 size() 计算集合中元素的总个数。首先为了并发性的考虑，ConcurrentHashMap 并没有使用全局计数器，而是分别在每个 segment 中使用一个 volatile 修饰的计数器count，这样当需要更新计数器时，不用锁定整个 ConcurrentHashMap。
             而 size() 在统计时，是先尝试 RETRIES_BEFORE_LOCK 次（默认是两次）通过不锁住 Segment 的方式来统计各个 Segment 大小，如果统计的过程中，容器的count发生了变化，则再采用对所有 segment 段加锁的方式来统计所有Segment的大小。

     6.ConcurrentHashMap 原理（JDK8）
         6.1 ConcurrentHashMap 的实现原理
             在 JDK8 及以上的版本中，ConcurrentHashMap 的底层数据结构依然采用“数组+链表+红黑树”，但是在实现线程安全性方面，抛弃了 JDK7 版本的 Segment分段锁的概念，而是采用了 synchronized + CAS 算法来保证线程安全。在ConcurrentHashMap中，
             大量使用 Unsafe.compareAndSwapXXX 的方法，这类方法是利用一个CAS算法实现无锁化的修改值操作，可以大大减少使用加锁造成的性能消耗。这个算法的基本思想就是不断比较当前内存中的变量值和你预期变量值是否相等，如果相等，则接受修改的值，
             否则拒绝你的而操作。因为当前线程中的值已经不是最新的值，你的修改很可能会覆盖掉其他线程修改的结果。
         6.2 扩容方法 transfer()
            太复杂了, 想看可以看原文
         6.3 put() 方法的 helpTransfer() 协助扩容：
             太复杂了, 想看可以看原文

     7.序列化与反序列化
        7.1 什么是序列化
             两个服务之间要传输一个数据对象，就需要将对象转换成二进制流，通过网络传输到对方服务，再转换成对象，供服务方法调用。这个编码和解码的过程称之为序列化和反序列化。所以序列化就是把 Java 对象变成二进制形式，本质上就是一个byte[]数组。
             将对象序列化之后，就可以写入磁盘进行保存或者通过网络中输出给远程服务了。反之，反序列化可以从网络或者磁盘中读取的字节数组，反序列化成对象，在程序中使用。
        7.2 序列化优点
             (1)永久性保存对象：将对象转为字节流存储到硬盘上，即使 JVM 停机，字节流还会在硬盘上等待，等待下一次 JVM 启动时，反序列化为原来的对象，并且序列化的二进制序列能够减少存储空间
             (2)方便网络传输：序列化成字节流形式的对象可以方便网络传输（二进制形式），节约网络带宽
             (3)通过序列化可以在进程间传递对象
        7.3 序列化的几种方式：
             7.3.1 Java 原生序列化
                 Java 默认通过 Serializable 接口实现序列化，只要实现了该接口，该类就会自动实现序列化与反序列化，该接口没有任何方法，只起标识作用。Java序列化保留了对象类的元数据（如类、成员变量、继承类信息等），以及对象数据等，兼容性最好，但不支持跨语言，而且性能一般。
                 实现 Serializable 接口的类在每次运行时，编译器会根据类的内部实现，包括类名、接口名、方法和属性等自动生成一个 serialVersionUID，serialVersionUID 主要用于验证对象在反序列化过程中，序列化对象是否加载了与序列化兼容的类，如果是具有相同类名的不同版本号的类，
                 在反序列化中是无法获取对象的，显式地定义 serialVersionUID 有两种用途：
                    在某些场合，希望类的不同版本对序列化兼容，因此需要确保类的不同版本具有相同的 serialVersionUID；
                    在某些场合，不希望类的不同版本对序列化兼容，因此需要确保类的不同版本具有不同的 serialVersionUID；
                 如果源码改变，那么重新编译后的 serialVersionUID 可能会发生变化，因此建议一定要显示定义 serialVersionUID 的属性值。
            7.3.2 Hessian 序列化:
                 Hessian 序列化是一种支持动态类型、跨语言、基于对象传输的网络协议。Java 对象序列化的二进制流可以被其他语言反序列化。 Hessian 协议具有如下特性：
                     自描述序列化类型。不依赖外部描述文件或接口定义， 用一个字节表示常用
                     基础类型，极大缩短二进制流
                     语言无关，支持脚本语言
                     协议简单，比 Java 原生序列化高效
                 Hessian 2.0 中序列化二进制流大小是 Java 序列化的 50%，序列化耗时是 Java 序列化的 30%，反序列化耗时是 Java 反序列化的20% 。
                 Hessian 会把复杂对象所有属性存储在一个 Map 中进行序列化。所以在父类、子类存在同名成员变量的情况下， Hessian 序列化时，先序列化子类 ，然后序列化父类，因此反序列化结果会导致子类同名成员变量被父类的值覆盖。
            7.3.3 Json 序列化
                 JSON 是一种轻量级的数据交换格式。JSON 序列化就是将数据对象转换为 JSON 字符串，在序列化过程中抛弃了类型信息，所以反序列化时需要提供类型信息才能准确地反序列化，相比前两种方式，JSON 可读性比较好，方便调试。
        7.4 为什么不建议使用Java序列化
             目前主流框架很少使用到 Java 序列化，比如 SpringCloud 使用的 Json 序列化，Dubbo 虽然兼容 Java 序列化，但默认使用的是 Hessian 序列化。这是为什么呢？主要是因为 JDK 默认的序列化方式存在以下一些缺陷：无法跨语言、
             易被攻击、序列化的流太大、序列化性能太差等
            7.4.1 无法跨语言
                Java 序列化只支持 Java 语言实现的框架，其它语言大部分都没有使用 Java 的序列化框架，也没有实现 Java 序列化这套协议，因此，两个不同语言编写的应用程序之间通信，无法使用 Java 序列化实现应用服务间传输对象的序列化和反序列化。
            7.4.2 易被攻击
                 对象是通过在 ObjectInputStream 上调用 readObject() 方法进行反序列化的，它可以将类路径上几乎所有实现了 Serializable 接口的对象都实例化。这意味着，在反序列化字节流的过程中，该方法可以执行任意类型的代码，这是非常危险的。
                 对于需要长时间进行反序列化的对象，不需要执行任何代码，也可以发起一次攻击。攻击者可以创建循环对象链，然后将序列化后的对象传输到程序中反序列化，这种情况会导致 hashCode 方法被调用次数呈次方爆发式增长, 从而引发栈溢出异常。
                 序列化通常会通过网络传输对象，而对象中往往有敏感数据，所以序列化常常成为黑客的攻击点，攻击者巧妙地利用反序列化过程构造恶意代码，使得程序在反序列化的过程中执行任意代码。 Java 工程中广泛使用的 Apache Commons Collections、Jackson、fastjson 等都出现过反序列化漏洞。
                 如何防范这种黑客攻击呢？有些对象的敏感属性不需要进行序列化传输，可以加 transient 关键字，避免把此属性信息转化为序列化的二进制流，除此之外，声明为 static 类型的成员变量也不能要序列化。如果一定要传递对象的敏感属性，可以使用对称与非对称加密方式独立传输，再使用某个
                 方法把属性还原到对象中。
            7.4.3 序列化后的流太大
                 序列化后的二进制流大小能体现序列化的性能。序列化后的二进制数组越大，占用的存储空间就越多，存储硬件的成本就越高。如果我们是进行网络传输，则占用的带宽就更多，这时就会影响到系统的吞吐量。
            7.4.4 序列化性能太差
                序列化的速度也是体现序列化性能的重要指标，如果序列化的速度慢，网络通信效率就低，从而增加系统的响应时间

     8.Atomic原子类与CAS原理
         8.1 Atomic 原子类的原理
             Atomic 原子操作类是基于无锁 CAS + volatile 实现的，并且类中的所有方法都使用 final 修饰，进一步保证线程安全。而 CAS 算法的具体实现方式在于 Unsafe 类中，Unsafe 类的所有方法都是 native 修饰的，也就是说所有方法都是直接调用操作系统底层资源进行执行相应任务。
             Atomic 使用乐观策略，每次操作时都假设没有冲突发生，并采用 volatile 配合 CAS 去修改内存中的变量，如果失败则重试，直到成功为止。
             乐观锁：乐观锁认为竞争不总是发生，因此每次操作共享资源时都不需要加锁，并将“比较-替换”这两个动作作为一个原子操作去修改内存中的变量，一旦发生冲突就重试，直到成功为止。无锁策略就是一种乐观策略，采用 volatile + CAS 来保证线程执行的安全性。
             悲观锁：悲观锁认为每次访问共享资源总会发生冲突，因此每次对共享资源进行操作时，都会去事先申请一个独占的锁，比如 synchronized 和 ReentronLock 都是独占锁。
         8.2 什么是 CAS：Compare And Swap
            8.2.1 CAS 的算法核心思想：
                 执行函数：CAS(V,E,U)，其包含3个参数：内存值V，旧预期值E，要修改的值U。
                     (1)当且仅当 预期值E 和 内存值V 相同时，才会将内存值修改为U并返回true；
                     (2)若V值和E值不同，则说明已经有其他线程做了更新，则当前线程不执行更新操作，但可以选择重新读取该变量再尝试再次修改该变量，也可以放弃操作。
                 CAS 一定要 volatile 变量配合，这样才能保证每次拿到的变量是主内存中最新的那个值，否则旧的预期值E对某条线程来说，永远是一个不会变的值E，只要某次CAS操作失败，永远都不可能成功。由于 CAS 无锁操作中没有锁的存在，因此不可能出现死锁的情况，也就是天生免疫死锁。
            8.2.2 CPU 指令对 CAS 的支持
                 由于 CAS 的步骤很多，那会不会存在一种情况：假设某个线程在判断 V 和 E 相同后，正要赋值时，切换了线程，更改了值，从而造成了数据不一致呢？答案是否定的，因为 CAS 是一种系统原语，原语属于操作系统用语范畴，是由若干条指令组成的，用于完成某个功能的一个过程，
                 并且原语的执行必须是连续的，在执行过程中不允许被中断，也就是说CAS是一条CPU的原子指令，不会造成所谓的数据不一致问题。
            8.2.3 CAS 的 ABA 问题及其解决方案：
                 假设这样一种场景，当第一个线程执行 CAS(V,E,U) 操作，在获取到当前变量V，准备修改为新值U前，另外两个线程已连续修改了两次变量V的值，使得该值又恢复为旧值，这样的话，我们就无法正确判断这个变量是否已被修改过。
                 解决方法：使用带版本的标志或者时间戳解决ABA问题，在更新数据时，只有要更新的数据和版本标识符合期望值，才允许替换。
         8.3 Unsafe 类
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
         8.4 原子操作类 Atomic
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
     9.ThreadLocal 原理总结
         9.1 什么是 ThreadLocal
             ThreadLocal 提供了线程内部的局部变量，当在多线程环境中使用 ThreadLocal 维护变量时，会为每个线程生成该变量的副本，每个线程只操作自己线程中的变量副本，不同线程间的数据相互隔离、互不影响，从而保证了线程的安全。
             ThreadLocal 适用于无状态，副本变量独立后不影响业务逻辑的高并发场景，如果业务逻辑强依赖于变量副本，则不适合用 ThreadLocal 解决，需要另寻解决方案。
         9.2 ThreadLocal 的数据结构
             在 JDK8 中，每个线程 Thread 内部都维护了一个 ThreadLocalMap 的数据结构，ThreadLocalMap 中有一个由内部类 Entry 组成的 table 数组，Entry 的 key 就是线程的本地化对象 ThreadLocal，而 value 则存放了当前线程所操作
             的变量副本。每个 ThreadLocal 只能保存一个副本 value，并且各个线程的数据互不干扰，如果想要一个线程保存多个副本变量，就需要创建多个ThreadLocal。
         9.3 ThreadLocal 的核心方法
             ThreadLocal 对外暴露的方法有4个：
                 initialValue()方法：返回为当前线程初始副本变量值。
                 get()方法：获取当前线程的副本变量值。
                 set()方法：保存当前线程的副本变量值。
                 remove()方法：移除当前前程的副本变量值
         9.4 ThreadLocal 的哈希冲突的解决方法：线性探测
             和 HashMap 不同，ThreadLocalMap 结构中没有 next 引用，也就是说 ThreadLocalMap 中解决哈希冲突的方式并非链表的方式，而是采用线性探测的方式，当发生哈希冲突时就将步长加1或减1，寻找下一个相邻的位置。
         9.5 ThreadLocal 的内存泄露
             在使用 ThreadLocal 时，当使用完变量后，必须手动调用 remove() 方法删除 entry 对象，否则会造成 value 的内存泄露，严格来说，ThreadLocal 是没有内存泄漏问题，有的话，那也是忘记执行 remove() 引起的，这是使用不规范导致的。
             不过有些人认为 ThreadLocal 的内存泄漏是跟 Entry 中使用弱引用 key 有关，这个结论是不对的。ThreadLocal 造成内存泄露的根本原因并不是 key 使用弱引用，因为即使 key 使用强引用，也会造成 Entry 对象的内存泄露，内存泄露的根
             本原因在于 ThreadLocalMap 的生命周期与当前线程 CurrentThread 的生命周期相同，且 ThreadLocal 使用完没有进行手动删除导致的。下面我们就针对两种情况进行分析：
            9.5.1 如果 key 使用强引用
                 如果在业务代码中使用完 ThreadLocal，则此时 Stack 中的 ThreadLocalRef 就会被回收了。
                 但是此时 ThreadLocalMap 中的 Entry 中的 Key 是强引用 ThreadLocal 的，会造成 ThreadLocal 实例无法回收。
                 如果我们没有删除 Entry 并且 CurrentThread 依然运行的情况下，强引用链如下图红色，会导致Entry内存泄漏。
                 所以结论就是：强引用无法避免内存泄漏。
            9.5.2 如果 key 使用弱引用
                 如果在业务代码中使用完 ThreadLocal，则此时 Stack 中的 ThreadLocalRef 就会被回收了。
                 但是此时 ThreadLocalMap 中的 Entry 中的 Key 是弱引用 ThreadLocal 的，会造成 ThreadLocal 实例被回收，此时 Entry 中的 key = null。
                 但是当我们没有手动删除 Entry 以及 CurrentThread 依然运行的时候，还是存在强引用链，但因为 ThreadLocalRef 已经被回收了，那么此时的 value 就无法访问到了，导致value内存泄漏
                 所以结论就是：弱引用也无法避免内存泄漏
            9.5.3 内存泄露的原因：
                 从上面的分析知道内存泄漏跟强弱引用无关，内存泄漏的前提有两个：
                     (1)ThreadLocalRef 用完后 Entry 没有手动删除。
                     (2)ThreadLocalRef 用完后 CurrentThread 依然在运行。
                 第一点表明当我们在使用完 ThreadLocal 后，调用其对应的 remove() 方法删除对应的 Entry 就可以避免内存泄漏
                 第二点是由于 ThreadLocalMap 是 CurrentThread 的一个属性，被当前线程引用，生命周期跟 CurrentThread 一样，如果当前线程结束 ThreadLocalMap 被回收，自然里面的 Entry 也被回收了，但问题是此时的线程不一定会被回收，比如线程是从线程池中获取的，用完后就放回池子里了
                 所以，我们可以得出在这小节开头的结论：ThreadLocal 内存泄漏根源是 ThreadLocalMap 的生命周期跟 Thread 一样，如果用完 ThreadLocal 没有手动删除就会内存泄漏。
            9.5.4 为什么使用弱引用：
                 前面讲到 ThreadLocal 的内存泄露与强弱引用无关，那么为什么还要用弱引用呢？
                 （1）Entry 中的 key（Threadlocal）是弱引用，目的是将 ThreadLocal 对象的生命周期跟线程周期解绑，用 WeakReference 弱引用关联的对象，只能生存到下一次垃圾回收之前，GC发生时，不管内存够不够，都会被回收。
                 （2）当我们使用完 ThreadLocal，而 Thread 仍然运行时，即使忘记调用 remove() 方法， 弱引用也会比强引用多一层保障：当 GC 发生时，弱引用的 ThreadLocal 被收回，那么 key 就为 null 了。而 ThreadLocalMap 中的 set()、get() 方法，
                     会针对 key == null (也就是 ThreadLocal 为 null) 的情况进行处理，如果 key == null，则系统认为 value 也应该是无效了应该设置为 null，也就是说对应的 value 会在下次调用 ThreadLocal 的 set()、get() 方法时，执行底层 ThreadLocalMap
                     中的 expungeStaleEntry() 方法进行清除无用的 value，从而避免内存泄露。
         9.6 ThreadLocal 的应用场景：
             （1）Hibernate 的 session 获取：每个线程访问数据库都应当是一个独立的 session 会话，如果多个线程共享同一个 session 会话，有可能其他线程关闭连接了，当前线程再执行提交时就会出现会话已关闭的异常，导致系统异常。使用 ThreadLocal 的方式能避免线程争抢session，提高并发安全性。
             （2）Spring 的事务管理：事务需要保证一组操作同时成功或失败，意味着一个事务的所有操作需要在同一个数据库连接上，Spring 采用 Threadlocal 的方式，来保证单个线程中的数据库操作使用的是同一个数据库连接，同时采用这种方式可以使业务层使用事务时不需要感知并管理 connection 对象，
                 通过传播级别，巧妙地管理多个事务配置之间的切换，挂起和恢复。
         9.7 如果想共享线程的 ThreadLocal 数据怎么办 ？
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
         9.8 为什么一般用 ThreadLocal 都要用 static?
            ThreadLocal 能实现线程的数据隔离，不在于它自己本身，而在于 Thread 的 ThreadLocalMap，所以，ThreadLocal 可以只实例化一次，只分配一块存储空间就可以了，没有必要作为成员变量多次被初始化。

    10.Reactor 网络模型 https://blog.csdn.net/a745233700/article/details/122660246
        10.1 什么是 Reactor 模型?
            Reactor 模式也叫做反应器设计模式，是一种为处理服务请求并发提交到一个或者多个服务处理器的事件设计模式。当请求抵达后，通过服务处理器将这些请求采用多路分离的方式分发给相应的请求处理器。
            Reactor 模式主要由 Reactor 和处理器 Handler 这两个核心部分组成，如下图所示，它俩负责的事情如下：
                 Reactor：负责监听和分发事件，事件类型包含连接事件、读写事件；
                 Handler ：负责处理事件，如 read -> 业务逻辑 （decode + compute + encode）-> send；
            在绝大多数场景下，处理一个网络请求有如下几个步骤：
                 (1)read：从 socket 读取数据。
                 (2)decode：解码，网络上的数据都是以 byte 的形式进行传输的，要想获取真正的请求，必需解码
                 (3)compute：计算，也就是业务处理。
                 (4)encode：编码，网络上的数据都是以 byte 的形式进行传输的，也就是 socket 只接收 byte，所以必需编码。
                 (5)send：发送应答数据
            对于Reactor模式来说，每当有一个 Event 输入到 Server 端时，Service Handler 会将其转发（dispatch）相对应的 Handler 进行处理。Reactor 模型中定义的三种角色：
                 Reactor：派发器，负责监听和分配事件，并将事件分派给对应的 Handler。新的事件包含连接建立就绪、读就绪、写就绪等。
                 Acceptor：请求连接器，处理客户端新连接。Reactor 接收到 client 端的连接事件后，会将其转发给 Acceptor，由 Acceptor 接收 Client 的连接，创建对应的 Handler，并向 Reactor 注册此 Handler。
                 Handler：请求处理器，负责事件的处理，将自身与事件绑定，执行非阻塞读/写任务，完成 channel 的读入，完成处理业务逻辑后，负责将结果写出 channel。可用资源池来管理。
        10.2 Reactor 模型的分类
            Reactor 模型中的 Reactor 可以是单个也可以是多个，Handler 同样可以是单线程也可以是多线程，所以组合的模式大致有如下三种：
                (1)单 Reactor 单线程模型
                (2)单 Reactor 多线程模型
                (3)主从 Reactor 单线程模型
                (4)主从 Reactor 多线程模型
            其中第三种的主从Reactor单线程模型没什么实际意义，所以下文就着重介绍其他三种模型

            10.2.1 单 Reactor 单线程模型
                详见单 Reactor 单线程模型.png
                处理流程:
                     （1）Reactor 线程通过 select 监听事件，收到事件后通过 Dispatch 进行分发
                     （2）如果是连接建立事件，则将事件分发给 Acceptor，Acceptor 会通过 accept() 方法获取连接，并创建一个Handler 对象来处理后续的响应事件
                     （3）如果是IO读写事件，则 Reactor 会将该事件交由当前连接的 Handler 来处理
                     （4）Handler 会完成 read -> 业务处理 -> send 的完整业务流程
                优缺点:
                    单 Reactor 单线程模型的优点在于将所有处理逻辑放在一个线程中实现，没有多线程、进程通信、竞争的问题。但该模型在性能与可靠性方面存在比较严重的问题：
                        (1)性能：只在代码上进行组件的区分，整体操作还是单线程，无法充分利用 CPU 资源，并且 Handler 业务处理部分没有异步，一个 Reactor 既要负责处理连接请求，又要负责处理读写请求，一般来
                            说处理连接请求是很快的，但处理读写请求时涉及到业务逻辑处理，相对慢很多。所以 Reactor 在处理读写请求时，其他请求只能等着，容易造成系统的性能瓶颈
                        (2)可靠性：一旦 Reactor 线程意外中断或者进入死循环，会导致整个系统通信模块不可用，不能接收和处理外部消息，造成节点故障
                         所以该单Reactor单进程模型不适用于计算密集型的场景，只适用于业务处理非常快速的场景。Redis的线程模型就是基于单 Reactor 单线程模型实现的，因为 Redis 业务处理主要是在内存中完成，
                        操作的速度是很快的，性能瓶颈不在 CPU 上，所以 Redis 对于命令的处理是单进程的。
            10.2.2 单 Reactor 多线程模型
                详见单 Reactor 多线程模型.png
                为了解决单Reactor单线程模型存在的性能问题，就演进出了单 Reactor 多线程模型，该模型在事件处理器部分采用了多线程（线程池）
                处理流程:
                     （1）Reactor 线程通过 select 监听事件，收到事件后通过 Dispatch 进行分发
                     （2）如果是连接建立事件，则将事件分发给 Acceptor，Acceptor 会通过 accept() 方法获取连接，并创建一个Handler 对象来处理后续的响应事件
                     （3）如果是IO读写事件，则 Reactor 会将该事件交由当前连接对应的 Handler 来处理
                     （4）与单Reactor单线程不同的是，Handler 不再做具体业务处理，只负责接收和响应事件，通过 read 接收数据后，将数据发送给后面的 Worker 线程池进行业务处理。
                     （5）Worker 线程池再分配线程进行业务处理，完成后将响应结果发给 Handler 进行处理。
                     （6）Handler 收到响应结果后通过 send 将响应结果返回给 Client。
                优缺点:
                     相对于第一种模型来说，在处理业务逻辑，也就是获取到 IO读写事件之后，交由线程池来处理，Handler 收到响应后通过 send 将响应结果返回给客户端。这样可以降低 Reactor
                    的性能开销，从而更专注的做事件分发工作了，提升整个应用的吞吐，并且 Handler 使用了多线程模式，可以充分利用 CPU 的性能。但是这个模型存在的问题：
                         （1）Handler 使用多线程模式，自然带来了多线程竞争资源的开销，同时涉及共享数据的互斥和保护机制，实现比较复杂
                         （2）单个 Reactor 承担所有事件的监听、分发和响应，对于高并发场景，容易造成性能瓶颈。
            10.2.3 主从 Reactor 多线程模型
                详见主从 Reactor 多线程模型.png
                单Reactor多线程模型解决了 Handler 单线程的性能问题，但是 Reactor 还是单线程的，对于高并发场景还是会有性能瓶颈，所以需要将 Reactor 调整为多线程模式，也就是接下来要介绍的主从 Reactor 多线程模型。主从 Reactor 多线程模型将 Reactor 分成两部分：
                     （1）MainReactor：只负责处理连接建立事件，通过 select 监听 server socket，将建立的 socketChannel 指定注册给 subReactor，通常一个线程就可以了
                     （2）SubReactor：负责读写事件，维护自己的 selector，基于 MainReactor 注册的 SocketChannel 进行多路分离 IO 读写事件，读写网络数据，并将业务处理交由 worker 线程池来完成。SubReactor 的个数一般和 CPU 个数相同
                处理流程:
                     （1）主线程中的 MainReactor 对象通过 select 监听事件，接收到事件后通过 Dispatch 进行分发，如果事件类型为连接建立事件则分发给 Acceptor 进行连接建立
                         连接建立：
                             a.从主线程池中随机选择一个 Reactor 线程作为 Acceptor 线程，用于绑定监听端口，接收客户端连接
                             b.Acceptor 线程接收客户端连接请求之后创建新的 SocketChannel，将其注册到主线程池的其它 Reactor 线程上，由其负责接入认证、IP黑白名单过滤、握手等操作。
                             c.步骤 b完成之后，业务层的链路正式建立，将 SocketChannel 从主线程池的 Reactor 线程的多路复用器上摘除，重新注册到 SubReactor 线程池的线程上，并创建一个 Handler 用于处理各种连接事件
                     （2）如果接收到的不是连接建立事件，则分发给 SubReactor，SubReactor 调用当前连接对应的 Handler 进行处理
                     （3）Handler 通过 read 读取数据后，将数据分发给 Worker 线程池进行业务处理，Worker 线程池则分配线程进行业务处理，完成后将响应结果发给 Handler
                     （4）Handler 收到响应结果后通过 send 将响应结果返回给 Client
                优缺点:
                     主从 Reactor 多线程模型的优点在于主线程和子线程分工明确，主线程只负责接收新连接，子线程负责完成后续的业务处理，同时主线程和子线程的交互也很简单，子线程接收主线程的连接后，只管业务处理即可，无须关注主线程，可以直接在子线程将处理结果发送给客户端。
                     该 Reactor 模型适用于高并发场景，并且 Netty 网络通信框架也是采用这种实现
        10.3 Reactor 优缺点
            （1）响应快，不必为单个同步时间所阻塞，虽然 Reactor 本身依然是同步的；
            （2）可以最大程度的避免复杂的多线程及同步问题，并且避免了多线程/进程的切换开销
            （3）可扩展性，可以方便地通过增加 Reactor 实例个数来充分利用 CPU 资源；
            （4）可复用性，Reactor 模型本身与具体事件处理逻辑无关，具有很高的复用性。

    11.Proactor 网络模型 https://blog.csdn.net/a745233700/article/details/122390285
        前面我们介绍了 Reactor 网络模型（文章地址：https://blog.csdn.net/a745233700/article/details/122660246），知道了 Reactor 是非阻塞同步网络模型，而 Proactor 是异步网络模型。
        11.1 阻塞型IO和非阻塞型IO
            (1)对于阻塞IO
                当用户程序执行 read，线程会被阻塞，一直等内核数据准备好，并把数据从内核缓冲区拷贝到应用程序的缓冲区中，当拷贝过程完成，read 才会返回
                详见阻塞IO.png
                阻塞等待的是「内核数据准备好」和「数据从内核态拷贝到用户态」这两个过程
            (2)非阻塞IO
                非阻塞的read请求在数据未准备好的情况下立即返回，可以继续往下执行，此时应用程序不断轮询内核，直到数据准备好，内核将数据拷贝到应用程序缓冲区，read 调用就可以获取到结果
                详见非阻塞IO.png
                这里最后一次 read 调用，获取数据的过程，是一个同步的过程，是需要等待的过程。这里的同步指的是内核态的数据拷贝到用户程序的缓存区这个过程。
            因此，无论 read 和 send 是阻塞 I/O，还是非阻塞 I/O 都是同步调用。因为在 read 调用时，内核将数据从内核空间拷贝到用户空间的过程都是需要等待的，也就是说这个过程是同步的，如果内核实现的拷贝效率不高，read 调用就会在这个同步过程中等待比较长的时间。
        11.2 异步IO
            指的是「内核数据准备好」和「数据从内核态拷贝到用户态」这两个过程都不用等待。当我们发起 aio_read （异步 I/O） 之后，就立即返回，内核自动将数据从内核空间拷贝到用户空间，这个拷贝过程同样是异步的，内核自动完成的，
            和前面的同步操作不一样，应用程序并不需要主动发起拷贝动作
            详见异步IO.png
        11.3 Reactor和Proactor的区别
            Proactor 正是使用了异步 I/O 技术，所以被称为异步网络模型。现在我们再来理解 Reactor 和 Proactor 的区别，就比较清晰了
                (1)Reactor 是同步非阻塞网络模型，感知的是就绪可读写事件。在每次感知到有事件发生（比如可读就绪事件）后，就需要应用进程主动调用 read 方法来完成数据的读取，也就是要应用进程主动将 socket 接收缓存中的数据读到应用进程内存中，这个过程是同步的，读取完数据后应用进程才能处理数据。
                (2)Proactor 是异步网络模式， 感知的是已完成的读写事件。在发起异步读写请求时，需要传入数据缓冲区的地址（用来存放结果数据）等信息，这样系统内核才可以自动帮我们把数据的读写工作完成，这里的读写工作全程由操作系统来做，并不需要像 Reactor 那样还需要应用进程主动发起 read/write
                    来读写数据，操作系统完成读写工作后，就会通知应用进程直接处理数据。
        11.4 Proactor 的执行流程
            无论是 Reactor，还是 Proactor，都是一种基于「事件分发」的网络编程模式，区别在于 Reactor 模式是基于「待完成」的 I/O 事件，而 Proactor 模式则是基于「已完成」的 I/O 事件
            详见Proactor模式.png
            介绍一下 Proactor 模式的工作流程：
                 (1)Proactor Initiator 负责创建 Proactor 和 Handler 对象，并将 Proactor 和 Handler 都通过  Asynchronous Operation Processor 注册到内核；
                 (2)Asynchronous Operation Processor 负责处理注册请求，并处理 I/O 操作；
                 (3)Asynchronous Operation Processor 完成 I/O 操作后通知 Proactor；
                 (4)Proactor 根据不同的事件类型回调不同的 Handler 进行业务处理；
                 (5)Handler 完成业务处理；
            需要注意的是：Proactor关注的不是就绪事件，而是完成事件，这是区分Reactor模式的关键点。
        11.5 Proactor 模型处理读取操作的主要流程
            下面就简单介绍下 Proactor 模型处理读取操作的主要流程：
                 （1）应用程序初始化一个异步读取操作，然后注册相应的事件处理器，此时事件处理器不关注读取就绪事件，而是关注读取完成事件，这是区别于Reactor的关键。
                 （2）事件分离器等待读取操作完成事件
                 （3）在事件分离器等待读取操作完成的时候，操作系统调用内核线程完成读取操作，并将读取的内容放入用户传递过来的缓存区中。这也是区别于Reactor的一点，Proactor中，应用程序需要传递缓存区。
                 （4）事件分离器捕获到读取完成事件后，激活应用程序注册的事件处理器，事件处理器直接从缓存区读取数据，而不需要进行实际的读取操作。
             异步IO都是操作系统负责将数据读写到应用传递进来的缓冲区供应用程序操作。
             Proactor中写入操作和读取操作基本一致，只不过监听的事件是写入完成事件而已。
        11.6 Proactor 的缺点
            Proactor 性能确实非常强大，效率也高，但是同样存在以下缺点：
                 （1）内存的使用：缓冲区在读或写操作的时间段内必须保持住，可能造成持续的不确定性，并且每个并发操作都要求有独立的缓存，相比Reactor模型，在Socket已经准备好读或写前，是不要求开辟缓存的；
                 （2）操作系统的支持：Windows 下通过一套完整的支持 socket 的异步编程接口，也就是通过 IOCP 实现了真正的异步，但 Linux 系统下的异步 IO 还不完善，aio 系列函数是由 POSIX 定义的异步操作接口，
                    不是真正的操作系统级别支持的，而是在用户空间模拟出来的异步，并且仅仅支持基于本地文件的 aio 异步操作，网络编程中的 socket 是不支持的。因此，Linux 系统下高并发网络编程都是以 Reactor 模型为主

    12.多线程状态
        详见多线程的六种状态.png
        (1)NEW：初始状态
        (2)RUNNABLE：运行状态（这里只按照JVM层面的标准，不看底层的实现，因为处于JVM运行状态的线程在实际上可能还要等待操作系统分配时间片，但是那些都是实现层，这里只按照JVM标准规范即可）
        (3)BLOCKED：阻塞状态，是线程在等待锁的状态
        (4)WAITING：等待状态，等待状态时线程会让出CPU、但是不会释放锁
        (5)TIMED_WAITING：等待状态，和WAITING总体上是一致的，但是区别在于在等待一定状态之后，会重新恢复到之前的状态
        (6)TERMINATED：被终止状态

    13.Thread 中wait和sleep的区别
         相同点：
         　　(1)wait和sleep方法都可以使线程进入阻塞状态
         　　(2)wait和sleep方法均是可中断方法，被中断后都会受到中断异常
         不同点：
         　　(1)所属类不同：wait是Object的方法，而sleep是Thead特有的方法
         　　(2)关于锁的释放：wait 会释放锁，sleep 睡觉了，抱着锁睡觉，不会释放！
         　　(3)使用位置不同：wait方法的执行必须在同步代码块中进行，而sleep则可以在任何位置　　
         　　(4)sleep方法短暂休眠之后会主动退出阻塞，而wait方法（没有指定等待的时间）则需要被其他线程中断后才能退出阻塞
     */
}




































