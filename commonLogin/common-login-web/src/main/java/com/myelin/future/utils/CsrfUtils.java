package com.myelin.future.utils;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by gabriel on 15-1-7.
 */
public class CsrfUtils {
    private static String SESSION_KEY = "cToken";
    private static String COOKIE_KEY = "c_token";


    public static boolean checkToken(HttpServletRequest request, HttpSession session) {
        /*首先校验request请求中是否有ctoken*/
        Cookie[] cookies = request.getCookies();
        boolean flag = false;
        if (cookies == null || cookies.length == 0) {
            return false;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_KEY)) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            return flag;
        }

        if (request.getMethod().equalsIgnoreCase("POST")) {
            String fromRequest = request.getParameter(COOKIE_KEY);
            String fromSession = (String) session.getAttribute(SESSION_KEY);
            if (fromRequest.equals(fromSession)) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }


    public static String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null || cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("c_token")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
