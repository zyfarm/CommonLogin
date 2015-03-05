package com.alibaba.base.tenant.sso.test.common;


import com.myelin.future.common.codec.EnDecrypts;
import com.myelin.future.session.common.CommonServletResponse;
import com.myelin.future.session.config.AbstractSessonConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.reflectionassert.ReflectionAssert;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by gabriel on 14-12-20.
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class CommonTest {
    public static HashMap<String, String> filter_configs = new HashMap<String, String>();

    static {
        filter_configs.put("configEnv", "daily");
        filter_configs.put("isOpen", "true");
        filter_configs.put("isAutoTransfer", "false");
        filter_configs.put("transferLoginUrl", "http://login.daily.base.com:7001/login/login.htm?type=bucLoginAdapter");
    }


    /**
     * 测试输入输出是否逻辑正确
     */
    @Test
    public void servletStreamTest() {
        HttpServletResponse response = new HttpServletResponse() {
            @Override
            public void addCookie(Cookie cookie) {

            }

            @Override
            public boolean containsHeader(String name) {
                return false;
            }

            @Override
            public String encodeURL(String url) {
                return null;
            }

            @Override
            public String encodeRedirectURL(String url) {
                return null;
            }

            @Override
            public String encodeUrl(String url) {
                return null;
            }

            @Override
            public String encodeRedirectUrl(String url) {
                return null;
            }

            @Override
            public void sendError(int sc, String msg) throws IOException {

            }

            @Override
            public void sendError(int sc) throws IOException {

            }

            @Override
            public void sendRedirect(String location) throws IOException {

            }

            @Override
            public void setDateHeader(String name, long date) {

            }

            @Override
            public void addDateHeader(String name, long date) {

            }

            @Override
            public void setHeader(String name, String value) {

            }

            @Override
            public void addHeader(String name, String value) {

            }

            @Override
            public void setIntHeader(String name, int value) {

            }

            @Override
            public void addIntHeader(String name, int value) {

            }

            @Override
            public void setStatus(int sc) {

            }

            @Override
            public void setStatus(int sc, String sm) {

            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                return null;
            }

            @Override
            public PrintWriter getWriter() throws IOException {
                return null;
            }

            @Override
            public void setCharacterEncoding(String charset) {

            }

            @Override
            public void setContentLength(int len) {

            }

            @Override
            public void setContentType(String type) {

            }

            @Override
            public void setBufferSize(int size) {

            }

            @Override
            public int getBufferSize() {
                return 0;
            }

            @Override
            public void flushBuffer() throws IOException {

            }

            @Override
            public void resetBuffer() {

            }

            @Override
            public boolean isCommitted() {
                return false;
            }

            @Override
            public void reset() {

            }

            @Override
            public void setLocale(Locale loc) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }
        };

        try {
            CommonServletResponse commonServletResponse = new CommonServletResponse(response);
            ServletOutputStream outputStream = commonServletResponse.getOutputStream();
            outputStream.write("hello".getBytes());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void codecTest() throws Exception {
        ReflectionAssert.assertLenientEquals(EnDecrypts.getEnDecrypt(EnDecrypts.BASE_64).encrypt("zhengyu.jy"), "rO0ABXQACnpoZW5neXUuank=");
        ReflectionAssert.assertLenientEquals(EnDecrypts.getEnDecrypt(EnDecrypts.BLOW_FISH).encrypt("zhengyu.jy"), "+xdDPglDNf2HVA==");
    }

    @Test
    public void filterConfigTest() throws Exception {
        AbstractSessonConfig sessionConfig = new AbstractSessonConfig();
        FilterConfig filterConfig = new FilterConfig() {


            @Override
            public String getFilterName() {
                return "TestFilter";
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public String getInitParameter(String name) {
                return filter_configs.get(name);
            }

            @Override
            public Enumeration getInitParameterNames() {
                return null;
            }
        };

        sessionConfig.init(filterConfig);
        Properties expectedProps = new Properties();
        expectedProps.setProperty("backend.expiredTime", "4500");
        expectedProps.setProperty("backend.visitor.expiredTime", "2700");
        expectedProps.setProperty("cookie.expiredTime", "3600");
        expectedProps.setProperty("version", "0");
        ReflectionAssert.assertLenientEquals(expectedProps, sessionConfig.getProperties());

    }
}
