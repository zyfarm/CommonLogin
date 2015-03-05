package com.myelin.future.utils;

import com.alibaba.citrus.turbine.Context;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by gabriel on 15-2-10.
 */
public class UrlUtils {
    public static String getFullUrlPath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (StringUtils.isBlank(queryString)) {
            return request.getRequestURL().toString();
        } else {
            return request.getRequestURL().toString() + "?" + queryString;
        }
    }


    public static String commonHeaderProcess(HttpSession session, Context context, HttpServletRequest request) {
        String userId = (String) session.getAttribute("userId");
        StringBuffer sb = new StringBuffer();
        sb.append("?");
        sb.append("redirectUrl=");
        sb.append(UrlUtils.getFullUrlPath(request));
        if (StringUtils.isBlank(userId)) {
            context.put("isLogin", false);
            context.put("loginUrl", WebProperties.props.get("loginAddr") + sb.toString());
        } else {
            context.put("logoutUrl", WebProperties.props.get("logoutAddr") + sb.toString());
            context.put("isLogin", true);
        }
        return userId;
    }
}
