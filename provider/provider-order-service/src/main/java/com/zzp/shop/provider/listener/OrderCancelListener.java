package com.zzp.shop.provider.listener;

import com.zzp.shop.common.constant.ShopConstant;
import com.zzp.shop.common.dto.MqDto;
import com.zzp.shop.provider.mapper.TradeOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * 订单取消监听器
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/4 14:09
 * @see  OrderCancelListener
 **/
@Component
@Slf4j
public class OrderCancelListener {

    @Resource
    private TradeOrderMapper orderMapper;

    /**
     * <p>接收订单取消的消息，更改订单状态</p>
     * @param dto {@link MqDto}
     */
    @StreamListener("order-in")
    public void get(@Payload MqDto dto){
        try {
            //1. 解析消息内容
            log.info("接受消息成功");
            //2. 查询订单
            Assert.notNull(dto.getOrderId(),"订单id不存在");
            var order = orderMapper.selectByPrimaryKey(dto.getOrderId());
            //3.更新订单状态为取消
            order.setOrderStatus(ShopConstant.SHOP_ORDER_CANCEL.getCode());
            orderMapper.updateByPrimaryKey(order);
            log.info("订单状态设置为取消");
        } catch (Exception e) {
            log.info("订单取消失败,原因：{}",e.getMessage());
            e.printStackTrace();
        }
    }
}
