package com.myelin.future.db.mybatis.dao;

import com.myelin.future.db.mybatis.dataobj.UserProfile;
import com.myelin.future.db.mybatis.dataobj.UserProfileExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserProfileMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table careme_user_profile
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    int countByExample(UserProfileExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table careme_user_profile
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    int deleteByExample(UserProfileExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table careme_user_profile
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table careme_user_profile
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    int insert(UserProfile record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table careme_user_profile
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    int insertSelective(UserProfile record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table careme_user_profile
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    List<UserProfile> selectByExample(UserProfileExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table careme_user_profile
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    UserProfile selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table careme_user_profile
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    int updateByExampleSelective(@Param("record") UserProfile record, @Param("example") UserProfileExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table careme_user_profile
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    int updateByExample(@Param("record") UserProfile record, @Param("example") UserProfileExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table careme_user_profile
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    int updateByPrimaryKeySelective(UserProfile record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table careme_user_profile
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    int updateByPrimaryKey(UserProfile record);
}