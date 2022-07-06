package com.learning.study.spring;

public class SpringLearning {
    /**
     * 1.谈谈你对Spring的理解？
     *      Spring框架是一个轻量级的开源框架, 平时接触到最多的还是IoC和AOP两个特性。IoC指的是控制反转，把对象的创建和依赖关系的维护交给Spring容器去管理。
     *      Spring通过工厂模式、反射机制等技术管理对象的作用域和生命周期。AoP一般称为面向切面编程，是面向对象的一种补充，将程序中独立于其他功能的方法抽取出来，使Java开发模块化，仅需专注于主业务即可
     *
     * 2.Spring Bean的生命周期
     *      Spring启动，查找并加载需要被Spring管理的bean，进行Bean的实例化
     *      Bean实例化后对将Bean的引入和值注入到Bean的属性中
     *      如果Bean实现了BeanNameAware接口的话，Spring将Bean的Id传递给setBeanName()方法
     *      如果Bean实现了BeanFactoryAware接口的话，Spring将调用setBeanFactory()方法，将BeanFactory容器实例传入
     *      如果Bean实现了ApplicationContextAware接口的话，Spring将调用Bean的setApplicationContext()方法，将bean所在应用上下文引用传入进来。
     *      如果Bean实现了BeanPostProcessor接口，Spring就将调用他们的postProcessBeforeInitialization()方法。
     *      如果Bean 实现了InitializingBean接口，Spring将调用他们的afterPropertiesSet()方法。类似的，如果bean使用init-method声明了初始化方法，该方法也会被调用
     *      如果Bean 实现了BeanPostProcessor接口，Spring就将调用他们的postProcessAfterInitialization()方法。
     *      此时，Bean已经准备就绪，可以被应用程序使用了。他们将一直驻留在应用上下文中，直到应用上下文被销毁。
     *      如果bean实现了DisposableBean接口，Spring将调用它的destory()接口方法，同样，如果bean使用了destory-method 声明销毁方法，该方法也会被调用。
     *
     *      总体的生命周期流程(记):
     *          1.加载spring bean,bean实例化
     *          2.bean的属性赋值
     *          3.BeanNameAware.setBeanName()
     *              4.BeanClassLoaderAware.setBeanClassLoader()
     *          5.BeanFactoryAware.setBeanFactory()
     *              6.EnvironmentAware.setEnvironment()
     *              7.EmbeddedValueResolverAware.setEmbeddedValueResolver()
     *              8.ResourceLoaderAware.setResourceLoader()
     *              9.ApplicationEventPublisherAware.setApplicationEventPublisher()
     *              10.MessageSourceAware.setMessageSource()
     *          11.ApplicationContextAware.setApplicationContext()
     *              12.ServletContextAware.setServletContext()
     *          13.BeanPostProcessor.postProcessBeforeInitialization()
     *          14.InitializingBean.afterPropertiesSet()
     *          15.调用自定义初始化方法
     *          16.BeanPostProcessor.postProcessAfterInitialization()
     *          17.bean可以使用了
     *          18.容器关闭
     *          19.DisposableBean.dispose()
     *          20.自定义销毁方法
     *          21.结束
     *
     */




















}
