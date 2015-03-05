/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */


package com.myelin.future.session.common;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Created by gabriel on 14-8-4.
 */
public class CommonServletRequest extends HttpServletRequestWrapper {

    private static final String[] IP_HEADERS = { "Proxy-Client-IP", "WL-Proxy-Client-IP", // 优先获取其他代理设置的真是用户ip
            "X-Real-IP", // Tengine设置remoteIP，如果没有拿到
            // NS-Client-IP，那么这就是真实的用户ip
            "X-Forwarded-For" };


    private CommonSession session;

    public CommonSession getSession() {
        return session;
    }

    public void setSession(CommonSession session) {
        this.session = session;
    }

    public CommonServletRequest(HttpServletRequest request) {
        super(request);
    }
    
    
    
}
