package com.ishichu.ijava.core.objectconvert;

import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.apache.commons.lang.ClassUtils.primitiveToWrapper;

/**
 * Created by shichu.fl on 2018/4/30.
 */
public class CastUtil {
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list, Class<T> clazz) {
        if (list == null) {
            return null;
        }

        return list.toArray((T[]) Array.newInstance(clazz, list.size()));
        //		return (T[]) list.toArray();
    }

    public static Object castWithDefault(Object obj, Class tClazz) {
        Object resultObj = cast(obj, tClazz);
        if (resultObj == null) {
            resultObj = defaults.get(tClazz);
        }
        return resultObj;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getDefault(Class<T> clazz) {
        return (T) defaults.get(clazz);
    }

    public static Object cast(Object obj, Class tClazz) {
        if (tClazz.isInstance(obj)) {
            return obj;
        }
        if (obj == null) {
            if (tClazz.isPrimitive()) {
                return getDefaultValueForPrimitive(tClazz);
            }
            return null;
        }
        Class fClazz = obj.getClass();
        if (fClazz == tClazz) {
            return obj;
        }

        // wrapped <-> primitive
        if (fClazz.isPrimitive() && primitiveToWrapper(fClazz) == tClazz) {
            return obj;
        }
        if (tClazz.isPrimitive() && primitiveToWrapper(tClazz) == fClazz) {
            return obj;
        }

        // int <-> long
        if ((fClazz == Integer.TYPE || fClazz == Integer.class) && (tClazz == Long.TYPE || tClazz == Long.class)) {
            return (long) (Integer) obj;
        }
        if ((fClazz == Long.TYPE || fClazz == Long.class) && (tClazz == Integer.TYPE || tClazz == Integer.class)) {
            return ((Long) obj).intValue();
        }

        // byte <-> int
        if ((fClazz == Byte.TYPE || fClazz == Byte.class) && (tClazz == Integer.TYPE || tClazz == Integer.class)) {
            return (int) (Byte) obj;
        }
        if ((fClazz == Integer.TYPE || fClazz == Integer.class) && (tClazz == Byte.TYPE || tClazz == Byte.class)) {
            return ((Integer) obj).byteValue();
        }

        // bigInteger <-> long
        if ((fClazz == BigInteger.class) && (tClazz == Long.TYPE || tClazz == Long.class)) {
            return ((BigInteger) obj).longValue();
        }
        if ((fClazz == Long.TYPE || fClazz == Long.class) && (tClazz == BigInteger.class)) {
            return new BigInteger("" + obj);
        }

        // double <-> big-decimal
        if ((fClazz == Double.TYPE || fClazz == Double.class) && tClazz == BigDecimal.class) {
            return new BigDecimal("" + obj);
        }
        if (fClazz == BigDecimal.class && (tClazz == Double.TYPE || tClazz == Double.class)) {
            return ((BigDecimal) obj).doubleValue();
        }

        // string -> [int, long, Date, Double, Float]
        if (fClazz == String.class && (tClazz == Integer.class || primitiveToWrapper(tClazz) == Integer.class)) {
            if (StringUtils.isEmpty((String) obj)) {
                return getDefault(Integer.class);
            }
            return Integer.parseInt((String) obj);
        }
        if (fClazz == String.class && (tClazz == Long.class || primitiveToWrapper(tClazz) == Long.class)) {
            if (StringUtils.isEmpty((String) obj)) {
                return getDefault(Long.class);
            }
            return Long.parseLong((String) obj);
        }
        if (fClazz == String.class && tClazz == Date.class) {
            if (StringUtils.isEmpty((String) obj)) {
                return null;
            }
            long time = Long.parseLong((String) obj);
            if (time == 0) return null;  // open_search's default value
            return new Date(time);
        }
        if (fClazz == String.class && (tClazz == Double.class || primitiveToWrapper(tClazz) == Double.class)) {
            if (StringUtils.isEmpty((String) obj)) {
                return getDefaultValueForPrimitive(Double.class);
            }
            return Double.parseDouble((String) obj);
        }
        if (fClazz == String.class && (tClazz == Float.class || primitiveToWrapper(tClazz) == Float.class)) {
            if (StringUtils.isEmpty((String) obj)) {
                return getDefaultValueForPrimitive(Float.class);
            }
            return Float.parseFloat((String) obj);
        }

        // date
        if (fClazz == Long.class && tClazz == Date.class) {
            return new Date((Long) obj);
        }
        if (fClazz == Date.class && tClazz == Long.class) {
            return ((Date) obj).getTime();
        }

        // string -> array
        if (fClazz == String.class && tClazz == Integer[].class) {  // int_array field output
            return StringMiscUtil.split2Ints((String) obj, "\t");
        }
        if (fClazz == String.class && tClazz == Long[].class) {  // long_array field output
            return StringMiscUtil.split2Longs((String) obj, "\t");
        }
        if (fClazz == String.class && tClazz == String[].class) {  // string_array field output
            return StringMiscUtil.split2Strings((String) obj, "\t");
        }

        // byte-array <-> string   when longtext comes as byte[]
        if (fClazz == byte[].class && tClazz == String.class) {
            return bytes2string(obj);
        }
        if (fClazz == String.class && tClazz == byte[].class) {
            return string2bytes(obj);
        }

        return null;
        //		throw new RuntimeException("unsupported class casting, " + fClazz.getName() + "," + tClazz.getName());
    }

    public static final String STR_ENCODING = "UTF-8";

    private static byte[] string2bytes(Object obj) {
        try {
            return ((String) obj).getBytes(STR_ENCODING);
        } catch (UnsupportedEncodingException e) {
            // ignored
        }
        return null;
    }

    private static String bytes2string(Object obj) {
        try {
            return new String((byte[]) obj, STR_ENCODING);
        } catch (UnsupportedEncodingException e) {
            // ignored
        }
        return null;
    }

    private static boolean DEFAULT_BOOLEAN;
    private static byte    DEFAULT_BYTE;
    private static short   DEFAULT_SHORT;
    private static int     DEFAULT_INT;
    private static long    DEFAULT_LONG;
    private static float   DEFAULT_FLOAT;
    private static double  DEFAULT_DOUBLE;

    public static Object getDefaultValueForPrimitive(Class clazz) {
        if (clazz.equals(boolean.class)) {
            return DEFAULT_BOOLEAN;
        } else if (clazz.equals(byte.class)) {
            return DEFAULT_BYTE;
        } else if (clazz.equals(short.class)) {
            return DEFAULT_SHORT;
        } else if (clazz.equals(int.class)) {
            return DEFAULT_INT;
        } else if (clazz.equals(long.class)) {
            return DEFAULT_LONG;
        } else if (clazz.equals(float.class)) {
            return DEFAULT_FLOAT;
        } else if (clazz.equals(double.class)) {
            return DEFAULT_DOUBLE;
        } else {
            throw new IllegalArgumentException("Class type " + clazz + " not supported");
        }
    }

    public static <T> T fillDefault(T obj) {
        return fillDefault(obj, null);
    }

    public static <T> T fillDefault(T obj, Set<String> ignoreFields) {
        if (obj == null) {
            return null;
        }
        if (ignoreFields == null) {
            ignoreFields = new HashSet<String>(0);
        }

        try {
            for (Field aField : ColumnMapping.getMapping4Class(obj.getClass()).fieldMap.values()) {
                if (aField.get(obj) == null && !ignoreFields.contains(aField.getName())) {
                    aField.set(obj, getDefault(aField.getType()));
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return obj;
    }

    public static Map<Class, Object> defaults = new HashMap<Class, Object>();
    static {
        defaults.put(Date.class, new Date(0));
        defaults.put(Boolean.class, false);
        defaults.put(Boolean.TYPE, false);
        defaults.put(Byte.class, (byte) -1);
        defaults.put(Byte.TYPE, (byte) -1);
        defaults.put(Integer.class, -1);
        defaults.put(Integer.TYPE, -1);
        defaults.put(Long.class, -1L);
        defaults.put(Long.TYPE, -1L);
        defaults.put(String.class, "NULL");
        defaults.put(String[].class, new String[0]);
        defaults.put(Integer[].class, new Integer[]{-1});
        defaults.put(Long[].class, new Long[]{-1L});
    }
}
