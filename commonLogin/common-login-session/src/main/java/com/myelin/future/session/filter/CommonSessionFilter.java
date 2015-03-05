/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.myelin.future.session.filter;


import com.alibaba.fastjson.JSONObject;
import com.myelin.future.common.constant.CommonConstant;
import com.myelin.future.common.retobj.ResultData;
import com.myelin.future.monitor.recorder.DefaultRecorder;
import com.myelin.future.session.common.CommonServletRequest;
import com.myelin.future.session.common.CommonServletResponse;
import com.myelin.future.session.common.CommonSession;
import com.myelin.future.session.common.SessionUtils;
import com.myelin.future.session.config.AbstractSessonConfig;
import com.myelin.future.session.config.CommonSessionConfig;
import com.myelin.future.session.config.SessionConfig;
import com.myelin.future.session.store.CommonCookieStore;
import com.myelin.future.session.store.CommonRedisStore;
import com.myelin.future.session.store.SessionStore;
import com.myelin.future.session.store.SessionStoreFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by gabriel on 14-8-3.
 */
public class CommonSessionFilter implements Filter {
    protected SessionConfig sessionConfig;
    protected FilterConfig filterConfig;
    protected SessionStoreFactory sessionStoreFactory;
    protected boolean isAutoTransfer = false;
    protected String autoLoginAddressUrl = "";
    List<String> exclusiveUrl = new ArrayList<String>();
    List<Pattern> patterns = new ArrayList<Pattern>();

    //private DefaultRecorder recorder;


    static final Class<? extends SessionStore>[] DEFAULT_STORE_CLASSES = new Class[]{CommonCookieStore.class, CommonRedisStore.class};


    protected void initSessionStoreFactory() {
        Map<String, Class<? extends SessionStore>> storeTypeMap =
                new HashMap<String, Class<? extends SessionStore>>();
        for (Class<? extends SessionStore> storeItem : DEFAULT_STORE_CLASSES) {
            String className = storeItem.getSimpleName();
            String storeName = StringUtils.substringBeforeLast(className, "Store");
            storeName = StringUtils.substringAfterLast(storeName, "Common").toLowerCase();
            storeTypeMap.put(storeName, storeItem);
        }

        sessionStoreFactory = new SessionStoreFactory();
        sessionStoreFactory.setStoreTypeMap(storeTypeMap);
    }


    protected void initSessionConfig(FilterConfig filterConfig) throws Exception {
        sessionConfig = new AbstractSessonConfig();
        sessionConfig.init(filterConfig);
    }

