package com.nh.dataspider.wxwork.controller;

import com.alibaba.fastjson.JSONObject;
import com.nh.dataspider.excelimport.entity.UserEntity;
import com.nh.dataspider.redis.util.RedisUtil;
import com.nh.dataspider.util.HttpUtil;
import com.nh.dataspider.wxwork.constant.WXWorkConstants;
import com.nh.dataspider.wxwork.model.AuthAgentInfo;
import com.nh.dataspider.wxwork.model.AuthUserInfo;
import com.nh.dataspider.wxwork.model.CorpInfo;
import com.nh.dataspider.wxwork.model.DepartmentInfo;
import com.nh.dataspider.wxwork.model.SignatureModel;
import com.nh.dataspider.wxwork.model.TemplateMsgInfo;
import com.nh.dataspider.wxwork.service.IWxWorkService;
import com.nh.dataspider.wxwork.util.AesException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 企业微信
 *
 * @author YlSaaS产品组
 * @version V3.1.0
 * @copyright 上海云令智享信息技术有限公司（https://www.ylsaas.com.cn）
 * @date 2022年1月11日
 */
@Slf4j
@Controller
//@Api(tags = "企业微信", value = "wxWork")
@RequestMapping("/Permission/wxWork")
public class WxWorkController {

	@Autowired
	private IWxWorkService wxWorkService;
	@Autowired
    private RedisUtil redisUtil;
//	@Autowired
//	private TenantApi tenantApi;
//	@Autowired
//    private UserService userService;
//	@Autowired
//    private UserProvider userProvider;

