package com.myelin.future.web.login.module.screen;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.myelin.future.exception.NotSupportLoginTypeException;
import com.myelin.future.server.adapter.LoginAdapter;
import com.myelin.future.utils.WebProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;


@Service
public class Login {
    @Autowired
    Map<String, LoginAdapter> adapter;

    private final static Logger logger = LoggerFactory.getLogger(Login.class);


    public void execute(@Param("redirectUrl") String redirectUrl, @Param("type") String loginType, Context context, HttpSession session, Navigator nav) {
        String baseId = (String) session.getAttribute("baseId");
        if (baseId != null) {
            nav.redirectToLocation(redirectUrl);
            return;
        }

        String[] loginTypeArray = WebProperties.props.get("currentLoginAdapter").split(",");
        if (loginType == null || StringUtils.isBlank(loginType)) {
            nav.redirectToLocation(adapter.get(loginTypeArray[0] + "LoginAdapter").proxyPass(redirectUrl));
            return;
        }

        boolean isActive = false;
        for (String loginTypeItem : loginTypeArray) {
            if (loginType.contains(loginTypeItem)) {
                isActive = true;
            }
        }

        if (!isActive) {
            throw new NotSupportLoginTypeException("没有激活的登录类型");
        }


        /**
         * 做为登录的统一入口,适配各种不同的登录机制
         */
        if (StringUtils.isNotBlank(adapter.get(loginType).proxyPass(redirectUrl))) {
            nav.redirectToLocation(adapter.get(loginType).proxyPass(redirectUrl));
            return;
        }
        ;
    }
}
