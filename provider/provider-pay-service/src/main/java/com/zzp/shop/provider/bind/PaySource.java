package com.zzp.shop.provider.bind;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 支付消息资源
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/5 15:14
 * @see  PaySource
 **/
public interface PaySource {
    /**
     * 发送支付回调
     * @return {@link MessageChannel}
     */
    @Output("pay-callback")
    MessageChannel sendPayCallBack();
}
