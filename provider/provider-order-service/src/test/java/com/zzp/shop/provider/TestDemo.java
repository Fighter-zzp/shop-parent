package com.zzp.shop.provider;

import com.zzp.shop.common.constant.ShopConstant;
import com.zzp.shop.common.exception.CastException;
import com.zzp.shop.common.exception.CustomerException;
import com.zzp.shop.common.utils.IDWorker;
import com.zzp.shop.common.vo.ResultVo;

public class TestDemo {
    public static void main(String[] args) {
        try{
            CastException.cast(ShopConstant.SHOP_REQUEST_PARAMETER_VALID);
        }catch (Exception e){
            var s =(CustomerException)e;
            System.out.println(s.getShopCode());
        }
    }
}
