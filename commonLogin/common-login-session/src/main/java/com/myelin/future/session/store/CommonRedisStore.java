package com.myelin.future.session.store;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myelin.future.common.retobj.RetValue;
import com.myelin.future.session.common.CommonSession;
import com.myelin.future.session.common.SessionUtils;
import com.myelin.future.session.config.ConfigEntry;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


/**
 * Created by gabriel on 14-8-24.
 */
public class CommonRedisStore implements SessionStore {

    // private static Logger logger = LoggerFactory.getLogger(CommonRedisStore.class);
    private CommonSession session;


    private RedisPoolMgr redisClient;
    private static String REDIS_HOST = "localhost";
    private static String CUSTOM_SESSION = "csid";
    private static int REDIS_PORT = 6379;


    // 更新时间更新标志位
    private volatile boolean expiredTimeChanged = false;
    // 服务端数据更新标志位
    private volatile boolean hasDataChanged = false;
    // 是否有过读缓存操作标志
    private volatile boolean alreadyRead = false;

    private volatile Map<String, Object> attributes;
    private volatile Map<String, Object> cusAttributes;


    // 用于存储从缓存上get的原始数据
    private volatile Map<String, Object> cacheAttributes;


    @Override
    public Object getAttribute(ConfigEntry configEntry, Properties properties) {
        if (StringUtils.isNotBlank(session.getId())) {
            if (attributes == null || !alreadyRead) {
                RetValue retValue = readFromRedis();
                if (retValue != null && retValue.getValue() == 0) {
                    alreadyRead = true;
                }
            }
        }

        String key = configEntry.getKey();
        if (configEntry.getPatternType() != 0) {
            key = configEntry.getPatternKey();
        }
        return attributes.get(key);
    }


    @Override
    public void setAttribute(ConfigEntry configEntry, Properties properties, Object value) {
        if (StringUtils.isNotEmpty(session.getId())) {
            if ((attributes == null || !alreadyRead) && !"sessionId".equals(configEntry.getKey())) {
                RetValue retValue = readFromRedis();
                if (retValue != null && retValue.getValue() == 0) {
                    alreadyRead = true;
                }
                if (!alreadyRead) {
                    return;
                }
            }
        }

        String key = configEntry.getKey();
        if (configEntry.getPatternType() != 0) {
            key = configEntry.getPatternKey();
        }

        if (null != value) {
            String valStr = ObjectUtils.toString(value);
            attributes.put(key, valStr);

            if (!"sessionId".equals(key)) {
                this.hasDataChanged = true;
            }

        } else {
            attributes.remove(key);
            this.hasDataChanged = true;
        }


    }

    public boolean isServiceDown() {
        return false;
    }

    /**
     * 判断缓存的属性是否发生变化
     */
    private boolean isAttributeHasChanged(Map<String, Object> cacheAttributes, Map<String, Object> attributes) {
        if (cacheAttributes == null || attributes == null) {
            return false;
        }

        if (cacheAttributes.size() != attributes.size()) {
            return true;
        }

        for (Map.Entry<String, Object> cacheAttributeItem : cacheAttributes.entrySet()) {
            if (!cacheAttributeItem.getValue().equals(attributes.get(cacheAttributeItem.getKey()))) {
                return true;
            }
        }

        return false;
    }


    public void removeAll() {
        this.attributes.clear();
        this.hasDataChanged = true;
    }

    public RetValue readFromRedis() {
        RetValue retValue = new RetValue();
        this.redisClient = RedisPoolMgr.getInstance();
        String redisData = redisClient.readValue(getSessionID());
        String cusRedisData = redisClient.readValue("csid-" + getSessionID());

        //logger.debug("[readRfromRedis]-redisData-" + redisData);
        //logger.debug("[readRfromRedis]-cusRedisData-" + cusRedisData);

        if (StringUtils.isBlank(redisData)) {
            retValue.setMsg("cache not exist");
            retValue.setValue(-1);
            retValue.setObj(null);
        } else {
            Map<String, Object> sessionData = (Map) JSON.parse(redisData);
            retValue.setMsg("SUCCESS");
            retValue.setObj(sessionData);
            retValue.setValue(0);
            attributes.putAll(sessionData);
            cacheAttributes.putAll(sessionData);

            Map<String, Object> customData = (Map) JSON.parse(cusRedisData);
            cusAttributes.putAll(customData);
        }
        return retValue;
    }

