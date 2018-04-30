package com.ishichu.ijava.core.objectconvert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by shichu.fl on 2018/4/30.
 */
public class ColumnMapping<T> {
    static volatile Map<Class, ColumnMapping> mappings = new HashMap<Class, ColumnMapping>();

    public Map<String, Field> fieldMap = new HashMap<String, Field>();

    public ColumnMapping(Class clazz) {
        // 1\ collect all fields
        List<Field> allFields = new LinkedList<Field>();
        do {
            Field[] tmpArray = clazz.getDeclaredFields();
            if (tmpArray != null) {
                allFields.addAll(0, Arrays.asList(tmpArray));
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);

        // 2\ store
        for (Field aField : allFields) {
            if (Modifier.isStatic(aField.getModifiers())) {  // ignore statis
                continue;
            }
            aField.setAccessible(true);
            fieldMap.put(aField.getName(), aField);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ColumnMapping<T> getMapping4Class(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        if (mappings.get(clazz) == null) {
            synchronized(ColumnMapping.class) {
                if (mappings.get(clazz) == null) {
                    ColumnMapping columnMapping = new ColumnMapping(clazz);
                    mappings.put(clazz, columnMapping);
                }
            }
        }

        return mappings.get(clazz);
    }
}
