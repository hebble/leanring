package com.learning.study.linux;

public class LinuxLearning {
    /**
     1.Linux 里的 2>&1 究竟是什么? https://blog.csdn.net/liupeifeng3514/article/details/79711694
        我们在Linux下经常会碰到nohup command>/dev/null 2>&1 &这样形式的命令。首先我们把这条命令大概分解下：
             (1)首先就是一个nohup：表示当前用户和系统的会话下的进程忽略响应HUP消息。
             (2)&是把该命令以后台的job的形式运行。
             (3)command>/dev/null较好理解，/dev/null表示一个空设备，就是说把 command 的执行结果重定向到空设备中，说白了就是不显示任何信息。
        可以把/dev/null 可以看作”黑洞”。它等价于一个只写文件。所有写入它的内容都会永远丢失，而尝试从它那儿读取内容则什么也读不到。
        1.1 2>&1的含义
            1.1.1 基本概念
                 /dev/null 表示空设备文件；
                 0 表示stdin标准输入；
                 1 表示stdout标准输出；
                 2 表示stderr标准错误。
            1.1.2 从command>/dev/null说起  => command 1 > /dev/null (只用了标准输出)
                其实这条命令是一个缩写版，对于一个重定向命令，肯定是a > b这种形式，那么command > /dev/null难道是command 充当 a 的角色，/dev/null 充当 b 的角色。这样看起来比较合理，其实一条命令肯定是充当不了 a，肯定是
                command 执行产生的输出来充当 a，其实就是标准输出 stdout。所以command > /dev/null相当于执行了command 1 > /dev/null。执行 command 产生了标准输出 stdout（用1表示），重定向到/dev/null的设备文件中。
            通过上面command > /dev/null等价于command 1 > /dev/null，那么对于2>&1也就好理解了，2就是标准错误，1是标准输出，那么这条命令不就是相当于把标准错误重定向到标准输出么。
        1.2 2>1和2>&1的写法有什么区别：
             2>1的作用是把标准错误的输出重定向到1，但这个1不是标准输出，而是一个文件!!!,文件名就是1；
             2>&1的作用是把标准错误的输出重定向到标准输出1，&指示不要把1当作普通文件，而是fd=1即标准输出来处理。
        1.3 command>a 2>a 与 command>a 2>&1的区别
            通过上面的分析，对于command>a 2>&1这条命令，等价于command 1>a 2>&1可以理解为执行 command 产生的标准输入重定向到文件 a 中，标准错误也重定向到文件 a 中。那么是否就说command 1>a 2>&1等价于command 1>a 2>a呢。
            其实不是，command 1>a 2>&1与command 1>a 2>a还是有区别的，区别就在于前者只打开一次文件a，后者会打开文件两次，并导致 stdout 被 stderr 覆盖。&1的含义就可以理解为用标准输出的引用，引用的就是重定向标准输出产生
            打开的 a。从IO效率上来讲，command 1>a 2>&1比command 1>a 2>a的效率更高。
        1.4 为何2>&1要写在后面？
             index.php task testOne >/dev/null 2>&1
             我们可以理解为，左边是标准输出，好，现在标准输出直接输入到/dev/null中，而2>&1是将标准错误重定向到标准输出，所以当程序产生错误的时候，相当于错误流向左边，而左边依旧是输入到/dev/null中。
             可以理解为，如果写在中间，那会把隔断标准输出指定输出的文件
             你可以用：
                 ls 2>1测试一下，不会报没有2文件的错误，但会输出一个空的文件1；
                 ls xxx 2>1测试，没有xxx这个文件的错误输出到了1中；
                 ls xxx 2>&1测试，不会生成1这个文件了，不过错误跑到标准输出了；
                 ls xxx >out.txt 2>&1，实际上可换成 ls xxx 1>out.txt 2>&1；重定向符号>默认是1，错误和输出都传到out.txt了。
     */
}
