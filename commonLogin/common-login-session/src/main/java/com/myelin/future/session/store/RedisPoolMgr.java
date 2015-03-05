package com.myelin.future.session.store;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.myelin.future.common.misc.CommonConfigs;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//import com.alibaba.base.tenant.sso.monitor.recorder.DefaultRecorder;

/**
 * Created by gabriel on 14-12-18.
 */
/*redis,目前是主从，采用redis异步同步的策略，应用不感知*/
public class RedisPoolMgr {
    public static RedisPoolMgr instance = null;

    private JedisPoolConfig config;
    private JedisPool jedisPool;
    private JedisPool slaveJedisPool;
    private String appName;
    private Cache<String, String> localCache;
    private Boolean isLocalCached;
    private Boolean slaveSwitch;

    public static RedisPoolMgr getInstance() {
        if (instance == null) {
            instance = new RedisPoolMgr();
        }
        return instance;
    }

    private RedisPoolMgr() {
        String host = CommonConfigs.props.get("cache.host");
        String backhost = CommonConfigs.props.get("cache.slave.host");
        String port = CommonConfigs.props.get("cache.port");
        String maxActive = CommonConfigs.props.get("cache.maxActive");
        String maxWait = CommonConfigs.props.get("cache.maxWait");
        String testOnBorrow = CommonConfigs.props.get("cache.testOnBorrow");
        String maxIdle = CommonConfigs.props.get("cache.maxIdle");
        appName = CommonConfigs.props.get("cache.appname");
        isLocalCached = Boolean.parseBoolean(CommonConfigs.props.get("cache.isLocalCached"));
        config = new JedisPoolConfig();
        config.setMaxIdle(Integer.parseInt(maxIdle));
        config.setMaxWaitMillis(Long.parseLong(maxWait));
        config.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));
        if (isLocalCached) {
            localCache = CacheBuilder.newBuilder().expireAfterWrite(60 * 10, TimeUnit.SECONDS).build();
        }

        jedisPool = new JedisPool(config, host, Integer.parseInt(port));
        slaveSwitch = Boolean.parseBoolean(CommonConfigs.props.get("cache.slaveSwitch"));
        if (slaveSwitch) {
            slaveJedisPool = new JedisPool(config, backhost, Integer.parseInt(port));
        }
    }

    /*同步数据*/
    private void slaveCopy() {
        if (!slaveSwitch) {
            return;
        }
        Jedis slaveJedis = null;
        try {
            slaveJedis = slaveJedisPool.getResource();//获取到slave的连接
            slaveJedis.slaveof(CommonConfigs.props.get("cache.host"), Integer.parseInt(CommonConfigs.props.get("cache.port")));//说明谁是slave的主
        } catch (Exception e) {
            //DefaultRecorder.record(e.getCause().getMessage(), CommonConstant.LOGGER_ERROR_LEVEL);
            returnBackUpBrokenResource(slaveJedis);
        } finally {
            returnBackUpResource(slaveJedis);
        }
    }

    private String slaveReadOperation(String key) {
        if (!slaveSwitch) {
            return null;
        }


        Jedis slaveJedis = null;
        try {
            slaveJedis = slaveJedisPool.getResource();//获取到slave的连接
            slaveJedis.slaveof(CommonConfigs.props.get("cache.host"), Integer.parseInt(CommonConfigs.props.get("cache.port")));//说明谁是slave的主
            String ret = slaveJedis.get(key);
            return ret;
        } catch (Exception e) {
            //DefaultRecorder.record(e.getCause().getMessage(), CommonConstant.LOGGER_ERROR_LEVEL);
            returnBackUpBrokenResource(slaveJedis);
        } finally {
            returnBackUpResource(slaveJedis);
        }
        return null;
    }

    public String readValue(String key) {
        Jedis jedis = null;

        try {
            //TODO 这里不用判断后端缓存的失效时间,因为登录态的失效实际是根据cookie判断的,在缓存上设置失效值是为了节省缓存资源
            String value = null;
            if (isLocalCached) {
                value = localCache.getIfPresent(appName + key);
            }
            if (value != null) {
                return value;
            } else {
                jedis = jedisPool.getResource();
                String ret = jedis.get(appName + key);
                if (ret != null && isLocalCached) {
                    localCache.put(appName + key, ret);
                }
                return ret;
            }
        } catch (Exception e) {
            //DefaultRecorder.record(e.getCause().getMessage(), CommonConstant.LOGGER_ERROR_LEVEL);
            returnBrokenResource(jedis);
            return slaveReadOperation(appName + key);
        } finally {
            returnResource(jedis);
        }
    }


    public Long delValue(String key) {
        Jedis jedis = null;
        try {
            String value = null;
            if (isLocalCached) {
                value = localCache.getIfPresent(appName + key);
            }
            if (value != null && isLocalCached) {
                localCache.invalidate(appName + key);
            }
            jedis = jedisPool.getResource();
            Long ret = jedis.del(appName + key);
            return ret;
        } catch (Exception e) {
            // DefaultRecorder.record(e.getCause().getMessage(), CommonConstant.LOGGER_ERROR_LEVEL);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    /*客户端用，主从模式下只能对主写，从readonly*/
    public String writeValue(String key, String value, int expire) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String ret = jedis.setex(appName + key, expire, value);
            slaveCopy();
            if (isLocalCached && value == null) {
                localCache.put(appName + key, "");//优先保证机器可用，再保证一致性;一致性可以通过nginx sesson sticky加强
            } else if (isLocalCached && value != null) {
                localCache.put(appName + key, value);//优先保证机器可用，再保证一致性;一致性可以通过nginx sesson sticky加强
            }
            return ret;
        } catch (Exception e) {
            //DefaultRecorder.record(e.getCause().getMessage(), CommonConstant.LOGGER_ERROR_LEVEL);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    /*仅服务端用*/
    public boolean writeMultiVal(HashMap<String, String> map, int expire) {
        Jedis jedis = null;
        try {
            Set<Map.Entry<String, String>> mapSetForLocal = map.entrySet();
            Iterator<Map.Entry<String, String>> iteratorLocal = mapSetForLocal.iterator();
            while (iteratorLocal.hasNext()) {
                Map.Entry<String, String> item = iteratorLocal.next();
                if (isLocalCached && item.getValue() == null) {
                    localCache.put(appName + item.getKey(), "");
                } else if (isLocalCached && item.getValue() != null) {
                    localCache.put(appName + item.getKey(), item.getValue());
                }

            }

            jedis = jedisPool.getResource();
            Pipeline pipeline = jedis.pipelined();
            Set<Map.Entry<String, String>> mapSet = map.entrySet();
            Iterator<Map.Entry<String, String>> iterator = mapSet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> item = iterator.next();
                pipeline.setex(appName + item.getKey(), expire, item.getValue());
            }
            pipeline.sync();
            return true;
        } catch (Exception e) {
            //DefaultRecorder.record(e.getCause().getMessage(), CommonConstant.LOGGER_ERROR_LEVEL);
            returnBrokenResource(jedis);
            return false;
        } finally {
            returnResource(jedis);
        }
    }

    private void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }

    private void returnBackUpResource(Jedis jedis) {
        if (jedis != null) {
            slaveJedisPool.returnResource(jedis);
        }
    }

    private void returnBackUpBrokenResource(Jedis jedis) {
        if (jedis != null) {
            slaveJedisPool.returnBrokenResource(jedis);
        }
    }

    private void returnBrokenResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnBrokenResource(jedis);
        }
    }

}
