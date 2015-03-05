package com.myelin.future.web.webx.module.screen;

import com.alibaba.citrus.turbine.Context;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class Index {


    public void execute(Context context, HttpServletRequest request, HttpSession session) {
        Cookie[] cookies = request.getCookies();
        StringBuilder sb = new StringBuilder();
        sb.append("curl --cookie ");
        sb.append("\"");
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                sb.append(cookies[i].getName());
                sb.append("=");
                sb.append(cookies[i].getValue());
                if (i != cookies.length - 1) {
                    sb.append(";");
                }
            }
        }
        sb.append("\"");
        context.put("curl", sb.toString());
        context.put("userId", "baseId:" + session.getAttribute("baseId"));
    }

}