	@Value("${wxwork_config.domain_url}")
    private String domainUrl;
	@Value("${wxwork_config.suite_id}")
    private String suiteId;

//    @ApiOperation("企业微信 验证回调URL")
    @GetMapping("/callback")
    @ResponseBody
//    @NoDataSourceBind
    public String wxWorkGetCallback(@RequestParam Map<String, String> requestMap, HttpServletResponse response) throws IOException {
    	String sEchoStr = wxWorkService.verifyUrl(requestMap);
    	// 验证URL成功，将sEchoStr返回]
    	return sEchoStr;
    }

//    @ApiOperation("企业微信 指令回调方法")
    @PostMapping("/callback")
    @ResponseBody
//    @NoDataSourceBind
    public String wxWorkPostCallback(@RequestParam Map<String, String> requestMap, @RequestBody String sReqData, HttpServletRequest request, HttpServletResponse response) throws AesException, ParserConfigurationException, SAXException, IOException, InterruptedException {
    	//在发生授权、通讯录变更、ticket变化等事件时，企业微信服务器会向应用的“指令回调URL”推送相应的事件消息。
    	//消息结构体将使用创建应用时的EncodingAESKey进行加密（特别注意, 在第三方回调事件中使用加解密算法，receiveid的内容为suiteid）
    	String retMsg = "success";

    	JSONObject json = wxWorkService.decryptWxWorkCallBack(requestMap, sReqData);

    	//发现企业微信回调的时候会回调两次，但两次返回的authCode一样，就会导致第二次获取永久授权码的时候出错，所以这边暂时先从redis中记录判断一下是否请求过（没有找到为什么会请求两次的原因）
    	if(json.containsKey("authCode")&&json.getString("authCode") != null && !"".equals(json.getString("authCode"))) {
    		String authCode = redisUtil.getString(json.getString("authCode"))==null?"":redisUtil.getString(json.getString("authCode")).toString();
        	if("1".equals(authCode)) {
        		return retMsg;
        	}
    	}

    	System.out.println(JSONObject.toJSONString(json));

    	String infoType = json.getString("infoType");
		if(infoType ==null || "".equals(infoType)) {
			return "fail";
		}

		switch (infoType){
			case WXWorkConstants.SUITE_TICKET :
				//suite_ticket由企业微信后台定时推送给“指令回调URL”，每十分钟更新一次
				//suite_ticket实际有效期为30分钟，可以容错连续两次获取suite_ticket失败的情况，但是请永远使用最新接收到的suite_ticket
				//每次接收企业微信的推送后，都给推送过来的 suite_ticket 缓存到 redis中，后续直接从redis中获取 suite_ticket
				String suiteTicket = json.getString("suiteTicket");
				System.out.println("推送的suiteTicket="+suiteTicket);
				redisUtil.insert(wxWorkService.getWxWorkRedisKey()+"suiteTicket", suiteTicket, 1800);
				break;
	        case WXWorkConstants.CREATE_AUTH:
	        	//授权通知（授权成功推送auth_code事件）
	        	//根据临时授权码获取永久授权码
	    		JSONObject permanentCodeJson = wxWorkService.getPermanentCode(json.getString("authCode"));
	    		redisUtil.insert(json.getString("authCode"), "1", 600);
	    		//授权方（企业）access_token,最长为512字节
	    		String accessToken = permanentCodeJson.getString("access_token");
	    		//授权方企业信息
	    		CorpInfo corpInfo = JSONObject.parseObject(permanentCodeJson.getString("auth_corp_info"), CorpInfo.class);
	    		//授权的应用信息，注意是一个数组，但仅旧的多应用套件授权时会返回多个agent，对新的单应用授权，永远只返回一个agent
	    		JSONObject authInfoJosn = JSONObject.parseObject(permanentCodeJson.getString("auth_info"));
	    		List<AuthAgentInfo> agentL = JSONObject.parseArray(authInfoJosn.getString("agent"), AuthAgentInfo.class);
	    		//授权管理员的信息，可能不返回（企业互联由上级企业共享第三方应用给下级时，不返回授权的管理员信息）
	    		AuthUserInfo adminInfo = JSONObject.parseObject(permanentCodeJson.getString("auth_user_info"), AuthUserInfo.class);

	    		//初始化好要创建的用户、岗位信息
	    		List<UserEntity> userL = Lists.newArrayList();
//	    		List<PositionEntity> positonL = Lists.newArrayList();
	    		//添加用户关系
//	    		wxWorkService.prepareUserAndPosition(agentL.get(0), accessToken, userL, positonL);
	    		//获取权限范围内的部门
//				List<DepartmentInfo> deptList = wxWorkService.getDeptList("", accessToken);
				//根据部门获取部门下的用户
//				List<String> deptIdL = deptList.stream().map(DepartmentInfo::getId).collect(Collectors.toList()).stream().map(l -> l+"").collect(Collectors.toList());
//				wxWorkService.getUserByDeptL(deptIdL, accessToken, userL, positonL);

	    		//切到默认库
//				DataSourceContext.toDefault();
				//租户注册
//	    		BaseTenantEntity tennat = wxWorkService.createTenant(corpInfo, adminInfo, permanentCodeJson.getString("permanent_code"));
//	    		System.out.println("tennat====="+tennat);
//	    		if(tennat != null) {
//	    			//更新租户的其他信息
////	    			tennat = wxWorkService.updateTenant(corpInfo, adminInfo, permanentCodeJson.getString("permanent_code"));
//
//	    			System.out.println("过滤之前的数量="+userL.size());
//	    			userL.stream().forEach(l -> {System.out.println("循环用户id="+l.getId());});
//	    			//从userL中排出授权管理员，因为在createTenant那步，已经存储了
//	    			userL = userL.stream().filter(l -> !l.getId().equals(adminInfo.getUserid())).collect(Collectors.toList());
//	    			System.out.println("过滤之后的数量="+userL.size());
//	    			userL.stream().forEach(l -> {System.out.println("循环用户id="+l.getId());});
//
//	    			//创建好account中的内容
//					wxWorkService.initAccount(userL, tennat.getId(), adminInfo.getUserid());
//
//					Thread.sleep(4000);
//
//	    			//切到具体的业务库，创建相应的部门、用户、岗位信息
//			        DataSourceContext.setDataSource(tennat.getId(), tennat.getDbserviceName());
//					//部门初始化
//			        //保存权限范围内的部门
//					wxWorkService.initDept(deptList);
//					//岗位初始化
//					wxWorkService.initPosition(positonL);
//					//用户初始化
//					wxWorkService.initUser(userL);
//	    		}else {
//	    			throw new BusinessException("创建租户失败！");
//	    		}

	            break;
	        case WXWorkConstants.CHANGE_AUTH:
	        	//授权变更通知
	        	//新增新加的授权的用户、部门
	        	//切到具体的业务库，创建相应的部门、用户、岗位信息
//	        	BaseTenantEntity tenant = tenantApi.findById(json.getString("authCorpId"));
//		        DataSourceContext.setDataSource(tenant.getId(), tenant.getDbserviceName());
//		        WxWorkModel model = wxWorkService.updateInit(json.getString("authCorpId"), tenant.getWxWorkPermanentCode());
//
//		        //切到默认库，创建好account中的内容
//				DataSourceContext.toDefault();
//				wxWorkService.deleteAccount(model.getToDeleteUserL(), tenant.getId());
//				wxWorkService.initAccount(model.getToAddUserL(), tenant.getId(), "admin");
	            break;
	        case WXWorkConstants.CHANGE_CONTACT:
	        	//成员、部门、标签通知事件
	        	String changeType = json.getString("changeType");
				switch (changeType){
					case WXWorkConstants.CREATE_USER :
						//新增成员事件

						break;
		            case WXWorkConstants.CREATE_PARTY:
		            	//新增部门事件

		            	break;
		            case WXWorkConstants.CHANGE_AUTH:
		            	//标签成员变更事件

		                break;
		            case WXWorkConstants.UPDATE_TAG:

		                break;
		            default:
		            	break;
				}

	            break;
	        case WXWorkConstants.CANCEL_AUTH:
	        	//授权取消通知
	        	//删除企业
//	        	wxWorkService.deleteTeannat(json.getString("authCorpId"));
	            break;
	        default:
	        	break;
		}

    	return retMsg;
    }
    
