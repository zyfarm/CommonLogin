/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.myelin.future.common.constant;

/**
 * Created by gabriel on 14-8-5.
 */
public class AccessLimitConfig {

    /**
     * 资源消耗级别
     */
    public static int SEMOPHORE_LOW_LEVEL=10;
    public static int SEMOPHORE_MID_LEVEL=60;
    public static int SEMOPHORE_HIGH_LEVEL=200;

    /**
     * 平均计数大小阈值
     */
    public static int AVG_COUNT_TIMES_THRESHHOLD=50;



    /**
     * 如果请求的时间超过这个阈值,说明请求本身有问题,不是由于并发造成的
     */
    public static int DROP_REQUEST_TIME_THRESHHOLD=5000;






}
