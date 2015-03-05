package com.myelin.future.common.codec;


/**
 * Created by gabriel on 14-10-31.
 */
/*
 * Created on 2004-4-16
 */


import com.myelin.future.common.misc.StreamUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;


public class BlowfishEncryptor implements EnDecrypt {

    private static String CIPHER_KEY = "ooxx";
    private static String CIPHER_NAME = "Blowfish/CFB8/NoPadding";
    private static String KEY_SPEC_NAME = "Blowfish";
    private static SecretKeySpec secretKeySpec = null;
    private static IvParameterSpec ivParameterSpec = null;
    private static final ThreadLocal encrypter_pool = new ThreadLocal();
    Cipher enCipher;
    Cipher deCipher;

    public BlowfishEncryptor() {
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("common-key");
            CIPHER_KEY = StringUtils.trim(StreamUtil.readText(is));

            secretKeySpec = new SecretKeySpec(CIPHER_KEY.getBytes(),
                    KEY_SPEC_NAME);
            ivParameterSpec = new IvParameterSpec((DigestUtils.md5Hex(CIPHER_KEY)
                    .substring(0, 8))
                    .getBytes());

            enCipher = Cipher.getInstance(CIPHER_NAME);
            deCipher = Cipher.getInstance(CIPHER_NAME);
            enCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            deCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        } catch (Exception e) {

        }
    }

    public String getByteString(byte[] b) {
        StringBuffer s = new StringBuffer(b.length * 3);

        for (int i = 0; i < b.length; i++) {
            s.append("|" + Integer.toHexString(b[i] & 0xff).toUpperCase());
        }

        return s.toString();
    }

    public static BlowfishEncryptor getEncrypter() {
        BlowfishEncryptor encrypter = (BlowfishEncryptor) encrypter_pool.get();

        if (encrypter == null) {
            encrypter = new BlowfishEncryptor();
            encrypter_pool.set(encrypter);
        }

        return encrypter;
    }

    @Override
    public String encrypt(String str) {
        String result = null;

        if (!StringUtils.isBlank(str)) {
            try {
                byte[] utf8 = str.getBytes();
                byte[] enc = enCipher.doFinal(utf8);

                result = new String(Base64.encodeBase64(enc));
            } catch (Exception ex) {

            }
        }

        return result;
    }

    @Override
    public String decrypt(String str) {
        String result = null;

        if (!StringUtils.isBlank(str)) {
            try {
                byte[] dec = Base64.decodeBase64(str.getBytes());
                result = new String(deCipher.doFinal(dec));
            } catch (Exception ex) {
                result = "";
            }
        }

        return result;
    }
}