    /**
     * 获取授权第三方的用户id集合
     * @param tenantId 租户id
     * @return
     */
//    @NoDataSourceBind()
    @ResponseBody
    @PostMapping("/getUserList/{tenantId}")
    public List<AuthUserInfo> getUserList(@PathVariable("tenantId") String tenantId) {
    	String corpId = "";
    	String permanentCode = "";
    	String accessToken = wxWorkService.getAccessToken(corpId, permanentCode);
		List<AuthUserInfo> list = wxWorkService.getUserIdList(accessToken);
		return list;
    }
    
    /**
     * 获取授权第三方的部门id集合
     * @param tenantId 租户id
     * @return
     */
//    @NoDataSourceBind()
    @PostMapping("/getDeptList/{tenantId}")
    @ResponseBody
    public List<DepartmentInfo> getDeptList(@PathVariable("tenantId") String tenantId) {
    	String corpId = "";
    	String permanentCode = "";
    	String accessToken = wxWorkService.getAccessToken(corpId, permanentCode);
		List<DepartmentInfo> list = wxWorkService.getDeptIdList("", accessToken);
		return list;
    }
    
//    @NoDataSourceBind()
    @GetMapping("/getPreAuthCode")
    @ResponseBody
    public String getPreAuthCode() {
		return wxWorkService.getPreAuthCode();
    }
    
    /**
     * 设置授权配置
     * @return
     */
//    @NoDataSourceBind()
    @GetMapping("/setSessionInfo/{preAuthCode}")
    @ResponseBody
    public JSONObject setSessionInfo(@PathVariable("preAuthCode") String preAuthCode) {
		return wxWorkService.setSessionInfo(preAuthCode);
    }
    
    /**
     * 获取应用的Signature
     * @return
     * @throws Exception 
     */
//    @NoDataSourceBind()
    @ResponseBody
    @PostMapping("/getSignature/{tenantId}")
    public String getSignature(@PathVariable("tenantId") String tenantId, @RequestBody SignatureModel signatureModel) throws Exception {
    	String corpId = "";
    	String permanentCode = "";
    	String jsapiTicket = wxWorkService.getJsapiTicket(corpId, permanentCode);
		return wxWorkService.getSh1Signature(jsapiTicket, signatureModel.getNoncestr(), signatureModel.getTimestamp(), signatureModel.getUrl());
    }
    
    /**
     * 获取应用的Signature
     * @return
     * @throws Exception 
     */
//    @NoDataSourceBind()
    @ResponseBody
    @GetMapping("/getAgentId/{tenantId}")
    public String getAgentId(@PathVariable("tenantId") String tenantId) throws Exception {
    	String corpId = "";
    	String permanentCode = "";
    	AuthAgentInfo authAgentInfo = wxWorkService.getAuthAgentInfo(corpId, permanentCode);
    	if(authAgentInfo != null) {
    		return authAgentInfo.getAgentid()+"";
    	}else {
    		return "不存在！";
    	}
    }
    
