package com.myelin.future.web.workbench.module.screen;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.myelin.future.utils.WebProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by gabriel on 15-2-7.
 */
@Service
public class Plan {

    @Autowired
    HttpServletRequest request;

    public void execute(Context context, HttpSession session, Navigator nav) {
        String userId = (String) session.getAttribute("userId");
        if (StringUtils.isBlank(userId)) {
            StringBuffer sb = new StringBuffer();
            sb.append("?");
            sb.append("redirectUrl=");
            sb.append(request.getRequestURL());
            nav.redirectToLocation(WebProperties.props.get("loginAddr") + sb.toString());
        }

    }
}
