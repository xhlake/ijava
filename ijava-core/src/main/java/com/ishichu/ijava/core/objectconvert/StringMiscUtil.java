package com.ishichu.ijava.core.objectconvert;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by shichu.fl on 2018/4/30.
 */
public class StringMiscUtil {
    public static String keyValue(String feature, String key) {
        return keyValue(feature, ";", ":", key);
    }

    /**
     * @param feature  errType:100;aa:bb
     * @param split ;
     * @param split2 :
     * @param key errType
     * @return 100
     */
    public static String keyValue(String feature, String split, String split2, String key) {
        if (feature == null) {
            return null;
        }
        if (StringUtils.isEmpty(split) || StringUtils.isEmpty(split2)) {
            return null;
        }
        String resultStr = null;
        for (String aStr : StringUtils.split(feature, split)) {
            if (aStr.startsWith(key + split2)) {
                resultStr = aStr.substring(key.length() + split2.length());
            }
        }
        return (StringUtils.isEmpty(resultStr)) ? null : resultStr;
    }

    public static Map<String, String> keyValues(String feature, Set<String> keys) {
        return keyValues(feature, ";", ":", keys);
    }

    public static Map<String, String> keyValues(String feature, String split, String split2, Set<String> keys) {
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (CollectionUtils.isEmpty(keys))
            return resultMap;
        for (String aKey : keys) {
            String aValue = keyValue(feature, split, split2, aKey);
            if (StringUtils.isNotEmpty(aValue))
                resultMap.put(aKey, aValue);
        }
        return resultMap;
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

    public static String append(String res, String op, String appendee) {
        if (StringUtils.isEmpty(appendee)) {
            return res;
        } else {
            if (StringUtils.isEmpty(res)) {
                return appendee;
            } else {
                return res + op + appendee;
            }
        }
    }

    public static Integer[] split2Ints(String str) {
        return split2Ints(str, ",");
    }

    public static Integer[] split2Ints(String str, String split) {
        String[] splits = split2Strings(str, split);

        ArrayList<Integer> resultList = new ArrayList<Integer>();
        for (String aSplit : splits) {
            if (StringUtils.isNotEmpty(aSplit) && isNumeric(aSplit)) {
                resultList.add(Integer.parseInt(aSplit));
            }
        }
        return resultList.toArray(new Integer[resultList.size()]);
    }

    public static Long[] split2Longs(String str) {
        return split2Longs(str, ",");
    }

    public static Long[] split2Longs(String str, String split) {
        String[] splits = split2Strings(str, split);

        ArrayList<Long> resultList = new ArrayList<Long>();
        for (String aSplit : splits) {
            Long aLong = parseLong(aSplit);
            if (aLong != null) resultList.add(aLong);
            //			if (StringUtils.isNotEmpty(aSplit) && isNumeric(aSplit)) {
            //				resultList.add(Long.parseLong(aSplit));
            //			}
        }
        return resultList.toArray(new Long[resultList.size()]);
    }

    public static Long parseLong(String str) {
        if (StringUtils.isNotEmpty(str) && isNumeric(str)) {
            return Long.parseLong(str);
        }
        return null;
    }

    public static String[] split2Strings(String str) {
        return split2Strings(str, ",");
    }

    public static String[] split2Strings(String str, String split) {
        if (StringUtils.isEmpty(str)) {
            return new String[0];
        }
        if (StringUtils.isEmpty(split)) {
            split = ",";
        }
        return StringUtils.split(str, split);
    }

    public static boolean isNumeric(String s) {
        return isNumeric(s,10);
    }

    public static boolean isNumeric(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
