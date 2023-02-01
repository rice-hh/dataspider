package com.nh.dataspider.baseproject.common.exception;

import com.nh.dataspider.baseproject.common.response.enums.AppCode;
import com.nh.dataspider.baseproject.common.response.enums.StatusCode;

import lombok.Getter;

/**
 * 异常类
 * @author nh
 *
 */
@Getter
public class APIException extends RuntimeException{
	
	/**
	 * 异常状态码
	 */
	private int code;
	
	/**
	 * 业务异常，一般前端会放到弹窗title上
	 */
    private String msg;

    // 手动设置异常
    public APIException(StatusCode statusCode, String message) {
        // message用于用户设置抛出错误详情，例如：当前价格-5，小于0
    	//抛出详细信息（在前端显示在弹窗体中，在ResultVo则保存在data中）
        super(message);
        // 状态码
        this.code = statusCode.getCode();
        // 状态码配套的msg
        this.msg = statusCode.getMsg();
    }

    // 默认异常使用APP_ERROR状态码
    public APIException(String message) {
        super(message);
        this.code = AppCode.APP_ERROR.getCode();
        this.msg = AppCode.APP_ERROR.getMsg();
    }
}
