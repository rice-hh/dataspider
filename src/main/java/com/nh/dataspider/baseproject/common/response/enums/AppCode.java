package com.nh.dataspider.baseproject.common.response.enums;

import lombok.Getter;

/**
 * 业务异常状态码枚举类
 * 当然不能有setter方法了，因此我们不能在用@Data注解了，我们要用@Getter
 * @author nh
 *
 */
@Getter
public enum AppCode implements StatusCode{
	APP_ERROR(2000, "业务异常"),
	PRICE_ERROR(2001, "价格异常");
	
	private int code;
	private String msg;
	
	AppCode(int code, String msg){
		this.code = code;
		this.msg = msg;
	}
}
