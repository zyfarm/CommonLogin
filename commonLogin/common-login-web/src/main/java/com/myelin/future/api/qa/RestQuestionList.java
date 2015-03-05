package com.myelin.future.api.qa;

import com.myelin.future.db.mybatis.dao.UserPostCustomMapper;
import com.myelin.future.db.mybatis.dataobj.UserCustomPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

/**
 * Created by gabriel on 15-2-9.
 */
@Controller
public class RestQuestionList {
    @Autowired
    UserPostCustomMapper mapper;


    @RequestMapping(value = "qa/question_list", method = RequestMethod.POST)
    public
    @ResponseBody
    List<UserCustomPost> fetchQuestionList(@RequestParam(value = "offset", required = false) String offset) {
        HashMap<Object, Object> params = new HashMap<Object, Object>();
        params.put("pageNum", null);
        params.put("pageSize", null);
        params.put("postType", 1);

        return null;
    }

}
