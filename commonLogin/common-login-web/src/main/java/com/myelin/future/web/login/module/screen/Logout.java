package com.myelin.future.web.login.module.screen;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.myelin.future.common.misc.CommonCookie;
import com.myelin.future.session.store.RedisPoolMgr;
import com.myelin.future.utils.WebProperties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by gabriel on 14-12-18.
 */

/**
 * 用户主动登出
 */
public class Logout {


    private void setCookie(HttpServletResponse response, String key, String val, String domain, String path, int age, boolean isHttpOnly) {
        CommonCookie cookie = new CommonCookie(key, val);
        cookie.setHttpOnly(true);
        cookie.setDomain(domain);
        cookie.setMaxAge(age);
        cookie.setPath(path);
        response.addCookie(cookie);
    }


    public void execute(@Param("redirectUrl") String redirectUrl, Context context, HttpSession session, HttpServletRequest request, HttpServletResponse response, Navigator nav) {
        Cookie[] cookies = request.getCookies();
        String ck2 = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase("ck2")) {
                ck2 = cookie.getValue();
                break;
            }
        }

        if (ck2 != null) {
            RedisPoolMgr redisPoolMgr = RedisPoolMgr.getInstance();
            redisPoolMgr.delValue(ck2);
            redisPoolMgr.delValue("csid-" + ck2);


            //清cookie
            String cookieDomain = WebProperties.props.get("cookie.domain");
            setCookie(response, "ck2", null, cookieDomain, "/", -1, false);
            setCookie(response, "an", null, cookieDomain, "/", -1, false);
            setCookie(response, "lg", null, cookieDomain, "/", -1, false);
            setCookie(response, "sg", null, cookieDomain, "/", -1, false);
            setCookie(response, "lvc", null, cookieDomain, "/", -1, false);
            setCookie(response, "c_token", null, cookieDomain, "/", -1, false);


            nav.redirectToLocation(redirectUrl);
        }
    }
}