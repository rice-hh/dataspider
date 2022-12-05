package com.nh.dataspider.wxwork.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.ServletInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.nh.dataspider.excelimport.entity.UserEntity;
import com.nh.dataspider.util.HttpUtil;
import com.nh.dataspider.util.Sh1Util;
import com.nh.dataspider.wxwork.constant.WXWorkConstants;
import com.nh.dataspider.wxwork.model.AuthAgentInfo;
import com.nh.dataspider.wxwork.model.AuthUserInfo;
import com.nh.dataspider.wxwork.model.CorpInfo;
import com.nh.dataspider.wxwork.model.DepartmentInfo;
import com.nh.dataspider.wxwork.model.TemplateMsgInfo;
import com.nh.dataspider.wxwork.model.WxWorkModel;
import com.nh.dataspider.wxwork.service.IWxWorkService;
import com.nh.dataspider.wxwork.util.AesException;
import com.nh.dataspider.wxwork.util.WXBizMsgCrypt;

@Service
public class WxWorkServiceImpl implements IWxWorkService {

    @Autowired
//    private RedisUtil redisUtil;
//    @Autowired
//    protected TenantApi tenantApi;
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private PositionService positionService;
//    @Autowired
//    private OrganizeService organizeService;
//    @Autowired
//    private AccountService accountService;

    @Value("${wxwork_config.suite_id}")
    private String suiteId;
    @Value("${wxwork_config.suite_secret}")
    private String suiteSecret;
    @Value("${wxwork_config.stoken}")
    private String stoken;
    @Value("${wxwork_config.sencoding_aeskey}")
    private String sencodingAeskey;
    @Value("${wxwork_config.domain_url}")
    private String domainUrl;
    @Value("${wxwork_config.session_auth_type}")
    private String session_auth_type;
    @Value("${wxwork_config.template_id}")
    private String templateId;

    //企业微信相关信息存在redis中的key的公共前缀
//    String wxWorkRedisKey = "wxWork:"+suiteId+":";

    String miniRedisKey = "miniProgram:";

    String ticketRedisKey = "miniTicket:";
    
    //应用通知模板id
//    String templateId= "ttCPDHCQAAtEPVukclZCW4pELH3za61Q";
    
    @Override
    public String getWxWorkRedisKey() {
    	return "wxWork:"+suiteId+":";
    }

