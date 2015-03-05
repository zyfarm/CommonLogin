package com.myelin.future.server.adapter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myelin.future.common.codec.BlowfishEncryptor;
import com.myelin.future.common.misc.CommonCookie;
import com.myelin.future.common.misc.StringEscapeUtil;
import com.myelin.future.common.misc.UniqIdUtils;
import com.myelin.future.db.mybatis.dataobj.UserProfile;
import com.myelin.future.server.config.ServerSessionConfig;
import com.myelin.future.server.exception.BackEndStoreException;
import com.myelin.future.session.store.RedisPoolMgr;
import org.apache.commons.codec.binary.Base64;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by gabriel on 14-12-7.
 */
@Service
public class LoginProxy {

    public static ServerSessionConfig serverSessionConfig;

    public static RedisPoolMgr redisPoolMgr;

    public static ThreadLocal<String> ctokenLocal = new ThreadLocal<String>();

    private static Pattern NOT_STANDARD_AN = Pattern.compile("[\u4e00-\u9fa5\ufe30-\uffa0]+");

    public static class CookieItem {
        private String cookieName;
        private String cookieNick;
        private Integer expire;
        private String domain;
        private Boolean isHttpOnly;
        private String EnCryptMethod;
        private String path;
        private String isBase64;

        public String getIsBase64() {
            return isBase64;
        }

        public void setIsBase64(String isBase64) {
            this.isBase64 = isBase64;
        }

        public String getCookieName() {
            return cookieName;
        }

        public void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }

        public String getCookieNick() {
            return cookieNick;
        }

        public void setCookieNick(String cookieNick) {
            this.cookieNick = cookieNick;
        }

        public Integer getExpire() {
            return expire;
        }

