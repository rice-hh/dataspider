package com.nh.dataspider.excelimport.constants;

public interface OtherFieldType {
	/**
     * 主键id
     */
    int ID = 1;
	
    /**
     * 当前时间
     */
    int CURRENT_TIME = 2;
    
    /**
     * 租户id
     */
    int TENANT_ID = 3;
    
    /**
     * 用户属性（以10**开头）
     */
    interface USER_TYPE {
    	int USER_ID = 1001;
    	int USER_NAME = 1002;
    }
    
    /**
     * 部门属性（以20**开头）
     */
    interface DEPT_TYPE {
    	int DEPT_ID = 2001;
    	int DEPT_NAME = 2002;
    }
}
