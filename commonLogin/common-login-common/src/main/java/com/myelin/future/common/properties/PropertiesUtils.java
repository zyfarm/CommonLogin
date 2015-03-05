/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.myelin.future.common.properties;



import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * Created by gabriel on 14-8-4.
 */
public class PropertiesUtils {

    public static long getPID() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        if (processName != null && processName.length() > 0) {
            try {
                return Long.parseLong(processName.split("@")[0]);
            } catch (Exception e) {
                return 0;
            }
        }

        return 0;
    }


    public static void printFieldsInfos(final Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.get(object) == null) {
                    if (field.getType() == Integer.class) {
                        field.set(object, 0);
                    } else if (field.getType() == String.class) {
                        field.set(object, "-");
                    }
                }
                System.out.println(field.getName() + ":" + field.get(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }


    public static void printMethodsInfos(final Object object) {
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            System.out.println(method.getName());
            try {
                method.invoke(object, null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }


    public static void printStaticFields(Class<? extends Object> cls) {
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {// 如果是静态成员变量
                field.setAccessible(true);
                try {
                    System.setProperty(field.getName(), field.get(null).toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
