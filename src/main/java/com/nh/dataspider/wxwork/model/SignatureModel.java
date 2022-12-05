package com.nh.dataspider.wxwork.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureModel {
	
    /**
	 * 随机字符串，由开发者随机生成
	 */
    private String noncestr;
    
    /**
	 * 由开发者生成的当前时间戳
	 */
    private String timestamp;
    
    /**
	 * 当前网页的URL，不包含#及其后面部分。注意：对于没有只有域名没有 path 的 URL ，浏览器会自动加上 / 作为 path，如打开 http://qq.com 则获取到的 URL 为 http://qq.com/
	 */
    private String url;
    
}
