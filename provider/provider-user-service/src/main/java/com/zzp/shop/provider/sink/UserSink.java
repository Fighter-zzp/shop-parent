package com.zzp.shop.provider.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 用户消息通道接收器
 * <p>
 *  创建订阅通道
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/4 11:37
 * @see  UserSink
 **/
public interface UserSink {
    /**
     * 订单取消消息通道
     * @return .
     */
    @Input("order-in3")
    SubscribableChannel receiveOrderCancel();
}
