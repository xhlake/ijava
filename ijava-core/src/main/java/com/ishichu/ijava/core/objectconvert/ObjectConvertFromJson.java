package com.ishichu.ijava.core.objectconvert;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by shichu.fl on 2018/4/30.
 */
public class ObjectConvertFromJson {

    public static <T> List<T> jsonArray2objList(JSONArray jsonArray, Class<T> clazz) {
        if(jsonArray != null && !jsonArray.isEmpty()) {
            List<T> resultList = new ArrayList();

            for(int i = 0; i < jsonArray.size(); ++i) {
                resultList.add(json2obj(jsonArray.getJSONObject(i), clazz));
            }

            return resultList;
        } else {
            return new ArrayList(0);
        }
    }

    public static <T> T json2obj(JSONObject json, Class<T> clazz) {
        if(json == null) {
            return null;
        } else {
            try {
                T resultObj = clazz.newInstance();
                Iterator var3 = ColumnMapping.getMapping4Class(clazz).fieldMap.entrySet().iterator();

                while(var3.hasNext()) {
                    Map.Entry<String, Field> _entry = (Map.Entry)var3.next();
                    String aKey = camel2hyphen((String)_entry.getKey());
                    Field aFiled = (Field)_entry.getValue();
                    if(json.get(aKey) != null) {
                        aFiled.set(resultObj, CastUtil.cast(json.get(aKey), aFiled.getType()));
                    }
                }

                return resultObj;
            } catch (Exception var7) {
                throw new RuntimeException("json2obj fail, json: " + json, var7);
            }
        }
    }

    public static String camel2hyphen(String str) {
        if(str == null) {
            return null;
        } else {
            String resultStr = "";
            char[] var2 = str.toCharArray();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                char aChar = var2[var4];
                if(Character.isUpperCase(aChar)) {
                    resultStr = resultStr + "_" + Character.toLowerCase(aChar);
                } else {
                    resultStr = resultStr + aChar;
                }
            }

            return resultStr;
        }
    }

}
