package com.nh.dataspider.wxwork.model;

import lombok.Data;

/**
 * 授权的应用信息，注意是一个数组，但仅旧的多应用套件授权时会返回多个agent，对新的单应用授权，永远只返回一个agent
 * @author ni.hua
 *
 */
@Data
public class AuthAgentInfo {
	
	//授权方应用id
    private Integer agentid;
    
    //授权方应用名字
    private String name;
    
    //授权方应用方形头像
    private String round_logo_url;
    
    //授权方应用圆形头像
    private String square_logo_url;
    
    //旧的多应用套件中的对应应用id，新开发者请忽略
    private Integer appid;
    
    //授权模式，0为管理员授权；1为成员授权
    private Integer auth_mode;
    
    //是否为代开发自建应用
    private Boolean is_customized_app;
    
    //应用对应的权限
    private Privilege privilege;
    
    //共享了应用的企业信息，仅当由企业互联或者上下游共享应用触发的安装时才返回
    private SharedFrom shared_from;
    
    @Data
    public class Privilege{
    	
    	//权限等级。1:通讯录基本信息只读 2:通讯录全部信息只读 3:通讯录全部信息读写 4:单个基本信息只读 5:通讯录全部信息只写
    	private Integer level;
    	
    	//应用可见范围（部门）
    	private Integer[] allow_party;
    	
    	//应用可见范围（成员）
    	private String[] allow_user;
    	
    	//应用可见范围（标签）
    	private Integer[] allow_tag;
    	
    	//额外通讯录（部门）
    	private Integer[] extra_party;
    	
    	//额外通讯录（成员）
    	private String[] extra_user;
    	
    	//额外通讯录（标签）
    	private Integer[] extra_tag;
    }
    
    @Data
    public class SharedFrom{
    	
    	//共享了应用的企业信息，仅当企业互联或者上下游共享应用触发的安装时才返回
    	private String corpid;
    	
    	//共享了途径，0表示企业互联，1表示上下游
    	private Integer share_type;
    }
}
