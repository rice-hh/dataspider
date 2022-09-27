package com.nh.dataspider.pay.model;

import java.util.List;

import com.alipay.api.internal.mapping.ApiField;
import com.nh.dataspider.pay.model.WechatTradeRequestModel.Amount;
import com.nh.dataspider.pay.model.WechatTradeRequestModel.Detail.GoodsDetail;

import lombok.Data;


/**
 * 支付
 *
 */
@Data
public class WechatTradeResponseModel {
	
    /**
     *  直连商户申请的公众号或移动应用appid。
     *  示例值：wxd678efh567hg6787
     */
	@ApiField("appid")
    private String appId;
	/**
     *  商户的商户号，由微信支付生成并下发。
     *  示例值：1230000109
     */
	@ApiField("mchid")
    private String mchId;
    /**
     * 商户系统内部订单号，只能是数字、大小写字母_-*且在同一个商户号下唯一。
     * 特殊规则：最小字符长度为6
     * 示例值：1217752501201407033233368018
     * 必填：否
     */
	@ApiField("out_trade_no")
    private String outTradeNo;
    /**
     * 微信支付系统生成的订单号。
     * 示例值：1217752501201407033233368018
     */
	@ApiField("transaction_id")
    private String transactionId;
    /**
     * 交易类型
     * 枚举值：JSAPI：公众号支付
     * 		NATIVE：扫码支付
     * 		APP：APP支付
     * 		MICROPAY：付款码支付
     * 		MWEB：H5支付
     * 		FACEPAY：刷脸支付
     * 示例值：MICROPAY
     */
	@ApiField("trade_type")
    private String tradeType;
	/**
     * 交易状态
     * 枚举值：SUCCESS：支付成功
     * 		REFUND：转入退款
     * 		NOTPAY：未支付
     * 		CLOSED：已关闭
     * 		REVOKED：已撤销（付款码支付）
     * 		USERPAYING：用户支付中（付款码支付）
     * 		PAYERROR：支付失败(其他原因，如银行返回失败)
     * 示例值：SUCCESS
     */
	@ApiField("trade_state")
    private String tradeState;
	/**
     * 交易状态描述
     * 示例值：支付成功
     */
	@ApiField("trade_state_desc")
    private String tradeStateDesc;
    /**
     * 银行类型，采用字符串类型的银行标识。银行标识请参考《银行类型对照表》
     * 示例值：CICBC_DEBIT
     */
	@ApiField("bank_type")
    private String bankType;
	/**
     * 附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用，实际情况下只有支付完成状态才会返回该字段（原样返回）
     * 示例值：自定义数据  
     */
	@ApiField("attach")
    private String attach;
    /**
     *  支付完成时间，遵循rfc3339标准格式，格式为yyyy-MM-DDTHH:mm:ss+TIMEZONE，yyyy-MM-DD表示年月日，T出现在字符串中，表示time元素的开头，HH:mm:ss表示时分秒，TIMEZONE表示时区（+08:00表示东八区时间，领先UTC 8小时，即北京时间）。
     *  例如：2015-05-20T13:29:35+08:00表示，北京时间2015年5月20日 13点29分35秒。
     *  示例值：2018-06-08T10:34:56+08:00
     */
	@ApiField("success_time")
    private String successTime;
    /**
     * 交易支付时间
     * 示例值：2014-11-27 15:45:57
     */
	@ApiField("payer")
    private Payer payer;
    /**
     * 发生支付交易的商户门店名称
     * 示例值：证大五道口店
     */
	@ApiField("amount")
    private Amount amount;
    /**
     * 买家在支付宝的用户id
     * 示例值：2088101117955611
     */
	@ApiField("buyer_user_id")
    private String buyerUserId;
	/**
     * 支付场景信息描述
     */
	@ApiField("scene_info")
    private SceneInfo sceneInfo;
	/**
     * 商家优惠金额
     * 示例值：88.88
     */
	@ApiField("promotion_detail")
    private List<PromotionDetail> promotionDetail;
	/**
     * 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
     * 示例值：1217752501201407033233368018
     */
	@ApiField("out_refund_no")
    private String outRefundNo;
	/**
     * 微信退款单号
     * 示例值：1217752501201407033233368018
     */
	@ApiField("refund_id")
    private String refundId;
	/**
     * 退款状态
     * 枚举值：SUCCESS：退款成功
     * 		CLOSED：退款关闭
     * 		ABNORMAL：退款异常，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，可前往【商户平台—>交易中心】，手动处理此笔退款
     * 示例值：SUCCESS
     */
	@ApiField("refund_status")
    private String refundStatus;
    
    @Data
    public static class Payer{
    	/**
    	 * 用户在直连商户appid下的唯一标识。
    	 * 示例值：oUpF8uMuAJO_M2pxb1Q9zNjWeS6o
    	 */
    	@ApiField("openid")
    	private String openId;
    }
    
    @Data
    public static class SceneInfo{
    	/**
    	 * 终端设备号（门店号或收银设备ID）。
    	 * 示例值：013467007045764
    	 */
    	@ApiField("device_id")
    	private String deviceId;
    }
    
    @Data
    public static class PromotionDetail{
    	/**
    	 * 券ID
    	 * 示例值：109519
    	 */
    	@ApiField("coupon_id")
    	private String couponId;
    	/**
    	 * 优惠名称
    	 * 示例值：单品惠-6
    	 */
    	@ApiField("name")
    	private String name;
    	/**
    	 * GLOBAL：全场代金券
    	 * SINGLE：单品优惠
    	 * 示例值：GLOBAL
    	 */
    	@ApiField("scope")
    	private String scope;
    	/**
    	 * CASH：充值型代金券
    	 * NOCASH：免充值型代金券
    	 * 示例值：CASH
    	 */
    	@ApiField("type")
    	private String type;
    	/**
    	 * 优惠券面额
    	 * 示例值：100
    	 */
    	@ApiField("amount")
    	private Integer amount;
    	/**
    	 * 活动ID
    	 * 示例值：931386
    	 */
    	@ApiField("stock_id")
    	private String stockiId;
    	/**
    	 * 微信出资，单位为分
    	 * 示例值：0
    	 */
    	@ApiField("wechatpay_contribute")
    	private Integer wechatpay_contribute;
    	/**
    	 * 商户出资，单位为分
    	 * 示例值：0
    	 */
    	@ApiField("merchant_contribute")
    	private Integer merchant_contribute;
    	/**
    	 * 其他出资，单位为分
    	 * 示例值：0
    	 */
    	@ApiField("other_contribute")
    	private String other_contribute;
    	/**
    	 * CNY：人民币，境内商户号仅支持人民币。
    	 * 示例值：CNY
    	 */
    	@ApiField("currency")
    	private String currency;
    	/**
    	 * 终端设备号（门店号或收银设备ID）。
    	 * 示例值：013467007045764
    	 */
    	@ApiField("goods_detail")
    	private List<GoodsDetail> goodsDetail;
    }
    
}
