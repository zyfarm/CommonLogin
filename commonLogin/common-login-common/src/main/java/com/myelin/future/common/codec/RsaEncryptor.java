package com.myelin.future.common.codec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;


public class RsaEncryptor implements EnDecrypt {

    public static String MODULE = "0";
    public static String EXPONENT = "1";

    
    @Override
    public String encrypt(String data) throws Exception {
        if (MODULE.equals(data)) {
            return this.getModules();
        }
        else {
            return this.getPublicExponent();
        }
    }


    @Override
    public String decrypt(String data) throws Exception {
        return decryptStringByJs(data);
    }

    /** 算法名称 */
    private static final String ALGORITHOM = "RSA";

    /** 保存生成的密钥对的文件名称。 */
    private static final String RSA_PAIR_FILENAME = "/__RSA_PAIR.txt";

    /** 密钥大小 */
    private static final int KEY_SIZE = 1024;

    /** 默认的安全服务提供者 */
    private static final Provider DEFAULT_PROVIDER = new BouncyCastleProvider();

    private static KeyPairGenerator keyPairGen = null;
    private static KeyFactory keyFactory = null;
    /** 缓存的密钥对。 */
    private static KeyPair oneKeyPair = null;

    private static File rsaPairFile = null;

    private RSAPublicKey publicKey;

    static {
        try {
            keyPairGen = KeyPairGenerator.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
            keyFactory = KeyFactory.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        }
        catch (NoSuchAlgorithmException ex) {

        }
        rsaPairFile = new File(getRSAPairFilePath());

    }


    public RsaEncryptor() {
        publicKey = getDefaultPublicKey();
    }


    /**
     * 生成并返回RSA密钥对。
     */
    private static synchronized KeyPair generateKeyPair() {
        try {
            keyPairGen.initialize(KEY_SIZE, new SecureRandom(DateFormatUtils.format(new Date(), "yyyyMMdd")
                .getBytes()));
            oneKeyPair = keyPairGen.generateKeyPair();
            saveKeyPair(oneKeyPair);
            return oneKeyPair;
        }
        catch (InvalidParameterException ex) {
            // LOGGER.error("KeyPairGenerator does not support a key length of "
            // + KEY_SIZE + ".", ex);
        }
        catch (NullPointerException ex) {
            // LOGGER.error("RSAUtils#KEY_PAIR_GEN is null, can not generate KeyPairGenerator instance.",ex);
        }
        return null;
    }


    /**
     * 返回生成/读取的密钥对文件的路径。
     */
    private static String getRSAPairFilePath() {
        String urlPath = RsaEncryptor.class.getResource("/").getPath();
        return (new File(urlPath).getParent() + RSA_PAIR_FILENAME);
    }


    /**
     * 若需要创建新的密钥对文件，则返回 {@code true}，否则 {@code false}。
     */
    private static boolean isCreateKeyPairFile() {
        // 是否创建新的密钥对文件
        boolean createNewKeyPair = false;
        if (!rsaPairFile.exists() || rsaPairFile.isDirectory()) {
            createNewKeyPair = true;
        }
        return createNewKeyPair;
    }


