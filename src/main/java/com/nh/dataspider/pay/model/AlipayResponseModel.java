package com.nh.dataspider.pay.model;

import com.alipay.api.internal.mapping.ApiField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付
 *
 */
@Data
public class AlipayResponseModel {
	
    /**
     * 请求的业务参数（原样返回）
     * 示例值：merchantBizType%3d3C%26merchantBizNo%3d2016010101111
     */
	@ApiField("passback_params")
    private String passbackParams;
    /**
     *  支付宝交易号。
     *  示例值：2013112011001004330000121536
     */
	@ApiField("trade_no")
    private String tradeNo;
    /**
     * 商户订单号
     * 示例值：6823789339978248
     * 必填：否
     */
	@ApiField("out_trade_no")
    private String outTradeNo;
    /**
     * 买家支付宝账号
     * 示例值：159****5620
     */
	@ApiField("buyer_logon_id")
    private String buyerLogonId;
    /**
     * 交易金额
     * 示例值：120.88
     */
	@ApiField("total_amount")
    private Double totalAmount;
	/**
     * 实收金额
     * 示例值：88.88
     */
	@ApiField("receipt_amount")
    private String receiptAmount;
	/**
     * 买家付款的金额
     * 示例值：8.88
     */
	@ApiField("buyer_pay_amount")
    private Double buyerPayAmount;
    /**
     * 使用集分宝付款的金额
     * 示例值：8.12
     */
	@ApiField("point_amount")
    private Double pointAmount;
    /**
     *  交易中可给用户开具发票的金额
     *  示例值：12.50
     */
	@ApiField("invoice_amount")
    private Double invoiceAmount;
    /**
     * 交易支付时间
     * 示例值：2014-11-27 15:45:57
     */
	@ApiField("gmt_payment")
    private String gmtPayment;
    /**
     * 发生支付交易的商户门店名称
     * 示例值：证大五道口店
     */
	@ApiField("storeName")
    private String store_name;
    /**
     * 买家在支付宝的用户id
     * 示例值：2088101117955611
     */
	@ApiField("buyer_user_id")
    private String buyerUserId;
	/**
     * 本次交易支付所使用的单品券优惠的商品优惠信息。
     * 只有在query_options中指定时才返回该字段信息。
     * 示例值：[{"goods_id":"STANDARD1026181538","goods_name":"雪碧","discount_amount":"100.00","voucher_id":"2015102600073002039000002D5O"}]
     */
	@ApiField("discount_goods_detail")
    private String discountGoodsDetail;
	/**
     * 商家优惠金额
     * 示例值：88.88
     */
	@ApiField("mdiscount_amount")
    private String mdiscountAmount;
	/**
     * 平台优惠金额
     * 示例值：88.88
     */
	@ApiField("discount_amount")
    private String discountAmount;
	/**
     * 平台优惠金额
     * 示例值：88.88
     */
	@ApiField("fund_bill_list")
    private String fundBillList;
	/**
     * 平台优惠金额
     * 示例值：88.88
     */
	@ApiField("voucher_detail_list")
    private String voucherDetailList;
	/**
     * 退款时间
     * 示例值：2022-08-11 13:51:37
     */
	@ApiField("gmt_refund_pay")
    private String gmtRefundPay;
	/**
     * 退款金额
     * 示例值：0.01
     */
	@ApiField("refund_fee")
    private String refundFee;
	
    @Data
    public static class VoucherDetailList{
    	/**
    	 * 券id
    	 * 示例值：2015102600073002039000002D5O
    	 */
    	@ApiField("id")
    	private String id;
    	/**
    	 * 券名称
    	 * 示例值：XX超市5折优惠
    	 */
    	@ApiField("name")
    	private String name;
    	/**
    	 * 券类型
    	 * 如：ALIPAY_FIX_VOUCHER - 全场代金券
    	 * 	 ALIPAY_DISCOUNT_VOUCHER - 折扣券
    	 * 	 ALIPAY_ITEM_VOUCHER - 单品优惠券
    	 * 	 ALIPAY_CASH_VOUCHER - 现金抵价券
    	 * 	 ALIPAY_BIZ_VOUCHER - 商家全场券
    	 * 注：不排除将来新增其他类型的可能，商家接入时注意兼容性避免硬编码
    	 * 示例值：ALIPAY_FIX_VOUCHER
    	 */
    	@ApiField("type")
    	private String type;
    	/**
    	 * 优惠券面额，它应该会等于商家出资加上其他出资方出资
    	 * 示例值：10.00
    	 */
    	@ApiField("amount")
    	private Double amount;
    	/**
    	 * 商家出资（特指发起交易的商家出资金额）
    	 * 示例值：9.00
    	 */
    	@ApiField("merchant_contribute")
    	private Double merchantContribute;
    	/**
    	 * 其他出资方出资金额，可能是支付宝，可能是品牌商，或者其他方，也可能是他们的一起出资
    	 * 示例值：1.00
    	 */
    	@ApiField("other_contribute")
    	private Double otherContribute;
    	/**
    	 * 优惠券备注信息
    	 * 示例值：学生专用优惠
    	 */
    	@ApiField("memo")
    	private String memo;
    	/**
    	 * 券模板id
    	 * 示例值：20171030000730015359000EMZP0
    	 */
    	@ApiField("template_id")
    	private String templateId;
    	/**
    	 * 如果使用的这张券是用户购买的，则该字段代表用户在购买这张券时用户实际付款的金额d
    	 * 示例值：2.01
    	 */
    	@ApiField("purchase_buyer_contribute")
    	private Double purchaseBuyerContribute;
    	/**
    	 * 如果使用的这张券是用户购买的，则该字段代表用户在购买这张券时商户优惠的金额
    	 * 示例值：1.03
    	 */
    	@ApiField("purchase_merchant_contribute")
    	private Double purchaseMerchantContribute;
    	/**
    	 * 如果使用的这张券是用户购买的，则该字段代表用户在购买这张券时平台优惠的金额
    	 * 示例值：0.82
    	 */
    	@ApiField("purchase_ant_contribute")
    	private Double purchaseAntContribute;
    }
    
    @Data
    public static class FundBillList{
    	/**
    	 * 交易使用的资金渠道，详见 支付渠道列表
    	 * 示例值：ALIPAYACCOUNT
    	 */
    	@ApiField("fund_channel")
    	private String fundChannel;
    	/**
    	 * 该支付工具类型所使用的金额
    	 * 示例值：10
    	 */
    	@ApiField("amount")
    	private Double amount;
    	/**
    	 * 渠道实际付款金额
    	 * 示例值：11.21
    	 */
    	@ApiField("real_amount")
    	private Double realAmount;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassbackParams{
    	/**
    	 * 租户id
    	 */
    	private String tenantId;
    	/**
    	 * 用户id
    	 */
    	private String userId;
    	/**
    	 * 模块id
    	 */
    	private String visualDevId;
    	/**
    	 * 业务数据id
    	 */
    	private String visualDataId;
    }
    
}