    /**
     * 验证回调url
     */
    @Override
    public String verifyUrl(Map<String, String> requestMap) {
        String sEchoStr = "";
        try {

            System.out.println("验证回调url的返回信息："+com.alibaba.fastjson.JSONObject.toJSONString(requestMap));

            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(stoken, sencodingAeskey, WXWorkConstants.SCORP_ID);

            // 解析出url上的参数值如下：
            String sVerifyMsgSig = URLDecoder.decode(requestMap.get("msg_signature"), "GBK");
            String sVerifyTimeStamp = URLDecoder.decode(requestMap.get("timestamp"), "GBK");
            String sVerifyNonce = URLDecoder.decode(requestMap.get("nonce"), "GBK");
            String sVerifyEchoStr = URLDecoder.decode(requestMap.get("echostr"), "GBK");

            //需要返回的明文
            sEchoStr = wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, sVerifyEchoStr);
        } catch (Exception e) {
            //验证URL失败，错误原因请查看异常
            e.printStackTrace();
        }
        return sEchoStr;
    }

    @Override
    public JSONObject decryptWxWorkCallBack(Map<String, String> requestMap, ServletInputStream in) throws AesException, ParserConfigurationException, SAXException, IOException {
        String sReqData = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String tempStr = "";   //作为输出字符串的临时串，用于判断是否读取完毕
        while(null != (tempStr=reader.readLine())){
        	sReqData+=tempStr;
        }
        
        return this.decryptWxWorkCallBack(requestMap, sReqData);
    }
    
    @Override
    public JSONObject decryptWxWorkCallBack(Map<String, String> requestMap, String sReqData) throws AesException, ParserConfigurationException, SAXException, IOException {
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(stoken, sencodingAeskey, suiteId);
        
        System.out.println("返回解密后的返回信息sReqData："+sReqData);
        System.out.println("返回解密后的返回信息map："+JSONObject.toJSONString(requestMap));

        // 解析出url上的参数值如下：
        String sVerifyMsgSig = URLDecoder.decode(requestMap.get("msg_signature"), "GBK");
        String sVerifyTimeStamp = URLDecoder.decode(requestMap.get("timestamp"), "GBK");
        String sVerifyNonce = URLDecoder.decode(requestMap.get("nonce"), "GBK");

        //返回解密后的原文
        String decryptMsg = wxcpt.DecryptMsg(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, sReqData);
        System.out.println("返回解密后的返回信息："+decryptMsg);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        StringReader sr = new StringReader(decryptMsg);
        InputSource is = new InputSource(sr);
        Document document = db.parse(is);

        Element root = document.getDocumentElement();

        String infoType = root.getElementsByTagName("InfoType").item(0) != null
                ? root.getElementsByTagName("InfoType").item(0).getTextContent() : "";
        String suiteId = root.getElementsByTagName("SuiteId").item(0) != null
                ? root.getElementsByTagName("SuiteId").item(0).getTextContent() : "";
        String timeStamp = root.getElementsByTagName("TimeStamp").item(0) != null
                ? root.getElementsByTagName("TimeStamp").item(0).getTextContent() : "";

        JSONObject json = new JSONObject();
        json.put("suiteId", suiteId);
        json.put("timeStamp", timeStamp);
        json.put("infoType", infoType);

        //推送 suite_ticket
        if(WXWorkConstants.SUITE_TICKET.equals(infoType)) {
            String tuiteTicket = root.getElementsByTagName("SuiteTicket").item(0).getTextContent();
            json.put("suiteTicket", tuiteTicket);
            //企业授权
        }else if(WXWorkConstants.CREATE_AUTH.equals(infoType)) {
            String auth_code = root.getElementsByTagName("AuthCode").item(0).getTextContent();
            json.put("authCode", auth_code);
            //授权变更
        }else if(WXWorkConstants.CHANGE_AUTH.equals(infoType)) {
            String auth_corp_id = root.getElementsByTagName("AuthCorpId").item(0).getTextContent();
            json.put("authCorpId", auth_corp_id);
            //取消授权
        }else if(WXWorkConstants.CANCEL_AUTH.equals(infoType)) {
            String auth_corp_id = root.getElementsByTagName("AuthCorpId").item(0).getTextContent();
            json.put("authCorpId", auth_corp_id);
        }else{
            // 事件回调（进入应用）
            //企业微信CorpID
            String toUserName = root.getElementsByTagName("ToUserName").item(0) != null
                    ? root.getElementsByTagName("ToUserName").item(0).getTextContent() : "";
            //成员UserID
            String fromUserName = root.getElementsByTagName("FromUserName").item(0) != null
                    ? root.getElementsByTagName("FromUserName").item(0).getTextContent() : "";
            String createTime = root.getElementsByTagName("CreateTime").item(0) != null
                    ? root.getElementsByTagName("CreateTime").item(0).getTextContent() : "";
            //消息类型，此时固定为：event
            String msgType = root.getElementsByTagName("MsgType").item(0) != null
                    ? root.getElementsByTagName("MsgType").item(0).getTextContent() : "";
            //事件类型：enter_agent（进入应用）
            String event = root.getElementsByTagName("Event").item(0) != null
                    ? root.getElementsByTagName("Event").item(0).getTextContent() : "";
            //事件KEY值，此事件该值为空
            String eventKey = root.getElementsByTagName("EventKey").item(0) != null
                    ? root.getElementsByTagName("EventKey").item(0).getTextContent() : "";
            //企业应用的id，整型。可在应用的设置页面查看
            String agentID = root.getElementsByTagName("AgentID").item(0) != null
                    ? root.getElementsByTagName("AgentID").item(0).getTextContent() : "";
            json.put("toUserName", toUserName);
            json.put("fromUserName", fromUserName);
            json.put("createTime", createTime);
            json.put("msgType", msgType);
            json.put("event", event);
            json.put("eventKey", eventKey);
            json.put("agentID", agentID);
        }

        return json;
    }

    /**
     * 获取第三方应用凭证（通过本接口获取的suite_access_token有效期为2小时，开发者需要进行缓存，不可频繁获取。）
     * @param suiteTicket 企业微信后台推送的ticket
     * @return
     */
    @Override
    public String getSuiteAccessToken() {
    	String wxWorkRedisKey = getWxWorkRedisKey();
        String redisKey = wxWorkRedisKey+"accessToken";
        String accessToken = "";// redisUtil.getString(redisKey)==null?"":redisUtil.getString(redisKey).toString();
        System.out.println("===从redis中获取的token"+accessToken);
        if("".equals(accessToken)) {
            JSONObject params = new JSONObject();
            //应用Id
            params.put("suite_id", suiteId);
            //应用secret
            params.put("suite_secret", suiteSecret);
            //企业微信后台推送的ticket
//            if(redisUtil.getString(wxWorkRedisKey+"suiteTicket") == null) {
//                throw new RuntimeException("suiteTicket失效，请重新刷新ticket！");
//            }
            String suiteTicket = "";//redisUtil.getString(wxWorkRedisKey+"suiteTicket").toString();
            System.out.println("redis中suiteTicket="+suiteTicket);
            params.put("suite_ticket", suiteTicket);
            //请求企业微信获取第三方应用凭证的接口
            String result = HttpUtil.sendHttpJsonPost(WXWorkConstants.SUITE_ACCESS_TOKEN_URL, params);
            System.out.println("第三方应用凭证获取返回结果："+result);
            JSONObject resultJosn = JSONObject.parseObject(result);
            if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
                throw new RuntimeException("第三方应用凭证获取失败！" + resultJosn.getString("errmsg"));
            }
            accessToken = resultJosn.getString("suite_access_token");
            System.out.println("请求企业微信获取的accessToken="+accessToken);
            //缓存到redis中（key:wxWork:应用id:accessToken）
            //通过本接口获取的suite_access_token有效期为2小时，开发者需要进行缓存，不可频繁获取。
//            redisUtil.insert(redisKey, accessToken, 7000);
        }
        return accessToken;
    }
    /**
     * 获取微信小程序应用凭证（通过本接口获取的access_token有效期为2小时，开发者需要进行缓存，不可频繁获取。）
     * @return
     */
    @Override
    public String getMiniAccessToken() {
        String redisKey = miniRedisKey+"accessToken";
        String accessToken = "";//redisUtil.getString(redisKey)==null?"":redisUtil.getString(redisKey).toString();
        System.out.println("===从redis中获取的小程序的accessToken"+accessToken);
        if(accessToken != null && !"".equals(accessToken)) {
            String url = WXWorkConstants.ACCESS_TOKEN_URL + "?corpid=" + WXWorkConstants.SCORP_ID + "&corpsecret=" + WXWorkConstants.MINI_SECRET;
            String result = HttpUtil.sendHttpGet(url);
            System.out.println("获取小程序accessToken的返回结果："+result);
            JSONObject resultJosn = JSONObject.parseObject(result);
            if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
                throw new RuntimeException("获取accessToken失败！" + resultJosn.getString("errmsg"));
            }
            accessToken = resultJosn.getString("access_token");
            long expireTime = resultJosn.getLongValue("expires_in");
            System.out.println("请求企业微信获取的accessToken="+accessToken);
            //缓存到redis中（key:wxWork:应用id:accessToken）
            //通过本接口获取的access_token有效期为2小时，开发者需要进行缓存，不可频繁获取。
//            redisUtil.insert(redisKey, accessToken, expireTime);
        }
        return accessToken;
    }
    @Override
    public String getMiniTicket(){
        String redisKey = ticketRedisKey+"ticket";
        String ticket = "";//redisUtil.getString(redisKey)==null?"":redisUtil.getString(redisKey).toString();
        if(ticket != null && !"".equals(ticket)) {
            String miniAccessToken = getMiniAccessToken();
            String url = WXWorkConstants.JSAPI_TICKET_URL+miniAccessToken+"&type=agent_config";
            String result = HttpUtil.sendHttpGet(url);
            System.out.println("ticket返回字符串 : "+result);

            //格式转换
            JSONObject resultJosn = JSONObject.parseObject(result);
            //{"errcode":0,"errmsg":"ok","ticket":"bxLdikRXVbTPdHSM05e5u5sUoXNKd8-41ZO3MhKoyN5OfkWITDGgnr2fwJ0m9E8NYzWKVZvdVtaUgWvsdshFKA","expires_in":7200}
            if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
                throw new RuntimeException("获取Ticket失败！" + resultJosn.getString("errmsg"));
            }
            ticket = resultJosn.getString("ticket");
            long expireTime = resultJosn.getLongValue("expires_in");
            System.out.println("jsapi_ticket======="+ticket);
//            redisUtil.insert(redisKey,ticket,expireTime);
        }
        return ticket;
    }

    /**
     * 获取服务商的token
     * @return
     */
    @Override
    public String getProviderToken() {
        JSONObject params = new JSONObject();
        //应用Id
        params.put("corpid", WXWorkConstants.SCORP_ID);
        //应用secret
        params.put("provider_secret", WXWorkConstants.PROVIDER_SECRET);
        //请求企业微信获取登录用户信息的接口
        String result = HttpUtil.sendHttpJsonPost(WXWorkConstants.GET_PROVIDER_TOKEN, params);
        System.out.println("登录用户信息获取返回结果："+result);
        JSONObject resultJosn = JSONObject.parseObject(result);
        if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
            throw new RuntimeException("登录用户信息获取失败！" + resultJosn.getString("errmsg"));
        }
        String providerAccessToken = resultJosn.getString("provider_access_token");
        return providerAccessToken;
    }

    /**
     * 获取登录用户信息
     * @param providerToken 授权登录服务商的网站时，使用应用提供商的provider_access_token
     * @param authCode oauth2.0授权企业微信管理员登录产生的code，最长为512字节。只能使用一次，5分钟未被使用自动过期
     * @return
     */
    @Override
    public JSONObject getLoginInfo(String providerToken, String authCode) {
        JSONObject params = new JSONObject();
        //oauth2.0授权企业微信管理员登录产生的code，最长为512字节。只能使用一次，5分钟未被使用自动过期
        params.put("auth_code", authCode);
        //请求企业微信获取登录用户信息的接口
        String result = HttpUtil.sendHttpJsonPost(WXWorkConstants.GET_LOGIN_INFO+providerToken, params);
        System.out.println("登录用户信息获取返回结果："+result);
        JSONObject resultJosn = JSONObject.parseObject(result);
        if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
            throw new RuntimeException("登录用户信息获取失败！" + resultJosn.getString("errmsg"));
        }
        return resultJosn;
    }

    /**
     * 获取用户敏感信息
     * @param userTicket 成员票据
     * @return
     */
    @Override
    public JSONObject getUserDetailInfo(String userTicket) {
        JSONObject params = new JSONObject();
        //成员票据
        params.put("user_ticket", userTicket);
        String suiteAccessToken = this.getSuiteAccessToken();
        //请求企业微信获取登录用户信息的接口
        String result = HttpUtil.sendHttpJsonPost(WXWorkConstants.USER_DETAIL_INFO+suiteAccessToken, params);
        System.out.println("用户敏感信息获取返回结果："+result);
        JSONObject resultJosn = JSONObject.parseObject(result);
        if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
            throw new RuntimeException("用户敏感信息获取失败！" + resultJosn.getString("errmsg"));
        }
        return resultJosn;
    }

    /**
     * 获取企业凭证（第三方服务商在取得企业的永久授权码后，通过此接口可以获取到企业的access_token。）
     * @param corpId 授权方corpid
     * @param permanentCode 永久授权码，通过get_permanent_code获取
     * @return
     */
    @Override
    public String getAccessToken(String corpId, String permanentCode) {
        JSONObject params = new JSONObject();
        //应用Id
        params.put("auth_corpid", corpId);
        //应用secret
        params.put("permanent_code", permanentCode);
        //请求企业微信获取企业凭证的接口
        String result = HttpUtil.sendHttpJsonPost(WXWorkConstants.CORP_INFO_URL+this.getSuiteAccessToken(), params);
        System.out.println("企业凭证获取返回结果："+result);
        JSONObject resultJosn = JSONObject.parseObject(result);
        if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
            throw new RuntimeException("企业凭证获取失败！" + resultJosn.getString("errmsg"));
        }
        return resultJosn.getString("access_token");
    }

    /**
     * 变更授权通知 新增新的部门、用户、岗位之类的
     * @param corpId 授权方的corpid
     * @param permanentCode 永久授权码
     * @return
     */
    @Override
    public WxWorkModel updateInit(String corpId, String permanentCode) {
        //查出该租户已经存在的用户、部门
//        List<OrganizeEntity> orgL = organizeService.getList();
//        List<UserEntity> userL = userService.getList();
//        System.out.println("userL="+JSONObject.toJSONString(userL));
//        List<PositionEntity> positionL = positionService.getList();
//
//        List<String> existDeptIdL = orgL.stream().map(OrganizeEntity::getId).collect(Collectors.toList());
//        List<String> existUserIdL = userL.stream().map(UserEntity::getId).collect(Collectors.toList());
//        List<String> existPositionIdL = positionL.stream().map(PositionEntity::getId).collect(Collectors.toList());
//
//        //获取企业凭证
//        String accessToken = getAccessToken(corpId, permanentCode);
//
//        JSONObject authInfo = this.getAuthInfo(corpId, permanentCode);
//        //授权的应用信息，注意是一个数组，但仅旧的多应用套件授权时会返回多个agent，对新的单应用授权，永远只返回一个agent
//        JSONObject authInfoJosn = JSONObject.parseObject(authInfo.getString("auth_info"));
//        List<AuthAgentInfo> agentL = com.alibaba.fastjson.JSONObject.parseArray(authInfoJosn.getString("agent"), AuthAgentInfo.class);
//
//        List<UserEntity> authUserL = Lists.newArrayList();
//        List<UserEntity> toDeleteUserL = Lists.newArrayList();
//        List<PositionEntity> positonL = Lists.newArrayList();
//
//        List<DepartmentInfo> deptList = this.getDeptList("", accessToken);
//        List<DepartmentInfo> toCreateDeptL = deptList.stream().filter(l -> !existDeptIdL.contains(l.getId()+"")).collect(Collectors.toList());
//        List<String> toDeleteDeptIdL = existDeptIdL.stream().filter(l -> !deptList.stream().anyMatch(t -> l.equals(t.getId()+""))).collect(Collectors.toList());
//        //根据部门获取部门下的用户
//        List<String> deptIdL = deptList.stream().map(DepartmentInfo::getId).collect(Collectors.toList()).stream().map(l -> l+"").collect(Collectors.toList());
//        this.getUserByDeptL(deptIdL, accessToken, authUserL, positonL);
//        //根据部门id获取要删除的部门下的用户
//        System.out.println("要删除的部门id="+JSONObject.toJSONString(toDeleteDeptIdL));
//        this.getUserByDeptL(toDeleteDeptIdL, accessToken, toDeleteUserL, null);
//        System.out.println("要删除的用户deleteUserL="+JSONObject.toJSONString(toDeleteUserL));
//
//        //获取权限范围内的用户、岗位
//        this.prepareUserAndPosition(agentL.get(0), accessToken, authUserL, positonL);
//        List<UserEntity> newUserL = authUserL.stream().distinct().collect(Collectors.toList());
//        System.out.println("newUserL="+JSONObject.toJSONString(newUserL));
//        List<UserEntity> toCreateUserL = newUserL.stream().filter(l -> !existUserIdL.contains(l.getId())).collect(Collectors.toList());
//        toDeleteUserL.addAll(userL.stream().filter(l -> !authUserL.stream().anyMatch(t -> t.getId().equals(l.getId()))).collect(Collectors.toList()));
//        //排出管理员
//        toDeleteUserL = toDeleteUserL.stream().filter(l -> !(l.getIsAdministrator()==1)).collect(Collectors.toList());
//        System.out.println("要删除的用户toDeleteUserL="+JSONObject.toJSONString(toDeleteUserL));
//
//        positonL = positonL.stream().distinct().collect(Collectors.toList());
//        List<PositionEntity> toCreatePositionL = positonL.stream().filter(l -> !existPositionIdL.contains(l.getId())).collect(Collectors.toList());
//
////        this.initDept(toCreateDeptL);
//        this.initPosition(toCreatePositionL);
//        System.out.println("要删除的用户toDeleteUserL汇总="+JSONObject.toJSONString(toDeleteUserL));
//        this.deleteUser(toDeleteUserL);
////        this.initUser(toCreateUserL);
        List<UserEntity> toCreateUserL = Lists.newArrayList();
        List<UserEntity> toDeleteUserL = Lists.newArrayList();
        WxWorkModel model = WxWorkModel.builder().toAddUserL(toCreateUserL).toDeleteUserL(toDeleteUserL).build();
        return model;
    }

    /**
     * 根据部门集合，获取这些部门下的所有用户
     * @param getUserByDeptL 部门id集合
     * @param accessToken 授权企业的凭证
     * @param userL
     * @param positonL
     */
