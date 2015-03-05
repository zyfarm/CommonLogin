package com.myelin.future.session.config;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.FilterConfig;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by gabriel on 14-8-6.
 */
public class AbstractSessonConfig implements SessionConfig {

    private volatile Properties defaultConfig = new Properties();
    protected volatile Map<String, ConfigEntry>[] configEntries;
    protected volatile Map<String, ConfigEntry>[] patternConfigEntries;
    protected volatile Map<String, Collection<ConfigEntry>>[] configGroups;
    protected volatile Properties[] properties;
    protected volatile Set<String>[] inUseStoreKeys;
    protected volatile String backendEnv;

    public String getBackendEnv() {
        return backendEnv;
    }

    public void setBackendEnv(String backendEnv) {
        this.backendEnv = backendEnv;
    }

    /**
     * 在配置文件中指明是哪个环境
     */
    private String configEnv = null;


    @Override
    public Set<String> getInUseStoreKeys(int version) {
        return this.inUseStoreKeys[version];
    }

    /**
     * 将一个文件流读入并转换为字符串
     */
    @Deprecated
    protected void readFromResourceToString(String configEnv) throws IOException {
        InputStream input = this.getClass().getResourceAsStream("/session.config." + configEnv + ".xml");
        StringBuffer sb = new StringBuffer();
        int tmp;
        int pos = 0;
        byte[] bytes = new byte[1024];
        while ((tmp = input.read()) != -1) {
            bytes[pos++] = (byte) tmp;
            sb.append(new String(bytes, Charset.forName("UTF-8")));
            if (pos == 1024) {
                pos = 0;
            }
        }
    }

    protected SessionSpringConfigFactory getSpringFactoryFromContext(ApplicationContext context) {
        Map<String, SessionSpringConfigFactory> beans = context.getBeansOfType(SessionSpringConfigFactory.class);
        if (beans.size() == 0) {
            throw new IllegalArgumentException("没有找到任何类型为" + SessionSpringConfigFactory.class.getName() + "的bean定义");
        } else if (beans.size() > 1) {
            throw new IllegalArgumentException("找到超过一个类型为" + SessionSpringConfigFactory.class.getName() + "的bean定义");
        } else {
            return beans.values().toArray(new SessionSpringConfigFactory[1])[0];
        }
    }


    protected static void applyCombineKeyConfig(Properties config, Map<String, Properties> combineKeyConfigMap, Properties defaultConfig) {
        String compressKey = config.getProperty("compressKey");
        ServerSessionList.getNickKeySet().add(compressKey);
        if (compressKey != null) {
            Properties combineKeyConfig = combineKeyConfigMap.get(compressKey);

            for (Map.Entry<String, String> entry : LimitAccessConfig.DEFAULT_COMBINE_KEY_CONFIG.entrySet()) {
                String name = entry.getKey();
                String defaultValue = entry.getValue();
                String value = combineKeyConfig.getProperty(name, defaultConfig.getProperty(name, defaultValue));
                config.put(name, value);
            }
        }
    }

    protected static Map<String, Properties> convertCombineKeyToMap(Collection<Properties> combineKeyConfig) {
        Map<String, Properties> combineKeyConfigMap = new HashMap<String, Properties>();
        if (combineKeyConfig != null && combineKeyConfig.size() > 0) {
            for (Properties config : combineKeyConfig) {
                combineKeyConfigMap.put(config.getProperty("compressKey"), config);
            }
        }
        return combineKeyConfigMap;
    }


    protected Collection<Properties> getConfigEntryPropertiesFromFactory(SessionSpringConfigFactory sessionFactory) {
        Collection<Properties> configEntries = sessionFactory.getConfigEntries();
        Collection<Properties> combineEntries = sessionFactory.getCombineKeyConfig();
        Properties defaultConfig = sessionFactory.getDefaultConfig();
        Map<String, Properties> combineKeyMap = convertCombineKeyToMap(combineEntries);

        Collection<Properties> result = null;
        if (configEntries != null && configEntries.size() > 0) {
            result = new ArrayList<Properties>(configEntries.size());
            for (Properties properties : configEntries) {
                Properties config = new Properties();
                ServerSessionList.getNickKeySet().add(properties.getProperty("nickKey"));
                config.putAll(defaultConfig);
                config.putAll(properties);
                applyCombineKeyConfig(config, combineKeyMap, defaultConfig);
                result.add(config);
            }
        }
        return result;
    }

