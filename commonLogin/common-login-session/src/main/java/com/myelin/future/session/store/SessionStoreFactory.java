package com.myelin.future.session.store;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by gabriel on 14-8-24.
 */
public class SessionStoreFactory {
    private Map<String, Class<? extends SessionStore>> storeTypeMap;

    public void setStoreTypeMap(Map<String, Class<? extends SessionStore>> storeTypeMap) {
        this.storeTypeMap = storeTypeMap;
    }

    public Map<String, SessionStore> createStoreMap() {
        Map<String, SessionStore> result = new HashMap<String, SessionStore>();
        for (Map.Entry<String, Class<? extends SessionStore>> entry : storeTypeMap.entrySet()) {
            String storeName = entry.getKey();
            Class<? extends SessionStore> storeClass = entry.getValue();
            try {
                SessionStore store = storeClass.newInstance();
                result.put(storeName, store);
            } catch (Exception e) {
                throw new RuntimeException("cannot init object");
            }
        }
        return result;
    }


}
