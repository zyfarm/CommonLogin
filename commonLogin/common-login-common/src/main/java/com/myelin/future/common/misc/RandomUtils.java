package com.myelin.future.common.misc;
/**
 * Created by gabriel on 14-10-31.
 */

import java.security.SecureRandom;

public class RandomUtils {
    public static int getInt(int min, int max) {
        return min + new Double(Math.random() * (max - min)).intValue();
    }

    public static String getRandomString(int length) {
        SecureRandom ran = new SecureRandom();
        String rt = "";
        String all = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int rint = 0;
        for (int i = 0; i < length; i++) {
            rint = ran.nextInt();
            if (rint < 0) {
                rint = -rint;
            }
            rint = rint % all.length();
            rt += all.substring(rint, rint + 1);
        }
        return rt;
    }

    

    public static String getRandomDigitString(int length) {
        SecureRandom ran = new SecureRandom();
        String rt = "";
        String all = "0123456789";
        int rint = 0;
        for (int i = 0; i < length; i++) {
            rint = ran.nextInt();
            if (rint < 0) {
                rint = -rint;
            }
            rint = rint % all.length();
            rt += all.substring(rint, rint + 1);
        }
        return rt;
    }

    public static void main(String args[]) {
        System.out.println(RandomUtils.getInt(1,999999999));
        System.out.println(RandomUtils.getRandomDigitString(9));
        System.out.println(RandomUtils.getRandomString(9));
    }
}