        public void setExpire(Integer expire) {
            this.expire = expire;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public Boolean getIsHttpOnly() {
            return isHttpOnly;
        }

        public void setIsHttpOnly(Boolean isHttpOnly) {
            this.isHttpOnly = isHttpOnly;
        }

        public String getEnCryptMethod() {
            return EnCryptMethod;
        }

        public void setEnCryptMethod(String enCryptMethod) {
            EnCryptMethod = enCryptMethod;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class SessionItem {
        private String loginType;
        private String expireTime;
        private String sessionKey;
        private String defaultVal;
        private String isBase64;

        public String getIsBase64() {
            return isBase64;
        }

        public void setIsBase64(String isBase64) {
            this.isBase64 = isBase64;
        }

        public String getDefaultVal() {
            return defaultVal;
        }

        public void setDefaultVal(String defaultVal) {
            this.defaultVal = defaultVal;
        }

        public String getLoginType() {
            return loginType;
        }

        public void setLoginType(String loginType) {
            this.loginType = loginType;
        }

        public String getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(String expireTime) {
            this.expireTime = expireTime;
        }

        public String getSessionKey() {
            return sessionKey;
        }

        public void setSessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
        }
    }

    protected static final ArrayList<SessionItem> sessionList = new ArrayList<SessionItem>();
    protected static final ArrayList<CookieItem> cookieList = new ArrayList<CookieItem>();

    protected static ServerSessionConfig getServerSessionConfigFromContext(ApplicationContext context) {
        Map<String, ServerSessionConfig> beans = context.getBeansOfType(ServerSessionConfig.class);
        if (beans.size() == 0) {
            throw new IllegalArgumentException("没有找到任何类型的bean定义");
        } else if (beans.size() > 1) {
            throw new IllegalArgumentException("没有找到任何类型的bean定义");
        } else {
            return beans.values().toArray(new ServerSessionConfig[1])[0];
        }
    }

    public static ArrayList<CookieItem> getCookieList() {
        return cookieList;
    }

    static {
        /**
         * 根据环境加载配置文件
         */
        ApplicationContext context = new ClassPathXmlApplicationContext("session/server.session.daily.xml");
        serverSessionConfig = getServerSessionConfigFromContext(context);
        Collection<Properties> props = serverSessionConfig.getSessionConfigs();

        Iterator<Properties> iteratorProperties = props.iterator();
        while (iteratorProperties.hasNext()) {
            Properties item = iteratorProperties.next();
            SessionItem sessionItem = new SessionItem();
            sessionItem.setExpireTime(item.getProperty("expireTime"));
            sessionItem.setLoginType(item.getProperty("loginType"));
            sessionItem.setSessionKey(item.getProperty("sessionKey"));
            sessionItem.setDefaultVal(item.getProperty("defaultVal"));
            sessionItem.setIsBase64(item.getProperty("isBase64"));
            sessionList.add(sessionItem);
        }

        Collection<Properties> cookieProps = serverSessionConfig.getCookieConfigs();
        Iterator<Properties> cookieIterator = cookieProps.iterator();
        while (cookieIterator.hasNext()) {
            Properties item = cookieIterator.next();
            CookieItem cookieItem = new CookieItem();
            cookieItem.setCookieName(item.getProperty("cookieName"));
            cookieItem.setCookieNick(item.getProperty("cookieNick"));
            cookieItem.setDomain(item.getProperty("domain"));
            cookieItem.setEnCryptMethod(item.getProperty("EncryptMethod"));
            cookieItem.setExpire(Integer.parseInt(item.get("expire").toString()));
            cookieItem.setIsHttpOnly(Boolean.parseBoolean(item.get("isHttpOnly").toString()));
            cookieItem.setPath(item.getProperty("path"));
            cookieItem.setIsBase64(item.getProperty("isBase64"));
            cookieList.add(cookieItem);
        }

        redisPoolMgr = RedisPoolMgr.getInstance();
    }

    public void init() {

    }


    /*种植私有session*/
    private void setCustomSession(HashMap<String, String> kv, HttpSession session, Integer expireTime) {
        HashMap<String, String> customMapper = new HashMap<String, String>();
        customMapper.put("ctime", new Date().toString());
        redisPoolMgr.writeValue("csid-" + session.getId(), JSON.toJSONString(customMapper), expireTime);
        if (kv != null) {
            if (!redisPoolMgr.writeMultiVal(kv, expireTime)) {
                throw new BackEndStoreException("backEndStoreExecption");
            }
            ;
        }
    }

    private String getCookieSg(UserProfile newBaseUserInfo) {
        return generateSg(newBaseUserInfo.getName(), newBaseUserInfo.getUserId());
    }

    private String generateSg(String accountName, String baseId) {
        StringBuilder sb = new StringBuilder();
        sb.append(accountName.charAt(accountName.length() - 1));
        sb.append(baseId.charAt(0));
        sb.append(baseId.charAt(baseId.length() - 1));
        return sb.toString();
    }

    /*种植公共session*/
    protected void setSystemSession(UserProfile newBaseUserInfo, HttpSession session, Integer expireTime) {
        /*创建预定义session*/
        HashMap<String, String> userMapper = buildSessionMap(newBaseUserInfo);

        /*创建cookie签名*/
        HashMap<String, String> redisData = new HashMap<String, String>();
        redisData.put(session.getId(), JSON.toJSONString(userMapper));

        /*写入*/
        if (!redisPoolMgr.writeMultiVal(redisData, expireTime)) {
            throw new BackEndStoreException("backendException");
        }
        ;
    }


    /*创建ck2对应的系統session*/
    protected HashMap<String, String> buildSessionMap(UserProfile userProfile) {
        HashMap<String, String> map = new HashMap<String, String>();
        for (SessionItem item : sessionList) {
            if (item.getSessionKey().equals("cToken")) {
                String ctoken = UniqIdUtils.getInstance().getUniqStr();
                map.put(item.getSessionKey(), ctoken);
                ctokenLocal.set(ctoken);
                continue;
            }


            try {
                Field field = userProfile.getClass().getDeclaredField(item.getSessionKey());
                field.setAccessible(true);
                if (field.getType().equals(java.util.Date.class)) {
                    Date date = (Date) field.get(userProfile);
                    map.put(item.getSessionKey(), date.toString());
                } else {
                    String val = (String) field.get(userProfile);
                    val = StringEscapeUtil.escapeSql(val);

                    map.put(item.getSessionKey(), val);
                }
            } catch (NoSuchFieldException e) {
                map.put(item.getSessionKey(), item.getDefaultVal());
            } catch (IllegalAccessException E) {
                continue;
            } catch (Exception e) {
                continue;
            }
        }
        return map;
    }

    private void setCookie(HttpServletResponse response, String key, String val, String domain, String path, int age, boolean isHttpOnly) {
        /*TODO cookie加密*/
        CommonCookie cookie = new CommonCookie(key, StringEscapeUtil.escapeSql(val));
        cookie.setHttpOnly(isHttpOnly);
        cookie.setDomain(domain);
        cookie.setMaxAge(age);
        cookie.setPath(path);
        response.addCookie(cookie);
    }

    private String base64andUrlEncode(String val) {
        String retVal = new String(Base64.encodeBase64(val.getBytes()));
        try {
            retVal = URLEncoder.encode(retVal, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            retVal = URLEncoder.encode(retVal);
        }
        return retVal;
    }

    private void setCommonCookie(HashMap<String, String> kvMap, HashMap<String, String> customCookie, HttpServletResponse response, String baseId) {

        for (CookieItem cookieItem : cookieList) {
            String val = kvMap.get(cookieItem.getCookieNick());

            if (cookieItem.getIsBase64().equals("true")) {

                if (NOT_STANDARD_AN.matcher(val).find() && cookieItem.getCookieName().equals("accountName")) { //如果用户名中含有中文

                    /*对中文名做base64和urlencode*/
                    String tmpVal = kvMap.get(cookieItem.getCookieNick());
                    val = base64andUrlEncode(tmpVal);
                    kvMap.put(cookieItem.getCookieNick(), val);

                    /*重新计算sg签名*/
                    String newSg = null;
                    try {
                        newSg = generateSg(URLDecoder.decode(kvMap.get("an"), "UTF-8"), baseId);
                    } catch (UnsupportedEncodingException e) {
                        newSg = generateSg(URLDecoder.decode(kvMap.get("an")), baseId);
                    }

                    kvMap.put("sg", newSg);
                    String sessionValue = redisPoolMgr.readValue(kvMap.get("ck2"));
                    JSONObject jsonObject = JSON.parseObject(sessionValue);
                    try {
                        jsonObject.put(cookieItem.getCookieName(), URLDecoder.decode(val, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        jsonObject.put(cookieItem.getCookieName(), URLDecoder.decode(val));
                    }
                    redisPoolMgr.writeValue(kvMap.get("ck2"), jsonObject.toJSONString(), 4500);
                }
            }

            if (!cookieItem.getEnCryptMethod().equals("none")) {
                if (cookieItem.getEnCryptMethod().equals("blowfish")) {
                    val = BlowfishEncryptor.getEncrypter().encrypt(val);
                    try {
                        val = URLEncoder.encode(val, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        val = URLEncoder.encode(val);
                    }
                }
            }

            setCookie(response, cookieItem.getCookieNick(), val, cookieItem.getDomain(), cookieItem.getPath(), cookieItem.getExpire(), cookieItem.getIsHttpOnly());
        }

        if (customCookie != null) {
            for (CookieItem cookieItem : cookieList) {
                Set<String> customKeySet = customCookie.keySet();
                Iterator<String> customIterator = customKeySet.iterator();
                while (customIterator.hasNext()) {
                    if (customIterator.next().equalsIgnoreCase(cookieItem.getCookieNick())) {
                        setCookie(response, cookieItem.getCookieNick(), customCookie.get(cookieItem.getCookieNick()), cookieItem.getDomain(), cookieItem.getPath(), cookieItem.getExpire(), cookieItem.getIsHttpOnly());
                    }
                }
            }
        }
    }


    private HashMap<String, String> getSystemCookies(UserProfile userProfile, HttpSession session) {
        HashMap<String, String> systemCookies = new HashMap<String, String>();
        systemCookies.put("ck2", session.getId());
        systemCookies.put("an", userProfile.getName());
        systemCookies.put("lg", "true");
        systemCookies.put("sg", getCookieSg(userProfile));
        Long currentTime = (System.currentTimeMillis() / 1000);
        systemCookies.put("lvc", currentTime.toString());
        systemCookies.put("c_token", ctokenLocal.get());
        return systemCookies;
    }


    public UserProfile syncLoginInfo(LoginAdapter loginAdapter, HttpSession session, HttpServletResponse response, HashMap<String, String> params) {
        UserProfile userProfile = loginAdapter.getProxyUserInfo(params);

        if (userProfile != null && !userProfile.getIsDisabled().equals((byte) 1) && !userProfile.getIsDeleted().equals((byte) 1)) {
            UserProfile newUserInfo = loginAdapter.syncProxyUserInfo(userProfile);
            if (newUserInfo != null) {
                /*设置系统session*/
                setSystemSession(newUserInfo, session, 4500);

                /*获取自定义的sesson*/
                HashMap<String, String> customKv = loginAdapter.getCustomSessionAttribute(newUserInfo);

                /*设置自定义的session*/
                setCustomSession(customKv, session, 4500);


                /*种植自定义cookie*/
                HashMap<String, String> customCookies = loginAdapter.getCustomCookieAttribute(newUserInfo);

                /*种植系统cookie*/
                HashMap<String, String> systemCookies = getSystemCookies(userProfile, session);
                setCommonCookie(systemCookies, customCookies, response, newUserInfo.getUserId());

                /*清空本地线程*/
                ctokenLocal.remove();
            }
        }
        return userProfile;
    }
}
