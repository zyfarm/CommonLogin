package com.myelin.future.session.common;


import com.alibaba.fastjson.JSON;
import com.myelin.future.session.config.ConfigEntry;
import com.myelin.future.session.config.SessionKeyConstants;
import com.myelin.future.session.store.SessionStoreType;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gabriel on 14-8-25.
 */
public class SessionUtils {

    private static Pattern cookiePattern = Pattern.compile("^[a-z0-9A-Z]+$");

    public static boolean verifyCookies(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }

        Matcher matcher = cookiePattern.matcher(value);
        return matcher.find();
    }

    public static String generateSecritySignature(CommonSession session) {
        String accountName = (String) session.getAttribute(SessionKeyConstants.ATTRIBUTE_ACCOUNT_NAME);
        String userID = (String) session.getAttribute(SessionKeyConstants.ATTRIBUTE_USER_ID);
        if (!StringUtils.isBlank(accountName) || !StringUtils.isBlank(userID)) {
            char nk = accountName.charAt(accountName.length() - 1);
            char uid1 = userID.charAt(0);
            char uid2 = userID.charAt(userID.length() - 1);
            return new StringBuffer().append(nk).append(uid1).append(uid2).toString();
        }
        return null;
    }

    /**
     * 序列化为json
     */
    public static String serilizeToJson(Map<String, Object> data) {
        return JSON.toJSONString(data);
    }

    public static String serilizeToJsonObject(Map<Object, Object> data) {
        return JSON.toJSONString(data);
    }


    public static void signatureCheck(CommonSession session) {
        if (isLogin(session)) {
            ConfigEntry configEntry = session.getSessionConfig().getConfigEntry(SessionKeyConstants.ATTRIBUTE_SIGNATURE);
            String sgFromCookie = (String) session.getSessionStore(SessionStoreType.COOKIE_STORE).getAttribute(configEntry, session.getSessionConfig().getProperties());

            /**
             * 无法获取cookie中的签名
             */
            if (StringUtils.isBlank(sgFromCookie)) {
                return;
            }


            /**
             * cookie签名校验
             */
            if (sgFromCookie.length() == 3) {
                String signature = generateSecritySignature(session);
                if (!signature.equals(sgFromCookie)) {
                    //session.setSignatureStatus(false);
                    /*这里应该让这个cookie失效*/
                    session.invalidate();
                }
            }

            /**
             * session校驗ccookie
             */
            ConfigEntry anConfigEntry = session.getSessionConfig().getConfigEntry(SessionKeyConstants.ATTRIBUTE_ACCOUNT_NAME);
            String anCookie = (String) session.getSessionStore(SessionStoreType.COOKIE_STORE).getAttribute(anConfigEntry, session.getSessionConfig().getProperties());
            String anSession = (String) session.getAttribute("name");
            if (anCookie != null && anSession != null && !anCookie.equals(anSession)) {
                session.invalidate();
            }
        }
    }


    public static boolean isLogin(CommonSession session) {
        return session != null && "true".equals(session.getAttribute(SessionKeyConstants.ATTRIBUTE_LOGIN));
    }


    public static boolean isVisitor(HttpSession session) {
        String userIdNum = (String) session.getAttribute(SessionKeyConstants.ATTRIBUTE_USER_ID_NUM);
        String trackId = (String) session.getAttribute(SessionKeyConstants.ATTRIBUTE_TRACK_ID);
        return StringUtils.isBlank(userIdNum) && StringUtils.isNotBlank(trackId);
    }

    public static void updateRedisExpired(CommonSession session) {
        try {
            if (session.isUseRedisStore()) {
                if (isLogin(session)) {
                    session.setExpiredTimeChanged(false);
                    String lastTime = (String) session.getAttribute("redisLastUpdateTime");
                    long redislastUpdatetime;
                    long systemCurrentTime = System.currentTimeMillis() / 1000;
                    if (StringUtils.isNotBlank(lastTime)) {
                        try {
                            redislastUpdatetime = Integer.parseInt(lastTime);
                        } catch (NumberFormatException e1) {
                            redislastUpdatetime = 0;
                        }
                        if ((systemCurrentTime - redislastUpdatetime) >= session.getUpdateTairInterval()) {
                            session.setExpiredTimeChanged(true);
                            session.setAttribute("redisLastUpdateTime", Long.toString(systemCurrentTime));
                        }
                    } else {
                        session.setAttribute("redisLastUpdateTime", Long.toString(systemCurrentTime));
                    }
                }
            }
        } catch (Exception ex) {

        }
    }


}
