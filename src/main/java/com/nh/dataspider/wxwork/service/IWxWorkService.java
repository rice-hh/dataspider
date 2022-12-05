package com.nh.dataspider.wxwork.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.nh.dataspider.excelimport.entity.UserEntity;
import com.nh.dataspider.wxwork.model.AuthAgentInfo;
import com.nh.dataspider.wxwork.model.AuthUserInfo;
import com.nh.dataspider.wxwork.model.DepartmentInfo;
import com.nh.dataspider.wxwork.model.TemplateMsgInfo;
import com.nh.dataspider.wxwork.model.WxWorkModel;
import com.nh.dataspider.wxwork.util.AesException;


public interface IWxWorkService {

	/**
	 * url验证
	 * @param requestMap
	 * @return
	 */
	String verifyUrl(Map<String, String> requestMap);

	JSONObject decryptWxWorkCallBack(Map<String, String> requestMap, ServletInputStream in) throws AesException, UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException;
	
	JSONObject decryptWxWorkCallBack(Map<String, String> requestMap, String sReqData) throws AesException, UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException;

	/**
	 * 获取永久授权码
	 * @param auth_code 临时授权码
	 * @return
	 * @throws JSONException
	 */
	JSONObject getPermanentCode(String auth_code) throws JSONException;

	/**
	 * 企业微信授权关联的租户注册的初始化
	 * @param corpInfo 企业微信返回的授权的企业信息
	 * @param userInfo 操作授权的管理员用户（授权企业对应的管理员用户）
	 * @param permanentCode 企业微信永久授权码,最长为512字节
	 * @throws InterruptedException
	 */
//	BaseTenantEntity createTenant(CorpInfo corpInfo, AuthUserInfo userInfo, String permanentCode) throws InterruptedException;

	/**
	 *
	 * @param agentInfo	授权信息
	 * @param accessToken 授权方（企业）access_token,最长为512字节。代开发自建应用安装时不返回。
	 * @param userL 拥有权限的用户集合
	 * @param positonL 拥有权限的岗位集合
	 * @param relationList 用户角色关系集合
	 */
//	void prepareUserAndPosition(AuthAgentInfo agentInfo, String accessToken, List<UserEntity> userL, List<PositionEntity> positonL);

	/**
	 * 初始化保存 应用权限范围内的部门信息
	 * @param deptList
	 */
//	void initDept(List<DepartmentInfo> deptList);

	/**
	 * 创建岗位
	 * @param positonL
	 */
//	void initPosition(List<PositionEntity> positonL);

	/**
	 * 创建用户
	 * @param userL
	 */
//	void initUser(List<UserEntity> userL);

	/**
	 * 获取部门列表
	 * @param deptId 部门id。获取指定部门及其下的子部门（以及子部门的子部门等等，递归）。 如果不填，默认获取全量组织架构
	 * @param accessToken 	调用接口凭证
	 */
	List<DepartmentInfo> getDeptList(String deptId, String accessToken);

	/**
	 * 取消授权的时候 删除企业信息
	 * @param authCorpId 取消授权的企业的id
	 */
	void deleteTeannat(String authCorpId);

	/**
	 * 创建account表中的用户
	 * @param userL 待新增的用户集合
	 * @param tenantId 当前租户id
	 * @param adminId 当前租户管理员id
	 */
	void initAccount(List<UserEntity> userL, String tenantId, String adminId);

	/**
	 * 变更授权通知 新增新的部门、用户、岗位之类的
	 * @param corpId 授权方的corpid
	 * @param permanentCode 永久授权码
	 * @return
	 */
	WxWorkModel updateInit(String corpId, String permanentCode);

	/**
	 * 获取第三方应用凭证（通过本接口获取的suite_access_token有效期为2小时，开发者需要进行缓存，不可频繁获取。）
	 * @param suiteTicket 企业微信后台推送的ticket
	 * @return
	 */
	String getSuiteAccessToken();

    /**
     * 获取微信小程序应用凭证（通过本接口获取的access_token有效期为2小时，开发者需要进行缓存，不可频繁获取。）
     * @return
     */
	String getMiniAccessToken();

    /**
     * 获取应用程序的ticket
     * @return
     */
    String getMiniTicket();

	/**
	 * 更新租户的其他信息
	 * @param corpInfo
	 * @param userInfo
	 * @param permanentCode
	 * @return
	 * @throws InterruptedException
	 */
//	BaseTenantEntity updateTenant(CorpInfo corpInfo, AuthUserInfo userInfo, String permanentCode) throws InterruptedException;

