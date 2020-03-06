package com.zzp.shop.provider.listener;

import com.zzp.shop.common.constant.ShopConstant;
import com.zzp.shop.common.dto.MqDto;
import com.zzp.shop.provider.domain.TradeCoupon;
import com.zzp.shop.provider.mapper.TradeCouponMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * 订单取消消息监听器
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/4 11:19
 * @see  OrderCancelListener
 **/
@Component
@Slf4j
public class OrderCancelListener {

    @Resource
    private TradeCouponMapper couponMapper;

    @StreamListener("order-in1")
    public void get(@Payload MqDto dto){
        try {
            log.info("Coupon-->获取到消息");
            if (Objects.nonNull(dto.getCouponId())){
                // 1. 查询优惠卷
                var coupon = couponMapper.selectByPrimaryKey(dto.getCouponId());
                Assert.notNull(coupon,"优惠卷不存在");
                // 2.更改优惠券状态
                coupon.setUsedTime(null);
                coupon.setIsUsed(ShopConstant.SHOP_COUPON_UNUSED.getCode());
                coupon.setOrderId(null);
                couponMapper.updateByPrimaryKey(coupon);
            }
            log.info("回退优惠券成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error("回退优惠券失败");
        }
    }

}