    /**
     * 获取授权第三方的部门id集合
     * @param tenantId 租户id
     * @return
     */
//    @NoDataSourceBind()
    @PostMapping("/msgPush/{tenantId}/{dbName}")
    @ResponseBody
    public void msgPush(@PathVariable("tenantId") String tenantId, @PathVariable("dbName") String dbName, @RequestBody TemplateMsgInfo templateMsgInfo) {
    	String corpId = "";
    	String permanentCode = "";
		wxWorkService.msgPush(corpId, permanentCode, templateMsgInfo);
    }
    
    private String getToke(String userId, String corpId) throws LoginException, UnsupportedEncodingException, AesException, ParserConfigurationException, SAXException, IOException {
//    	DataSourceContext.toDefault();
//		AccountEntity accountEntity = userService.findAccountByUIdAndCorpId(userId, corpId);
//
//		System.out.println("查询到的account信息返回："+JSONObject.toJSONString(accountEntity));
//
//        //切换租户库
//        DataSourceContext.setDataSource(accountEntity.getTenant().getId(), accountEntity.getTenant().getDbserviceName());
//        UserInfo userInfo = userService.userInfo(accountEntity);
//        System.out.println("查询到的userInfo信息返回1："+JSONObject.toJSONString(userInfo));
//        if(userInfo != null){
//        	userInfo.setLoginSource("wxWorkLogin");
//        	userInfo = userProvider.add(userInfo);
//        	System.out.println("查询到的userInfo信息返回2："+JSONObject.toJSONString(userInfo));
//        }
//        System.out.println("查询到的userInfo信息返回3："+JSONObject.toJSONString(userInfo));
//        accountEntity.setUserInfoId(userInfo.getId());

        String token = "";// JwtUtil.createAccessToken(accountEntity.getUserInfoId());
        return token;
    }
    
//    @ApiOperation("企业微信租户与当前租户关联")
    @RequestMapping("/linkWxTenant")
//    @NoDataSourceBind
    public String linkWxTenant(@RequestParam Map<String, String> requestMap, HttpServletResponse response) throws InterruptedException, LoginException, AesException, ParserConfigurationException, SAXException, IOException {
    	System.out.println("进入linkWxTenant方法。。。。");
    	// 解析出url上的参数值如下：
    	//临时授权码（临时授权码10分钟后会失效，第三方服务商需尽快使用临时授权码换取永久授权码及授权信息）
        String auth_code = URLDecoder.decode(requestMap.get("auth_code"), "GBK");
        System.out.println("linkWxTenant-auth_code="+auth_code);
        //过期时间
        String expires_in = URLDecoder.decode(requestMap.get("expires_in"), "GBK");
        System.out.println("linkWxTenant-expires_in="+expires_in);
        //请求的state参数（这边传入的是当前租户的id拼接userId）
        String state = URLDecoder.decode(requestMap.get("state"), "GBK");
        System.out.println("linkWxTenant-state="+state);
        String tenantId = state.split("_")[0];
        String userId = state.split("_")[1];
        
//        DataSourceContext.toDefault();
//        BaseTenantEntity tenant = tenantApi.findById(tenantId);
//        if(tenant != null) {
//        	System.out.println("linkWxTenant-tenant="+JSONObject.toJSONString(tenant));
//        	JSONObject permanentCodeJson = wxWorkService.getPermanentCode(auth_code);
//    		redisUtil.insert(auth_code, "1", 600);
//    		
//    		//授权方（企业）access_token,最长为512字节
//    		String accessToken = permanentCodeJson.getString("access_token");
//    		//授权方企业信息
//    		CorpInfo corpInfo = JSONObject.parseObject(permanentCodeJson.getString("auth_corp_info"), CorpInfo.class);
//    		
//    		//更新租户的其他信息（corpId, 永久授权码）
//    		tenant = wxWorkService.updateTenant(tenant, corpInfo, permanentCodeJson.getString("permanent_code"));
//    		
//    		//授权的应用信息，注意是一个数组，但仅旧的多应用套件授权时会返回多个agent，对新的单应用授权，永远只返回一个agent
//    		JSONObject authInfoJosn = JSONObject.parseObject(permanentCodeJson.getString("auth_info"));
//    		List<AuthAgentInfo> agentL = JSONObject.parseArray(authInfoJosn.getString("agent"), AuthAgentInfo.class);
//    		//授权管理员的信息，可能不返回（企业互联由上级企业共享第三方应用给下级时，不返回授权的管理员信息）
////    		AuthUserInfo adminInfo = JSONObject.parseObject(permanentCodeJson.getString("auth_user_info"), AuthUserInfo.class);
//
//    		//初始化好要创建的用户、岗位信息
//    		List<UserEntity> userL = Lists.newArrayList();
//    		List<PositionEntity> positonL = Lists.newArrayList();
//    		//添加用户关系
//    		wxWorkService.prepareUserAndPosition(agentL.get(0), accessToken, userL, positonL);
//    		//获取权限范围内的部门
//    		List<DepartmentInfo> deptList = wxWorkService.getDeptList("", accessToken);
//    		//根据部门获取部门下的用户
//    		List<String> deptIdL = deptList.stream().map(DepartmentInfo::getId).collect(Collectors.toList()).stream().map(l -> l+"").collect(Collectors.toList());
//    		wxWorkService.getUserByDeptL(deptIdL, accessToken, userL, positonL);
//
//    		//切到默认库
//    		DataSourceContext.toDefault();
//    		System.out.println("过滤之前的数量="+userL.size());
//			userL.stream().forEach(l -> {System.out.println("循环用户id="+l.getId());});
//			//从userL中排出授权管理员，因为在createTenant那步，已经存储了
////			userL = userL.stream().filter(l -> !l.getId().equals(adminInfo.getUserid())).collect(Collectors.toList());
//			System.out.println("过滤之后的数量="+userL.size());
//			userL.stream().forEach(l -> {System.out.println("循环用户id="+l.getId());});
//
//			//创建好account中的内容
//			wxWorkService.initAccount(userL, tenant.getId(), userId);
//
//			Thread.sleep(4000);
//
//			//切到具体的业务库，创建相应的部门、用户、岗位信息
//	        DataSourceContext.setDataSource(tenant.getId(), tenant.getDbserviceName());
//			//部门初始化
//	        //保存权限范围内的部门
//			wxWorkService.initDept(deptList);
//			//岗位初始化
////			wxWorkService.initPosition(positonL);
//			//用户初始化
//			wxWorkService.initUser(userL);
//        }
        
        String token = "";// getToke(userId, tenant.getQywxCorpId());
        System.out.println("token="+token);
//                System.out.println("doLogin的URL=====："+request.getSession().getId()+"跳转地址：" + domainUrl + "/?token="+token);
        return "redirect:"+domainUrl+"/ylsaas/system/service?token="+token;
    }
    
//    @ApiOperation("企业微信 应用主页")
    @RequestMapping("/login")
//    @NoDataSourceBind
    public String oAuth2API(HttpServletRequest request,HttpServletResponse response) {
    	System.out.println("serverName="+request.getServerName());
    	System.out.println("contextPath="+request.getContextPath());
        //获取项目域名
        log.info("domain name: " + domainUrl);
        //拼接微信回调地址
//        String callbackUrl =  domainUrl + contextPath + "/wxWork/doLogin";
        String callbackUrl = domainUrl+"/api/system/Permission/wxWork/doLogin";
        String redirect_uri = "";
        try {
               redirect_uri = URLEncoder.encode(callbackUrl, "utf-8");
        } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
               log.error("cdoe error: " + e.getMessage());
        }
        //snsapi_base：静默授权，可获取成员的基础信息（UserId与DeviceId）；
        //snsapi_userinfo：静默授权，可获取成员的详细信息，但不包含头像、二维码等敏感信息；
        //snsapi_privateinfo：手动授权，可获取成员的详细信息，包含头像、二维码等敏感信息。
        String oauth2Url = "redirect:"+WXWorkConstants.OAUTH2_URL + "?appid=" + suiteId + "&redirect_uri=" + redirect_uri + "&response_type=code&scope=snsapi_privateinfo&state=ylsaas#wechat_redirect";
        return oauth2Url;
    }
  /*  @ApiOperation("微信小程序登陆")
    @RequestMapping("/miniLogin")
    @NoDataSourceBind
    public String miniLogin(HttpServletRequest request,HttpServletResponse response) throws LoginException {
        System.out.println("serverName="+request.getServerName());
        System.out.println("contextPath="+request.getContextPath());
        String code = request.getParameter("code");
        System.out.println("miniLogin方法里的code=="+code);

        //拼接微信回调地址
        String callbackUrl = domainUrl+"/api/system/Permission/wxWork/doMiniLogin";
        String redirect_uri = "";
        try {
            redirect_uri = URLEncoder.encode(callbackUrl, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("code error: " + e.getMessage());
        }
        //snsapi_base：静默授权，可获取成员的基础信息（UserId与DeviceId）；
        //snsapi_userinfo：静默授权，可获取成员的详细信息，但不包含头像、二维码等敏感信息；
        //snsapi_privateinfo：手动授权，可获取成员的详细信息，包含头像、二维码等敏感信息。
        String oauth2Url = "redirect:"+WXWorkConstants.OAUTH2_URL + "?appid=" + suiteId + "&redirect_uri=" + redirect_uri + "&response_type=code&scope=snsapi_privateinfo&state=ylsaas#wechat_redirect";
        return oauth2Url;
       // return getThirdLoginToken(code,miniAccessToken);
    }*/

