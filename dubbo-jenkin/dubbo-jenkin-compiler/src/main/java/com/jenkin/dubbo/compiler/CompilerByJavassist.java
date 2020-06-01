package com.jenkin.dubbo.compiler;

import javassist.*;

import java.io.File;
import java.io.FileOutputStream;

/**
 * javassist是一款字节码编辑工具，同时也是一个动态类库，它可以直接检查、修改以及创建java类。
 * 以下例子就是创建一个动态类
 */
public class CompilerByJavassist {

    public static void main(String[] args) throws Exception {
        // 1、创建Class的容器
        ClassPool pool = ClassPool.getDefault();
        // 2、通过ClassPool生成一个public的新类Emp.java
        CtClass ctClass = pool.makeClass("com.jenkin.dubbo.compiler.Emp");
        // 3、添加属性 private String ename
        CtField enameField = new CtField(pool.getCtClass("java.lang.String"), "ename", ctClass);
        enameField.setModifiers(Modifier.PRIVATE);
        ctClass.addField(enameField);

        // private int eno;
        CtField enoField = new CtField(pool.getCtClass("int"), "eno", ctClass);
        enameField.setModifiers(Modifier.PRIVATE);
        ctClass.addField(enoField);

        // 4、为属性ename和eno添加getXXX和setXXX方法
        ctClass.addMethod(CtNewMethod.getter("getEname", enameField));
        ctClass.addMethod(CtNewMethod.setter("setEname", enameField));
        ctClass.addMethod(CtNewMethod.getter("getEno", enoField));
        ctClass.addMethod(CtNewMethod.setter("setEno", enoField));

        // 5、添加构造函数
        CtConstructor ctConstructor = new CtConstructor(new CtClass[] {},
                ctClass);
        // 为构造函数设置函数体
        StringBuffer buffer = new StringBuffer();
        buffer.append("{\n").append("ename=\"yy\";\n").append("eno=001;\n}");
        ctConstructor.setBody(buffer.toString());
        // 把构造函数添加到新的类中
        ctClass.addConstructor(ctConstructor);

        // 添加自定义方法
        CtMethod ctMethod = new CtMethod(CtClass.voidType, "printInfo",
                new CtClass[] {}, ctClass);
        // 为自定义方法设置修饰符
        ctMethod.setModifiers(Modifier.PUBLIC);
        // 为自定义方法设置函数体
        StringBuffer buffer2 = new StringBuffer();
        buffer2.append("{\nSystem.out.println(\"begin!\");\n")
                .append("System.out.println(ename);\n")
                .append("System.out.println(eno);\n")
                .append("System.out.println(\"over!\");\n").append("}");
        ctMethod.setBody(buffer2.toString());
        ctClass.addMethod(ctMethod);

        // 测试
        // 生成class
        Class<?> clazz = ctClass.toClass();
        Object obj = clazz.newInstance();
        //反射 执行方法
        obj.getClass().getMethod("printInfo", new Class[] {})
                .invoke(obj, new Object[] {});

        // 把生成的class文件写入文件
        byte[] byteArr = ctClass.toBytecode();
        FileOutputStream fos = new FileOutputStream(new File("G:\\DevCache\\Cache\\dubbo-2.5.4\\Note\\template\\Emp.class"));
        fos.write(byteArr);
        fos.close();
    }
}