    private boolean isExclusiveUrl(String targetUrl) {
        if (patterns != null && patterns.size() != 0) {
            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(targetUrl);
                if (matcher.find()) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getFullPathString(HttpServletRequest request) {
        try {
            if (request.getQueryString() != null) {
                return URLEncoder.encode(request.getRequestURL() + "?" + request.getQueryString(), "UTF-8").toString();
            } else {
                return URLEncoder.encode(request.getRequestURL().toString(), "UTF-8").toString();
            }
        } catch (UnsupportedEncodingException e) {
            return URLEncoder.encode(request.getRequestURL() + "?" + request.getQueryString()).toString();
        }
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        try {
            this.filterConfig = filterConfig;
            //this.recorder = DefaultRecorder.getInstance();
            String isOpen = getParameter("isOpen");
            if (StringUtils.isBlank(isOpen) || isOpen.equals("false")) {
                return;
            }

            String urlExclusion = getParameter("urlExclusion");//filterConfig.getInitParameter("urlExclusion");
            if (urlExclusion != null) {
                String[] urlArray = urlExclusion.split(";");
                exclusiveUrl = Arrays.asList(urlArray);

                for (String item : exclusiveUrl) {
                    Pattern pattern = Pattern.compile(item);
                    patterns.add(pattern);
                }
            }

            //filterConfig.getServletContext().get


            this.isAutoTransfer = Boolean.parseBoolean(getParameter("isAutoTransfer"));//Boolean.parseBoolean(filterConfig.getInitParameter("isAutoTransfer"));
            if (this.isAutoTransfer) {
                this.autoLoginAddressUrl = getParameter("transferLoginUrl");//filterConfig.getInitParameter("transferLoginUrl");
            }

            initSessionConfig(filterConfig);
            initSessionStoreFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected CommonSession createSession(Map<String, SessionStore> storeMap, CommonServletRequest request,
                                          CommonServletResponse response, FilterConfig filterConfig, SessionConfig sessionConfig) {
        return new CommonSession(response, request, filterConfig.getServletContext(), sessionConfig, storeMap);
    }


    protected CommonSession createCommonSession(CommonServletRequest request, CommonServletResponse response) {
        CommonSession commonSession;
        Map<String, SessionStore> storeMap = this.sessionStoreFactory.createStoreMap();
        commonSession = this.createSession(storeMap, request, response, filterConfig, sessionConfig);
        commonSession.setSessionEnv(sessionConfig.getBackendEnv());
        commonSession.init();
        return commonSession;
    }


    private String acquireUserSessionInfo(CommonSession session, char seperator) {
        String baseId = (String) session.getAttribute("baseId");
        String nick = (String) session.getAttribute("nick");
        String accountName = (String) session.getAttribute("accountName");
        String sessionKey = session.getId();
        ArrayList<String> infoArray = new ArrayList<String>();
        infoArray.add(baseId);
        infoArray.add(nick);
        infoArray.add(accountName);
        infoArray.add(sessionKey);

        return StringUtils.join(infoArray.toArray(), seperator);
    }

    /**
     * session有效期校验
     */
    private void sessionExpiredCheck(CommonSession session) {
        if (SessionUtils.isLogin(session)) {//只有登录后才校验
            long currentTime = System.currentTimeMillis() / 1000;
            String lastVisitTime = (String) session.getAttribute("lastVisitCookie");

            if (lastVisitTime == null) {
                return;
            }

            Long lastVTime = Long.parseLong(lastVisitTime);

            if (lastVTime != null) {
                if (SessionUtils.isLogin(session)) {
                    boolean isExipred = (currentTime - lastVTime) > CommonSessionConfig.cookieExpiredTime ? true : false;
                    if (isExipred) {//session超时,记录用户的信息
                        DefaultRecorder.getInstance().backendRecord("[SESSION-EXPIRED]" + acquireUserSessionInfo(session, '|'), CommonConstant.LOGGER_ERROR_LEVEL);
                        session.invalidate();
                    } else {
                        if (currentTime >= lastVTime) {
                            session.setAttribute("lastVisitCookie", currentTime);
                        } else {
                            DefaultRecorder.getInstance().backendRecord("[SESSION-ERROR]" + acquireUserSessionInfo(session, '|'), CommonConstant.LOGGER_ERROR_LEVEL);
                            //session.invalidate();
                        }
                    }
                }
            }
        }
    }

    private boolean isStaticResource(CommonServletRequest request) {
        return false;
    }


    protected String getParameter(String s) {
        String ret = filterConfig.getInitParameter(s);
        if (StringUtils.isBlank(ret)) {
            return System.getProperty(s);
        }

        return ret;
    }


    protected String getParameter(FilterConfig filterConfig, String key) {
        return filterConfig.getInitParameter(key);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (getParameter("isOpen").equals("false")) {
            chain.doFilter(request, response);
        } else {
            CommonServletRequest commRequest;
            CommonServletResponse commResponse;
            CommonSession commonSession;
            commRequest = new CommonServletRequest((HttpServletRequest) request);
            commResponse = new CommonServletResponse((HttpServletResponse) response);

            HttpServletRequest servletRequest = (HttpServletRequest) request;
            if (isStaticResource(commRequest) || isExclusiveUrl(servletRequest.getRequestURL().toString())) {
                chain.doFilter(request, response);
                return;
            }
            commonSession = createCommonSession(commRequest, commResponse);
            commRequest.setSession(commonSession);
            commResponse.setSession(commonSession);
            DefaultRecorder.getInstance().putRequest((HttpServletRequest) request);
             /*判断是否是ajax请求*/
            if (this.isAutoTransfer && ((HttpServletRequest) request).getHeader("X-Requested-With") != null && ((HttpServletRequest) request).getHeader("X-Requested-With").equals("XMLHttpRequest")) {
                if (!SessionUtils.isLogin(commonSession)) { //如果用户没有登录态
                    ResultData code = new ResultData();
                    code.setData(CommonConstant.SessionErrorInfo.SESSION_EXPIRE_MSG);
                    code.setErrCode(CommonConstant.SessionErrorInfo.SESSION_EXPIRE);
                    code.setErrMsg(CommonConstant.SessionErrorInfo.SESSION_EXPIRE_MSG);
                    String data = JSONObject.toJSONString(code);
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("application/json; charset=utf-8");
                    response.getOutputStream().write(data.getBytes("UTF-8"));
                    return;
                }
            }


            if (isAutoTransfer && !SessionUtils.isLogin(commonSession)) { //如果自动跳转打开且用户是未登录状态
                StringBuilder sbuf = new StringBuilder();
                sbuf.append(this.autoLoginAddressUrl);
                sbuf.append("&");
                sbuf.append("redirectUrl=");
                sbuf.append(getFullPathString((HttpServletRequest) request));
                ((HttpServletResponse) response).sendRedirect(sbuf.toString());
                return;
            }

            sessionExpiredCheck(commonSession);

            /**
             * 更新redis超时时间
             */
            SessionUtils.updateRedisExpired(commonSession);

            if (CommonSessionConfig.isUseLoginCheck()) {
                SessionUtils.signatureCheck(commonSession);
            }


            try {
                chain.doFilter(commRequest, commResponse);
                commonSession = commRequest.getSession();
                if (null != commonSession) {
                    commonSession.commit();
                }
            } catch (Exception e) {
                DefaultRecorder.getInstance().errorRecord(e, CommonConstant.LOGGER_ERROR_LEVEL);
            } finally {
                commResponse.commitBuffer();
            }
        }
    }


    @Override
    public void destroy() {

    }
}