//    @Override
//    public void getUserByDeptL(List<String> toCreateDeptIdL, String accessToken, List<UserEntity> userL, List<PositionEntity> positonL) {
//        System.out.println("部门集合长度="+toCreateDeptIdL.size());
//        if(toCreateDeptIdL != null && toCreateDeptIdL.size()>0) {
//            List<AuthUserInfo> deptUserListAll = Lists.newArrayList();
//            //获取部门成员
//            for(String deptId : toCreateDeptIdL) {
//                deptUserListAll.addAll(this.getDeptUserList(deptId, accessToken, 1));
//            }
//            deptUserListAll = deptUserListAll.stream().distinct().collect(Collectors.toList());
//            List<String> userIdL = deptUserListAll.stream().map(AuthUserInfo::getUserid).collect(Collectors.toList());
//            System.out.println("getUserByDeptL == userIdL="+userIdL);
//            if(userIdL.size()>0) {
//                this.initUserEntityL(userIdL, accessToken, userL, positonL);
//            }
//        }
//    }

    /**
     * 取消授权的时候 删除企业信息
     * @param authCorpId 取消授权的企业的id
     */
    @Override
    public void deleteTeannat(String authCorpId) {
//        BaseTenantDeForm deForm = new BaseTenantDeForm();
//        deForm.setIsClear(1);
//        tenantApi.delete(authCorpId, deForm);
    }

    public void deleteUser(List<UserEntity> userL) {
//        if(userL != null && userL.size()>0) {
//            for(UserEntity u : userL) {
//                userService.delete(u);
//            }
//        }
    }

    /**
     * 获取永久授权码
     * @param auth_code 临时授权码
     * @return
     * @throws JSONException
     */
    @Override
    public JSONObject getPermanentCode(String auth_code) throws JSONException {
        //获取第三方应用凭证
        String suiteAccessToken = getSuiteAccessToken();

        JSONObject params = new JSONObject();
        params.put("auth_code", auth_code);
        String result = HttpUtil.sendHttpJsonPost(WXWorkConstants.PERMANENT_CODE_URL+suiteAccessToken, params);
        System.out.println("永久授权码获取返回结果： "+result);
        //格式转换
        JSONObject resultJosn = JSONObject.parseObject(result);
        //注意：因历史原因，该接口在调用失败时才返回errcode。没返回errcode视为调用成功
        if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))){
            throw new RuntimeException("授权码获取失败！" + resultJosn.getString("errmsg"));
        }
        return resultJosn;
    }
    
    /**
     * 获取预授权码
     * @return
     * @throws JSONException
     */
    @Override
    public String getPreAuthCode() throws JSONException {
        //获取第三方应用凭证
        String suiteAccessToken = getSuiteAccessToken();
        String result = HttpUtil.sendHttpGet(WXWorkConstants.PRE_AUTH_CODE+suiteAccessToken);
        System.out.println("获取预授权码返回结果： "+result);
        //格式转换
        JSONObject resultJosn = JSONObject.parseObject(result);
        //注意：因历史原因，该接口在调用失败时才返回errcode。没返回errcode视为调用成功
        if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))){
            throw new RuntimeException("获取预授权码失败！" + resultJosn.getString("errmsg"));
        }
        return resultJosn.getString("pre_auth_code");
    }
    
    /**
     * 获取应用的jsapi_ticket
     * @param corpId
     * @param permanentCode
     * @return
     * @throws JSONException
     */
	@Override
    public String getJsapiTicket(String corpId, String permanentCode) throws JSONException {
    	 String redisKey = ticketRedisKey+"ticket";
         String ticket = "";// redisUtil.getString(redisKey)==null?"":redisUtil.getString(redisKey).toString();
         if(ticket != null && !"".equals(ticket)){
             String accessToken = getAccessToken(corpId, permanentCode);
             //access_token	:第三方应用开发，获取方式参考“获取企业凭证”
             String url = WXWorkConstants.JSAPI_TICKET_URL+accessToken+"&type=agent_config";
             String result = HttpUtil.sendHttpGet(url);
             System.out.println("获取应用的jsapi_ticket返回字符串 : "+result);

             //格式转换
             JSONObject resultJosn = JSONObject.parseObject(result);
             //{"errcode":0,"errmsg":"ok","ticket":"bxLdikRXVbTPdHSM05e5u5sUoXNKd8-41ZO3MhKoyN5OfkWITDGgnr2fwJ0m9E8NYzWKVZvdVtaUgWvsdshFKA","expires_in":7200}
             if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
                 throw new RuntimeException("获取应用的jsapi_ticket失败！" + resultJosn.getString("errmsg"));
             }
             ticket = resultJosn.getString("ticket");
             long expireTime = resultJosn.getLongValue("expires_in");
             System.out.println("jsapi_ticket======="+ticket);
//             redisUtil.insert(redisKey,ticket,expireTime);
         }
         return ticket;
    }
    
	/**
	 * 生成sh1加密的字符串
	 * @param jsapiTicket 获取方式详见企业微信 JSSDK 文档
	 * @param noncestr 随机字符串，由开发者随机生成
	 * @param timestamp 由开发者生成的当前时间戳
	 * @param url 当前网页的URL，不包含#及其后面部分。注意：对于没有只有域名没有 path 的 URL ，浏览器会自动加上 / 作为 path，如打开 http://qq.com 则获取到的 URL 为 http://qq.com/
	 * @return
	 * @throws Exception
	 */
	@Override
    public String getSh1Signature(String jsapiTicket, String noncestr, String timestamp, String url) throws Exception {
		SortedMap<String, String> signParams = new TreeMap<String, String>();
		signParams.put("jsapi_ticket", jsapiTicket);
		signParams.put("noncestr", noncestr);
		signParams.put("timestamp", timestamp);
		signParams.put("url", url);
		
        return Sh1Util.createSHA1Sign(signParams);
    }
    
    /**
     * 设置授权配置
     * @param preAuthCode 预授权码
     * @return
     * @throws JSONException
     */
	@Override
    public JSONObject setSessionInfo(String preAuthCode) throws JSONException {
        //获取第三方应用凭证
        String suiteAccessToken = getSuiteAccessToken();
        
        JSONObject params = new JSONObject();
        params.put("pre_auth_code", preAuthCode);
        JSONObject sessionInfo = new JSONObject();
        //授权类型：0 正式授权， 1 测试授权。 默认值为0。注意，请确保应用在正式发布后的授权类型为“正式授权”
        sessionInfo.put("auth_type", Integer.parseInt(session_auth_type));
        params.put("session_info", sessionInfo);
        String result = HttpUtil.sendHttpJsonPost(WXWorkConstants.SET_SESSION_INFO+suiteAccessToken, params);
        
        System.out.println("设置授权配置返回结果： "+result);
        //格式转换
        JSONObject resultJosn = JSONObject.parseObject(result);
        //注意：因历史原因，该接口在调用失败时才返回errcode。没返回errcode视为调用成功
        if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))){
            throw new RuntimeException("设置授权配置失败！" + resultJosn.getString("errmsg"));
        }
        return resultJosn;
    }

	/**
	 * 获取企业授权信息
	 * @param corpId 授权方corpid
	 * @param permanentCode 永久授权码，通过get_permanent_code获取
	 * @return
	 */
	@Override
	public JSONObject getAuthInfo(String corpId, String permanentCode) {
        JSONObject params = new JSONObject();
        params.put("auth_corpid", corpId);
        params.put("permanent_code", permanentCode);
        //获取第三方应用凭证
        String suiteAccessToken = getSuiteAccessToken();
        String result = HttpUtil.sendHttpJsonPost(WXWorkConstants.AUTH_INFO_URL+suiteAccessToken, params);
        System.out.println("企业授权信息获取返回结果： "+result);
        //格式转换
        JSONObject resultJosn = JSONObject.parseObject(result);
        //注意：因历史原因，该接口在调用失败时才返回errcode。没返回errcode视为调用成功
        if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))){
            throw new RuntimeException("授权码获取失败！" + resultJosn.getString("errmsg"));
        }
        return resultJosn;
    }
	
	/**
	 * 获取授权应用信息
	 * @param corpId 企业微信id
	 * @param permanentCode 第三方永久授权码
	 * @return
	 */
	@Override
	public AuthAgentInfo getAuthAgentInfo(String corpId, String permanentCode) {
		JSONObject authInfo = this.getAuthInfo(corpId, permanentCode);
        //授权的应用信息，注意是一个数组，但仅旧的多应用套件授权时会返回多个agent，对新的单应用授权，永远只返回一个agent
        JSONObject authInfoJosn = JSONObject.parseObject(authInfo.getString("auth_info"));
        List<AuthAgentInfo> agentL = com.alibaba.fastjson.JSONObject.parseArray(authInfoJosn.getString("agent"), AuthAgentInfo.class);
        if(agentL != null && agentL.size()>0) {
        	return agentL.get(0);
        }
        return null;
	}
    
    /**
     * 企业微信消息推送
     * @param corpId 企业微信id
     * @param permanentCode 第三方永久授权码
     * @param userIdL 推送的用户id集合
     * @param isAll 是否全部推送
     * @param templateMsgInfo 消息内容
     * @return
     */
	@Override
    public JSONObject msgPush(String corpId, String permanentCode, TemplateMsgInfo templateMsgInfo) {
        AuthAgentInfo agentInfo = this.getAuthAgentInfo(corpId, permanentCode);
        if(agentInfo != null) {
        	int angentId = agentInfo.getAgentid();
        	
        	String accessToken = this.getAccessToken(corpId, permanentCode);
        	
        	JSONObject params = new JSONObject();
        	if(templateMsgInfo.isAll()) {
        		params.put("touser", "@all");
        	}else {
        		String touser = "";
        		for(String userId : templateMsgInfo.getToUserIdL()) {
        			touser += userId +"|";
        		}
        		if(touser.lastIndexOf("|")>0) {
        			touser = touser.substring(0, touser.length()-1);
        		}
        		params.put("touser", touser);
        	}
            params.put("msgtype", "template_msg");
            params.put("agentid", angentId);
            
            JSONObject templateMsg = new JSONObject();
            templateMsg.put("template_id", templateId);
            templateMsg.put("url", domainUrl+templateMsgInfo.getAppendUrl());
//            templateMsg.put("url", domainUrl+"/ylsaas/NewDashBoard");
            templateMsg.put("title", templateMsgInfo.getMsgTitle());
            JSONArray contentIem = new JSONArray();
            JSONObject contentIemJson = new JSONObject();
            contentIemJson.put("key", "标题");
            contentIemJson.put("value", templateMsgInfo.getMsgTitleValue());
            contentIem.add(contentIemJson);
            contentIemJson = new JSONObject();
            contentIemJson.put("key", "发送人");
            contentIemJson.put("value", templateMsgInfo.getMsgUserValue());
            contentIem.add(contentIemJson);
            contentIemJson = new JSONObject();
            contentIemJson.put("key", "时间");
            contentIemJson.put("value", templateMsgInfo.getMsgTimeValue());
            contentIem.add(contentIemJson);
            templateMsg.put("content_item", contentIem);
            
            params.put("template_msg", templateMsg);
            
        	String result = HttpUtil.sendHttpJsonPost(WXWorkConstants.MSG_PUSH_URL+accessToken, params);
        	System.out.println("发送应用消息返回结果： "+result);
            //格式转换
            JSONObject resultJosn = JSONObject.parseObject(result);
            //注意：因历史原因，该接口在调用失败时才返回errcode。没返回errcode视为调用成功
            if(resultJosn.containsKey("errcode") && !WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))){
                throw new RuntimeException("发送应用消息失败！" + resultJosn.getString("errmsg"));
            }
            return resultJosn;
        }
        return null;
    }

    /**
     * 企业微信授权关联的租户注册的初始化
     * @param corpInfo 企业微信返回的授权的企业信息
     * @param userInfo 操作授权的管理员用户（授权企业对应的管理员用户）
     * @param permanentCode 企业微信永久授权码,最长为512字节
     * @throws InterruptedException
     */
