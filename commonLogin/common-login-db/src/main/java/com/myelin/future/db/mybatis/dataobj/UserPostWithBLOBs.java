package com.myelin.future.db.mybatis.dataobj;

public class UserPostWithBLOBs extends UserPost {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column careme_post.post_content
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    private String postContent;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column careme_post.title
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    private String title;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column careme_post.last_event
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    private String lastEvent;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column careme_post.post_content
     *
     * @return the value of careme_post.post_content
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    public String getPostContent() {
        return postContent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column careme_post.post_content
     *
     * @param postContent the value for careme_post.post_content
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    public void setPostContent(String postContent) {
        this.postContent = postContent == null ? null : postContent.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column careme_post.title
     *
     * @return the value of careme_post.title
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column careme_post.title
     *
     * @param title the value for careme_post.title
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column careme_post.last_event
     *
     * @return the value of careme_post.last_event
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    public String getLastEvent() {
        return lastEvent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column careme_post.last_event
     *
     * @param lastEvent the value for careme_post.last_event
     *
     * @mbggenerated Mon Feb 09 17:44:49 CST 2015
     */
    public void setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent == null ? null : lastEvent.trim();
    }
}