//    @ApiOperation("企业微信 登录")
    @RequestMapping("/doLogin")
//    @NoDataSourceBind
    public String oAuth2Login(HttpServletRequest request, @RequestParam String code,HttpServletResponse response) throws LoginException, UnsupportedEncodingException, AesException, ParserConfigurationException, SAXException, IOException {
        System.out.println("code************:" + code);
        //从redis中获取access_token
        String suiteAccessToken = wxWorkService.getSuiteAccessToken();
        System.out.println("===从redis中获取的token"+suiteAccessToken);
        if(suiteAccessToken != null && !"".equals(suiteAccessToken)){
            String url = WXWorkConstants.CORP_USER_INFO_URL+suiteAccessToken+"&code="+code;
            String result = HttpUtil.sendHttpGet(url);
            System.out.println("user : "+result);

            //格式转换
            JSONObject resultJosn = JSONObject.parseObject(result);
            System.out.println("访问用户身份获取返回："+JSONObject.toJSONString(resultJosn));
            //{"errcode":0,"user_ticket":"gh87H1aREclIlDZIQjSz4qPNK8A-XpVEGAsHco5Ij_IR1SUTADGiiuYn9RRcqeEBcLTe5ZNeiAKPqjTXYHvDZRWVvF1waFv_1UYxV-LIO9o","CorpId":"wwdb56e4dbb0ef0d4f","UserId":"li.zhen","DeviceId":"cba5f1b3cb5d2d15","errmsg":"ok","open_userid":"woCPDHCQAA3X41G5Uj3YBTG8OTVmITAw","expires_in":1800,"parents":[]}
            if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
				throw new RuntimeException("访问用户身份获取失败！" + resultJosn.getString("errmsg"));
			}

            String userId = resultJosn.getString("UserId");
            String corpId = resultJosn.getString("CorpId");
            
            String token = getToke(userId, corpId);
            System.out.println("token="+token);
//                    System.out.println("doLogin的URL=====："+request.getSession().getId()+"跳转地址：" + domainUrl + "/?token="+token);
            return "redirect:"+domainUrl+"/ylsaas/NewDashBoard?token="+token;
        }else{
                log.info("suite_access_token失效啦，登陆失败~~~~");
                // 跳转至登陆界面
                return "redirect:?token=" + "";
        }
    }
