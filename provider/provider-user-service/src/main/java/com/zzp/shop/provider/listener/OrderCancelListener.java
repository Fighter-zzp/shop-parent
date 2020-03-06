package com.zzp.shop.provider.listener;

import com.zzp.shop.common.constant.ShopConstant;
import com.zzp.shop.common.dto.MqDto;
import com.zzp.shop.provider.api.UserService;
import com.zzp.shop.provider.domain.TradeUserMoneyLog;
import com.zzp.shop.provider.mapper.TradeUserMapper;
import com.zzp.shop.provider.mapper.TradeUserMoneyLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * 订单取消消息监听器
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/4 11:42
 * @see  OrderCancelListener
 **/
@Slf4j
@Component
public class OrderCancelListener {

    /**
     * 用户服务接口
     */
    @Resource
    private UserService userService;

    @StreamListener("order-in3")
    public void get(@Payload MqDto dto){
        try {
            log.info("接收消息");
            if (Objects.nonNull(dto.getUserMoney()) && dto.getUserMoney().compareTo(BigDecimal.ZERO)>0){
                // 2. 接收数据
                var userMoneyLog = new TradeUserMoneyLog();
                BeanUtils.copyProperties(dto,userMoneyLog);
                userMoneyLog.setMoneyLogType(ShopConstant.SHOP_USER_MONEY_REFUND.getCode());
                // 3. 更新数据
                var paidFlag = userService.updateMoneyPaid(userMoneyLog);
                Assert.isTrue(paidFlag,"余额回退失败");
                log.info("余额回退成功");
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
