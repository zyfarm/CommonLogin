package com.myelin.future.web.login.module.screen;


import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.myelin.future.common.codec.EnDecrypts;
import com.myelin.future.common.codec.RsaEncryptor;
import org.springframework.stereotype.Service;


@Service
public class Registry {


    public void execute(Context context, Navigator nav) {
        try {
            String module = EnDecrypts.getEnDecrypt(EnDecrypts.RSA).encrypt(RsaEncryptor.MODULE);
            String exponent = EnDecrypts.getEnDecrypt(EnDecrypts.RSA).encrypt(RsaEncryptor.EXPONENT);
            context.put("mod", module);
            context.put("exp", exponent);
        } catch (Exception e) {

        }
    }
}
