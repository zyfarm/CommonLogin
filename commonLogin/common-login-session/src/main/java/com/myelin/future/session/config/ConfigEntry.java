/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.myelin.future.session.config;

/**
 * Created by gabriel on 14-8-4.
 */
public class ConfigEntry {

    /**
     * 实际用于存储的key
     */
    private volatile String key;

    /**
     * 显示给用户看的key
     */
    private volatile String nickKey;


    /**
     * 存储类型,cookie,redis,db
     */
    private volatile String storeType;

    /**
     * 是否需要加密
     */
    private volatile boolean isEnCrypt;

    /**
     * 是否做base64编码
     */
    private volatile boolean isBase64;

    /**
     * 是否需要做转义
     */
    private volatile boolean isEscape;

    /**
     * 是否需要组合压缩
     */
    private volatile boolean isCompress;


    /**
     * 压缩后的组合key
     */
    private volatile String compressKey;


    /**
     * 存储周期
     */
    private volatile int lifeCycle = -1;

    /**
     * cookie起作用的域
     */
    private volatile String domain;

    /**
     * cookie的路径
     */
    private volatile String path = "/";

    /**
     * 是否开启http only属性
     */
    private volatile boolean isHttpOnly;

    /**
     * 是否开启只读模式
     */
    private volatile boolean isReadOnly;

    /**
     * 模式匹配方式
     */
    private volatile int patternType = 0;


    private volatile String patternKey;

    public String getPatternKey() {
        return patternKey;
    }

    public void setPatternKey(String patternKey) {
        this.patternKey = patternKey;
    }

    /**
     * 是否独立存储
     */
    private volatile boolean isStoreAlone = false;

    /**
     * 是否安全
     */
    private volatile boolean secure = false;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigEntry that = (ConfigEntry) o;

        if (compressKey != that.compressKey) return false;
        if (isBase64 != that.isBase64) return false;
        if (isCompress != that.isCompress) return false;
        if (isEnCrypt != that.isEnCrypt) return false;
        if (isEscape != that.isEscape) return false;
        if (isHttpOnly != that.isHttpOnly) return false;
        if (isReadOnly != that.isReadOnly) return false;
        if (isStoreAlone != that.isStoreAlone) return false;
        if (lifeCycle != that.lifeCycle) return false;
        if (patternType != that.patternType) return false;
        if (secure != that.secure) return false;
        if (domain != null ? !domain.equals(that.domain) : that.domain != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (nickKey != null ? !nickKey.equals(that.nickKey) : that.nickKey != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (storeType != null ? !storeType.equals(that.storeType) : that.storeType != null) return false;

        return true;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNickKey() {
        return nickKey;
    }

    public void setNickKey(String nickKey) {
        this.nickKey = nickKey;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public boolean isEnCrypt() {
        return isEnCrypt;
    }

    public void setisEnCrypt(boolean isEnCrypt) {
        this.isEnCrypt = isEnCrypt;
    }

    public boolean isBase64() {
        return isBase64;
    }

    public void setisBase64(boolean isBase64) {
        this.isBase64 = isBase64;
    }

    public boolean isEscape() {
        return isEscape;
    }

    public void setisEscape(boolean isEscape) {
        this.isEscape = isEscape;
    }

    public boolean isCompress() {
        return isCompress;
    }

    public void setisCompress(boolean isCompress) {
        this.isCompress = isCompress;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (nickKey != null ? nickKey.hashCode() : 0);
        result = 31 * result + (storeType != null ? storeType.hashCode() : 0);
        result = 31 * result + (isEnCrypt ? 1 : 0);
        result = 31 * result + (isBase64 ? 1 : 0);
        result = 31 * result + (isEscape ? 1 : 0);
        result = 31 * result + (isCompress ? 1 : 0);
        result = 31 * result + (compressKey != null ? compressKey.hashCode() : 0);
        result = 31 * result + lifeCycle;
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (isHttpOnly ? 1 : 0);
        result = 31 * result + (isReadOnly ? 1 : 0);
        result = 31 * result + patternType;
        result = 31 * result + (isStoreAlone ? 1 : 0);
        result = 31 * result + (secure ? 1 : 0);
        return result;
    }

    public String getCompressKey() {

        return compressKey;
    }

    public void setCompressKey(String compressKey) {
        this.compressKey = compressKey;
    }

    public int getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(int lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isHttpOnly() {
        return isHttpOnly;
    }

    public void setisHttpOnly(boolean isHttpOnly) {
        this.isHttpOnly = isHttpOnly;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setisReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public int getPatternType() {
        return patternType;
    }

    public void setPatternType(int patternType) {
        this.patternType = patternType;
    }

    public boolean isStoreAlone() {
        return isStoreAlone;
    }

    public void setisStoreAlone(boolean isStoreAlone) {
        this.isStoreAlone = isStoreAlone;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }
}
