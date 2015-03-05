/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.myelin.future.common.retobj;

import java.io.Serializable;

/**
 * Created by gabriel on 14-8-2.
 */
public class RetValue implements Serializable {

    private static final long serialVersionUID = -4170935720189438567L;

    public int value;
    public String msg;
    public Object obj;

    public RetValue() {
        value = 0;
        msg = "success";
        obj = null;
    }


    public RetValue(int val, String info, Object data) {
        value = val;
        msg = info;
        obj = data;
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
