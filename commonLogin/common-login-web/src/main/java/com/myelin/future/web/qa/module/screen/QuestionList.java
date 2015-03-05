package com.myelin.future.web.qa.module.screen;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.myelin.future.db.mybatis.dao.UserPostCustomMapper;
import com.myelin.future.db.mybatis.dataobj.QuestionVO;
import com.myelin.future.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gabriel on 15-2-7.
 */
@Service
public class QuestionList {

    @Autowired
    HttpServletRequest request;

    @Autowired
    UserPostCustomMapper mapper;

    public void execute(Context context, HttpSession session, Navigator nav) {
        String userId = UrlUtils.commonHeaderProcess(session, context, request);

        HashMap<Object, Object> params = new HashMap<Object, Object>();
        params.put("postType", 1);
        params.put("pageNum", 0);
        params.put("pageSize", 20);
        params.put("postUserId", userId);
        List<QuestionVO> postList = mapper.selectUserPostRelationByCondition(params);
        context.put("postList", postList);
    }
}