package com.nh.dataspider.wxwork.model;

import java.util.List;

import com.nh.dataspider.excelimport.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WxWorkModel {
	
	//需要新增的用户
    private List<UserEntity> toAddUserL;
    
    //需要删除的用户
    private List<UserEntity> toDeleteUserL;
    
}
