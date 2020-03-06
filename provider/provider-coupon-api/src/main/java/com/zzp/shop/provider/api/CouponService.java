package com.zzp.shop.provider.api;

import com.zzp.shop.provider.domain.TradeCoupon;

/**
 * 优惠卷服务提供接口
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/2 22:34
 * @see  CouponService
 **/
public interface CouponService {

    /**
     * 根据优惠卷id查询优惠卷
     * @param couponId 优惠卷id
     * @return {@link TradeCoupon}
     */
    TradeCoupon findOne(Long couponId);

    /**
     * 更细优惠券状态
     * @param coupon 优惠卷
     * @return .
     */
    Boolean updateCouponStatus(TradeCoupon coupon);
}
