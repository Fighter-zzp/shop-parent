package com.zzp.shop.provider.bind;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
/**
 * 订单资源
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/3 18:44
 * @see  OrderSource
 **/
public interface OrderSource {
    /**
     * 发送订单取消
     * @return {@link MessageChannel}
     */
    @Output("order-out")
    MessageChannel sendOrderCancel();
}
