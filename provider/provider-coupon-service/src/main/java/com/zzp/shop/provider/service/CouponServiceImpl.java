package com.zzp.shop.provider.service;

import com.zzp.shop.common.constant.ShopConstant;
import com.zzp.shop.common.exception.CastException;
import com.zzp.shop.provider.api.CouponService;
import com.zzp.shop.provider.domain.TradeCoupon;
import com.zzp.shop.provider.mapper.TradeCouponMapper;
import org.apache.dubbo.config.annotation.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 优惠卷服务接口实现类
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/2 22:36
 * @see  CouponServiceImpl
 **/
@Service(version = "0.0.1")
public class CouponServiceImpl implements CouponService {

    @Resource
    private TradeCouponMapper couponMapper;


    @Override
    public TradeCoupon findOne(Long couponId) {
        return couponMapper.selectByPrimaryKey(couponId);
    }

    @Override
    public Boolean updateCouponStatus(TradeCoupon coupon) {
        if (Objects.isNull(coupon) || Objects.isNull(coupon.getCouponId())){
            CastException.cast(ShopConstant.SHOP_REQUEST_PARAMETER_VALID);
        }
        if (couponMapper.updateByPrimaryKey(coupon)<=0){
            return ShopConstant.SHOP_FAIL.getSuccess();
        }
        return ShopConstant.SHOP_SUCCESS.getSuccess();
    }
}
