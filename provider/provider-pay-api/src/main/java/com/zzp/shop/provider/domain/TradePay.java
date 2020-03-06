package com.zzp.shop.provider.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
/**
 * 支付实体类
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/5 12:07
 * @see  TradePay
 **/
@Data
@Table(name = "trade_pay")
public class TradePay implements Serializable {
    /**
     * 支付编号
     */
    @Id
    @Column(name = "pay_id")
    private Long payId;

    /**
     * 订单编号
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 支付金额
     */
    @Column(name = "pay_amount")
    private BigDecimal payAmount;

    /**
     * 是否已支付 1否 2是
     */
    @Column(name = "is_paid")
    private Integer isPaid;

    private static final long serialVersionUID = 1L;
}
