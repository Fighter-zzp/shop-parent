package com.zzp.shop.provider.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 用户余额日志实体类
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/4 11:59
 * @see  TradeUserMoneyLog
 **/
@Data
@Table(name = "trade_user_money_log")
public class TradeUserMoneyLog implements Serializable {
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
     * 日志类型 1订单付款 2 订单退款
     */
    @Column(name = "money_log_type")
    private Integer moneyLogType;

    @Column(name = "use_money")
    private BigDecimal userMoney;

    /**
     * 日志时间
     */
    @Column(name = "create_time")
    private Date createTime;

}
