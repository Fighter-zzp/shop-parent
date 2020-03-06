package com.zzp.shop.provider.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 订单实体类
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/2 17:11
 * @see  TradeOrder
 **/
@Data
@Table(name = "trade_order")
public class TradeOrder implements Serializable {
    private static final long serialVersionUID = -5873161501338569345L;
    /**
     * 订单ID
     */
    @Id
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 订单状态 0未确认 1已确认 2已取消 3无效 4退款
     */
    @Column(name = "order_status")
    private Integer orderStatus;

    /**
     * 支付状态 0未支付 1支付中 2已支付
     */
    @Column(name = "pay_status")
    private Integer payStatus;

    /**
     * 发货状态 0未发货 1已发货 2已收货
     */
    @Column(name = "shipping_status")
    private Integer shippingStatus;

    /**
     * 收货地址
     */
    @Column(name = "address")
    private String address;

    /**
     * 收货人
     */
    @Column(name = "consignee")
    private String consignee;

    /**
     * 商品ID
     */
    @Column(name = "goods_id")
    private Long goodsId;

    /**
     * 商品数量
     */
    @Column(name = "goods_number")
    private Integer goodsNumber;

    /**
     * 商品价格
     */
    @Column(name = "goods_price")
    private BigDecimal goodsPrice;

    /**
     * 商品总价
     */
    @Column(name = "goods_amount")
    private BigDecimal goodsAmount;

    /**
     * 运费
     */
    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    /**
     * 订单价格
     */
    @Column(name = "order_amount")
    private BigDecimal orderAmount;

    /**
     * 优惠券ID
     */
    @Column(name = "coupon_id")
    private Long couponId;

    /**
     * 优惠券
     */
    @Column(name = "coupon_paid")
    private BigDecimal couponPaid;

    /**
     * 已付金额
     */
    @Column(name = "money_paid")
    private BigDecimal moneyPaid;

    /**
     * 支付金额
     */
    @Column(name = "pay_amount")
    private BigDecimal payAmount;

    /**
     * 创建时间
     */
    @Column(name = "add_time")
    private Date addTime;

    /**
     * 订单确认时间
     */
    @Column(name = "confirm_time")
    private Date confirmTime;

    /**
     * 支付时间
     */
    @Column(name = "pay_time")
    private Date payTime;
}