//	@Override
//	public BaseTenantEntity createTenant(CorpInfo corpInfo, AuthUserInfo userInfo, String permanentCode) throws InterruptedException {
//		//新建租户
//    	BaseTenantEntity entity = new BaseTenantEntity();
//    	entity.setLogo(corpInfo.getCorp_square_logo_url());
//    	entity.setEnCode(userInfo.getMobile()==null?userInfo.getUserid():userInfo.getMobile());
//        entity.setId(corpInfo.getCorpid());
//        entity.setComPanyName(corpInfo.getCorp_name());
//        entity.setFullName(corpInfo.getCorp_full_name());
//        entity.setCreatorTime(new Date());
//        entity.setCreatorUserId(userInfo.getUserid());
//        entity.setMaxUser(5);
//        entity.setMaxSize(1);
//        //记录企业永久授权码
//        entity.setWxWorkPermanentCode(permanentCode);
//
//        String dbName = PinYinUtil.getFirstSpell(corpInfo.getCorp_name())+RandomUtil.getRandomCode(4);
//        entity.setDbserviceName(dbName);
//
//        System.out.println("entityjson="+JSONObject.toJSONString(entity));
//        entity = tenantApi.initByWxWork(JSONObject.toJSONString(entity));
//        System.out.println("result="+entity);
//        if(entity != null) {
//        	System.out.println(dbName+"租户创建成功！");
//        	return entity;
//        }
//
//        return null;
//	}

    /**
     * 企业微信授权关联的租户注册的初始化
     * @param corpInfo 企业微信返回的授权的企业信息
     * @param userInfo 操作授权的管理员用户（授权企业对应的管理员用户）
     * @param permanentCode 企业微信永久授权码,最长为512字节
     * @throws InterruptedException
     */
