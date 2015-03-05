package com.myelin.future.web.exist.module.action;


import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.FormGroup;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.myelin.future.db.mybatis.dao.UserProfileMapper;
import com.myelin.future.db.mybatis.dataobj.UserProfile;
import com.myelin.future.db.mybatis.dataobj.UserProfileExample;
import com.myelin.future.exception.AccountDisabledException;
import com.myelin.future.exception.SystemErrorException;
import com.myelin.future.server.adapter.LoginAdapter;
import com.myelin.future.server.adapter.LoginProxy;
import com.myelin.future.web.exist.module.common.UserPwForm;
import com.myelinji.site.careme.commo.lib.ApplicationContextBeanHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;


@Service
public class LoginCheckAction {

    @Autowired
    HttpServletResponse response;

    @Autowired
    UserProfileMapper mapper;

    @Autowired
    LoginProxy loginProxy;

    @Autowired
    ApplicationContextBeanHelper applicationContextBeanHelper;


    public void doCheck(@FormGroup("UserPwForm") UserPwForm userPwForm, @Param("redirectUrl") String redirectUrl, HttpSession session, Context context, Navigator nav) {
        String userName = userPwForm.getUserName();
        String passWord = userPwForm.getPassWord();

        if (StringUtils.isBlank(userName) || StringUtils.isBlank(passWord)) {
            context.put("errMsg", "用户名或密码不能为空");
            return;
        }

        UserProfileExample example = new UserProfileExample();
        example.or().andNameEqualTo(userName);

        List<UserProfile> retList = mapper.selectByExample(example);
        if (retList == null || retList.size() == 0) {
            context.put("errMsg", "不存在这个用户");
            return;
        }

        example.clear();
        example.or().andNameEqualTo(userName).andPasswordEqualTo(passWord);


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", retList.get(0).getUserId());


        try {
            LoginAdapter adapter = (LoginAdapter) applicationContextBeanHelper.getApplicationContext().getBean("commonLoginAdapter");
            UserProfile baseUserInfo = loginProxy.syncLoginInfo(adapter, session, response, params);
            if (baseUserInfo == null) {
                throw new SystemErrorException("系统错误");
            } else if (baseUserInfo.getIsDisabled().equals(1)) {
                throw new AccountDisabledException("帐号被禁用");
            } else if (baseUserInfo.getIsDeleted().equals(1)) {
                throw new AccountDisabledException("帐号被删除");
            }
            nav.redirectToLocation(redirectUrl);
        } catch (Exception e) {
            throw new SystemErrorException("系统错误");
        }
    }
}
