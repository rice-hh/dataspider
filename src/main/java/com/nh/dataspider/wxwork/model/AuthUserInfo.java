package com.nh.dataspider.wxwork.model;

import lombok.Data;

@Data
public class AuthUserInfo {
	
	//授权管理员的userid，可能为空（企业互联由上级企业共享第三方应用给下级时，不返回授权的管理员信息）
    private String userid;
    
    //授权管理员的open_userid，可能为空（企业互联由上级企业共享第三方应用给下级时，不返回授权的管理员信息）
    private String open_userid;
    
    //授权管理员的name，可能为空（企业互联由上级企业共享第三方应用给下级时，不返回授权的管理员信息）
    private String name;
    
    //授权管理员的头像url，可能为空（企业互联由上级企业共享第三方应用给下级时，不返回授权的管理员信息）
    private String avatar;
    
    //手机号码，代开发自建应用需要管理员授权才返回；第三方仅通讯录应用可获取；对于非第三方创建的成员，第三方通讯录应用也不可获取；上游企业不可获取下游企业成员该字段
    private String mobile;
    
    //性别。0表示未定义，1表示男性，2表示女性。第三方仅通讯录应用可获取；对于非第三方创建的成员，第三方通讯录应用也不可获取；上游企业不可获取下游企业成员该字段。注：不可获取指返回值0
    private String gender;
    
    //成员所属部门id列表，仅返回该应用有查看权限的部门id；成员授权模式下，固定返回根部门id，即固定为1
    private String[] department;
    
    //职务信息；代开发自建应用需要管理员授权才返回；第三方仅通讯录应用可获取；对于非第三方创建的成员，第三方通讯录应用也不可获取；上游企业不可获取下游企业成员该字段
    private String position;
    
    //邮箱，代开发自建应用需要管理员授权才返回；第三方仅通讯录应用可获取；对于非第三方创建的成员，第三方通讯录应用也不可获取；上游企业不可获取下游企业成员该字段
    private String email;
    
}
