package com.zzp.shop.provider.api;

import com.zzp.shop.common.vo.ResultVo;
import com.zzp.shop.provider.domain.TradePay;
import org.apache.dubbo.remoting.RemotingException;

/**
 * 支付服务接口
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/5 14:29
 * @see  PayService
 **/
public interface PayService {
    /**
     * 创建支付
     * @param tradePay 支付实体类
     * @return {@link ResultVo}
     */
    ResultVo createPayment(TradePay tradePay);

    /**
     * 回调支付
     * @param tradePay 支付实体类
     * @return {@link ResultVo}
     * @throws Exception .
     */
    ResultVo callbackPayment(TradePay tradePay) throws Exception;
}
