package com.myelin.future.session.store;

import java.util.HashMap;

/**
 * Created by gabriel on 14-8-24.
 */
public enum SessionStoreType {


    COOKIE_STORE("cookie"),
    REDIS_STORE("redis");

    private static HashMap<String, SessionStoreType> container = new HashMap<String, SessionStoreType>();

    static {
        container.put("cookie", COOKIE_STORE);
        container.put("redis", REDIS_STORE);
    }

    public String getStoreType() {
        return storeType;
    }

    private String storeType;

    SessionStoreType(String storeType) {
        this.storeType = storeType;
    }

    public static SessionStoreType getSessionStoreType(String key) {
        return container.get(key);
    }
}
