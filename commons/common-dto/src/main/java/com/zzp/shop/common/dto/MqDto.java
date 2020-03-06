package com.zzp.shop.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
/**
 * MQ对应订单的传输
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/2 16:13
 * @see  MqDto
 **/
@Data
@ApiModel(description = "MQ对应订单的传输")
public class MqDto {
    private Long orderId;
    private Long couponId;
    private Long userId;
    private BigDecimal userMoney;
    private Long goodsId;
    private Integer goodsNum;
}
