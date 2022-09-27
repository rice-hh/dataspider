package com.nh.dataspider.pay.test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.nh.dataspider.pay.model.AlipayResponseModel;
import com.nh.dataspider.pay.model.AlipayResponseModel.PassbackParams;
import com.nh.dataspider.pay.model.WechatTradeRequestModel;
import com.nh.dataspider.pay.model.WechatTradeRequestModel.Amount;
import com.nh.dataspider.pay.model.WechatTradeResponseModel;
import com.nh.dataspider.pay.util.PayUtil;
import com.nh.dataspider.util.DateUtil;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.exception.ValidationException;

public class PayTest {
	
	@Autowired
    private PayUtil payUtil;
	
	/**
     * 生成主键id
     * @return
     */
    public String uuId() {
        String uuid = UUID.randomUUID().toString();
        String temp = uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24);
        return temp;
    }

	/**
	 * 获取支付宝付款二维码
	 * @param visualdevEntity
	 * @param visualDataId 业务id
	 * @param allDataMap 业务数据
	 * @param user 当前操作人
	 * @return
	 * @throws Exception
	 */
	public String getAliPayQRCode() throws Exception {
    	String qRCode = "";
    	
    	String amount = "0.01";
		//获取订单编号
		String tradeNo = this.uuId();
		//获取订单标题
		String subject = this.uuId();
		PassbackParams backParam = PassbackParams.builder().tenantId("").userId("").visualDataId("").visualDevId("").build();
		//返回参数
		String passBackParams = JSONObject.toJSONString(backParam);
		
		//定义请求参数实体
    	AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
	    model.setSubject(subject);
	    model.setTotalAmount(amount);
	    model.setOutTradeNo(tradeNo);
	    model.setPassbackParams(passBackParams);
	    
	    String appAuthToken = "";
	    //请求获取支付二维码
	    qRCode = payUtil.getAliPayQRCode(model, appAuthToken);
    	
    	return qRCode;
    }
	
	/**
	 * 获取微信付款二维码
	 * @param visualdevEntity
	 * @param visualDataId 业务id
	 * @param allDataMap 业务数据
	 * @param user 当前操作人
	 * @return
	 * @throws Exception
	 */
	public String getWechatPayQRCode() throws Exception {
    	String qRCode = "";
    	
    	double amount = 0.01;
		//获取订单编号
		String tradeNo = this.uuId();
		//获取订单标题
		String tradeTitle = this.uuId();
		PassbackParams backParam = PassbackParams.builder().userId("").visualDataId("").build();
		//返回参数
		String passBackParams = JSONObject.toJSONString(backParam);
		System.out.println("passBackParams1="+passBackParams);
		
		//直连商户号
	    String merchantId = "";
	    //商户证书序列号
	    String merchantSerialNumber = "";
	    //商户私钥
	    String privateKeyUrl = ""+"apiclient_key.pem";
	    System.out.println("privateKeyUrl="+privateKeyUrl);
	    //apiV3Key
	    String apiV3Key = "";
	    String appId = "";
		
		//定义请求参数实体
		WechatTradeRequestModel model = new WechatTradeRequestModel();
		Amount am = new Amount();
		am.setTotal((int)(amount * 100));
		model.setAmount(am);
		model.setDescription(tradeTitle);
		model.setOut_trade_no(tradeNo);
	    model.setAppid(appId);
	    model.setMchid(merchantId);
	    model.setAttach(passBackParams);
	    
	    //请求获取支付二维码
	    qRCode = payUtil.getWeChatPayQRCode(model, merchantId, merchantSerialNumber, privateKeyUrl, apiV3Key, "/");
    	
    	return qRCode;
    }
	
	/**
     * 支付宝支付完后业务相关的回调方法
     * @throws IOException 
     * @throws ParseException 
     * @throws SQLException 
     */
    @RequestMapping("/public/pay/alipay/businessNotify")
    public String aliPayBusinessNotify(@RequestParam Map<String, String> requestMap) {
    	String retStr = "success";
    	System.out.println("requestMap="+JSONObject.toJSONString(requestMap));
    	AlipayResponseModel responseModel = JSONObject.parseObject(JSONObject.toJSONString(requestMap), AlipayResponseModel.class);
    	
    	PassbackParams passbackParams = JSONObject.parseObject(responseModel.getPassbackParams(), PassbackParams.class);
    	try {
			this.saveAlipayNotifyInfo(responseModel);
			System.out.println("SUCCESSSUCCESSSUCCESSSUCCESSSUCCESS");
		} catch (Exception e) {
			e.printStackTrace();
			retStr = "fail";
			System.out.println("FAILFAILFAILFAILFAILFAILFAILFAILFAIL");
		}
    	
        return retStr;
    }
    
    /**
	 * 保存支付后回调信息的相关参数
	 * @param responseModel 通知信息参数
	 * @throws SQLException
	 * @throws ParseException
	 * @throws IOException
	 */
	public void saveAlipayNotifyInfo(AlipayResponseModel responseModel) {
		System.out.println("responseModel="+JSONObject.toJSONString(responseModel));
	}
	
    @RequestMapping("/public/pay/wechatpay/businessNotify/{tenantId}/{visualDevId}")
    public Object wechatPayBusinessNotify(@PathVariable("tenantId") String tenantId, @PathVariable("visualDevId") String visualDevId, HttpServletRequest request) {
    	JSONObject retJson = new JSONObject();
    	
    	System.out.println("wechatpay/businessNotify"+tenantId+"----"+visualDevId);
    	try {
			this.saveWechatpayNotifyInfo(request, tenantId, visualDevId);
			System.out.println("SUCCESSSUCCESSSUCCESSSUCCESSSUCCESS");
			retJson.put("code", "SUCCESS");
	    	retJson.put("message", "成功");
		} catch (Exception e) {
			e.printStackTrace();
			retJson.put("code", "FAIL");
	    	retJson.put("message", "失败");
	    	System.out.println("FAILFAILFAILFAILFAILFAILFAILFAILFAIL");
		}
    	
        return retJson;
    }
    
    /**
	 * 微信支付回调保存
	 * @param request
	 * @param tenantId
	 * @param visualDevId 
	 * @throws SQLException
O	 * @throws ParseException
	 * @throws IOException
	 * @throws HttpCodeException
	 * @throws NotFoundException
	 * @throws ValidationException
	 * @throws com.wechat.pay.contrib.apache.httpclient.exception.ParseException
	 * @throws GeneralSecurityException
	 */
	public void saveWechatpayNotifyInfo(HttpServletRequest request, String tenantId, String visualDevId) throws SQLException, ParseException, IOException, HttpCodeException, NotFoundException, ValidationException, com.wechat.pay.contrib.apache.httpclient.exception.ParseException, GeneralSecurityException {
		
		//直连商户号
	    String merchantId = "";
	    //商户证书序列号
	    String merchantSerialNumber = "";
	    //商户私钥
	    String privateKeyUrl = ""+"";
	    System.out.println("privateKeyUrl="+privateKeyUrl);
	    //微信支付平台证书保存路径
	    String apiV3Key = "";
	    		
	    String decryptData = payUtil.wechatNotifyVerfiyAndSign(request, merchantId, merchantSerialNumber, privateKeyUrl, apiV3Key);
	    if(decryptData != null && !"".equals(decryptData)) {
	    	WechatTradeResponseModel wechatTradeResponseModel = JSONObject.parseObject(decryptData, WechatTradeResponseModel.class);
	    	System.out.println("wechatTradeResponseModel="+JSONObject.toJSONString(wechatTradeResponseModel));
	    	if(wechatTradeResponseModel != null) {
	    		String attach = wechatTradeResponseModel.getAttach();
	    		System.out.println("passbackParams-attach="+attach);
	    		PassbackParams passbackParams = JSONObject.parseObject(attach, PassbackParams.class);
	    		
	    	}
	    }
	}
	
	/**
	 * 支付宝退款请求
	 * @param visual
	 * @param visualDataId 业务id
	 * @param user
	 * @throws AlipayApiException
	 * @throws SQLException
	 * @throws ParseException
	 * @throws IOException
	 */
	public String alipayRefund() throws AlipayApiException, SQLException, ParseException, IOException {
		String retStr = "退款失败！";
		
		//获取支付金额
		//需要退款的金额，该金额不能大于订单金额，单位为元，支持两位小数。
		String amount = "0.01";
		//获取订单编号
		String tradeNo = "";
		
		//定义请求参数
		AlipayTradeRefundModel model = new AlipayTradeRefundModel();
		model.setRefundAmount(amount);
		model.setOutTradeNo(tradeNo);
		
		String appAuthToken = "";
		
		//请求退款
		JSONObject aliPayRefund = payUtil.aliPayRefund(model, appAuthToken);
		if(aliPayRefund != null) {
			//接口中code=10000，仅代表本次退款请求成功，不代表退款成功。
			//接口返回fund_change=Y为退款成功，fund_change=N或无此字段值返回时需通过退款查询接口进一步确认退款状态
			if("Y".equals(aliPayRefund.getString("fund_change"))) {
				AlipayResponseModel responseModel = JSONObject.parseObject(aliPayRefund.toJSONString(), AlipayResponseModel.class);
			}else {
				retStr = "退款转态已变化！";
			} 
		}
	    return retStr;
	}
	
	/**
	 * 微信退款申请
	 * @param visual
	 * @param visualDataId 业务id
	 * @param user
	 * @throws AlipayApiException
	 * @throws SQLException
	 * @throws ParseException
	 * @throws IOException
	 */
	public void wechatPayRefundApply() throws Exception {
		//获取支付金额
		//需要退款的金额，该金额不能大于订单金额，单位为元，支持两位小数。
		double amount = 0.01;
		//获取订单编号
		String tradeNo = "";
		//获取退款单号
		String refundNo = "";
		
		//直连商户号
	    String merchantId = "";
	    //商户证书序列号
	    String merchantSerialNumber = "";
	    //商户私钥
	    String privateKeyUrl = "";
	    //微信支付平台证书保存路径
	    String apiV3Key = "";
		
		//定义请求参数实体
		WechatTradeRequestModel model = new WechatTradeRequestModel();
		Amount am = new Amount();
		am.setTotal((int)(amount * 100));
		am.setRefund((int)(amount * 100));
		model.setAmount(am);
		model.setOut_trade_no(tradeNo);
		model.setOut_refund_no(refundNo);
	    payUtil.weChatPayRefund(model, merchantId, merchantSerialNumber, privateKeyUrl, apiV3Key, "/");
	}
	
    @RequestMapping("/public/pay/wechatpay/refundNotify/{tenantId}/{visualDevId}/{visualDataId}/{userId}")
    public Object wechatPayRefundNotify(@PathVariable("tenantId") String tenantId, @PathVariable("visualDevId") String visualDevId, @PathVariable("visualDataId") String visualDataId, @PathVariable("userId") String userId, HttpServletRequest request) {
    	JSONObject retJson = new JSONObject();
    	
    	System.out.println("wechatpay/refundNotify"+tenantId+"----"+visualDevId);
    	
    	try {
			this.wechatPayRefundNotify(request, tenantId, visualDevId, visualDataId);
			System.out.println("SUCCESSSUCCESSSUCCESSSUCCESSSUCCESS");
			retJson.put("code", "SUCCESS");
	    	retJson.put("message", "成功");
		} catch (Exception e) {
			e.printStackTrace();
			retJson.put("code", "FAIL");
	    	retJson.put("message", "失败");
	    	System.out.println("FAILFAILFAILFAILFAILFAILFAILFAILFAIL");
		}
    	
        return retJson;
    }
    
    /**
	 * 微信退款通知
	 * @param request
	 * @param tenantId
	 * @param visualDevId
	 * @param visualDataId
	 * @param user
	 * @throws Exception
	 */
	public void wechatPayRefundNotify(HttpServletRequest request, String tenantId, String visualDevId, String visualDataId) throws Exception {
		System.out.println("visualDevId="+visualDevId);
		//直连商户号
	    String merchantId = "";
	    //商户证书序列号
	    String merchantSerialNumber = "";
	    //商户私钥
	    String privateKeyUrl = "";
	    System.out.println("privateKeyUrl="+privateKeyUrl);
	    //微信支付平台证书保存路径
	    String apiV3Key = "";
	    		
	    String decryptData = payUtil.wechatNotifyVerfiyAndSign(request, merchantId, merchantSerialNumber, privateKeyUrl, apiV3Key);
	    if(decryptData != null && !"".equals(decryptData)) {
	    	WechatTradeResponseModel wechatTradeResponseModel = JSONObject.parseObject(decryptData, WechatTradeResponseModel.class);
	    	System.out.println("wechatTradeResponseModel="+JSONObject.toJSONString(wechatTradeResponseModel));
	    	//微信返回时间转换
	    	DateUtil.getDateByRFC3339(wechatTradeResponseModel.getSuccessTime()).getTime();
	    }
	}
}
