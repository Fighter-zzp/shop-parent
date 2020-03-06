package com.zzp.shop.provider.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * Goods实体类
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/2 17:41
 * @see  TradeGoods
 **/
@Data
@Table(name = "trade_goods")
public class TradeGoods implements Serializable {
    private static final long serialVersionUID = 2145900513272989626L;
    @Id
    @Column(name = "goods_id")
    @GeneratedValue(generator = "JDBC")
    private Long goodsId;

    /**
     * 商品名称
     */
    @Column(name = "goods_name")
    private String goodsName;

    /**
     * 商品库存
     */
    @Column(name = "goods_number")
    private Integer goodsNumber;

    /**
     * 商品价格
     */
    @Column(name = "goods_price")
    private BigDecimal goodsPrice;

    /**
     * 商品描述
     */
    @Column(name = "goods_desc")
    private String goodsDesc;

    /**
     * 添加时间
     */
    @Column(name = "add_time")
    private Date addTime;
}
