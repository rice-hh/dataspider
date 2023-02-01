package com.nh.dataspider.pay.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author YlSaaS产品组
 * @version V3.1.0
 * @copyright 上海云令智享信息技术有限公司
 * @date 2021/3/16 10:51
 */
@Data
@Component
public class PaySysConfig {
	
	/**
	 * 阿里支付回调地址
	 * 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	 */
//	@Value("${pay_config.alipay.notify_url}")
	public String aliNotifyUrl;
	
	/**
	 * 阿里支付回调地址
	 * 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
	 */
//	@Value("${pay_config.alipay.return_url}")
	public String aliReturnUrl;
	
	/**
	 * 阿里第三方应用id
	 */
//	@Value("${pay_config.alipay.third_appid}")
	public String thirdAppId;
	
	/**
	 * 阿里 私钥 pkcs8格式的
	 */
//	@Value("${pay_config.alipay.rsa_private_key}")
	public String rsaPrivateKey;
	
	/**
	 * 阿里 支付宝公钥
	 */
//	@Value("${pay_config.alipay.public_key}")
	public String alipayPublicKey;
	
	/**
	 * 微信支付 回调地址
	 */
//	@Value("${pay_config.wechat.trade_notify_url}")
	public String wechatTradeNotifyUrl;
	
	/**
	 * 微信退款 回调地址
	 */
//	@Value("${pay_config.wechat.refund_notify_url}")
	public String wechatRefundNotifyUrl;
	
}