//    @Override
//    public BaseTenantEntity createTenant(CorpInfo corpInfo, AuthUserInfo userInfo, String permanentCode) throws InterruptedException {
//
//        BaseTenantEntity entity = tenantApi.initByWxWork(corpInfo.getCorpid(), corpInfo.getCorp_name(), userInfo.getUserid());
//
//        System.out.println("result="+entity);
//        if(entity != null) {
//            System.out.println(entity.getDbserviceName()+"租户创建成功！");
//            return entity;
//        }
//
//        return null;
//    }

    /**
     * 更新租户的其他信息
     * @param corpInfo
     * @param userInfo
     * @param permanentCode
     * @return
     * @throws InterruptedException
     */
//    @Override
//    public BaseTenantEntity updateTenant(CorpInfo corpInfo, AuthUserInfo userInfo, String permanentCode) throws InterruptedException {
//        BaseTenantEntity entity = tenantApi.findById(corpInfo.getCorpid());
//        entity.setLogo(corpInfo.getCorp_square_logo_url());
//        if(userInfo != null) {
//        	entity.setEnCode(userInfo.getMobile()==null?userInfo.getUserid():userInfo.getMobile());
//        }
//        entity.setFullName(corpInfo.getCorp_full_name());
//        //记录企业永久授权码
//        entity.setWxWorkPermanentCode(permanentCode);
//
//        tenantApi.updateById(entity);
//
//        return entity;
//    }
    
    /**
     * 关联更新租户信息（保存corpId，permanentCode）
     * @param entity
     * @param corpInfo
     * @param permanentCode
     * @return
     */
