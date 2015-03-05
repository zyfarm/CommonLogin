package com.myelin.future.db.mybatis.dataobj;

import java.io.Serializable;

/**
 * Created by gabriel on 15-2-9.
 */
public class QuestionVO implements Serializable {


    private static final long serialVersionUID = -4765418157794785368L;

    private Long qid;
    private Integer qfavorite;
    private Long aid;
    private String title;
    private String userId;
    private String userImg;
    private String lastEvent;
    private String acontent;
    private String nick;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Long getQid() {
        return qid;
    }

    public void setQid(Long qid) {
        this.qid = qid;
    }

    public Integer getQfavorite() {
        return qfavorite;
    }

    public void setQfavorite(Integer qfavorite) {
        this.qfavorite = qfavorite;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
    }

    public String getAcontent() {
        return acontent;
    }

    public void setAcontent(String acontent) {
        this.acontent = acontent;
    }
}
