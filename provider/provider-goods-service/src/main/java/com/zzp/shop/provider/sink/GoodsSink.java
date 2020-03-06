package com.zzp.shop.provider.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;
/**
 * GoodsSink
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/4 15:25
 * @see  GoodsSink
 **/
public interface GoodsSink {
    /**
     * 订单取消通道
     * @return .
     */
    @Input("order-in2")
    SubscribableChannel subMsg();
}
