package com.myelin.future.web.home.module.screen;


import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.myelin.future.server.adapter.LoginAdapter;
import com.myelin.future.utils.WebProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;


@Service
public class Index {
    @Autowired
    Map<String, LoginAdapter> adapter;

    public void execute(Context context, HttpSession session, Navigator nav) {
        String[] loginTypeArray = WebProperties.props.get("currentLoginAdapter").split(",");
        nav.redirectToLocation(adapter.get(loginTypeArray[0] + "LoginAdapter").proxyPass(WebProperties.props.get("defaultRedirectUrl")));
        return;
    }
}
