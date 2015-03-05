/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.myelin.future.session.config;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 限流的配置类,主要用于限制并发,请求白名单等
 */
public class LimitAccessConfig {
    public static final Properties EMPTY_PROPERTIES = new Properties();
    public static final String CONFIG_SEPARATORS = ",;";
    public static final String COMPRESS_KEY = "compressKey";

    public static final Map<String, String> DEFAULT_COMBINE_KEY_CONFIG = new HashMap<String, String>();

    static {
        DEFAULT_COMBINE_KEY_CONFIG.put("lifeCycle", "-1");
        DEFAULT_COMBINE_KEY_CONFIG.put("domain", "");
        DEFAULT_COMBINE_KEY_CONFIG.put("cookiePath", "/");
        DEFAULT_COMBINE_KEY_CONFIG.put("httpOnly", "false");
    }
}
