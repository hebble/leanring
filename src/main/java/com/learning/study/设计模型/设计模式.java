package com.learning.study.设计模型;

public class 设计模式 {
    /**
     （1）工厂模式：Spring使用工厂模式，通过BeanFactory和ApplicationContext来创建对象
     （2）单例模式：Bean默认为单例模式
     （3）策略模式：例如Resource的实现类，针对不同的资源文件，实现了不同方式的资源获取策略
     （4）代理模式：Spring的AOP功能用到了JDK的动态代理和CGLIB字节码生成技术
     （5）模板方法：可以将相同部分的代码放在父类中，而将不同的代码放入不同的子类中，用来解决代码重复的问题。比如RestTemplate, JmsTemplate, JpaTemplate
     （6）适配器模式：Spring AOP的增强或通知（Advice）使用到了适配器模式，Spring MVC中也是用到了适配器模式适配Controller(笔记本的适配器))
     （7）观察者模式：Spring事件驱动模型就是观察者模式的一个经典应用。
     （8）桥接模式：可以根据客户的需求能够动态切换不同的数据源。比如我们的项目需要连接多个数据库，客户在每次访问中根据需要会去访问不同的数据库
     (9)责任链模式: (员工请假)
     */
}
