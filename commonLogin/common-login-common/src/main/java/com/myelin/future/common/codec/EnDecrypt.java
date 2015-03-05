package com.myelin.future.common.codec;

public interface EnDecrypt {

    /**
     * 加密
     * 
     * @param data
     * @return
     */
    public String encrypt(String data) throws Exception;


    /**
     * 解密
     * 
     * @param data
     * @return
     */
    public String decrypt(String data) throws Exception;
    
}
