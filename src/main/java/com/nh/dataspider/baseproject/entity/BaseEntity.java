package com.nh.dataspider.baseproject.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class BaseEntity {
	
	@NotNull(message="名称不为空")
	private String name;
	
	@Min(value = 0, message = "不能为负数")
	private String value;
}
