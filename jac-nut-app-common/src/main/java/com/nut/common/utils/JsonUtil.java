package com.nut.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.utils
 * @Author: yzl
 * @CreateTime: 2021-06-15 13:32
 * @Version: 1.0
 */
@Slf4j
public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectMapper mapperExcludeNull = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapperExcludeNull.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapperExcludeNull.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String toJson(Object obj) throws JsonProcessingException {
        return toJson(obj, mapper);
    }

    private static String toJson(Object obj, ObjectMapper objectMapper) throws JsonProcessingException {
        if (obj == null)
            return null;
        return objectMapper.writeValueAsString(obj);
    }

    public static String toJsonIgnoreNull(Object obj) throws JsonProcessingException {
        return toJson(obj, mapperExcludeNull);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || "".equals(json)) {
            return null;
        }
        T t = null;
        try {
            t = mapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        return t;
    }

    public static <T> T fromJson(InputStream is, Class<T> clazz) {
        if (is == null)
            return null;

        T t = null;
        try {
            t = mapper.readValue(is, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return t;
    }

    public static <T> T fromJson(byte[] src, Class<T> clazz) {
        if (src == null)
            return null;

        T t = null;
        try {
            t = mapper.readValue(src, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String json) throws IOException {
        return fromJson(json, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> toList(String json) throws IOException {
        return fromJson(json, List.class);
    }

    public static <T> List<T> toList(String jsonArray, Class<T> clazz) throws IOException {
        return mapper.readValue(jsonArray, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    public static <T> T fromJson(String json, JavaType javaType) throws IOException {
        return mapper.readValue(json, javaType);
    }


    /**
     * 从json字符串反序列化到bean
     */
    public static <T> T fromJson(String jsonString, TypeReference<?> valueTypeRef) {
        try {
            T result = mapper.readValue(jsonString, valueTypeRef);
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Deserialize from JSON failed.", e);
        }
    }
}
