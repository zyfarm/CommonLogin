package com.myelin.future.server.adapter;


import com.myelin.future.db.mybatis.dataobj.UserProfile;

import java.util.HashMap;

/**
 * Created by gabriel on 14-11-27.
 */
public interface LoginAdapter<T> {

    /**
     * 如果有跳转可以直接跳转
     */
    public String proxyPass(String redirectUrl);


    /**
     * 获得第三方用户信息
     */
    public UserProfile getProxyUserInfo(HashMap<T, T> params);


    /**
     * 同步第三方信息
     */
    public UserProfile syncProxyUserInfo(UserProfile baseUserInfo);


    /**
     * 标识当前登录方式
     */
    public String loginType();


    /**
     * 获取自定义session
     *
     * @return
     */
    public HashMap<String, String> getCustomSessionAttribute(UserProfile baseUserInfo);


    /**
     * 获取自定义cookie
     *
     * @return
     */
    public HashMap<String, String> getCustomCookieAttribute(UserProfile LobaseUserInfo);

}
