package com.myelin.future.session.store;


import com.myelin.future.common.codec.BlowfishEncryptor;
import com.myelin.future.common.dataobj.ConcurrentHashSet;
import com.myelin.future.common.misc.CommonCookie;
import com.myelin.future.session.common.Attribute;
import com.myelin.future.session.common.CommonServletRequest;
import com.myelin.future.session.common.CommonServletResponse;
import com.myelin.future.session.common.CommonSession;
import com.myelin.future.session.config.ConfigEntry;
import com.myelin.future.session.config.SessionConfig;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gabriel on 14-8-24.
 */
public class CommonCookieStore implements SessionStore {


    private CommonSession session;
    private ConcurrentHashMap<String, Attribute> attributes;
    private ConcurrentHashMap<String, String> cookies;
    private Set<String> dirty;
    public static final String COMBINE_SEPARATOR = "&";
    public static final String KEY_VALUE_SEPARATOR = "=";


    public CommonServletRequest getRequest() {
        return this.session.getRequest();
    }

    public CommonServletResponse getResponse() {
        return this.session.getResponse();
    }


    public CommonSession getSession() {
        return session;
    }

    public void setSession(CommonSession session) {
        this.session = session;
    }


    private Map<String, String> separateCookies(String cookieValue) {
        Map<String, String> separateCookies = new HashMap<String, String>();

        String[] contents = StringUtils.split(cookieValue, COMBINE_SEPARATOR);
        if (contents != null && contents.length > 0) {
            for (String content : contents) {
                String[] keyValue = StringUtils.split(content, KEY_VALUE_SEPARATOR, 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    separateCookies.put(key, value);
                }
            }
        }
        return separateCookies;
    }


    private void decodeCompressCookie(ConfigEntry configEntry, Properties properties) {
        String compressKey = configEntry.getCompressKey();
        String cookieValue = cookies.get(compressKey);

        Map<String, String> separateCookies = separateCookies(cookieValue);

        SessionConfig config = session.getSessionConfig();


        Collection<ConfigEntry> configGroup = config.getConfigGroup(compressKey); // 按客户端当前版本解析
        if (configGroup != null && configGroup.size() > 0) {
            for (ConfigEntry thisConfigEntry : configGroup) {
                String key = thisConfigEntry.getKey();
                if (thisConfigEntry.getPatternType() != 0) {
                    key = thisConfigEntry.getPatternKey();
                }
                if (!dirty.contains(key)) { // 如果该属性没有被写入过，才进行解码，否则原来写入的值会被覆盖
                    decodeSingleCookie(thisConfigEntry, properties, separateCookies);
                }
            }
        } else {
            /*logger.warn("configGroup不应该为空，请检查" + config.getClass().getName() + "的实现");*/
        }
    }

    /**
     * 解码单个cookie
     */
    public static String decodeValue(String value, ConfigEntry configEntry, Properties properties) {
        if (StringUtils.isBlank(value)) {
            return value;
        }


        try {
            value = URLDecoder.decode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }

        if (configEntry.isEscape()) {
            try {
                value = StringEscapeUtils.unescapeJava(value);
            } catch (Exception e) {
                return value;
            }
        } else if (configEntry.isBase64()) {
            return new String(Base64.decodeBase64(value.getBytes()));
        } else if (configEntry.isEnCrypt()) {
            return BlowfishEncryptor.getEncrypter().decrypt(value);
        }
        return value;
    }


    private void decodeSingleCookie(ConfigEntry configEntry, Properties properties, Map<String, String> cookies) {
        String nickKey = configEntry.getNickKey();
        String cookieValue = cookies.get(nickKey);

        String key = configEntry.getKey();
        if (configEntry.getPatternType() != 0) {
            key = configEntry.getPatternKey();
        }
        String value = decodeValue(cookieValue, configEntry, properties); // value可能为null
        Attribute attribute = new Attribute(configEntry, properties, value);

        attributes.put(key, attribute);
    }


    private void decodeCookie(ConfigEntry configEntry, Properties properties) {
        if (configEntry.isCompress()) {
            decodeCompressCookie(configEntry, properties);
        } else {
            decodeSingleCookie(configEntry, properties, cookies);
        }
    }


