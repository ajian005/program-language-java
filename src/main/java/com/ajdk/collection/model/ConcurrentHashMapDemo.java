package com.ajdk.collection.model;

import org.junit.Assert;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapDemo {
    static ConcurrentHashMap<String, String> map  = new ConcurrentHashMap();
    public static void main(String[] args) {
        map.put("key", "value");
        String value = map.get("key");
        System.out.println("value=" + value);
        Assert.assertEquals("value=value", "value", value);

    }
}
