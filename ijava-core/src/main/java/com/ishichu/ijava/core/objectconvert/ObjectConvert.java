package com.ishichu.ijava.core.objectconvert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;

/**
 * Created by shichu.fl on 2018/4/30.
 */
public class ObjectConvert {

    // 使用需要慎重
    public static <T> T event2Obj(Map<String, Serializable> map, Class<T> clazz) throws Exception {
        if (map == null) {
            return null;
        }
        T resultObj = clazz.newInstance();
        Map<String, Field> fieldMap = ColumnMapping.getMapping4Class(clazz).fieldMap;
        for (Map.Entry<String, Serializable> _entry : map.entrySet()) {
            // 此处对属性命名有约束，一般不需要做转换
            Field aField = fieldMap.get(hyphen2Camel(_entry.getKey()));
            if (aField != null) {
                aField.set(resultObj, CastUtil.cast(_entry.getValue(), aField.getType()));
            }
        }

        return resultObj;
    }

    public static String hyphen2Camel(String str) {
        if (str == null) {
            return null;
        }
        str = str.toLowerCase();

        String resultStr = "";
        boolean flag = false;
        for (char aChar : str.toCharArray()) {
            if (aChar == '_') {
                flag = true;
                continue;
            }
            if (flag) {
                aChar = Character.toUpperCase(aChar);
                flag = false;
            }
            resultStr += aChar;
        }
        return resultStr;
    }

    public static Map<String, Serializable> escapeTab(Map<String, Serializable> aRow) {
        if (aRow == null) {
            return null;
        }
        HashMap<String, Serializable> resultMap = new HashMap<String, Serializable>();
        for (Map.Entry<String, Serializable> _entry : aRow.entrySet()) {
            String key = _entry.getKey();
            Serializable value = _entry.getValue();
            if (value != null && value instanceof String) {
                value = ((String) value).replace("\t", "\\t");
            }
            resultMap.put(key, value);
        }
        return resultMap;
    }

    public static class DemoObject {
        private String string;
        private Date date;
        private int intValue;
        private Integer integer;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public Integer getInteger() {
            return integer;
        }

        public void setInteger(Integer integer) {
            this.integer = integer;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }


    @Test
    public void test() {
        Map<String, Serializable> aRow = new HashMap<String, Serializable>();
        aRow.put("string", "string");
        aRow.put("date", new Date());
        aRow.put("intValue", 1);
        aRow.put("integer", 2);

        DemoObject demoObject = null;
        try {
            demoObject = event2Obj(aRow, DemoObject.class);
        } catch (Exception e) {
        }
        System.err.println(demoObject);
        if (demoObject != null) {
            Field[] fields = demoObject.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                    System.err.println(field.get(demoObject));
                } catch (IllegalAccessException e) {
                }
            }
        }
    }

}