    public void WriteToRedis() {
        if (isServiceDown()) {
            return;
        } else {
            this.redisClient = RedisPoolMgr.getInstance();
            if (attributes != null && attributes.size() > 0) {
                long expireTime = session.getMaxCacheExpiretime();
                if (SessionUtils.isVisitor(session)) {
                    expireTime = session.getMaxVisitorExpiretime();
                }
                Integer exp = ((Long) (expireTime)).intValue();
                redisClient.writeValue(session.getId(), SessionUtils.serilizeToJson(attributes), exp);
                redisClient.writeValue(CUSTOM_SESSION + "-" + session.getId(), SessionUtils.serilizeToJson(cusAttributes), exp);
            } else {
                redisClient.delValue(CUSTOM_SESSION + "-" + session.getId());
                redisClient.delValue(session.getId());
            }
        }
    }

    public boolean isExpiredTimeChanged() {
        return expiredTimeChanged;
    }

    public void setExpiredTimeChanged(boolean expiredTimeChanged) {
        this.expiredTimeChanged = expiredTimeChanged;
    }

    @Override
    public void commit() {
        if (hasDataChanged) {
            if (isAttributeHasChanged(cacheAttributes, attributes)) {
                WriteToRedis();
                return;
            }
        }


        if (expiredTimeChanged) {
            if (isAttributeHasChanged(cacheAttributes, attributes)) {
                readFromRedis();
                WriteToRedis();
            }
        }
    }

    @Override
    public void init(CommonSession session) {
        this.session = session;
        this.expiredTimeChanged = false;
        this.hasDataChanged = false;
        this.alreadyRead = false;
        this.attributes = new HashMap<String, Object>();
        this.cusAttributes = new HashMap<String, Object>();
        this.cacheAttributes = new HashMap<String, Object>();
    }


    @Override
    public SessionStoreType getStoreType() {
        return SessionStoreType.REDIS_STORE;
    }

    @Override
    public void clear() {

    }


    @Override
    public String getSessionID() {
        return session.getSessionId();
    }

    private JSONObject getCustomAttributeAll() {
        this.redisClient = RedisPoolMgr.getInstance();
        String jsonMap = redisClient.readValue(CUSTOM_SESSION + "-" + session.getId());
        return JSONObject.parseObject(jsonMap);
    }

    @Override
    public String getCustomAttribute(String key) {
        this.redisClient = RedisPoolMgr.getInstance();
        String jsonMap = redisClient.readValue(CUSTOM_SESSION + "-" + session.getId());
        JSONObject obj = JSONObject.parseObject(jsonMap);
        if (obj == null) {
            return null;
        }
        return obj.getString(key);
    }

    private String kv2json(String key, String value) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(key, value);
        return JSONObject.toJSONString(params);
    }

    @Override
    public boolean setCustomAttribute(String key, String value) {
        /**
         * 获取session失效时间
         */
        long expireTime = session.getMaxCacheExpiretime();
        if (SessionUtils.isVisitor(session)) {
            expireTime = session.getMaxVisitorExpiretime();
        }


        /**
         * 按key读取原有数据
         */

        JSONObject allMap = getCustomAttributeAll(); //读取所有自定义的session
        String jsonVal;
        if (allMap != null) {
            allMap.put(key, value);
            jsonVal = allMap.toJSONString();
        } else {
            return false;
        }

        redisClient = RedisPoolMgr.getInstance();
        Integer exp = ((Long) (session.getMaxCacheExpiretime())).intValue();
        redisClient.writeValue(CUSTOM_SESSION + "-" + session.getId(), jsonVal, exp);
        return true;
    }
}
