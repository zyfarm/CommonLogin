package com.myelin.future.session.config;

import java.util.Properties;

/**
 * Created by gabriel on 14-8-7.
 */
public class CommonSessionConfig {
    public static String COOKIE_EXPIRED_TIME = "cookie.expiredTime";
    public static String BACKEND_EXPIRED_TIME = "backend.expiredTime";
    public static String BACKEND_VISITOR_EXPIRED_TIME = "backend.visitor.expiredTime";
    public static String DISASTER_SWITCH = "disaster.switch";
    public static String SESSIONID_CHECK_SWITCH = "sessionIdCheckSwitch";
    public static String SESSION_ID_LOG_SWITCH = "sessionIdLogSwitch";
    public static String SSL_VERIFY = "sslVerify";
    public static String SESSION_DEBUG = "sessionDebug";
    public static String COOKIE_VERIFY_SWITCH = "cookieVerifySwitch";
    public static String CROSS_DOMAIN_ALLOW = "crossDomainAllow";


    public static int cookieExpiredTime = 60;/*cookie过期时间*/
    private static int backendExpiredTime = 900;/*后端存储过期时间*/
    private static int backendVisitorExpiredTime; /*后端存储访客过期时间*/
    private static boolean disasterSwitch;/*灾备开关*/
    private static boolean sessionIdCheckSwitch;/*session检测开关*/
    private static boolean sessionIdLogSwitch;/*session日志开关*/
    private static boolean sslVerify;/*ssl加密处理*/
    private static boolean sessionDebug;/*sessionDebug开关*/
    public static boolean cookieVerifySwitch = true; /*cookie 校验开关*/
    public volatile static boolean useLoginCheck = true;
    public volatile static boolean crossDomainAllow = false;

    public static boolean isUseLoginCheck() {
        return useLoginCheck;
    }

    public static void setUseLoginCheck(boolean useLoginCheck) {
        CommonSessionConfig.useLoginCheck = useLoginCheck;
    }

    public volatile static boolean needResponseBuffered = true;


    public int getBackendExpiredTime() {
        return backendExpiredTime;
    }

    public void setBackendExpiredTime(int backendExpiredTime) {
        this.backendExpiredTime = backendExpiredTime;
    }

    public int getBackendVisitorExpiredTime() {
        return backendVisitorExpiredTime;
    }

    public void setBackendVisitorExpiredTime(int backendVisitorExpiredTime) {
        this.backendVisitorExpiredTime = backendVisitorExpiredTime;
    }

    public int getCookieExpiredTime() {
        return cookieExpiredTime;

    }

    public void setCookieExpiredTime(int cookieExpiredTime) {
        this.cookieExpiredTime = cookieExpiredTime;
    }


    public boolean isDisasterSwitch() {
        return disasterSwitch;
    }

    public void setDisasterSwitch(boolean disasterSwitch) {
        this.disasterSwitch = disasterSwitch;
    }

    public boolean isSessionIdCheckSwitch() {
        return sessionIdCheckSwitch;
    }

    public void setSessionIdCheckSwitch(boolean sessionIdCheckSwitch) {
        this.sessionIdCheckSwitch = sessionIdCheckSwitch;
    }

    public boolean isSessionIdLogSwitch() {
        return sessionIdLogSwitch;
    }

    public void setSessionIdLogSwitch(boolean sessionIdLogSwitch) {
        this.sessionIdLogSwitch = sessionIdLogSwitch;
    }

    public boolean isSslVerify() {
        return sslVerify;
    }

    public void setSslVerify(boolean sslVerify) {
        this.sslVerify = sslVerify;
    }

    public boolean isSessionDebug() {
        return sessionDebug;
    }

    public void setSessionDebug(boolean sessionDebug) {
        this.sessionDebug = sessionDebug;
    }

    public static void updateSwitch(Properties properties) {
        String dsw = (String) properties.get(DISASTER_SWITCH);
        if (dsw != null) {
            disasterSwitch = Boolean.parseBoolean(dsw);
        }


        String sidlog = (String) properties.get(SESSION_ID_LOG_SWITCH);
        if (sidlog != null) {
            sessionIdLogSwitch = Boolean.parseBoolean(sidlog);
        }

        String sidCheck = (String) properties.get(SESSIONID_CHECK_SWITCH);
        if (sidCheck != null) {
            sessionIdCheckSwitch = Boolean.parseBoolean(sidCheck);
        }


        String ssl = (String) properties.get(SSL_VERIFY);
        if (ssl != null) {
            sslVerify = Boolean.parseBoolean(ssl);
        }

        String sd = (String) properties.get(SESSION_DEBUG);
        if (sd != null) {
            sessionDebug = Boolean.parseBoolean(sd);
        }
    }

    public static void updateSessionExpiredTime(Properties properties) {

        String ct = (String) properties.get(COOKIE_EXPIRED_TIME);
        if (ct != null) {
            cookieExpiredTime = Integer.parseInt(ct);
        }

        String bt = (String) properties.get(BACKEND_EXPIRED_TIME);
        if (bt != null) {
            backendExpiredTime = (Integer.parseInt(bt));
        }


        String bvt = (String) properties.get(BACKEND_VISITOR_EXPIRED_TIME);
        if (bvt != null) {
            backendVisitorExpiredTime = Integer.parseInt(bvt);
        }
    }

}
