/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.myelin.future.session.common;


import com.myelin.future.common.misc.ParamsCheckUtils;
import com.myelin.future.common.misc.UniqIdUtils;
import com.myelin.future.common.retobj.RetValue;
import com.myelin.future.session.config.*;
import com.myelin.future.session.store.CommonCookieStore;
import com.myelin.future.session.store.CommonRedisStore;
import com.myelin.future.session.store.SessionStore;
import com.myelin.future.session.store.SessionStoreType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.*;

/**
 * Created by gabriel on 14-8-4.
 */
public class CommonSession implements HttpSession {


    public boolean isUseRedisStore() {
        return useRedisStore;
    }


    public void setUseRedisStore(boolean useRedisStore) {
        this.useRedisStore = useRedisStore;
    }


    private volatile boolean storeCookie = true;
    private volatile long creationTime;
    private volatile String sessionId;
    private ServletContext context;
    private volatile boolean useRedisStore = false;
    private volatile String sessionEnv = "daily";

    public String getSessionEnv() {
        return sessionEnv;
    }

    public void setSessionEnv(String sessionEnv) {
        this.sessionEnv = sessionEnv;
    }

    private volatile boolean init = false;
    private volatile boolean expiredTimeChanged = false;

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    public boolean isExpiredTimeChanged() {
        return expiredTimeChanged;
    }

    public void setExpiredTimeChanged(boolean expiredTimeChanged) {
        this.expiredTimeChanged = expiredTimeChanged;
    }

    public int getUpdateTairInterval() {

        return updateTairInterval;
    }


    private static ThreadLocal<CommonSession> localSession = new ThreadLocal<CommonSession>();


    private volatile int updateTairInterval = 900;// 900秒
    private volatile int MAX_INACTIVE_INTERVAL = 1800;
    private volatile long MAX_CACHE_EXPIRETIME = 4500;
    private volatile long MAX_VISITOR_EXPIRETIME = 2700;

    private volatile String SESSION_ID = "sessionId";
    private volatile CommonServletRequest request;
    private volatile CommonServletResponse response;
    private volatile SessionConfig sessionConfig;

    private Map<String, Object> writeCache = new HashMap<String, Object>();
    private Map<String, SessionStore> currentStoreMap = new HashMap<String, SessionStore>();
    private Map<String, SessionStore> storeMap;


    public long getMaxCacheExpiretime() {
        return MAX_CACHE_EXPIRETIME;
    }

    public long getMaxVisitorExpiretime() {
        return MAX_VISITOR_EXPIRETIME;
    }

    private void setSessionStore(Map<String, SessionStore> map) {
        for (SessionStore sessionStore : map.values()) {
            sessionStore.init(this);
        }
    }


    private void fetchInUseStores() {
        Set<String> storeKeys = sessionConfig.getInUseStoreKeys(0);
        for (String storeKey : storeKeys) {
            SessionStore sessionStore = storeMap.get(storeKey);

            if (sessionStore != null) {
                if (sessionStore.getStoreType().getStoreType().equals("redis")) {
                    useRedisStore = true;
                }
                currentStoreMap.put(sessionStore.getClass().toString(), sessionStore);
            }
        }
    }


    public Properties getProperties() {
        Properties properties;
        properties = sessionConfig.getProperties();
        return properties == null ? new Properties() : properties;
    }


