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
     */
}




































