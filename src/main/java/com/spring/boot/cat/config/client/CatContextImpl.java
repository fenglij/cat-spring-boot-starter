package com.spring.boot.cat.config.client;

import com.dianping.cat.Cat;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fenglijian
 * @date 2022-10-26 09:53
 */
public class CatContextImpl implements Cat.Context{

    private Map<String, String> properties = new HashMap<>(16);

    @Override
    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }
}