	/**
	 * 获取部门成员
	 * @param deptId 获取的部门id
	 * @param accessToken 调用接口凭证
	 * @param fetchChild 是否递归获取子部门下面的成员：1-递归获取，0-只获取本部门
	 * @return
	 */
	List<AuthUserInfo> getDeptUserList(String deptId, String accessToken, int fetchChild);

	/**
	 * 根据部门集合，获取这些部门下的所有用户
	 * @param getUserByDeptL 部门id集合
	 * @param accessToken 授权企业的凭证
	 * @param userL
	 * @param positonL
	 * @param relationList 用户角色关系
	 */
//	void getUserByDeptL(List<String> toCreateDeptIdL, String accessToken, List<UserEntity> userL, List<PositionEntity> positonL);

	/**
	 * 删除account
	 * @param userL 用户集合
	 * @param tenantId 租户id
	 */
	void deleteAccount(List<UserEntity> userL, String tenantId);

	/**
	 * 获取服务商的token
	 * @return
	 */
	String getProviderToken();

	/**
	 * 获取登录用户信息
	 * @param providerToken 授权登录服务商的网站时，使用应用提供商的provider_access_token
	 * @param authCode oauth2.0授权企业微信管理员登录产生的code，最长为512字节。只能使用一次，5分钟未被使用自动过期
	 * @return
	 */
	JSONObject getLoginInfo(String providerToken, String authCode);

	/**
	 * 获取用户敏感信息
	 * @param userTicket 成员票据
	 * @return
	 */
	JSONObject getUserDetailInfo(String userTicket);

	/**
	 * 获取企业成员的userid与对应的部门ID列表
	 * @param accessToken 授权企业的凭证
	 * @return
	 */
	List<AuthUserInfo> getUserIdList(String accessToken);

	/**
	 * 获取部门列表
	 * @param deptId 部门id。获取指定部门及其下的子部门（以及子部门的子部门等等，递归）。 如果不填，默认获取全量组织架构
	 * @param accessToken 	调用接口凭证
	 */
	List<DepartmentInfo> getDeptIdList(String deptId, String accessToken);

	/**
	 * 获取企业凭证（第三方服务商在取得企业的永久授权码后，通过此接口可以获取到企业的access_token。）
	 * @param corpId 授权方corpid
	 * @param permanentCode 永久授权码，通过get_permanent_code获取
	 * @return
	 */
	String getAccessToken(String corpId, String permanentCode);

	/**
	 * 企业微信消息推送
	 * @param corpId 企业微信id
	 * @param permanentCode 第三方永久授权码
	 * @param userIdL 推送的用户id集合
	 * @param isAll 是否全部推送
	 * @param templateMsgInfo 消息内容
	 * @return
	 */
	JSONObject msgPush(String corpId, String permanentCode, TemplateMsgInfo templateMsgInfo);

	/**
	 * 获取预授权码
	 * @return
	 * @throws JSONException
	 */
	String getPreAuthCode() throws JSONException;

	String getWxWorkRedisKey();

	/**
	 * 设置授权配置
	 * @param preAuthCode 预授权码
	 * @return
	 * @throws JSONException
	 */
	JSONObject setSessionInfo(String preAuthCode) throws JSONException;

	/**
	 * 关联更新租户信息（保存corpId，permanentCode）
	 * @param entity
	 * @param corpInfo
	 * @param permanentCode
	 * @return
	 */
//	BaseTenantEntity updateTenant(BaseTenantEntity entity, CorpInfo corpInfo, String permanentCode);

	/**
	 * 获取应用的jsapi_ticket
	 * @param corpId
	 * @param permanentCode
	 * @return
	 * @throws JSONException
	 */
	String getJsapiTicket(String corpId, String permanentCode) throws JSONException;

	/**
	 * 生成sh1加密的字符串
	 * @param jsapiTicket 获取方式详见企业微信 JSSDK 文档
	 * @param noncestr 随机字符串，由开发者随机生成
	 * @param timestamp 由开发者生成的当前时间戳
	 * @param url 当前网页的URL，不包含#及其后面部分。注意：对于没有只有域名没有 path 的 URL ，浏览器会自动加上 / 作为 path，如打开 http://qq.com 则获取到的 URL 为 http://qq.com/
	 * @return
	 * @throws Exception
	 */
	String getSh1Signature(String jsapiTicket, String noncestr, String timestamp, String url) throws Exception;

	/**
	 * 获取企业授权信息
	 * @param corpId 授权方corpid
	 * @param permanentCode 永久授权码，通过get_permanent_code获取
	 * @return
	 */
	JSONObject getAuthInfo(String corpId, String permanentCode);

	/**
	 * 获取授权应用信息
	 * @param corpId 企业微信id
	 * @param permanentCode 第三方永久授权码
	 * @return
	 */
	AuthAgentInfo getAuthAgentInfo(String corpId, String permanentCode);

}
