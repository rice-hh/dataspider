package com.nh.dataspider.pay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlipayRequestModel {
	
	/**
     * 授权方式。
     * 支持：authorization_code：使用应用授权码换取应用授权令牌
     * 	   app_auth_token。refresh_token：使用app_refresh_token刷新获取新授权令牌。
     * 示例值：authorization_code或者refresh_token
     * 必填：是
     */
    private String grant_type;
    /**
     * 应用授权码，传入应用授权后得到的 app_auth_code。
     * 说明：grant_type 为 authorization_code 时，本参数必填；
     * 	   grant_type 为 refresh_token 时不填。
     * 示例值：1cc19911172e4f8aaa509c8fb5d12F56
     * 必填：否
     */
    private String code;
    /**
     * 刷新令牌，上次换取访问令牌时得到。
     * 本参数在 grant_type 为 authorization_code 时不填；
     * 					为 refresh_token 时必填，且该值来源于此接口的返回值 app_refresh_token（即至少需要通过 grant_type=authorization_code 调用此接口一次才能获取）。
     * 示例值：201509BBdcba1e3347de4e75ba3fed2c9abebE36
     * 必填：否
     */
    private String refresh_token;
	
//    /**
//     * 商户订单号。
//     * 由商家自定义，64个字符以内，仅支持字母、数字、下划线且需保证在商户端不重复。
//     * 示例值：20150320010101001
//     * 必填：是
//     */
//    private String out_trade_no;
//    /**
//     *  订单总金额。
//     *  单位为元，精确到小数点后两位，取值范围：[0.01,100000000] 。
//     *  示例值：88.88
//     *  必填：是
//     */
//    private double total_amount;
//    /**
//     *  订单标题。
//     *  注意：不可使用特殊字符，如 /，=，& 等。
//     *  示例值：Iphone6 16G
//     *  必填：是
//     */
//    private String subject;
//    /**
//     * 销售产品码。
//     * 如果签约的是当面付快捷版，则传 OFFLINE_PAYMENT；其它支付宝当面付产品传 FACE_TO_FACE_PAYMENT；不传则默认使用 FACE_TO_FACE_PAYMENT。
//     * 示例值：FACE_TO_FACE_PAYMENT
//     * 必填：否
//     */
//    private String product_code;
//    /**
//     * 卖家支付宝用户 ID。 
//     * 如果该值为空，则默认为商户签约账号对应的支付宝用户 ID。不允许收款账号与付款方账号相同
//     * 示例值：2088102146225135
//     * 必填：否
//     */
//    private String seller_id;
//    /**
//     * 订单附加信息。
//     * 如果请求时传递了该参数，将在异步通知、对账单中原样返回，同时会在商户和用户的pc账单详情中作为交易描述展示
//     * 示例值：Iphone6 16G
//     * 必填：否
//     */
//    private String body;
//    /**
//     * 支付授权码。
//     * 当面付场景传买家的付款码（25~30开头的长度为16~24位的数字，实际字符串长度以开发者获取的付款码长度为准）或者刷脸标识串（fp开头的35位字符串）。
//     * 示例值：28763443825664394
//     * 必填：否
//     */
//    private GoodsDetail goods_detail;
//    /**
//     * 业务扩展参数
//     * 必填：否
//     */
//    private ExtendParams extend_params;
//    /**
//     * 可打折金额
//     * 参与优惠计算的金额，单位为元，精确到小数点后两位，取值范围为 [0.01,100000000]。如果该值未传入，但传入了【订单总金额】和【不可打折金额】，则该值默认为【订单总金额】-【不可打折金额】
//     * 示例值：80.00
//     * 必填：否
//     */
//    private double discountable_amount;
//    /**
//     *  商户门店编号。
//     *  指商户创建门店时输入的门店编号。
//     *  示例值：NJ_001
//     *  必填：否
//     */
//    private String store_id;
//    /**
//     * 商户操作员编号。
//     * 示例值：yx_001
//     * 必填：否
//     */
//    private String operator_id;
//    /**
//     * 商户机具终端编号。
//     * 示例值：NJ_T_001
//     * 必填：否
//     */
//    private String terminal_id;
//    /**
//     * 商户原始订单号，最大长度限制 32 位
//     * 示例值：20161008001
//     * 必填：否
//     */
//    private String merchant_order_no;
    
    @Data
    public static class GoodsDetail{
    	/**
    	 * 商品的编号
    	 * 示例值：apple-01
    	 * 必填：是
    	 */
    	private String goods_id;
    	/**
    	 * 商品名称
    	 * 示例值：ipad
    	 * 必填：是
    	 */
    	private String goods_name;
    	/**
    	 * 商品数量
    	 * 示例值：1
    	 * 必填：是
    	 */
    	private double quantity;
    	/**
    	 * 商品单价，单位为元
    	 * 示例值：2000
    	 * 必填：是
    	 */
    	private double price;
    	/**
    	 * 商品类目
    	 * 示例值：34543238
    	 * 必填：否
    	 */
    	private String goods_category;
    	/**
    	 * 商品类目树，从商品类目根节点到叶子节点的类目id组成，类目id值使用|分割
    	 * 示例值：124868003|126232002|126252004
    	 * 必填：否
    	 */
    	private String categories_tree;
    	/**
    	 * 商品的展示地址
    	 * 示例值：http://www.alipay.com/xxx.jpg
    	 * 必填：否
    	 */
    	private String show_url;
    }
    
    @Data
    public static class ExtendParams{
    	/**
    	 * 系统商编号
    	 * 该参数作为系统商返佣数据提取的依据，请填写系统商签约协议的PID
    	 * 示例值：2088511833207846
    	 * 必填：是
    	 */
    	private String sys_service_provider_id;
    	/**
    	 * 卡类型
    	 * 示例值：S0JP0000
    	 * 必填：是
    	 */
    	private String card_type;
    	/**
    	 * 特殊场景下，允许商户指定交易展示的卖家名称
    	 * 示例值：XXX的跨境小铺
    	 * 必填：是
    	 */
    }
}
