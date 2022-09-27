package com.nh.dataspider.pay.model;

import java.util.List;

import com.alipay.api.internal.mapping.ApiField;

import lombok.Data;

/**
 * 微信支付
 *
 */
@Data
public class WechatTradeRequestModel {
	
	private String sign;
    /**
     * 由微信生成的应用ID，全局唯一
     * 请求基础下单接口时请注意APPID的应用属性，例如公众号场景下，需使用应用属性为公众号的APPID
     * 示例值：wxd678efh567hg6787
     * 必填：是
     */
    private String appid;
    /**
     *  直连商户的商户号，由微信支付生成并下发
     *  示例值：1230000109
     *  必填：是
     */
    private String mchid;
    /**
     *  商品描述
     *  示例值：Image形象店-深圳腾大-QQ公仔
     *  必填：是
     */
    private String description;
    /**
     * 商户系统内部订单号，只能是数字、大小写字母_-*且在同一个商户号下唯一
     * 示例值：1217752501201407033233368018
     * 必填：是
     */
    private String out_trade_no;
    /**
	 * 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
	 * 示例值：1217752501201407033233368018
	 */
	private String out_refund_no;
	/**
	 * 若商户传入，会在下发给用户的退款消息中体现退款原因
	 * 示例值：商品已售完
	 */
	private String reason;
    /**
     * 订单失效时间，遵循rfc3339标准格式，格式为yyyy-MM-DDTHH:mm:ss+TIMEZONE，yyyy-MM-DD表示年月日，T出现在字符串中，表示time元素的开头，HH:mm:ss表示时分秒，TIMEZONE表示时区（+08:00表示东八区时间，领先UTC8小时，即北京时间）。
     * 例如：2015-05-20T13:29:35+08:00表示，北京时间2015年5月20日 13点29分35秒。
     * 示例值：2018-06-08T10:34:56+08:00
     * 必填：否
     */
    private String time_expire;
    /**
     * 附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用，实际情况下只有支付完成状态才会返回该字段
     * 示例值：自定义数据  
     * 必填：否
     */
    private String attach;
    /**
     * 通知URL必须为直接可访问的URL，不允许携带查询串，要求必须为https地址。
     * 格式：URL
     * 示例值：https://www.weixin.qq.com/wxpay/pay.php
     */
    private String notify_url;
    /**
     * 订单优惠标记
     * 示例值：WXG
     * 必填：否
     */
    private String goods_tag;
    /**
     * 订单金额信息
     * 必填：是
     */
    private Amount amount;
    /**
     *  优惠功能
     *  必填：否
     */
    private Detail detail;
    /**
     * 支付场景描述
     * 必填：否
     */
    private SceneInfo scene_info;
    /**
     * 结算信息
     * 必填：否
     */
    private String SettleInfo;
    
    @Data
    public static class Amount{
    	/**
    	 * 订单总金额，单位为分。
    	 * 示例值：100
    	 * 必填：是
    	 */
    	private Integer total;
    	/**
    	 * 退款金额，单位为分，只能为整数，不能超过原订单支付金额。
    	 * 示例值：888
    	 */
    	private Integer refund;
    	/**
    	 * 用户支付金额，单位为分。
    	 * 示例值：100
    	 * 必填：否
    	 */
    	@ApiField("payer_total")
    	private Integer payerTotal;
    	/**
    	 * CNY：人民币，境内商户号仅支持人民币。
    	 * 示例值：CNY
    	 * 必填：否
    	 */
    	private String currency = "CNY";
    	/**
    	 * 用户支付币种
    	 * 示例值：CNY
    	 * 必填：否
    	 */
    	@ApiField("payer_currency")
    	private String payerCurrency;
    }
    
    @Data
    public static class Detail{
    	/**
    	 * 1、商户侧一张小票订单可能被分多次支付，订单原价用于记录整张小票的交易金额。
    	 * 2、当订单原价与支付金额不相等，则不享受优惠。
    	 * 3、该字段主要用于防止同一张小票分多次支付，以享受多次优惠的情况，正常支付订单不必上传此参数。
    	 * 示例值：608800
    	 * 必填：否
	     */
    	private Integer cost_price;
    	/**
    	 * 商家小票ID
    	 * 示例值：微信123
    	 * 必填：否
    	 */
    	private String invoice_id;
    	/**
    	 * 单品列表信息
    	 * 条目个数限制：【1，6000】
    	 * 必填：否
    	 */
    	private List<GoodsDetail> goods_detail;
    	
    	@Data
        public static class GoodsDetail{
        	/**
        	 * 由半角的大小写字母、数字、中划线、下划线中的一种或几种组成。
        	 * 示例值：1246464644
        	 * 必填：是
        	 */
        	private String merchant_goods_id;
        	/**
        	 * 微信支付定义的统一商品编号（没有可不传）
        	 * 示例值：1001
        	 * 必填：否
        	 */
        	private String wechatpay_goods_id;
        	/**
        	 * 商品的实际名称
        	 * 示例值：iPhoneX 256G
        	 * 必填：否
        	 */
        	private String goods_name;
        	/**
        	 * 用户购买的数量
        	 * 示例值：1
        	 * 必填：是
        	 */
        	private Integer quantity;
        	/**
        	 * 商品单价，单位为分
        	 * 示例值：828800
        	 * 必填：是
        	 */
        	private Integer unit_price;
        	/**
        	 * 商品编码
        	 * 示例值：M1006
        	 */
        	@ApiField("goods_id")
        	private String goodsId;
        	/**
        	 * 商品优惠金额
        	 * 示例值：0
        	 * 必填：否
        	 */
        	@ApiField("discount_amount")
        	private Integer discountAmount;
        	/**
        	 * 商品备注信息
        	 * 示例值：商品备注信息
        	 * 必填：否
        	 */
        	@ApiField("goods_remark")
        	private String goodsRemark;
        }
    }
    
    @Data
    public static class SceneInfo{
    	/**
    	 * 用户的客户端IP，支持IPv4和IPv6两种格式的IP地址。
    	 * 示例值：14.23.150.211
    	 * 必填：是
    	 */
    	private Integer payer_client_ip	;
    	/**
    	 * 商户端设备号（门店号或收银设备ID）。
    	 * 示例值：013467007045764
    	 * 必填：否
    	 */
    	private String device_id;
    	/**
    	 * 商户门店信息
    	 * 必填：否
    	 */
    	private StoreInfo store_info;
    	
    	@Data
        public static class StoreInfo{
        	/**
        	 * 商户侧门店编号
        	 * 示例值：0001
        	 * 必填：是
        	 */
        	private String id;
        	/**
        	 * 商户侧门店名称
        	 * 示例值：腾讯大厦分店
        	 * 必填：否
        	 */
        	private String name;
        	/**
        	 * 地区编码，详细请见省市区编号对照表。
        	 * 示例值：440305
        	 * 必填：否
        	 */
        	private String area_code;
        	/**
        	 * 详细的商户门店地址
        	 * 示例值：广东省深圳市南山区科技中一道10000号
        	 * 必填：否
        	 */
        	private String address;
        }
    }
    
    @Data
    public static class SettleInfo{
    	/**
    	 * 是否指定分账
    	 * 示例值：false
    	 * 必填：否
    	 */
    	private boolean profit_sharing = false;
    }
    
}
