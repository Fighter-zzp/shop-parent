package com.zzp.shop.provider.listener;

import com.zzp.shop.common.constant.ShopConstant;
import com.zzp.shop.provider.domain.TradeOrder;
import com.zzp.shop.provider.domain.TradePay;
import com.zzp.shop.provider.mapper.TradeOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * 支付回调监听器
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/5 15:53
 * @see  PayCallBackListener
 **/
@Slf4j
@Component
public class PayCallBackListener {

    @Resource
    private TradeOrderMapper orderMapper;

    @StreamListener(value = "pay-in")
    public void receiveMsg(@Payload TradePay tradePay){
        log.info("收到支付消息");
        try{
            // 1. 查询订单
            var order = orderMapper.selectByPrimaryKey(tradePay.getOrderId());
            // 2. 修改订单支付状态
            order.setPayStatus(ShopConstant.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
            // 3. 更新数据库
            var i = orderMapper.updateByPrimaryKey(order);
            Assert.isTrue(i>0,"数据库修改失败");
            log.info("更改订单支付状态为已支付");
        }catch (Exception e){
            log.error("支付消息异常："+e.getMessage());
            e.printStackTrace();
        }
    }

}
