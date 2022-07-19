package com.nut.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: ThreadLocal Cache工具类
 * @author: hcb
 * @createTime: 2021/01/20 10:59
 * @version:1.0
 */
public class ThreadLocalCacheUtils {

    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    /**
     * 向ThreadLocal缓存值
     **/
    public static void setByKey(String key, Object value) {
        Map<String, Object> map = threadLocal.get();
        if (null == map) {
            map = new HashMap<>(8);
        }
        map.put(key, value);
        threadLocal.set(map);
    }

    /**
     * 从ThreadLocal里获取缓存Key的值
     **/
    public static Object getByKey(String key) {
        Map<String, Object> map = threadLocal.get();
        if (null == map) {
            return null;
        } else {
            return map.get(key);
        }
    }

    /**
     * 从ThreadLocal里移除缓存Key
     **/
    public static void removeByKey(String key) {
        Map<String, Object> map = threadLocal.get();
        if (null != map) {
            map.remove(key);
            threadLocal.set(map);
        }
    }

    /**
     * 从ThreadLocal里获取缓存
     **/
    public static Object get() {
        return threadLocal.get();
    }

    /**
     * 移除当前线程缓存
     * 用于释放当前线程ThreadLocal资源
     */
    public static void remove() {
        threadLocal.remove();
    }

}

