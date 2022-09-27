package com.nh.dataspider.util;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author YlSaaS产品组
 * @version V3.1.0
 * @copyright 上海云令智享信息技术有限公司
 * @date 2021/3/16 10:45
 */
public class Base64Util {

    /**
     * 把文件转化为base64.
     *
     * @param filePath 源文件路径
     */
    public static String fileToBase64(String filePath) {
        if (filePath != null && !"".equals(filePath)) {
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(filePath));
                return Base64.encodeBase64String(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * base64转化为文件.
     *
     * @param base64   base64
     * @param filePath 目标文件路径
     */
    public static void base64ToFile(String base64, String filePath) {
        try {
            Files.write(Paths.get(filePath), Base64.decodeBase64(base64), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
		System.out.println(fileToBase64("G:\\nh\\tmp\\jnpf\\imgtobase64\\a9962f5ea79f364872e5b5ebbd38e76.png"));
	}
}
