package com.myelin.future.common.misc;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gabriel on 14-12-20.
 */
public class DateUtils {
    public static String STATIC_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String translateDate(Date date) {
        return new SimpleDateFormat(STATIC_FORMAT).format(date);
    }

    public static Date translateString(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(STATIC_FORMAT);
            return sdf.parse(date);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static void main(String args[]) {
        System.out.println(DateUtils.translateDate(new Date()));
    }

}
