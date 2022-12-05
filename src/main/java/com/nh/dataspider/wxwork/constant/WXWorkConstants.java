package com.nh.dataspider.wxwork.constant;

public class WXWorkConstants {

	/**服务商id**/
	public final static String SCORP_ID = "wwdb56e4dbb0ef0d4f";
    /*小程序应用的凭证密钥*/
	public final static String MINI_SECRET = "P9U3ojVrlX-d7su3_PlUcgW0Pqo01e1lvdpa1QyKCW4";
	/**服务商的secret**/
	public final static String PROVIDER_SECRET = "zgxLnC5uZjWR5KHWnndSh5riq3RA9y3TL4m-8X1P2wh97gyHM9eeanSstttHlJ9l";

	/**应用Id**/
//	public final static String SUITE_ID = "ww78268e075847f2eb";
	/**应用密钥**/
//	public final static String SUITE_SECRET = "WiXFz8IXcqf64DGmOlhUjPyfD5Af_HFIeY7Hq1nsnJo";
	/**加密参数**/
//	public final static String STOKEN = "8m92aguxa8rlCq97aRjPvNQJJb87NIX";
	/**加密参数**/
//	public final static String SENCODING_AESKEY = "2dsfPcTUnhud3Dqf5FX7rdqVtqf35uq8axgxLnbPBVu";

	// 类型 常量
	/** 返回成功码 **/
	public final static String SUCCESS_CODE = "0";
	/**推送suite_ticket**/
	public final static String SUITE_TICKET = "suite_ticket";
	/** 授权通知**/
	public final static String CREATE_AUTH = "create_auth";
	/** 授权变更 **/
	public final static String CHANGE_AUTH = "change_auth";
	/**授权取消**/
	public final static String CANCEL_AUTH = "cancel_auth";
	/**成员、部门、标签通知事件**/
	public final static String CHANGE_CONTACT = "change_contact";
	/**新增成员事件**/
	public final static String CREATE_USER = "create_user";
	/**新增部门事件**/
	public final static String CREATE_PARTY = "create_party";
	/**标签成员变更事件**/
	public final static String UPDATE_TAG = "update_tag";

	// 路径
	/**获取第三方应用凭证 (suite_access_token)**/
	public final static String SUITE_ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/service/get_suite_token";
	/**获取服务商的token**/
	public final static String GET_PROVIDER_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/service/get_provider_token";
	/**获取预授权码**/
	public final static String PRE_AUTH_CODE = "https://qyapi.weixin.qq.com/cgi-bin/service/get_pre_auth_code?suite_access_token=";
	/**获取永久授权码**/
	public final static String PERMANENT_CODE_URL = "https://qyapi.weixin.qq.com/cgi-bin/service/get_permanent_code?suite_access_token=";
	/**设置授权配置**/
	public final static String SET_SESSION_INFO = "https://qyapi.weixin.qq.com/cgi-bin/service/set_session_info?suite_access_token=";
	/**获取人员详细信息，参数access_token=ACCESS_TOKEN&userid=USERID*/
	public final static String GET_USER_DETAIL = "https://qyapi.weixin.qq.com/cgi-bin/user/get";
	/**获取成员授权列表，参数access_token=ACCESS_TOKEN*/
	public final static String GET_USERLIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/list_member_auth?access_token=";
	/**获取企业成员的userid与对应的部门ID列表，参数access_token=ACCESS_TOKEN*/
	public final static String GET_USERIDLIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/list_id?access_token=";
	/**获取部门列表（部门及其下的子部门）【从2022年8月15日10点开始，“企业管理后台 - 管理工具 - 通讯录同步”的新增IP将不能再调用此接口，企业可通过「获取部门ID列表」接口获取部门ID列表ORG_IDLIST_URL】 **/
	public final static String ORG_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/list";
	/**获取部门列表（部门及其下的子部门） **/
	public final static String ORG_IDLIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/simplelist";
	/**获取标签成员 **/
	public final static String TAG_USER_URL = "https://qyapi.weixin.qq.com/cgi-bin/tag/get";
	/**获取部门成员 **/
	public final static String ORG_USER_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist";
	/**获取企业授权信息**/
	public final static String AUTH_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/service/get_auth_info?suite_access_token=";
	/**获取企业凭证**/
	public final static String CORP_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/service/get_corp_token?suite_access_token=";
	/**第三方应用oauth2链接**/
	public final static String OAUTH2_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";
	/**获取访问用户身份(第三方应用)**/
	public final static String CORP_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/service/getuserinfo3rd?suite_access_token=";
    /**获取访问者身份(小程序)**/
	//public final static String MINI_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=";
    public final static String MINI_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/miniprogram/jscode2session?access_token=";
	/**获取登录用户信息**/
	public final static String GET_LOGIN_INFO = "https://qyapi.weixin.qq.com/cgi-bin/service/get_login_info?access_token=";
	/**获取访问用户敏感信息**/
	public final static String USER_DETAIL_INFO = "https://qyapi.weixin.qq.com/cgi-bin/service/getuserdetail3rd?suite_access_token=";

	public final static String ACCESS_TOKEN_URL  = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
    /**获取应用的jsapi_ticket**/
	public final static String JSAPI_TICKET_URL = "https://qyapi.weixin.qq.com/cgi-bin/ticket/get?access_token=";
	
	/**发送应用消息**/
	public final static String MSG_PUSH_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
	

}
