package com.nut.driver.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MengJinyue
 * @create 2021/1/29
 * @Describtion gson解析
 */
public class GsonUtils {
    public static String objectToJson(Object object) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(object);
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, clazz);
    }

    public static Map<String, String> str2Map(String str) {
        Map<String, String> result = new HashMap<>();
        String[] results = str.split("&");
        if (results != null && results.length > 0) {
            for (int var = 0; var < results.length; ++var) {
                String pair = results[var];
                String[] kv = pair.split("=", 2);
                if (kv != null && kv.length == 2) {
                    result.put(kv[0], kv[1]);
                }
            }
        }
        return result;
    }
}