    /**
     * 将指定的RSA密钥对以文件形式保存。
     * 
     * @param keyPair
     *            要保存的密钥对。
     */
    private static void saveKeyPair(KeyPair keyPair) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = FileUtils.openOutputStream(rsaPairFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(keyPair);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(oos);
            IOUtils.closeQuietly(fos);
        }
    }


    /**
     * 返回RSA密钥对。
     */
    private static KeyPair getKeyPair() {
        // 首先判断是否需要重新生成新的密钥对文件
        if (isCreateKeyPairFile()) {
            // 直接强制生成密钥对文件，并存入缓存。
            return generateKeyPair();
        }
        if (oneKeyPair != null) {
            return oneKeyPair;
        }
        return readKeyPair();
    }


    // 同步读出保存的密钥对
    private static KeyPair readKeyPair() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = FileUtils.openInputStream(rsaPairFile);
            ois = new ObjectInputStream(fis);
            oneKeyPair = (KeyPair) ois.readObject();
            return oneKeyPair;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(ois);
            IOUtils.closeQuietly(fis);
        }
        return null;
    }


    /**
     * 根据给定的系数和专用指数构造一个RSA专用的公钥对象。
     * 
     * @param modulus
     *            系数。
     * @param publicExponent
     *            专用指数。
     * @return RSA专用公钥对象。
     */
    private static RSAPublicKey generateRSAPublicKey(byte[] modulus, byte[] publicExponent) {
        RSAPublicKeySpec publicKeySpec =
                new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent));
        try {
            return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        }
        catch (InvalidKeySpecException ex) {
            // LOGGER.error("RSAPublicKeySpec is unavailable.", ex);
        }
        catch (NullPointerException ex) {
            // LOGGER.error("RSAUtils#KEY_FACTORY is null, can not generate KeyFactory instance.",
            // ex);
        }
        return null;
    }


    /**
     * 根据给定的系数和专用指数构造一个RSA专用的私钥对象。
     * 
     * @param modulus
     *            系数。
     * @param privateExponent
     *            专用指数。
     * @return RSA专用私钥对象。
     */
    private static RSAPrivateKey generateRSAPrivateKey(byte[] modulus, byte[] privateExponent) {
        RSAPrivateKeySpec privateKeySpec =
                new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(privateExponent));
        try {
            return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        }
        catch (InvalidKeySpecException ex) {
            // LOGGER.error("RSAPrivateKeySpec is unavailable.", ex);
        }
        catch (NullPointerException ex) {
            // LOGGER.error("RSAUtils#KEY_FACTORY is null, can not generate KeyFactory instance.",
            // ex);
        }
        return null;
    }


    /**
     * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的私钥对象。
     * 
     * @param modulus
     *            系数。
     * @param privateExponent
     *            专用指数。
     * @return RSA专用私钥对象。
     */
    private static RSAPrivateKey getRSAPrivateKey(String hexModulus, String hexPrivateExponent) {
        if (StringUtils.isBlank(hexModulus) || StringUtils.isBlank(hexPrivateExponent)) {
            /*
             * if(LOGGER.isDebugEnabled()) { LOGGER.debug(
             * "hexModulus and hexPrivateExponent cannot be empty. RSAPrivateKey value is null to return."
             * ); }
             */
            return null;
        }
        byte[] modulus = null;
        byte[] privateExponent = null;
        try {
            modulus = Hex.decodeHex(hexModulus.toCharArray());
            privateExponent = Hex.decodeHex(hexPrivateExponent.toCharArray());
        }
        catch (DecoderException ex) {
            // LOGGER.error("hexModulus or hexPrivateExponent value is invalid. return null(RSAPrivateKey).");
        }
        if (modulus != null && privateExponent != null) {
            return generateRSAPrivateKey(modulus, privateExponent);
        }
        return null;
    }


    /**
     * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的公钥对象。
     * 
     * @param modulus
     *            系数。
     * @param publicExponent
     *            专用指数。
     * @return RSA专用公钥对象。
     */
    private static RSAPublicKey getRSAPublidKey(String hexModulus, String hexPublicExponent) {
        if (StringUtils.isBlank(hexModulus) || StringUtils.isBlank(hexPublicExponent)) {
            /*
             * if(LOGGER.isDebugEnabled()) { LOGGER.debug(
             * "hexModulus and hexPublicExponent cannot be empty. return null(RSAPublicKey)."
             * ); }
             */
            return null;
        }
        byte[] modulus = null;
        byte[] publicExponent = null;
        try {
            modulus = Hex.decodeHex(hexModulus.toCharArray());
            publicExponent = Hex.decodeHex(hexPublicExponent.toCharArray());
        }
        catch (DecoderException ex) {
            // LOGGER.error("hexModulus or hexPublicExponent value is invalid. return null(RSAPublicKey).");
        }
        if (modulus != null && publicExponent != null) {
            return generateRSAPublicKey(modulus, publicExponent);
        }
        return null;
    }


    /**
     * 使用指定的公钥加密数据。
     * 
     * @param publicKey
     *            给定的公钥。
     * @param data
     *            要加密的数据。
     * @return 加密后的数据。
     */
    private static byte[] encrypt(PublicKey publicKey, byte[] data) throws Exception {
        Cipher ci = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        ci.init(Cipher.ENCRYPT_MODE, publicKey);
        return ci.doFinal(data);
    }


    /**
     * 使用指定的私钥解密数据。
     * 
     * @param privateKey
     *            给定的私钥。
     * @param data
     *            要解密的数据。
     * @return 原数据。
     */
    private static byte[] decrypt(PrivateKey privateKey, byte[] data) throws Exception {
        Cipher ci = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        ci.init(Cipher.DECRYPT_MODE, privateKey);
        return ci.doFinal(data);
    }


    /**
     * 使用给定的公钥加密给定的字符串。
     * <p />
     * 若 {@code publicKey} 为 {@code null}，或者 {@code plaintext} 为 {@code null}
     * 则返回 {@code null}。
     * 
     * @param publicKey
     *            给定的公钥。
     * @param plaintext
     *            字符串。
     * @return 给定字符串的密文。
     */
    private static String encryptString(PublicKey publicKey, String plaintext) {
        if (publicKey == null || plaintext == null) {
            return null;
        }
        byte[] data = plaintext.getBytes();
        try {
            byte[] en_data = encrypt(publicKey, data);
            return new String(Hex.encodeHex(en_data));
        }
        catch (Exception ex) {
            // LOGGER.error(ex.getCause().getMessage());
        }
        return null;
    }


    /**
     * 使用默认的公钥加密给定的字符串。
     * <p />
     * 若{@code plaintext} 为 {@code null} 则返回 {@code null}。
     * 
     * @param plaintext
     *            字符串。
     * @return 给定字符串的密文。
     */
    private static String encryptString(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        byte[] data = plaintext.getBytes();
        KeyPair keyPair = getKeyPair();
        try {
            byte[] en_data = encrypt((RSAPublicKey) keyPair.getPublic(), data);
            return new String(Hex.encodeHex(en_data));
        }
        catch (NullPointerException ex) {
            // LOGGER.error("keyPair cannot be null.");
        }
        catch (Exception ex) {
            // LOGGER.error(ex.getCause().getMessage());
        }
        return null;
    }


    /**
     * 使用给定的私钥解密给定的字符串。
     * <p />
     * 若私钥为 {@code null}，或者 {@code encrypttext} 为 {@code null}或空字符串则返回
     * {@code null}。 私钥不匹配时，返回 {@code null}。
     * 
     * @param privateKey
     *            给定的私钥。
     * @param encrypttext
     *            密文。
     * @return 原文字符串。
     */
    private static String decryptString(PrivateKey privateKey, String encrypttext) {
        if (privateKey == null || StringUtils.isBlank(encrypttext)) {
            return null;
        }
        try {
            byte[] en_data = Hex.decodeHex(encrypttext.toCharArray());
            byte[] data = decrypt(privateKey, en_data);
            return new String(data);
        }
        catch (Exception ex) {
            // LOGGER.error(String.format("\"%s\" Decryption failed. Cause: %s",
            // encrypttext, ex.getCause().getMessage()));
        }
        return null;
    }


    /**
     * 使用默认的私钥解密给定的字符串。
     * <p />
     * 若{@code encrypttext} 为 {@code null}或空字符串则返回 {@code null}。 私钥不匹配时，返回
     * {@code null}。
     * 
     * @param encrypttext
     *            密文。
     * @return 原文字符串。
     */
    private static String decryptString(String encrypttext) {
        if (StringUtils.isBlank(encrypttext)) {
            return null;
        }
        KeyPair keyPair = getKeyPair();
        try {
            byte[] en_data = Hex.decodeHex(encrypttext.toCharArray());
            byte[] data = decrypt((RSAPrivateKey) keyPair.getPrivate(), en_data);
            return new String(data);
        }
        catch (NullPointerException ex) {
            // LOGGER.error("keyPair cannot be null.");
        }
        catch (Exception ex) {
            // LOGGER.error(String.format("\"%s\" Decryption failed. Cause: %s",
            // encrypttext, ex.getMessage()));
        }
        return null;
    }


    /**
     * 使用默认的私钥解密由JS加密（使用此类提供的公钥加密）的字符串。
     * 
     * @param encrypttext
     *            密文。
     * @return {@code encrypttext} 的原文字符串。
     */
    private static String decryptStringByJs(String encrypttext) {
        String text = decryptString(encrypttext);
        if (text == null) {
            return null;
        }
        return StringUtils.reverse(text);
    }


    /** 返回已初始化的默认的公钥。 */
    private static RSAPublicKey getDefaultPublicKey() {
        KeyPair keyPair = getKeyPair();
        if (keyPair != null) {
            return (RSAPublicKey) keyPair.getPublic();
        }
        return null;
    }


    /**
     * 获得modules
     * 
     * @return
     */
    private String getModules() {
        return new String(Hex.encodeHex(this.publicKey.getModulus().toByteArray()));
    }


    /**
     * 获得Exponent
     * 
     * @return
     */
    private String getPublicExponent() {
        return new String(Hex.encodeHex(this.publicKey.getPublicExponent().toByteArray()));
    }
}
