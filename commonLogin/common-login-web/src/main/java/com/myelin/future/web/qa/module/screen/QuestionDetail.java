package com.myelin.future.web.qa.module.screen;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.myelin.future.db.mybatis.dao.UserPostMapper;
import com.myelin.future.db.mybatis.dataobj.UserPostExample;
import com.myelin.future.db.mybatis.dataobj.UserPostWithBLOBs;
import com.myelin.future.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by gabriel on 15-2-10.
 */
@Service
public class QuestionDetail {

    @Autowired
    HttpServletRequest request;

    @Autowired
    UserPostMapper userPostMapper;

    public void execute(@Param("qid") Long qid, Context context, HttpSession session, Navigator nav) {
        UrlUtils.commonHeaderProcess(session, context, request);

        UserPostExample condition = new UserPostExample();
        condition.or().andPostIdEqualTo(qid);

        List<UserPostWithBLOBs> retList = userPostMapper.selectByExampleWithBLOBs(condition);
        if (retList != null && retList.size() != 0) {
            context.put("question_detail", retList.get(0));
        } else {
            context.put("question_detail", null);
        }
    }
}