//	@Override
//    public BaseTenantEntity updateTenant(BaseTenantEntity entity, CorpInfo corpInfo, String permanentCode) {
//        entity.setQywxCorpId(corpInfo.getCorpid());
//        //记录企业永久授权码
//        entity.setWxWorkPermanentCode(permanentCode);
//
//        tenantApi.updateById(entity);
//
//        return entity;
//    }

    /**
     *
     * @param agentInfo	授权信息
     * @param accessToken 授权方（企业）access_token,最长为512字节。代开发自建应用安装时不返回。
     * @param adminUserId 授权方管理员id
     * @param userL 拥有权限的用户集合
     * @param positonL 拥有权限的岗位集合
     * @param relationList 用户角色关系集合
     */
//    @Override
//    public void prepareUserAndPosition(AuthAgentInfo agentInfo, String accessToken, List<UserEntity> userL, List<PositionEntity> positonL) {
//        //应用可见范围（成员）
//        if(agentInfo.getPrivilege() != null) {
//            List<String> userIdL = Lists.newArrayList();
//
//            //获取权限范围内的用户、岗位
//            if(agentInfo.getPrivilege().getAllow_user() != null) {
//                String[] allowUser = agentInfo.getPrivilege().getAllow_user();
//                userIdL.addAll(Arrays.asList(allowUser));
//            }
//            System.out.println("userIdL1="+userIdL);
//            //获取权限范围内的标签用户
//            if(agentInfo.getPrivilege().getAllow_tag() != null) {
//                Integer[] allowTag = agentInfo.getPrivilege().getAllow_tag();
//                if(allowTag.length>0) {
//                    List<String> tagUserIdL = Lists.newArrayList();
//                    for(int tagId : allowTag) {
//                        List<AuthUserInfo> usersL = this.getUsersByTagId(tagId, accessToken);
//                        if(usersL != null && usersL.size()>0) {
//                            tagUserIdL.addAll(usersL.stream().map(AuthUserInfo::getUserid).collect(Collectors.toList()));
//                        }
//                    }
//                    userIdL.addAll(tagUserIdL);
//                }
//            }
//            System.out.println("userIdL2="+userIdL);
//            //去重
//            userIdL = userIdL.stream().distinct().collect(Collectors.toList());
//            System.out.println("userIdL3="+userIdL);
//            if(userIdL.size()>0) {
//                this.initUserEntityL(userIdL, accessToken, userL, positonL);
//            }
//        }
//    }

    /**
     * 初始化保存 应用权限范围内的部门信息
     * @param deptList
     */
