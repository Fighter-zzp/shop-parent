package com.zzp.shop.common.exception;

import com.zzp.shop.common.constant.ShopConstant;

/**
 * 自定义异常
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/2 16:29
 * @see  CustomerException
 **/
public class CustomerException extends RuntimeException{

    private ShopConstant shopCode;

    public CustomerException(ShopConstant shopCode) {
        this.shopCode = shopCode;
    }

    /**
     * 获取异常
     * @return .
     */
    public ShopConstant getShopCode() {
        return shopCode;
    }
}
