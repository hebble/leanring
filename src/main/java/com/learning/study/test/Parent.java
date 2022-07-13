package com.learning.study.test;

public class Parent {
    private int i = test();
    private int e = test2();
    private static int j = method();
    private static int k = method2();

    static {
        System.out.println("Parent静态代码块 ");
    }

    {
        System.out.println("Parent构造代码块 ");
    }

    public Parent() {
        System.out.println("Parent构造方法 ");
    }

    public int test() {
        System.out.println("Parent被重写成员变量i ");
        return 1;
    }

    public int test2() {
        System.out.println("Parent未被重写成员变量e ");
        return 1;
    }

    public static int method() {
        System.out.println("Parent被重写静态变量j  ");
        return 1;
    }

    public static int method2() {
        System.out.println("Parent未被重写静态变量e ");
        return 1;
    }
}
