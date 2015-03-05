package com.myelin.future.session.config;

import java.util.ArrayList;
import java.util.List;


/**
 * 设置一些专门的session存储在客户端和服务端 Created by gabriel on 14-8-24.
 */
public class DoubleWriteConfigMgr {

    public static List<String> doubleWriteList = new ArrayList<String>();

    static {
        doubleWriteList.add("isLogin");

        doubleWriteList.add("sessionId");
        doubleWriteList.add("baseId");
        doubleWriteList.add("baseIdNum");

        doubleWriteList.add("cToken");
        doubleWriteList.add("accountName");
        doubleWriteList.add("signature");
    }


    public static boolean isDoubleWrite(String key) {
        return doubleWriteList.contains(key);
    }
}
