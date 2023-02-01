package com.nh.dataspider.baseproject.common.response.model;

import com.nh.dataspider.baseproject.common.response.enums.ResultCode;
import com.nh.dataspider.baseproject.common.response.enums.StatusCode;

import lombok.Data;

/**
 * 返回结果 包装类
 * 预设了几种默认的方法
 * @author nh
 *
 */
@Data
public class ResultVo {

	/**
	 * 状态码
	 */
	private int code;
	
	/**
	 * 返回信息
	 */
	private String msg;
	
	/**
	 * 返回对象
	 */
	private Object data;
	
	/**
	 * 手动设置返回vo
	 * @param code
	 * @param msg
	 * @param data
	 */
	public ResultVo(int code, String msg, Object data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	
	/**
	 * 默认返回成功状态码，数据对象
	 * @param data
	 */
	public ResultVo(Object data) {
		this.code = ResultCode.SUCCESS.getCode();
		this.msg = ResultCode.SUCCESS.getMsg();
		this.data = data;
	}
	
	/**
	 * 返回指定状态码，数据对象
	 * @param statusCode
	 * @param data
	 */
	public ResultVo(StatusCode statusCode, Object data) {
		this.code = statusCode.getCode();
		this.msg = statusCode.getMsg();
		this.data = data;
	}
	
	/**
	 * 只返回转态码
	 * @param statusCode
	 */
	public ResultVo(StatusCode statusCode) {
		this.code = statusCode.getCode();
		this.msg = statusCode.getMsg();
		this.data = null;
	}
}
