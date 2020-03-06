package com.zzp.shop.provider.api;

import com.zzp.shop.common.vo.ResultVo;
import com.zzp.shop.provider.domain.TradeOrder;

/**
 * 订单服务接口
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/2 17:12
 * @see  OrderService
 **/
public interface OrderService {
    /**
     * 订单确认
     * @param order 订单
     * @return {@link ResultVo}
     */
    ResultVo confirmOrder(TradeOrder order);
}
