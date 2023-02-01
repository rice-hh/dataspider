package com.nh.dataspider.baseproject.common.response.enums;

import lombok.Getter;

/**
 * 状态码枚举类
 * 当然不能有setter方法了，因此我们不能在用@Data注解了，我们要用@Getter
 * @author nh
 *
 */
@Getter
public enum ResultCode implements StatusCode{
	SUCCESS(1000, "请求成功"),
	FAILD(1001, "请求失败"),
	VALIDATE_ERROR(1002, "参数校验失败"),
	RESPONSE_PACK_ERROR(1003, "response返回包转失败");
	
	private int code;
	private String msg;
	
	ResultCode(int code, String msg){
		this.code = code;
		this.msg = msg;
	}
}
