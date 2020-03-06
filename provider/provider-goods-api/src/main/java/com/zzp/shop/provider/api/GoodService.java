package com.zzp.shop.provider.api;

import com.zzp.shop.common.vo.ResultVo;
import com.zzp.shop.provider.domain.TradeGoods;
import com.zzp.shop.provider.domain.TradeGoodsNumberLog;

/**
 * 货品服务提供接口
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/2 17:42
 * @see  GoodService
 **/
public interface GoodService {
    /**
     * 根据goodsid获取货品
     * @param goodsId .
     * @return .
     */
    TradeGoods findOne(Long goodsId);

    /**
     * 扣减库存
     * @param goodsNumberLog 库存类
     * @return 扣除是否成功
     */
    Boolean reduceGoodsNum(TradeGoodsNumberLog goodsNumberLog);
}
