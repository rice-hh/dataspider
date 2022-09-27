package com.nh.dataspider.excelimport.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("user")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

	private String userId;
	
	private String userName;
	
	private String orgId;
	
	private String orgName;
	
	private String tenantId;
}
