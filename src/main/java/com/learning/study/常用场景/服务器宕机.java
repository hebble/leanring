package com.learning.study.常用场景;

public class 服务器宕机 {
    /**
     1.服务器宕机
        告警系统, 会立即将这个服务自动重启(可以用k8s), 给运维, 开发人员发一份邮件
     2.CPU飙升排查
         (1)使用top命令，查询资源占用情况
            查出CPU使用率过高的java进程id
         (2)使用top -H -p 进程id
            查出该进程下CPU使用率过高的线程id
         (3)​printf "%x\n" PID
            将PID转为十六进制的TID, 我们之所以需要将PID转为十六进制是因为在堆栈信息中，PID是以十六进制形式存在的
         (4)​jstack PID | grep TID -A 100​
            查询堆栈信息
     */
}