    @Override
    public void setAttribute(ConfigEntry configEntry, Properties properties, Object value) {
        String key = configEntry.getKey();
        if (configEntry.getPatternType() != 0) {
            key = configEntry.getPatternKey();
        }

        String v = ObjectUtils.toString(value, null);
        Attribute attr = new Attribute(configEntry, properties, v);
        attributes.put(key, attr);
        dirty.add(key);

    }


    public Object getAttribute(ConfigEntry configEntry, Properties properties) {
        String key = configEntry.getKey();
        if (configEntry.getPatternType() != 0) {
            key = configEntry.getPatternKey();
        }

        Attribute attribute = attributes.get(key);
        if (attribute == null) {
            decodeCookie(configEntry, properties);
            attribute = attributes.get(key);
        }
        return attribute.getValue();
    }

    public static String encodeValue(String value, ConfigEntry configEntry, Properties properties) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        if (configEntry.isEscape()) {
            value = StringEscapeUtils.escapeJava(value);
        }

        if (configEntry.isEnCrypt()) {
            value = BlowfishEncryptor.getEncrypter().encrypt(value);
        }

        if (configEntry.isBase64()) {
            String tmp = new String(Base64.encodeBase64(value.getBytes()));
            value = tmp;
        }

        try {
            value = URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return null;
        }
        return value;
    }


    private void addCookieToResponse(String name, String value, String domain, int maxAge, String path, boolean httpOnly, boolean secure) {
        CommonCookie cookie = new CommonCookie(name, value);
        if (StringUtils.isNotBlank(domain)) {
            cookie.setDomain(domain);
        }

        if (StringUtils.isNotBlank(path)) {
            cookie.setPath(path);
        }

        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);

        CommonServletResponse response = this.getResponse();
        response.addCookie(cookie);
    }


    private void addCookiesToResponse(ConfigEntry configEntry, String name, String value, boolean removed) {
        if (removed && !cookies.containsKey(name)) { //传入的cookie
            return;
        }

        String domain = configEntry.getDomain();
        int maxAge = !removed ? configEntry.getLifeCycle() : 0;
        String path = configEntry.getPath();
        boolean httpOnly = configEntry.isHttpOnly();

        addCookieToResponse(name, value, domain, maxAge, path, httpOnly, configEntry.isSecure());
    }


    private void encodeSingleCookie(ConfigEntry configEntry, Properties properties) {
        String name = configEntry.getNickKey();
        Object attribute = getAttribute(configEntry, properties);
        String value = attribute != null ? attribute.toString() : StringUtils.EMPTY;
        value = encodeValue(value, configEntry, properties);
        addCookiesToResponse(configEntry, name, value, attribute == null);
    }

    private void encodeCookie(ConfigEntry configEntry, Properties properties) {
        if (configEntry.isCompress()) {

        } else {
            encodeSingleCookie(configEntry, properties);
        }

    }


    @Override
    public void commit() {
        String[] originalDirty = dirty.toArray(new String[dirty.size()]);

        for (String cookie : originalDirty) {
            Attribute attribute = attributes.get(cookie);
            ConfigEntry configEntry = attribute.getConfigEntry();
            Properties properties = attribute.getProperties();
            encodeCookie(configEntry, properties);
        }
    }

    private void acquireCookies() {
        Cookie[] cookies = this.getRequest().getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                this.cookies.put(cookie.getName(), cookie.getValue());
            }
        }

    }


    @Override
    public void init(CommonSession session) {
        this.session = session;
        this.attributes = new ConcurrentHashMap<String, Attribute>();
        this.cookies = new ConcurrentHashMap<String, String>();
        this.dirty = new ConcurrentHashSet();
        acquireCookies();
    }

    @Override
    public SessionStoreType getStoreType() {
        return SessionStoreType.COOKIE_STORE;
    }

    @Override
    public void clear() {

    }

    @Override
    public String getSessionID() {
        return null;
    }


    @Override
    public boolean setCustomAttribute(String key, String value) {
        return false;
    }

    @Override
    public String getCustomAttribute(String key) {
        return null;
    }
}
