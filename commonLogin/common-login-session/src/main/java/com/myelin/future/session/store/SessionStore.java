package com.myelin.future.session.store;

/**
 * Created by gabriel on 14-8-24.
 */


import com.myelin.future.session.common.CommonSession;
import com.myelin.future.session.config.ConfigEntry;

import java.util.Properties;


public interface SessionStore {


    public Object getAttribute(ConfigEntry configEntry, Properties properties);


    public void setAttribute(ConfigEntry configEntry, Properties properties, Object value);


    public void commit();

    public void init(CommonSession session);


    public SessionStoreType getStoreType();


    public void clear();

    public String getSessionID();

    public boolean setCustomAttribute(String key, String value);

    public String getCustomAttribute(String key);
}