//    @Override
//    public void initDept(List<DepartmentInfo> deptList) {
//        if(deptList != null && deptList.size()>0) {
//            OrganizeEntity parentOrgE = null;
//            OrganizeEntity orgE = null;
//            //先查出改企业的根节点部门
//            if(parentOrgE == null) {
//                List<OrganizeEntity> parentOrgL = organizeService.getByParentId("-1");
//                if(parentOrgL != null && parentOrgL.size()>0) {
//                    parentOrgE = parentOrgL.get(0);
//                }
//            }
//
//            for(DepartmentInfo dept : deptList) {
//                orgE = new OrganizeEntity();
//                orgE.setParentId(parentOrgE==null?null:parentOrgE.getId());
//                orgE.setFullName(dept.getName());
//                orgE.setId(dept.getId()+"");
//                orgE.setDeptSource("qywx");
//                organizeService.create(orgE);
//            }
//        }
//    }

    /**
     * 创建岗位
     * @param positonL
     */
//    @Override
//    public void initPosition(List<PositionEntity> positonL) {
//        if(positonL != null && positonL.size()>0) {
//            positonL.stream().forEach(l -> {
//                positionService.create(l);
//            });
//        }
//    }

    /**
     * 创建用户
     * @param userL
     */
//    @Override
//    public void initUser(List<UserEntity> userL) {
//        if(userL != null && userL.size()>0) {
//            userL.stream().forEach(l -> {
//            	l.setUserSource("qywx");
//                userService.create(l);
//            });
//        }
//    }

    /**
     * 创建account表中的用户
     * @param userL 待新增的用户集合
     * @param tenantId 当前租户id
     * @param adminId 当前租户管理员id
     */
    @Override
    public void initAccount(List<UserEntity> userL, String tenantId, String adminId) {
//        if(userL != null && userL.size()>0) {
//            UserInfo userInfo = new UserInfo();
//            userInfo.setTenantId(tenantId);
//            userInfo.setUserId(adminId);
//            for(UserEntity userE: userL) {
//                accountService.create(userE, userInfo);
//            }
//        }
    }

    /**
     * 删除account
     * @param userL 用户集合
     * @param tenantId 租户id
     */
    @Override
    public void deleteAccount(List<UserEntity> userL, String tenantId) {
//        if(userL != null && userL.size()>0) {
//            for(UserEntity userE: userL) {
//                AccountEntity model = accountService.findAccountByTenantId(userE.getAccount(), tenantId);
//                if(model != null) {
//                    accountService.delete(model);
//                }
//            }
//        }
    }

    /**
     * 根据企业微信用户id，获取用户详细信息
     * @param userId 企业微信用户id
     * @param accessToken 授权企业的凭证
     * @return
     */
    private AuthUserInfo getUserInfoByUserId(String userId, String accessToken) {
        AuthUserInfo userInfo = null;

        String result = HttpUtil.sendHttpGet(WXWorkConstants.GET_USER_DETAIL+"?access_token="+accessToken+"&userid="+userId);
        System.out.println("用户详细信息获取返回结果： "+result);

        JSONObject resultJosn = JSONObject.parseObject(result);

        if(WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
            userInfo = com.alibaba.fastjson.JSONObject.parseObject(result, AuthUserInfo.class);
        }

        return userInfo;
    }
    
    /**
     * 获取企业成员的userid与对应的部门ID列表
     * @param accessToken 授权企业的凭证
     * @return
     */
    @Override
    public List<AuthUserInfo> getUserIdList(String accessToken) {
    	List<AuthUserInfo> userL = Lists.newArrayList();
    	
    	JSONObject params = new JSONObject();
        params.put("limit", 100);
        //获取第三方应用凭证
        String result = HttpUtil.sendHttpJsonPost(WXWorkConstants.GET_USERIDLIST_URL+accessToken, params);
    	
        System.out.println("获取企业成员的userid与对应的部门ID列表返回结果： "+result);

        JSONObject resultJosn = JSONObject.parseObject(result);
        

        if(WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
        	userL = com.alibaba.fastjson.JSONObject.parseArray(resultJosn.getString("member_auth_list"), AuthUserInfo.class);
        }

        return userL;
    }

    private List<AuthUserInfo> getUsersByTagId(int tagId, String accessToken) {
        List<AuthUserInfo> userL = null;

        String result = HttpUtil.sendHttpGet(WXWorkConstants.TAG_USER_URL+"?access_token="+accessToken+"&tagid="+tagId);
        System.out.println("标签成员信息获取返回结果： "+result);
        JSONObject resultJosn = JSONObject.parseObject(result);

        if(WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
            userL = com.alibaba.fastjson.JSONObject.parseArray(resultJosn.getString("userlist"), AuthUserInfo.class);
        }

        return userL;
    }

    /**
     * 获取部门列表
     * @param deptId 部门id。获取指定部门及其下的子部门（以及子部门的子部门等等，递归）。 如果不填，默认获取全量组织架构
     * @param accessToken 	调用接口凭证
     */
    @Override
    public List<DepartmentInfo> getDeptList(String deptId, String accessToken) {
        List<DepartmentInfo> deptL = Lists.newArrayList();
        String url = WXWorkConstants.ORG_INFO_URL+"?access_token="+accessToken;
        if(deptId != null && !"".equals(deptId)) {
            url = WXWorkConstants.ORG_INFO_URL+"?access_token="+accessToken+"&id="+deptId;
        }
        String result = HttpUtil.sendHttpGet(url);
        System.out.println("部门列表获取返回结果： "+result);
        JSONObject resultJosn = JSONObject.parseObject(result);

        if(WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
            deptL = com.alibaba.fastjson.JSONObject.parseArray(resultJosn.getString("department"), DepartmentInfo.class);
        }
        return deptL;
    }
    
    /**
     * 获取部门列表
     * @param deptId 部门id。获取指定部门及其下的子部门（以及子部门的子部门等等，递归）。 如果不填，默认获取全量组织架构
     * @param accessToken 	调用接口凭证
     */
    @Override
    public List<DepartmentInfo> getDeptIdList(String deptId, String accessToken) {
        List<DepartmentInfo> deptL = Lists.newArrayList();
        String url = WXWorkConstants.ORG_IDLIST_URL+"?access_token="+accessToken;
        if(deptId != null && !"".equals(deptId)) {
            url = WXWorkConstants.ORG_IDLIST_URL+"?access_token="+accessToken+"&id="+deptId;
        }
        String result = HttpUtil.sendHttpGet(url);
        System.out.println("部门列表获取返回结果： "+result);
        JSONObject resultJosn = JSONObject.parseObject(result);

        if(WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
            deptL = com.alibaba.fastjson.JSONObject.parseArray(resultJosn.getString("department_id"), DepartmentInfo.class);
        }
        return deptL;
    }

    /**
     * 获取部门成员
     * @param deptId 获取的部门id
     * @param accessToken 调用接口凭证
     * @param fetchChild 是否递归获取子部门下面的成员：1-递归获取，0-只获取本部门
     * @return
     */
    @Override
    public List<AuthUserInfo> getDeptUserList(String deptId, String accessToken, int fetchChild) {
        List<AuthUserInfo> userL = Lists.newArrayList();
        String url = WXWorkConstants.ORG_USER_URL+"?access_token="+accessToken+"&department_id="+deptId+"&fetch_child="+fetchChild;
        String result = HttpUtil.sendHttpGet(url);
        System.out.println("部门用户列表获取返回结果： "+result);
        JSONObject resultJosn = JSONObject.parseObject(result);

        if(WXWorkConstants.SUCCESS_CODE.equals(resultJosn.getString("errcode"))) {
            userL = com.alibaba.fastjson.JSONObject.parseArray(resultJosn.getString("userlist"), AuthUserInfo.class);
        }
        return userL;
    }

}
