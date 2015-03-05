package com.myelin.future.server.config;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by gabriel on 14-12-18.
 */
public class ServerSessionConfig {

    private List<String> authorizedHost;
    private Properties defaultProperties;

    private Collection<Properties> sessionConfigs;
    private Collection<Properties> cookieConfigs;

    private String redisEnv;

    public String getRedisEnv() {
        return redisEnv;
    }

    public void setRedisEnv(String redisEnv) {
        this.redisEnv = redisEnv;
    }

    public List<String> getAuthorizedHost() {
        return authorizedHost;
    }

    public void setAuthorizedHost(List<String> authorizedHost) {
        this.authorizedHost = authorizedHost;
    }

    public Collection<Properties> getCookieConfigs() {
        return cookieConfigs;
    }

    public void setCookieConfigs(Collection<Properties> cookieConfigs) {
        this.cookieConfigs = cookieConfigs;
    }

    public Collection<Properties> getSessionConfigs() {
        return sessionConfigs;
    }

    public void setSessionConfigs(Collection<Properties> sessionConfigs) {
        this.sessionConfigs = sessionConfigs;
    }

    public Properties getDefaultProperties() {
        return defaultProperties;
    }

    public void setDefaultProperties(Properties defaultProperties) {
        this.defaultProperties = defaultProperties;
    }
}
