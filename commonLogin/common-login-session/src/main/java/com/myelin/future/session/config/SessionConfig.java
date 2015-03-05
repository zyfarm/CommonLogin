package com.myelin.future.session.config;

import javax.servlet.FilterConfig;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

/**
 * Created by gabriel on 14-8-6.
 */
public interface SessionConfig {

    /**
     * 根据key获得ConfigEntry
     */
    public ConfigEntry getConfigEntry(String key);

    /**
     * 获取现在使用的storeKey
     */
    public Set<String> getInUseStoreKeys(int version);


    /**
     * 获取某一个组合cookie的所有config内容
     */
    public Collection<ConfigEntry> getConfigGroup(String key);


    /**
     * 用于添加固定的配置，如sessionID的配置
     */
    public void addConfigEntry(ConfigEntry configEntry);


    /**
     * 返回所有配置的key
     */
    public Collection<String> getKeys(int version);


    /**
     * filter方式的初始化
     */
    public void init(FilterConfig filterConfig) throws Exception;


    /**
     * 获取配置属性
     */
    public Properties getProperties();


    /**
     * 返回默认配置
     */
    public Properties getDefaultConfig();

    public String getBackendEnv();

}