//    @ApiOperation("小程序免密登陆")
    @RequestMapping("/miniLogin")
//    @NoDataSourceBind
    @ResponseBody
    public Object getThirdLoginToken(HttpServletRequest request, @RequestParam String code,HttpServletResponse response) throws LoginException {
//        String miniAccessToken = wxWorkService.getMiniAccessToken();
//        String url = WXWorkConstants.MINI_USER_INFO_URL+miniAccessToken+"&js_code="+code+"&grant_type=authorization_code";
//        System.out.println("===code===="+code);
//        String result = HttpUtil.sendHttpGet(url);
//        System.out.println("user : "+result);
//
//        //格式转换
//        JSONObject resultJosn = JSONObject.parseObject(result);
//        System.out.println("getThirdLoginToken==访问用户身份获取返回："+JSONObject.toJSONString(resultJosn));
//        //{"corpid": "CORPID","userid": "USERID","session_key": "kJtdi6RF+Dv67QkbLlPGjw==","errcode": 0,"errmsg": "ok"}
//        if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
//            throw new RuntimeException("访问用户身份获取失败！" + resultJosn.getString("errmsg"));
//        }
//        //注：第三方小程序此处返回加密的userid
//        String userId = resultJosn.getString("userid");
//        System.out.println("用户在企业内的UserId==="+userId);
//        String corpId = resultJosn.getString("corpid");
//        //会话密钥 session_key 是对用户数据进行加密签名的密钥，为了应用自身的数据安全，开发者服务器不应该把会话密钥下发到小程序，也不应该对外提供这个密钥
//        String session_key = resultJosn.getString("session_key");
//        System.out.println("会话密钥==="+session_key);
//
//        DataSourceContext.toDefault();
//        AccountEntity accountEntity = userService.findAccountByUIdAndTId(userId, corpId);
//
//        System.out.println("查询到的account信息返回："+JSONObject.toJSONString(accountEntity));
//
//        //切换租户库
//        DataSourceContext.setDataSource(accountEntity.getTenant().getId(), accountEntity.getTenant().getDbserviceName());
//        UserInfo userInfo = userService.userInfo(accountEntity);
//        System.out.println("查询到的userInfo信息返回1："+JSONObject.toJSONString(userInfo));
//        if(userInfo != null){
//            userInfo.setLoginSource("wxWorkLogin");
//            userInfo = userProvider.add(userInfo);
//            System.out.println("查询到的userInfo信息返回2："+JSONObject.toJSONString(userInfo));
//        }
//        System.out.println("查询到的userInfo信息返回3："+JSONObject.toJSONString(userInfo));
//        accountEntity.setUserInfoId(userInfo.getId());
//
//        String token = JwtUtil.createAccessToken(accountEntity.getUserInfoId());
//        System.out.println("token="+token);
//        MiniLoginEntity loginVO = new MiniLoginEntity();
//        loginVO.setToken(token);
//        loginVO.setAccount(accountEntity.getLoginName());
//        return loginVO;
    	return null;
    }

