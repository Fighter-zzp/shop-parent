package com.zzp.shop.provider.api;

import com.zzp.shop.provider.domain.TradeUser;
import com.zzp.shop.provider.domain.TradeUserMoneyLog;

/**
 * 用户服务接口
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/2 18:02
 * @see  UserService
 **/
public interface UserService {
    /**
     * 根据用户id查询用户
     * @param userId .
     * @return .
     */
    TradeUser findOne(Long userId);

    /**
     * 更改已付金额
     * <p>支付后改变数据库用户金额</p>
     * @param userMoneyLog {@link TradeUserMoneyLog}
     * @return .
     */
    Boolean updateMoneyPaid(TradeUserMoneyLog userMoneyLog);
}
