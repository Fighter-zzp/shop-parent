package com.zzp.shop.common.exception;

import com.zzp.shop.common.constant.ShopConstant;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单异常抛出类
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/2 16:26
 * @see  CastException
 **/
@Slf4j
public class CastException {

    public static void cast(ShopConstant shopCode) {
        log.error(shopCode.toString());
        throw new CustomerException(shopCode);
    }
}