//    @ApiOperation("企业微信 扫码登录")
    @RequestMapping("/qrLogin")
//    @NoDataSourceBind
    public String qrLogin(HttpServletRequest request, @RequestParam String auth_code,HttpServletResponse response) throws LoginException, UnsupportedEncodingException, AesException, ParserConfigurationException, SAXException, IOException {
        System.out.println("authCode************:" + auth_code);
        String providerToken = wxWorkService.getProviderToken();
        System.out.println("providerToken==="+providerToken);
        if(providerToken != null && !"".equals(providerToken)){
        	JSONObject loginInfo = wxWorkService.getLoginInfo(providerToken, auth_code);

        	AuthUserInfo authUserInfo = JSONObject.parseObject(loginInfo.get("user_info").toString(), AuthUserInfo.class);

        	String userId = authUserInfo.getUserid();
            String corpId = loginInfo.getJSONObject("corp_info").get("corpid").toString();

            String token = getToke(userId, corpId);
            System.out.println("token="+token);
//                System.out.println("doLogin的URL=====："+request.getSession().getId()+"跳转地址：" + domainUrl + "/?token="+token);
            return "redirect:"+domainUrl+"/ylsaas/NewDashBoard?token="+token;
        }else{
                log.info("suite_access_token失效啦，登陆失败~~~~");
                // 跳转至登陆界面
                return "redirect:?token=" + "";
        }
    }

//    @ApiOperation("企业微信数据回调方法（与指令回调不同的是，解密的receivedid不一样）")
    @PostMapping("/dataCallback")
    @ResponseBody
//    @NoDataSourceBind
    public String wxWorkDataCallback(@RequestParam Map<String, String> requestMap, @RequestBody String sReqData, HttpServletResponse response) throws IOException, AesException, ParserConfigurationException, SAXException, InterruptedException {
//    	String sEchoStr = wxWorkService.verifyUrl(requestMap);
    	return "success";
    }

//    @ApiOperation("企业微信数据回调方法（与指令回调不同的是，解密的receivedid不一样）")
    @GetMapping("/dataCallback")
    @ResponseBody
//    @NoDataSourceBind
    public String wxWorkDataGetCallback(@RequestParam Map<String, String> requestMap, HttpServletResponse response) throws IOException, AesException, ParserConfigurationException, SAXException, InterruptedException {
    	String sEchoStr = wxWorkService.verifyUrl(requestMap);
    	return sEchoStr;
    }
//    @ApiOperation("获取微信小程序应用的jsapi_ticket")
    @RequestMapping("/getMiniTicket")
//    @NoDataSourceBind
    @ResponseBody
    public Object getMiniTicket() {
        Object miniTicket = wxWorkService.getMiniTicket();
        return miniTicket;
    }
}
