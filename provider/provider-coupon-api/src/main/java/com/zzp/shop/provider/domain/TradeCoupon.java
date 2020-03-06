package com.zzp.shop.provider.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "trade_coupon")
public class TradeCoupon implements Serializable {
    /**
     * 优惠券ID
     */
    @Id
    @Column(name = "coupon_id")
    private Long couponId;

    /**
     * 优惠券金额
     */
    @Column(name = "coupon_price")
    private BigDecimal couponPrice;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 订单ID
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 是否使用 0未使用 1已使用
     */
    @Column(name = "is_used")
    private Integer isUsed;

    /**
     * 使用时间
     */
    @Column(name = "used_time")
    private Date usedTime;

    private static final long serialVersionUID = 1L;
}
