package com.zzp.shop.provider.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * OrderSink
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/4 14:07
 * @see  OrderSink
 **/
public interface OrderSink {
    /**
     * 订单取消通道
     * @return {@link SubscribableChannel}
     */
    @Input("order-in")
    SubscribableChannel receiveOrderCancel();
}