    private boolean verifyCookieExist(String cookieName) {
        Properties properties = getProperties();
        ConfigEntry configEntry = getConfigEntry(cookieName);
        SessionStore store = null;
        if (configEntry != null) {
            store = getSessionStore(SessionStoreType.getSessionStoreType(configEntry.getStoreType()));
        }

        Object targetCookieValue = null;
        if (store != null && store instanceof CommonCookieStore) {
            targetCookieValue = store.getAttribute(configEntry, properties);
        }

        if (targetCookieValue != null) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * cookie校验
     */
    private void verifyCookies() {
        String cookie = (String) getAttribute(SESSION_ID);
        /**
         * 如果cookie的值不合法
         */
        if (cookie == null || cookie.length() != 32 || !ParamsCheckUtils.verifyCookies(cookie)) {
            /*清除登录态*/

        }
    }


    private void initLocalSession() {
        if (localSession.get() == null) {
            localSession.set(this);
        } else {
            localSession.remove();
            localSession.set(this);
        }
    }

    public void init() {

        if (this.init) {
            return;
        }

        this.storeCookie = ((request != null) && (response != null));


        /**
         * 是否启用cookie存储
         */
        if (!this.storeCookie) {
            this.storeMap.remove(SessionStoreType.COOKIE_STORE.getStoreType());
        }

        setSessionStore(this.storeMap);

        fetchInUseStores();

        if (CommonSessionConfig.cookieVerifySwitch) {
            verifyCookies();
        }

        generateSessionId();

        initLocalSession();

        this.init = true;


    }

    /**
     * 创建唯一的sessionID
     */
    private String generateSessionIdInner() {
        sessionId = UniqIdUtils.getInstance().getUniqStr();
        return sessionId;
    }

    public void generateSessionId() {
        sessionId = (String) getAttribute(SESSION_ID);
        if (StringUtils.isBlank(sessionId)) {
            generateSessionIdInner();
            if (sessionId != null) {
                setAttribute(SessionKeyConstants.ATTRIBUTE_SESSION_ID, this.sessionId);
            }
        }
    }


    public Map<String, SessionStore> getStoreMap() {
        return storeMap;
    }

    public CommonSession(CommonServletResponse response, CommonServletRequest request, ServletContext context, SessionConfig sessionConfig, Map<String, SessionStore> storeMap) {
        this.response = response;
        this.request = request;
        this.context = context;
        this.sessionConfig = sessionConfig;
        this.storeMap = storeMap;
    }


    public String getSessionId() {
        return sessionId;
    }


    public ServletContext getContext() {
        return context;
    }

    public void setContext(ServletContext context) {
        this.context = context;
    }

    public CommonServletRequest getRequest() {
        return request;
    }

    public void setRequest(CommonServletRequest request) {
        this.request = request;
    }

    public CommonServletResponse getResponse() {
        return response;
    }

    public void setResponse(CommonServletResponse response) {
        this.response = response;
    }

    public SessionConfig getSessionConfig() {
        return sessionConfig;
    }


    @Override
    public long getCreationTime() {
        return this.creationTime;
    }


    @Override
    public String getId() {
        return this.sessionId;
    }

    @Override
    public long getLastAccessedTime() {
        return this.creationTime;
    }

    @Override
    public ServletContext getServletContext() {
        return this.context;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.MAX_INACTIVE_INTERVAL = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.MAX_INACTIVE_INTERVAL;
    }


    private static interface Callback {
        public Object perform(SessionStore store, ConfigEntry configEntry, Properties properties, CommonSession session);

        public boolean isSetAttribute();
    }

    private ConfigEntry getConfigEntry(String name) {
        ConfigEntry configEntry;
        configEntry = sessionConfig.getConfigEntry(name);
        return configEntry;
    }


    private Object acquireOnAttrite(String name, Callback callback) {
        ConfigEntry configEntry = getConfigEntry(name);
        if (configEntry != null) {
            if (!configEntry.isReadOnly()) {
                SessionStore sessionStore = storeMap.get(configEntry.getStoreType());
                if (sessionStore != null) {
                    Properties properties = sessionConfig.getProperties();
                    return callback.perform(sessionStore, configEntry, properties, this);
                }
            }
        } else {
            throw new RuntimeException("没有这个cookie" + name);
        }

        return null;
    }


    protected SessionStore getSessionStore(SessionStoreType sessionStoreType) {
        SessionStore sessionStore;
        sessionStore = this.storeMap.get(sessionStoreType.getStoreType());
        if (sessionStore == null) {
            sessionStore = this.currentStoreMap.get(sessionStoreType.getStoreType());
        }
        return sessionStore;
    }

    private Object performOnAttribute(String name, Callback callback) {
        ConfigEntry entry = getConfigEntry(name);
        if (entry != null) {
            if (!entry.isReadOnly()) {
                SessionStore store = getSessionStore(SessionStoreType.getSessionStoreType(entry.getStoreType()));
                if (store != null) {
                    Properties properties = getProperties();
                    return callback.perform(store, entry, properties, this);
                }
            }
        }

        return null;
    }

    private void clearFromStore(String key) {
        ConfigEntry configEntry = sessionConfig.getConfigEntry(key);
        if (configEntry.getLifeCycle() <= 0 && configEntry.getStoreType() != null) {
            this.writeCache.remove(key);
            setAttribute(key, null);
        }
    }


    public void invalidate() {
        for (String key : sessionConfig.getKeys(0)) {
            clearFromStore(key);
        }


        CommonRedisStore redisStore = (CommonRedisStore) this.getStoreMap().get("redis");
        if (redisStore != null) {
            redisStore.removeAll();
        }
    }

    @Override
    public Object getAttribute(String name) {
        try {
            Object value = ObjectUtils.toString(writeCache.get(name), null);
            if (value != null) {
                return value;
            }

            value = acquireOnAttrite(name, new Callback() {
                @Override
                public Object perform(SessionStore store, ConfigEntry configEntry, Properties properties, CommonSession session) {
                    if (DoubleWriteConfigMgr.isDoubleWrite(configEntry.getKey())) {
                        try {
                            Object value = store.getAttribute(configEntry, properties);
                            if (value == null) {
                                store = getSessionStore(store.getStoreType());
                            }
                            return store.getAttribute(configEntry, properties);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return store.getAttribute(configEntry, properties);
                }

                @Override
                public boolean isSetAttribute() {
                    return false;
                }
            });

            return value;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object getValue(String name) {
        return null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return null;
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    public String getCustomSessionAttribute(String key) {
        SessionStore sessionStore = getSessionStore(SessionStoreType.getSessionStoreType("redis"));
        return sessionStore.getCustomAttribute(key);
    }

    /**
     * @param key   写入的key
     * @param value 写入的值
     * @return 是否写成功
     */
    public RetValue setCustomSessionAttribute(String key, String value) {
        RetValue ret = new RetValue();

        /**
         * 判断是否登录态
         */
        if (!SessionUtils.isLogin(this)) {
            ret.setValue(-1);
            ret.setMsg("没有登录态");
            return ret;
        }


        /**
         * 判断是否是白名单中允许添加的session
         */
        /**
         * 判断session白名单
         */
       /* List<String> authroizedKeys = getSessionConfig().getAuthorizedSessionKeys();
        if (!authroizedKeys.contains(key)) {
            ret.setValue(-2);
            ret.setMsg("非法的session种植");
            return ret;
        }*/



        /*TODO 不能写预先定义的session*/
        SessionStore sessionStore = getSessionStore(SessionStoreType.getSessionStoreType("redis"));
        boolean result = sessionStore.setCustomAttribute(key, value);
        if (!result) {
            ret.setValue(-1);
        }
        return ret;
    }

    private void setAttributeInWriteCache(String name, Object value) {
        if (name != null && value != null) {
            writeCache.put(name, value);
        } else if (value == null) {
            writeCache.remove(name);
        }
    }

    @Override
    public void setAttribute(String name, final Object value) {
        setAttributeInWriteCache(name, value);
        performOnAttribute(name, new Callback() {

            @Override
            public Object perform(SessionStore store, ConfigEntry configEntry, Properties properties,
                                  CommonSession session) {
                if (DoubleWriteConfigMgr.isDoubleWrite(configEntry.getKey())) {
                    for (SessionStore item : currentStoreMap.values()) {
                        item.setAttribute(configEntry, properties, value);
                    }
                } else {
                    store.setAttribute(configEntry, properties, value);
                }
                return null;
            }

            @Override
            public boolean isSetAttribute() {
                return true;
            }
        });
    }

    @Override
    public void putValue(String name, Object value) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public void removeValue(String name) {

    }


    @Override
    public boolean isNew() {
        return false;
    }


    public void commit() {
        for (SessionStore sessionStore : currentStoreMap.values()) {
            if (sessionStore instanceof CommonRedisStore) {
                setExpiredTimeChanged(isExpiredTimeChanged());
                ((CommonRedisStore) sessionStore).setExpiredTimeChanged(isExpiredTimeChanged());
            }
            sessionStore.commit();
        }
    }
}
