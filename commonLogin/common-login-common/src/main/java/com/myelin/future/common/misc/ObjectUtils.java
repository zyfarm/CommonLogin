package com.myelin.future.common.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by gabriel on 14-12-18.
 */
public class ObjectUtils<T1, T2> {
    public T2 transferObject(T1 src, T2 target) {
        Method[] methods = target.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                String fieldName = method.getName().substring(3);
                String prifix = fieldName.substring(0, 1).toLowerCase();
                try {
                    Field[] fields = src.getClass().getDeclaredFields();
                    Field field = src.getClass().getDeclaredField(prifix + fieldName.substring(1));
                    field.setAccessible(true);
                    method.setAccessible(true);
                    method.invoke(target, field.get(src));
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return target;
    }
}
