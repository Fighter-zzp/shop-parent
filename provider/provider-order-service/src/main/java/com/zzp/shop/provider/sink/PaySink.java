package com.zzp.shop.provider.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 支付Sink
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/5 16:07
 * @see  PaySink
 **/
public interface PaySink {
    /**
     * 支付回调通道
     * @return {@link SubscribableChannel}
     */
    @Input("pay-in")
    SubscribableChannel receive();
}
