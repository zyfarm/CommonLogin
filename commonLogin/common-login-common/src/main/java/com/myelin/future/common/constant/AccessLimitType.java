/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.myelin.future.common.constant;

/**
 * Created by gabriel on 14-8-4.
 */
public enum AccessLimitType {
    COUNT_LIMIT_TYPE(0),
    RESPONSE_TIME_LIMIT_TYPE(1),
    COUNT_AND_RESPONSE_TIME_LIMIT_TYPE(2)
    ;
    private final int type;

    public int getType() {
        return type;
    }

    public AccessLimitType getLimitType(final int type){
        for(AccessLimitType v:values()){
            if(v.getType()==type){
                return v;
            }
        }
        return null;
    }

    AccessLimitType(int type) {
        this.type = type;
    }
}
