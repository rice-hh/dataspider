package com.nh.dataspider.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;

public class EncodeUtil {

	/**
	 * MD5加密
	 * @Author ni.hua
	 * @Commnet
	 * @Date 2020年5月19日
	 * @param rawPass
	 * @return
	 */
	public static String MD5Encode (String rawPass) {
		return DigestUtils.md5Hex(rawPass.getBytes());
	}
	
	/**
	 * sha1
	 * @Author ni.hua
	 * @Commnet
	 * @Date 2020年5月19日
	 * @param rawPass
	 * @return
	 */
	public static String SHA1Encode (String rawPass) {
		return DigestUtils.sha1Hex(rawPass.getBytes());
	}
	
	/**
     * DES加密
     * @param content  字符串内容
     * @param password 密钥
     */
    public static String DESEncrypt(String content, String password){
        return des(content,password,Cipher.ENCRYPT_MODE);
    }

    /**
     * DES解密
     * @param content  字符串内容
     * @param password 密钥
     */
    public static String DESDecrypt(String content, String password){
        return des(content,password,Cipher.DECRYPT_MODE);
    }

    /**
     * DES加密/解密公共方法
     * @Author ni.hua
     * @Commnet
     * @Date 2020年5月19日
     * @param content 字符串内容
     * @param password 密钥
     * @param type 加密：{@link Cipher#ENCRYPT_MODE}，解密：{@link Cipher#DECRYPT_MODE}
     * @return
     */
    private static String des(String content, String password, int type) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(type, keyFactory.generateSecret(desKey), random);

            if (type == Cipher.ENCRYPT_MODE) {
                byte[] byteContent = content.getBytes("utf-8");
                return Hex2Util.parseByte2HexStr(cipher.doFinal(byteContent));
            } else {
                byte[] byteContent = Hex2Util.parseHexStr2Byte(content);
                assert byteContent != null;
                return new String(cipher.doFinal(byteContent));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * AES加密
     * @param content  字符串内容
     * @param password 密钥
     */
    public static String AESEncrypt(String content, String password){
        return aes(content,password,Cipher.ENCRYPT_MODE);
    }


    /**
     * AES解密
     * @param content  字符串内容
     * @param password 密钥
     */
    public static String AESDecrypt(String content, String password){
        return aes(content,password,Cipher.DECRYPT_MODE);
    }

    /**
     * AES加密/解密 公共方法
     * @Author ni.hua
     * @Commnet
     * @Date 2020年5月19日
     * @param content 字符串
     * @param password 密钥
     * @param type 加密：{@link Cipher#ENCRYPT_MODE}，解密：{@link Cipher#DECRYPT_MODE}
     * @return
     */
    private static String aes(String content, String password, int type) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            generator.init(128, random);
            SecretKey secretKey = generator.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(type, key);
            if (type == Cipher.ENCRYPT_MODE) {
                byte[] byteContent = content.getBytes("utf-8");
                return Hex2Util.parseByte2HexStr(cipher.doFinal(byteContent));
            } else {
                byte[] byteContent = Hex2Util.parseHexStr2Byte(content);
                return new String(cipher.doFinal(byteContent));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args) {
    	System.out.println(SHA1Encode("111111"));
	}
	
}
