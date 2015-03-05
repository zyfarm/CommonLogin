package com.myelin.future.common.misc;


import org.apache.commons.lang3.StringUtils;

/**
 * Created by gabriel on 14-12-26.
 */
public class StringEscapeUtil {
    public static String escapeSql(String str) {
        str = StringUtils.replace(str, "\"", "");
        str = StringUtils.replace(str, "'", "");
        str = StringUtils.replace(str, "\\", "");
        str = StringUtils.replace(str, "/", "");
        str = StringUtils.replace(str, ">", "");
        str = StringUtils.replace(str, "<", "");
        return StringUtils.replace(str, "&", "");
    }
}
