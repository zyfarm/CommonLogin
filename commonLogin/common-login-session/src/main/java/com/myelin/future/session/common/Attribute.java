package com.myelin.future.session.common;


import com.myelin.future.session.config.ConfigEntry;

import java.util.Properties;

/**
 * Created by gabriel on 14-8-25.
 */
public class Attribute {

    private ConfigEntry configEntry;

    private Properties properties;

    private Object value;

    public Attribute(ConfigEntry configEntry, Properties properties) {
        this(configEntry, properties, null);
    }

    public Attribute(ConfigEntry configEntry, Properties properties, Object value) {
        this.configEntry = configEntry;
        this.properties = properties;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public ConfigEntry getConfigEntry() {
        return configEntry;
    }

    public Properties getProperties() {
        return properties;
    }

}