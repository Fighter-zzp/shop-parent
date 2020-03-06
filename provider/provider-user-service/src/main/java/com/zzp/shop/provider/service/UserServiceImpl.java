package com.zzp.shop.provider.service;

import com.zzp.shop.common.constant.ShopConstant;
import com.zzp.shop.common.exception.CastException;
import com.zzp.shop.provider.api.UserService;
import com.zzp.shop.provider.domain.TradeUser;
import com.zzp.shop.provider.domain.TradeUserMoneyLog;
import com.zzp.shop.provider.mapper.TradeUserMapper;
import com.zzp.shop.provider.mapper.TradeUserMoneyLogMapper;
import org.apache.dubbo.config.annotation.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 见名知义
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/2 18:01
 * @see  UserServiceImpl
 **/
@Service(version = "0.0.1")
public class UserServiceImpl implements UserService {

    @Resource
    private TradeUserMapper userMapper;

    @Resource
    private TradeUserMoneyLogMapper userMoneyLogMapper;

    @Override
    public TradeUser findOne(Long userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public Boolean updateMoneyPaid(TradeUserMoneyLog userMoneyLog) {
        // 1.校验参数是否合法
        if (Objects.isNull(userMoneyLog) ||
                Objects.isNull(userMoneyLog.getOrderId()) ||
                Objects.isNull(userMoneyLog.getUserId()) ||
                Objects.isNull(userMoneyLog.getUserMoney()) ||
        userMoneyLog.getUserMoney().compareTo(BigDecimal.ZERO)<=0){
            CastException.cast(ShopConstant.SHOP_REQUEST_PARAMETER_VALID);
        }
        // 2. 查看订单余额日志
        var example = new Example(TradeUserMoneyLog.class);
        example.createCriteria()
                .andEqualTo("orderId",userMoneyLog.getOrderId())
                .andEqualTo("userId",userMoneyLog.getUserId());
        // 查找是否有对应的订单日志
        var count = userMoneyLogMapper.selectCountByExample(example);
        var tradeUser = userMapper.selectByPrimaryKey(userMoneyLog.getUserId());
        // 3. 扣减余额
        if (userMoneyLog.getMoneyLogType().equals(ShopConstant.SHOP_USER_MONEY_PAID.getCode())){
            if (count>0){
                // 已经付款
                CastException.cast(ShopConstant.SHOP_ORDER_PAY_STATUS_IS_PAY);
            }
            // 未付款就直接扣余额
            tradeUser.setUserMoney(new BigDecimal(tradeUser.getUserMoney())
                    .subtract(userMoneyLog.getUserMoney()).longValue());
            userMapper.updateByPrimaryKey(tradeUser);
        }
        // 4. 回退余额
        // 是否回退
        if (userMoneyLog.getMoneyLogType().equals(ShopConstant.SHOP_USER_MONEY_REFUND.getCode())){
            if(count<0){
                // 没有付款，无法回退
                CastException.cast(ShopConstant.SHOP_ORDER_PAY_STATUS_NO_PAY);
            }
            // 防止多次回退
            var e = new Example(TradeUserMoneyLog.class);
            e.createCriteria()
                    .andEqualTo("orderId",userMoneyLog.getOrderId())
                    .andEqualTo("userId",userMoneyLog.getUserId())
                    .andEqualTo("moneyLogType",ShopConstant.SHOP_USER_MONEY_REFUND.getCode());
            if (userMoneyLogMapper.selectCountByExample(e)>0){
                CastException.cast(ShopConstant.SHOP_USER_MONEY_REFUND_ALREADY);
            }
            // 退款处理
            tradeUser.setUserMoney(new BigDecimal(
                    tradeUser.getUserMoney()).add(userMoneyLog.getUserMoney()).longValue());
            userMapper.updateByPrimaryKey(tradeUser);
        }
        //5. 记录订单余额使用日志
        userMoneyLog.setCreateTime(new Date());
        userMoneyLogMapper.insert(userMoneyLog);
        return ShopConstant.SHOP_SUCCESS.getSuccess();
    }
}
