package com.myelin.future.web.exist.module.action;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.FormGroup;
import com.myelin.future.common.codec.EnDecrypts;
import com.myelin.future.db.mybatis.dao.UserProfileMapper;
import com.myelin.future.db.mybatis.dataobj.UserProfile;
import com.myelin.future.db.mybatis.dataobj.UserProfileExample;
import com.myelin.future.web.exist.module.common.RegistryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Service
public class RegistryAction {

    @Autowired
    UserProfileMapper userProfileMapper;

    public void doRegistry(@FormGroup("RegistryForm") RegistryForm registryForm, HttpSession httpSession,
                           Context context, Navigator nav) {


        try {
            String userName = registryForm.getName();
            String passWord = EnDecrypts.getEnDecrypt(EnDecrypts.RSA).decrypt(registryForm.getPasswd());
            String desPW = EnDecrypts.getEnDecrypt(EnDecrypts.DES).encrypt(passWord);
            String nick = registryForm.getNick();

            UserProfileExample userProfileExample = new UserProfileExample();
            userProfileExample.or().andNameEqualTo(userName).andPasswordEqualTo(desPW);

            List<UserProfile> retList = userProfileMapper.selectByExample(userProfileExample);
            if (retList != null && retList.size() == 1) {
                context.put("errMsg", "已经注册过了");
            }

            UserProfile userProfile = new UserProfile();
            userProfile.setName(userName);
            userProfile.setPassword(desPW);
            userProfile.setCtime(new Date());
            userProfile.setNick(nick);
            userProfileMapper.insert(userProfile);

            /**
             * 种植cookie
             */

            context.put("errMsg", "注册成功!");
        } catch (Exception e) {
            e.printStackTrace();
            context.put("errMsg", "提交失败，请稍后重试");
            return;
        }
    }
}
