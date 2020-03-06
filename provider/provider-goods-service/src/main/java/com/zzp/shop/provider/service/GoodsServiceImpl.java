package com.zzp.shop.provider.service;

import com.zzp.shop.common.constant.ShopConstant;
import com.zzp.shop.common.exception.CastException;
import com.zzp.shop.provider.api.GoodService;
import com.zzp.shop.provider.domain.TradeGoods;
import com.zzp.shop.provider.domain.TradeGoodsNumberLog;
import com.zzp.shop.provider.mapper.TradeGoodsMapper;
import com.zzp.shop.provider.mapper.TradeGoodsNumberLogMapper;
import org.apache.dubbo.config.annotation.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * 货品服务接口实现
 * <p>
 * 人皆知有用之用，而莫知无用之用也
 * </p>
 *
 * @author 佐斯特勒
 * @version v1.0.0
 * @date 2020/3/2 17:47
 * @see GoodsServiceImpl
 **/
@Service(version = "0.0.1")
public class GoodsServiceImpl implements GoodService {
    /**
     * 货品持久层接口
     */
    @Resource
    private TradeGoodsMapper goodsMapper;

    /**
     * 库存变更日志持久层接口
     */
    @Resource
    private TradeGoodsNumberLogMapper goodsNumberLogMapper;

    @Override
    public TradeGoods findOne(Long goodsId) {
        return goodsMapper.selectByPrimaryKey(goodsId);
    }

    @Override
    public Boolean reduceGoodsNum(TradeGoodsNumberLog goodsNumberLog) {
        // 判断库存的非空检验
        if (Objects.isNull(goodsNumberLog) ||
                Objects.isNull(goodsNumberLog.getOrderId()) ||
                Objects.isNull(goodsNumberLog.getGoodsNumber()) ||
                goodsNumberLog.getGoodsNumber() <= 0) {
            CastException.cast(ShopConstant.SHOP_REQUEST_PARAMETER_VALID);
        }
        var goods = goodsMapper.selectByPrimaryKey(goodsNumberLog.getGoodsId());
        // 判断库存是否够
        if (goods.getGoodsNumber() < goodsNumberLog.getGoodsNumber()) {
            //库存不足
            CastException.cast(ShopConstant.SHOP_GOODS_NUM_NOT_ENOUGH);
        }
        // 减库存
        goods.setGoodsNumber(goods.getGoodsNumber() - goodsNumberLog.getGoodsNumber());
        goodsMapper.updateByPrimaryKeySelective(goods);

        // 库存日志库更改
        goodsNumberLog.setGoodsNumber(-(goodsNumberLog.getGoodsNumber()));
        goodsNumberLog.setLogTime(new Date());
        var row = goodsNumberLogMapper.insert(goodsNumberLog);
        if (row<=0){
            // 插入失败
            return ShopConstant.SHOP_FAIL.getSuccess();
        }
        return ShopConstant.SHOP_SUCCESS.getSuccess();
    }
}
