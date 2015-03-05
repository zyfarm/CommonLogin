package com.myelin.future.db.mybatis.dao;


import com.myelin.future.db.mybatis.dataobj.QuestionVO;
import com.myelin.future.db.mybatis.dataobj.UserCustomPost;

import java.util.HashMap;
import java.util.List;

/**
 * Created by gabriel on 15-2-9.
 */
public interface UserPostCustomMapper {

    /**
     * 根据条件返回用户post信息
     *
     * @param condition
     * @return
     */
    public List<UserCustomPost> selectUserPostByCondition(HashMap<Object, Object> condition);


    /**
     * @param condition
     * @return
     */
    public int selectUserPostByConditionCount(HashMap<Object, Object> condition);


    /**
     * @param condition
     * @return
     */
    public List<QuestionVO> selectUserPostRelationByCondition(HashMap<Object, Object> condition);

}



