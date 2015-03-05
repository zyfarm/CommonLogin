package com.myelin.future.server.adapter;


import com.myelin.future.db.mybatis.dao.UserProfileMapper;
import com.myelin.future.db.mybatis.dataobj.UserProfile;
import com.myelin.future.db.mybatis.dataobj.UserProfileExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;


/**
 * Created by gabriel on 14-11-27.
 */
@Service
public class CommonLoginAdapter implements LoginAdapter<String> {

    @Autowired
    UserProfileMapper userProfileMapper;


    @Override
    public String proxyPass(String redirectUrl) {
        return "/exist/login_exist.htm?redirectUrl=" + redirectUrl;
    }

    @Override
    public UserProfile getProxyUserInfo(HashMap<String, String> params) {
        String userId = params.get("userId");


        UserProfileExample critria = new UserProfileExample();
        critria.or().andUserIdEqualTo(userId);

        List<UserProfile> retList = userProfileMapper.selectByExample(critria);
        if (retList == null || retList.size() == 0) {
            return null;
        } else {
            return retList.get(0);
        }
    }

    @Override
    public UserProfile syncProxyUserInfo(UserProfile baseUserInfo) {
        return baseUserInfo;
    }


    @Override
    public String loginType() {
        return "common";
    }

    @Override
    public HashMap<String, String> getCustomSessionAttribute(UserProfile baseUserInfo) {
        return null;
    }

    @Override
    public HashMap<String, String> getCustomCookieAttribute(UserProfile baseUserInfo) {
        return null;
    }
}
