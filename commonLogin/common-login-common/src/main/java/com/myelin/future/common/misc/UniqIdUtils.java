/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.myelin.future.common.misc;


import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * Created by gabriel on 14-8-4.
 */
public class UniqIdUtils {
    public static UniqIdUtils self = new UniqIdUtils();
    private MessageDigest mHasher;
    private String hostaddr;
    private Random random = new SecureRandom();
    private static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    private UniqIdUtils() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostaddr = inetAddress.getHostAddress();
        } catch (Exception e) {
            hostaddr = String.valueOf(System.currentTimeMillis());
        }

        if (StringUtils.isBlank(hostaddr)) {
            hostaddr = String.valueOf(System.currentTimeMillis());
        }

        try {
            mHasher = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            mHasher = null;
        }

    }

    public static UniqIdUtils getInstance() {
        if (self == null) {
            return new UniqIdUtils();
        }
        return self;
    }

    public String hash(String uniqId) {
        byte[] bt = this.mHasher.digest(uniqId.getBytes());
        int len = bt.length;

        char[] out = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            out[j++] = digits[(0xF0 & bt[i]) >>> 4];
            out[j++] = digits[0x0F & bt[i]];
        }

        return new String(out);

    }

    public String getUniqStr() {
        return hash(getUniqID());
    }


    public static String getUniqIdByAutoIncremnetId(Long id, byte accountType) {
        StringBuffer sb = new StringBuffer();
        long t = System.currentTimeMillis();
        sb.append(t);
        sb.append("-");
        sb.append(RandomUtils.getInt(100000, 999999));
        sb.append("-");
        sb.append(id);
        sb.append("-");
        sb.append(accountType);
        sb.append("-");
        sb.append(Thread.currentThread().hashCode());
        sb.append("-");
        sb.append(UUID.randomUUID());
        return sb.toString();
    }


    public String getUniqID() {
        StringBuffer sb = new StringBuffer();
        long t = System.currentTimeMillis();

        sb.append(t);

        sb.append("-");

        sb.append(random.nextInt(89999) + 10000);

        sb.append("-");
        sb.append(hostaddr);

        sb.append("-");
        sb.append(Thread.currentThread().hashCode());
        sb.append("-");
        sb.append(UUID.randomUUID());
        return sb.toString();
    }


    public static String getUniqBaesId(String accountId, Integer accountType) {
        StringBuffer sb = new StringBuffer();
        long t = System.currentTimeMillis();
        sb.append(t);
        sb.append("-");
        sb.append(accountType);
        sb.append("-");
        sb.append(accountId);
        sb.append("-");
        sb.append(RandomUtils.getInt(100000, 999999));
        sb.append("-");
        sb.append(Thread.currentThread().getId());
        return sb.toString();
    }

    public static void main(String args[]) {
        UniqIdUtils uniqIdUtils = new UniqIdUtils();
        System.out.println(uniqIdUtils.hash(uniqIdUtils.getUniqIdByAutoIncremnetId(10L, (byte) 2)));
        System.out.println(uniqIdUtils.hash(uniqIdUtils.getUniqIdByAutoIncremnetId(103123000L, (byte) 1)));

    }
}
