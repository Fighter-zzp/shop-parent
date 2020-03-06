package com.zzp.shop.provider.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 优惠卷sink
 * <p>
 *  用来接通通道
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/4 11:16
 * @see  CouponSink
 **/
public interface CouponSink {
    /**
     * 获取订阅通道
     * @return {@link SubscribableChannel}
     */
    @Input("order-in1")
    SubscribableChannel receiveCancelMessage();
}
