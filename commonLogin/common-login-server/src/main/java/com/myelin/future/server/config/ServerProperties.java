package com.myelin.future.server.config;

import java.io.IOException;
import java.util.*;

/**
 * Created by gabriel on 14-12-28.
 */
public class ServerProperties {
    public static HashMap<String, String> props = new HashMap<String, String>();


    static {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("server.properties"));
            Set<Map.Entry<Object, Object>> entries = properties.entrySet();
            Iterator<Map.Entry<Object, Object>> iter = entries.iterator();
            while (iter.hasNext()) {
                Map.Entry<Object, Object> item = iter.next();
                props.put(item.getKey().toString(), item.getValue().toString());
            }
        } catch (IOException e) {

        }
    }
}
