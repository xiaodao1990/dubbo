package com.jenkin.dubbo.wrapper;

import com.alibaba.dubbo.common.bytecode.NoSuchMethodException;
import com.alibaba.dubbo.common.bytecode.NoSuchPropertyException;
import com.alibaba.dubbo.common.bytecode.Wrapper;

import java.lang.reflect.InvocationTargetException;

public class Wrapper0 extends Wrapper {
    // 字段名列表
    public static String[] pns;

    // 字段名与字段类型的映射关系
    public static java.util.Map<String, Class<?>> pts;

    // 方法名列表
    public static String[] mns;

    // 声明的方法名列表
    public static String[] dmns;

    // 每个public方法的参数类型
    public static Class[] mts0;
    public static Class[] mts1;
    public static Class[] mts2;

    public String[] getPropertyNames() {
        return pns;
    }

    public boolean hasProperty(String n) {
        return pts.containsKey(n);
    }

    public Class getPropertyType(String n) {
        return (Class) pts.get(n);
    }

    public String[] getMethodNames() {
        return mns;
    }

    public String[] getDeclaredMethodNames() {
        return dmns;
    }

    public void setPropertyValue(Object o, String n, Object v) {
        Car w;
        try {
            w = ((Car) o);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        throw new NoSuchPropertyException("Not found property \"" + n + "\" field or setter method in class Car.");
    }

    public Object getPropertyValue(Object o, String n) {
        Car w;
        try {
            w = ((Car) o);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        if (n.equals("brand")) {
            return  w.getBrand();
        }
        if (n.equals("weight")) {
            return  w.getWeight();
        }
        throw new NoSuchPropertyException("Not found property \"" + n + "\" field or setter method in class Car.");
    }

    public Object invokeMethod(Object o, String n, Class[] p, Object[] v) throws NoSuchMethodException, InvocationTargetException {
        Car w;
        try {
            w = ((Car) o);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        try {
            if ("make".equals(n) && p.length == 2) {
                w.make((java.lang.String) v[0], ((Number) v[1]).longValue());
                return null;
            }
            if ("getBrand".equals(n) && p.length == 0) {
                return  w.getBrand();
            }
            if ("getWeight".equals(n) && p.length == 0) {
                return  w.getWeight();
            }
        } catch (Throwable e) {
            throw new java.lang.reflect.InvocationTargetException(e);
        }
        throw new NoSuchMethodException("Not found method \"" + n + "\" in class Car.");
    }
}