package com.zzp.shop.provider.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 货品数记录日志实体类
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/2 17:44
 * @see  TradeGoodsNumberLog
 **/
@Data
@Table(name = "trade_goods_number_log")
public class TradeGoodsNumberLog implements Serializable {
    /**
     * 商品ID
     */
    @Column(name = "goods_id")
    private Long goodsId;

    /**
     * 订单ID
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 库存数量
     */
    @Column(name = "goods_number")
    private Integer goodsNumber;

    @Column(name = "log_time")
    private Date logTime;

    private static final long serialVersionUID = 1L;
}
