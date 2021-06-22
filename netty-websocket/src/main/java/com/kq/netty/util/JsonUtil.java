package com.kq.netty.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author kq
 * @date 2021-06-22 17:10
 * @since 2020-0630
 */
public class JsonUtil {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        //忽略字段不匹配错误
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 创建 ObjectNode
     * @return
     */
    public static ObjectNode createJson() {
        return objectMapper.createObjectNode();
    }

    /**
     * 字符串转 java bean
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T string2Bean(String json, Class<T> clazz){
        T t = null;
        try {
            t = objectMapper.readValue(json,clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 字符串转 Map
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Map<String,Object> string2Map(String json, Class<T> clazz){
        Map<String,Object> map = null;
        try {
            map = objectMapper.readValue(json, new TypeReference<Map<String,Object>>() {});
        } catch (Exception e) {
            map = Collections.emptyMap();
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 字符串转 List<Bean>
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> string2BeanList(String json, Class<T> clazz){
        List<T> t = null;
        try {
            t = objectMapper.readValue(json, new TypeReference<List<T>>() {});
        } catch (Exception e) {
            t = Collections.emptyList();
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 字符串转 Bean[]
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T[] string2BeanArray(String json, Class<T> clazz){
        T[] t = null;
        try {
            t = objectMapper.readValue(json, new TypeReference<T[]>() {});
        } catch (Exception e) {
            t = (T[])new Object[0];
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 字符串转 JsonNode
     * @param json
     * @return
     */
    public static JsonNode string2Json(String json) {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonNode;
    }

    /**
     * java bean 或者 Map 或者 JsonNode 转字符串
     * @param o
     * @return
     */
    public static String object2String(Object o) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * java bean 或者 Map 或者 JsonNode 转 JsonNode
     * @param o
     * @return
     */
    public static JsonNode object2Json(Object o) {
        JsonNode jsonNode = null;
        try {
            String jsonString = objectMapper.writeValueAsString(o);
            jsonNode = objectMapper.readTree(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonNode;
    }

    /**
     * jsonNode 转 JsonNode
     * @param jsonNode
     * @return
     */
    public static <T> T json2Bean(JsonNode jsonNode, Class<T> clazz) {
        String json = jsonNode.toString();
        return string2Bean(json, clazz);
    }

//    public static void main(String[] args) {
//        ObjectNode person = objectMapper.createObjectNode();
//        person.put("id","1101");
//        person.put("name","张三");
//        person.put("age",35);
//        person.put("sex","男");
//
//        List<ObjectNode> children = new ArrayList<>(1);
//        ObjectNode child = objectMapper.createObjectNode();
//        child.put("id","1102");
//        child.put("name","张小三");
//        child.put("age",12);
//        child.put("sex","男");
//
//        children.add(child);
//
//        person.putArray("children").addAll(children);
//
//        System.out.println(person.toPrettyString());
//    }



}
