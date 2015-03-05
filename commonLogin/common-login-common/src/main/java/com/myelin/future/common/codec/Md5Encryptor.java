package com.myelin.future.common.codec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by gabriel on 14-10-31.
 */
public class Md5Encryptor implements EnDecrypt{


    @Override
    public String encrypt(String data) throws Exception {
        return md5(data);
    }





    @Override
    public String decrypt(String data) throws Exception {
        return null;
    }




    /**
     * Defeats instantiation.
     */
    public Md5Encryptor() {
    }

    /**
     * 调用SunAPI返回16位字节数组摘要
     *
     * @param text 明文
     * @return byte[] msgDigest.digest();
     */
    public static byte[] md5Bytes(String text) {
        if (null == text || "".equals(text)) {
            return new byte[0];
        }

        MessageDigest msgDigest = null;
        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }

        msgDigest.update(text.getBytes());
        byte[] bytes = msgDigest.digest();
        return bytes;
    }

    /**
     * 对字符串进行MD5加密
     *
     * @param text        明文
     * @param isReturnRaw 是否直接用API返回new String(byte[16])(注意不同系统间编码问题)
     * @return if(isNeedRaw) 32位密文 else 16位密文
     */
    public static String md5(String text, boolean isReturnRaw) {
        if (null == text || "".equals(text)) {
            return text;
        }

        byte[] bytes = md5Bytes(text);
        if (isReturnRaw) {
            return new String(bytes);
        }

        String md5Str = new String();
        byte tb;
        char low;
        char high;
        char tmpChar;

        for (int i = 0; i < bytes.length; i++) {
            tb = bytes[i];

            tmpChar = (char) ((tb >>> 4) & 0x000f);
            if (tmpChar >= 10) {
                high = (char) (('a' + tmpChar) - 10);
            } else {
                high = (char) ('0' + tmpChar);
            }
            md5Str += high;

            tmpChar = (char) (tb & 0x000f);
            if (tmpChar >= 10) {
                low = (char) (('a' + tmpChar) - 10);
            } else {
                low = (char) ('0' + tmpChar);
            }
            md5Str += low;
        }

        return md5Str;
    }

    /**
     * 对字符串进行MD5加密
     *
     * @param text 明文
     * @return 32位密文
     */
    private static String md5(String text) {
        return md5(text, false);
    }

    /**
     * y64( MD5( text, true ) )
     *
     * @param text 明文
     * @return 密文
     * @see
     */
    public static String md5Yahoo(String text) {
        if (null == text || "".equals(text)) {
            return text;
        }

        byte[] bytes = md5Bytes(text);
        return Base64.encodeYahoo64(bytes);
    }

}
