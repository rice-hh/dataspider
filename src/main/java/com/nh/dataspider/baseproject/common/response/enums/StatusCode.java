package com.nh.dataspider.baseproject.common.response.enums;

/**
 * 定义一个状态码的接口，所有状态码都需要实现它，有了标准才好做事
 * @author nh
 *
 */
public interface StatusCode {
	public int getCode();
	public String getMsg();
}
