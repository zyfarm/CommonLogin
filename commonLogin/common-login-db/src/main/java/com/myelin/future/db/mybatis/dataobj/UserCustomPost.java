package com.myelin.future.db.mybatis.dataobj;

import java.util.Date;

/**
 * Created by gabriel on 15-2-9.
 */
public class UserCustomPost {
    public Long postId;
    public Integer postType;
    public Long upPoint;
    public Long downPoint;
    public Date ctime;
    public Date utime;
    public Integer parentId;
    public Integer isHidden;
    public Integer isCryptonym;
    public String postContent;
    public String title;
    public Integer favoriteNum;
    public Integer commentNum;
    public String nick;
    public String name;
    public String userDesc;
    public String userId;
    public String userImg;
    public String lastEvent;

    public String getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Integer getPostType() {
        return postType;
    }

    public void setPostType(Integer postType) {
        this.postType = postType;
    }

    public Long getUpPoint() {
        return upPoint;
    }

    public void setUpPoint(Long upPoint) {
        this.upPoint = upPoint;
    }

    public Long getDownPoint() {
        return downPoint;
    }

    public void setDownPoint(Long downPoint) {
        this.downPoint = downPoint;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getUtime() {
        return utime;
    }

    public void setUtime(Date utime) {
        this.utime = utime;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Integer isHidden) {
        this.isHidden = isHidden;
    }

    public Integer getIsCryptonym() {
        return isCryptonym;
    }

    public void setIsCryptonym(Integer isCryptonym) {
        this.isCryptonym = isCryptonym;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getFavoriteNum() {
        return favoriteNum;
    }

    public void setFavoriteNum(Integer favoriteNum) {
        this.favoriteNum = favoriteNum;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
}

