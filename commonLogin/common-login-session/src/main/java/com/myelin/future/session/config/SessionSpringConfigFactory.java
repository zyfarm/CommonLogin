package com.myelin.future.session.config;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by gabriel on 14-8-6.
 */
public class SessionSpringConfigFactory {
    private Properties defaultConfig;

    private List<String> authorizedSessions;

    public List<String> getAuthorizedSessions() {
        return authorizedSessions;
    }

    public void setAuthorizedSessions(List<String> authorizedSessions) {
        this.authorizedSessions = authorizedSessions;
    }

    private Collection<Properties> combineKeyConfig;

    private Collection<Properties> configEntries;

    private Collection<Properties> properties;

    public Properties getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(Properties defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public Collection<Properties> getCombineKeyConfig() {
        return combineKeyConfig;
    }

    public void setCombineKeyConfig(Collection<Properties> combineKeyConfig) {
        this.combineKeyConfig = combineKeyConfig;
    }

    public void setConfigEntries(Collection<Properties> configEntries) {
        this.configEntries = configEntries;
    }

    public Collection<Properties> getConfigEntries() {
        return configEntries;
    }

    public void setProperties(Collection<Properties> properties) {
        this.properties = properties;
    }

    public Collection<Properties> getProperties() {
        return properties;
    }


}
