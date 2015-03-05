package com.myelin.future.common.codec;


import com.myelin.future.common.constant.CommonConstant;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.Charset;
import java.security.Key;

public class DesEncryptor implements EnDecrypt {

    public static final String ALGOTYPE = "DES";
    public static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";
    public static final int keylength = 56;


    @Override
    public String encrypt(String data) throws Exception {
        Key deskey = toKey(Base64.decodeBase64(CommonConstant.deskey));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, deskey);
        String tmp = new String(Base64.encodeBase64(cipher.doFinal(data.getBytes("US-ASCII"))), Charset.forName("US-ASCII"));
        return tmp;
    }


    private Key toKey(byte[] key) throws Exception {
        DESKeySpec deskeyspec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGOTYPE);
        SecretKey secretkey = keyFactory.generateSecret(deskeyspec);
        return secretkey;
    }

    @Override
    public String decrypt(String data) throws Exception {
        Key k = toKey(Base64.decodeBase64(CommonConstant.deskey));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);
        String ret = new String(cipher.doFinal(Base64.decodeBase64(data)));
        return ret;
    }

}
