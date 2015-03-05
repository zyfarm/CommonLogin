package com.myelin.future.common.constant;


public class CommonConstant {

    public static String deskey = "Je/LHGEZ0OM";
    public static int TAOBAO_USER = 0;
    public static int AYUN_USER = 1;
    public static int BUC_USER = 2;
    public static String HOME_URL = ".caremesite.com";
    public static int SUCCESS = 0;
    public static Long MAX_ACCESS_EXPIRES = 3600 * 1000L;
    public static Long MAX_RE_ACCESS_EXPIRES = 3600 * 1000L * 10;
    public static Long MAX_CODE_EXPIRE = 60 * 10 * 1000L;
    public static Integer LOGGER_INFO_LEVEL = 1;
    public static Integer LOGGER_DEBUG_LEVEL = 2;
    public static Integer LOGGER_ERROR_LEVEL = 3;
    public static Integer LOGGER_WARN_LEVEL = 4;


    /*OAUTH 错误码*/
    public static class OAuthErrorInfo {
        public static int OAUTH_SYSTEM_EXCEPTION = -1;
        public static String OAUTH_SYSTEM_EXCEPTION_MSG = "系统授权错误";


        public static int OAUTH_PROBLEM_EXCEPTION = -2;
        public static String OAUTH_PROBLEM_EXCEPTION_MSG = "业务授权错误";


        public static int OAUTH_REFRESH_EXPIRES_EXCEPTION = -3;
        public static String OAUTH_REFRESH_EXPIRES_EXCEPTION_MSG = "刷新令牌超时错误";

        public static int OAUTH_EXPIRES_EXCEPTION = -4;
        public static String OAUTH_EXPIRES_EXCEPTION_MSG = "访问令牌超时错误";

        public static int OAUTH_CODE_EXPIRE_EXCEPTION = -5;
        public static String OAUTH_CODE_EXPIRE_EXCEPTION_MSG = "code码失效";
    }

    /*SESSION错误信息*/
    public static class SessionErrorInfo {
        public static int SESSION_ERROR_NOT_EXIST = -1;
        public static String SESSION_ERROR_NOT_EXIST_MSG = "不存在登录态";

        public static int SESSION_EXPIRE = -2;
        public static String SESSION_EXPIRE_MSG = "会话超时过期";
    }


    public static int OAUTH_INVALID_PARAMS = -1;
    public static String OAUTH_INVALID_PARAMS_MSG = "请求参数错误";


    public static int OAUTH_NOEXIST_APP = -2;
    public static String OAUTH_NOEXIST_APP_MSG = "不存在这个应用";


    public static int OAUTH_NOEXIST_SCOPE = -3;
    public static String OAUTH_NOEXIST_SCOPE_MSG = "不存在这个权限";


}
