package com.myelin.future.common.codec;


public class EnDecrypts {
    public static final int DES = 0;// 对称加密
    public static final int RSA = 1;// 非对称加密
    public static final int MD5 = 2;// MD5加密
    public static final int BLOW_FISH=3;//blowfish加密
    public static final int BASE_64=4;//Base64加密

    public static EnDecrypt[] factory = new EnDecrypt[5];


    public static void addEnDecrypt(Integer code, EnDecrypt encrypt) {
        if (code > factory.length) {
            EnDecrypt[] newendecryptor = new EnDecrypt[factory.length + 1];
            System.arraycopy(factory, 0, newendecryptor, 0, newendecryptor.length);
            factory = newendecryptor;
        }
        factory[code] = encrypt;
    }

    static {
        addEnDecrypt(DES, new DesEncryptor());//DES加密
        addEnDecrypt(RSA, new RsaEncryptor());//RSA加密
        addEnDecrypt(MD5, new Md5Encryptor());//MD5加密
        addEnDecrypt(BLOW_FISH, new BlowfishEncryptor());//MD5加密
        addEnDecrypt(BASE_64,new Base64());
    }


    public static EnDecrypt getEnDecrypt(Integer code) {
        return factory[code];
    }

    public static void main(String args[]) throws Exception{
        System.out.println(EnDecrypts.getEnDecrypt(EnDecrypts.DES).encrypt("root123"));
    }

}