    /**
     * 根据环境选择合适的配置文件,加载并解析
     */
    protected void loadConfigEntryFile(String configEnv) {
        Collection<Properties> properties;
        /**
         * 根据环境加载配置文件
         */
        ApplicationContext context = new ClassPathXmlApplicationContext("session.config." + configEnv + ".xml");
        SessionSpringConfigFactory sessionFactory = getSpringFactoryFromContext(context);

        /**
         * 返回所有的属性
         */
        properties = sessionFactory.getProperties();
        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("illegal parameters");
        }

        /**
         * 将configEntries转换为对象
         */
        Collection<Properties> configEntries = getConfigEntryPropertiesFromFactory(sessionFactory);
        ConfigEntryResult configEntryResult = convertProperties2ConfigEntries(configEntries);

        Properties[] propertiesArray = new Properties[1];
        propertiesArray[0] = new Properties();

        for (Properties prop : properties) {
            propertiesArray[0].putAll(prop);
        }

        /**
         * 将properties转换为对象
         */
        PropertiesResult propertyResult = new PropertiesResult();
        propertyResult.setProperties(propertiesArray);

        if (configEntryResult == null) {
            throw new IllegalArgumentException("参数错误");
        }


        this.defaultConfig = sessionFactory.getDefaultConfig();
        this.configEntries = configEntryResult.getConfigEntries();
        this.patternConfigEntries = configEntryResult.getPatternConfigEntries();
        this.configGroups = configEntryResult.getConfigGroups();
        this.inUseStoreKeys = configEntryResult.getInUseStoreKeys();
        this.properties = propertyResult.getProperties();
        updateSessionExpiredTime(this.properties[0]);
    }

    protected void updateSessionExpiredTime(Properties props) {
        CommonSessionConfig.updateSessionExpiredTime(props);
    }

    protected static ConfigEntry populateConfigEntry(Properties properties) {
        ConfigEntry configEntry = new ConfigEntry();
        try {
            BeanUtils.populate(configEntry, properties);
        } catch (Exception e) {
            throw new IllegalArgumentException("error");
        }

        return configEntry;
    }

    /**
     * 将configEntires列表转换为对象
     */
    protected ConfigEntryResult convertProperties2ConfigEntries(Collection<Properties> entries) {
        Queue<ConfigEntry> queue = new ArrayDeque<ConfigEntry>(entries.size());
        for (Properties item : entries) {
            ConfigEntry entry = populateConfigEntry(item);
            if (entry != null) {
                queue.add(entry);
            }
        }

        Map<String, ConfigEntry>[] configEntries = new Map[1];
        Map<String, ConfigEntry>[] patternConfigEntries = new Map[1];
        Map<String, Collection<ConfigEntry>>[] configGroups = new Map[1];
        Set<String>[] inUseStoreTypes = new Set[1];

        configEntries[0] = new ConcurrentHashMap<String, ConfigEntry>();
        configGroups[0] = new ConcurrentHashMap<String, Collection<ConfigEntry>>();
        inUseStoreTypes[0] = new HashSet<String>();
        patternConfigEntries[0] = new ConcurrentHashMap<String, ConfigEntry>();

        for (ConfigEntry configItem : queue) {
            if (configItem.getPatternType() == 0) { //cookie完全匹配模式
                ConfigUtils.addConfigEntry(configItem, configEntries[0], configGroups[0]);
                inUseStoreTypes[0].add(configItem.getStoreType());
            } else if (configItem.getPatternType() == 1 || configItem.getPatternType() == 2) {
                ConfigUtils.addConfigEntry(configItem, patternConfigEntries[0], configGroups[0]);
                inUseStoreTypes[0].add(configItem.getStoreType());
            } else {
                throw new IllegalArgumentException("参数不正确");
            }
        }

        return new ConfigEntryResult(configEntries, configGroups, inUseStoreTypes, patternConfigEntries);
    }


    /**
     * 静态内部配置项类
     */
    protected class ConfigEntryResult {
        public Map<String, ConfigEntry>[] configEntries;

        public Map<String, Collection<ConfigEntry>>[] configGroups;

        public Set<String>[] inUseStoreKeys;

        public Map<String, ConfigEntry>[] patternConfigEntries;

        public ConfigEntryResult(Map<String, ConfigEntry>[] configEntries, Map<String, Collection<ConfigEntry>>[] configGroups, Set<String>[] inUseStoreKeys, Map<String, ConfigEntry>[] patternConfigEntries) {
            this.configEntries = configEntries;
            this.configGroups = configGroups;
            this.inUseStoreKeys = inUseStoreKeys;
            this.patternConfigEntries = patternConfigEntries;
        }

        public Map<String, ConfigEntry>[] getConfigEntries() {
            return configEntries;
        }

        public void setConfigEntries(Map<String, ConfigEntry>[] configEntries) {
            this.configEntries = configEntries;
        }

        public Map<String, Collection<ConfigEntry>>[] getConfigGroups() {
            return configGroups;
        }

        public void setConfigGroups(Map<String, Collection<ConfigEntry>>[] configGroups) {
            this.configGroups = configGroups;
        }

        public Set<String>[] getInUseStoreKeys() {
            return inUseStoreKeys;
        }

        public void setInUseStoreKeys(Set<String>[] inUseStoreKeys) {
            this.inUseStoreKeys = inUseStoreKeys;
        }

        public Map<String, ConfigEntry>[] getPatternConfigEntries() {
            return patternConfigEntries;
        }

        public void setPatternConfigEntries(Map<String, ConfigEntry>[] patternConfigEntries) {
            this.patternConfigEntries = patternConfigEntries;
        }
    }

    /**
     * 静态内部属性配置类
     */
    protected class PropertiesResult {
        public Properties[] properties;

        public Properties[] getProperties() {
            return properties;
        }

        public void setProperties(Properties[] properties) {
            this.properties = properties;
        }
    }


    /**
     * 初始化配置项
     */
    protected void initConfigEntry(String configEnv) throws IOException {
        loadConfigEntryFile(configEnv);
    }


    @Override
    public ConfigEntry getConfigEntry(String key) {
        return configEntries[0].get(key);
    }


    private void addToGroups(Collection<ConfigEntry> source, Map<String, ConfigEntry> target) {
        if (source != null) {
            for (ConfigEntry configEntry : source) {
                target.put(configEntry.getKey(), configEntry);
            }
        }
    }


    @Override
    public Collection<ConfigEntry> getConfigGroup(String key) {
        Map<String, ConfigEntry> groups = new ConcurrentHashMap<String, ConfigEntry>();
        addToGroups(configGroups[0].get(key), groups);
        return groups.values();
    }

    public Set<String>[] getInUseStoreKeys() {
        return inUseStoreKeys;
    }

    public void setInUseStoreKeys(Set<String>[] inUseStoreKeys) {
        this.inUseStoreKeys = inUseStoreKeys;
    }

    @Override
    public void addConfigEntry(ConfigEntry configEntry) {

    }

    private <T> T getFromArray(T[] array, int version) {
        int last = array.length - 1;
        if (version > last) {
            return array[last];
        } else {
            return array[version];
        }
    }


    @Override
    public Collection<String> getKeys(int version) {
        Collection<String> keys = new LinkedList<String>();
        keys.addAll(getFromArray(configEntries, version).keySet());
        return keys;
    }


    @Override
    public void init(FilterConfig filterConfig) throws Exception {
        this.configEnv = filterConfig.getInitParameter("configEnv");
        initConfigEntry(configEnv);
    }

    @Override
    public Properties getProperties() {
        return this.properties[0];
    }

    @Override
    public Properties getDefaultConfig() {
        return this.defaultConfig;
    }
}
