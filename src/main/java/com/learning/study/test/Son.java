package com.learning.study.test;

public class Son extends Parent {
    private int i = test();
    private int b = test22();
    private static int j = method();
    private static int a = method22();

    static {
        System.out.println("Son静态代码块 ");
    }

    {
        System.out.println("Son构造代码块 ");
    }

    public int test() {
        System.out.println("Son子类重写成员变量i ");
        return 2;
    }
    public int test22() {
        System.out.println("Son子类未重写成员变量b ");
        return 2;
    }

    public static int method() {
        System.out.println("Son子类为重写静态变量j ");
        return 2;
    }
    public static int method22() {
        System.out.println("Son子类未为重写静态变量a ");
        return 2;
    }

    public Son() {
        System.out.println("Son构造方法 ");
    }

    public static void main(String[] args) {
        Son son1 = new Son();
        System.out.println();
        System.out.println("--------------------------");
        Son son2 = new Son();
    }
}
