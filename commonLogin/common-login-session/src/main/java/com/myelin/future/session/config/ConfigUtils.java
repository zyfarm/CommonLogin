package com.myelin.future.session.config;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by gabriel on 14-8-7.
 */
public class ConfigUtils {


    public static void addConfigEntry(ConfigEntry configEntry,Map<String,ConfigEntry> configEntries,Map<String,Collection<ConfigEntry>> configGroups){
        if(configEntry!=null){
            String key=configEntry.getKey();
            if(StringUtils.isNotBlank(key)){
                if(!configEntries.containsKey(key)){ //如果还没有放入configEntires中
                    if(StringUtils.isNotBlank(configEntry.getStoreType())){
                        configEntries.put(configEntry.getKey(),configEntry);
                        addConfig2Group(configEntry,configGroups);
                    }else{
                        throw new IllegalArgumentException("配置项错误");
                    }

                }else{
                    throw new IllegalArgumentException("配置项错误");
                }
            }else{
                throw new IllegalArgumentException("配置项错误");
            }
        }else{
            throw new IllegalArgumentException("配置项错误");
        }
    }


    public static void addConfig2Group(ConfigEntry configEntry,Map<String,Collection<ConfigEntry>> configGroups){
        String compressKey = configEntry.getCompressKey();
        if(StringUtils.isNotBlank(compressKey)){
            Collection<ConfigEntry> configGroup = configGroups.get(compressKey);
            if(configGroup==null){
                configGroup = new ConcurrentLinkedQueue<ConfigEntry>();
                configGroups.put(compressKey, configGroup);
            }
            configGroup.add(configEntry);
        }
    }









}
