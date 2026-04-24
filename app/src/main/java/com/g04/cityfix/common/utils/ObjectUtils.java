package com.g04.cityfix.common.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectUtils {
    /**
     * Cast an Object to key-value structures
     * @param obj Target object
     * @return A map
     * @author Jerry Yang
     */
    public static Map<String, Object> toMap(Object obj) {
        Map<String, Object> result = new HashMap<>();
        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    result.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                Log.e("Cast to hashmap","Failure");
            }
        }

        return result;
    }
}
