package com.myelin.future.web.exist.module.screen;


import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.myelin.future.common.codec.EnDecrypts;
import com.myelin.future.common.codec.RsaEncryptor;
import com.myelin.future.session.common.CommonSession;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpSession;

/**
 * Created by gabriel on 14-11-27.
 */
public class LoginExist {


    public void execute(@Param("redirectUrl") String redirectUrl, Context context, HttpSession session, Navigator nav) {
        try {
            String module = EnDecrypts.getEnDecrypt(EnDecrypts.RSA).encrypt(RsaEncryptor.MODULE);
            String exponent = EnDecrypts.getEnDecrypt(EnDecrypts.RSA).encrypt(RsaEncryptor.EXPONENT);
            CommonSession coms = (CommonSession) session;
            String userId = (String) coms.getAttribute("userId");
            if (!StringUtils.isBlank(userId)) {
                nav.redirectToLocation(redirectUrl);
            } else {
                context.put("redirectUrl", redirectUrl);
                context.put("mod", module);
                context.put("exp", exponent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
