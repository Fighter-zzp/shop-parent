package com.zzp.shop.provider.service;

import com.alibaba.fastjson.JSON;
import com.zzp.shop.common.constant.ShopConstant;
import com.zzp.shop.common.exception.CastException;
import com.zzp.shop.common.utils.IDWorker;
import com.zzp.shop.common.vo.ResultVo;
import com.zzp.shop.provider.api.PayService;
import com.zzp.shop.provider.bind.PaySource;
import com.zzp.shop.provider.domain.TradeMqProducerTemp;
import com.zzp.shop.provider.domain.TradePay;
import com.zzp.shop.provider.mapper.TradeMqProducerTempMapper;
import com.zzp.shop.provider.mapper.TradePayMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * 支付服务实现类
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/5 14:36
 * @see  PayServiceImpl
 **/
@Service(version = "0.0.1")
@Slf4j
public class PayServiceImpl implements PayService {

    /**
     * 数据包装
     */
    private String tag = "paid";
    private String topic = "payTopic";
    private String group = "payProducerGroup";

    /**
     * 支付mapper
     */
    @Resource
    private TradePayMapper tradePayMapper;

    /**
     * MQ消息生产Mapper
     */
    @Resource
    private TradeMqProducerTempMapper mqProducerTempMapper;

    /**
     * 线程池
     */
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 唯一id
     */
    @Resource
    private IDWorker idWorker;

    /**
     * 支付消息资源
     */
    @Resource
    private PaySource paySource;

    @Override
    public ResultVo createPayment(TradePay tradePay) {
        if (Objects.isNull(tradePay) || Objects.isNull(tradePay.getOrderId())){
            // 订单或者支付不存在
            CastException.cast(ShopConstant.SHOP_REQUEST_PARAMETER_VALID);
        }
        // 1. 判断支付状态
        var e = new Example(TradePay.class);
        e.createCriteria()
                .andEqualTo("orderId",tradePay.getOrderId())
                .andEqualTo("isPaid",ShopConstant.SHOP_PAYMENT_IS_PAID.getCode());
        var count = tradePayMapper.selectCountByExample(e);
        if (count>0){
            // 已经支付过
            CastException.cast(ShopConstant.SHOP_PAYMENT_IS_PAID);
        }
        // 2. 设置支付转态  --- 未支付
        tradePay.setIsPaid(ShopConstant.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        // 3. 保存支付订单
        tradePay.setPayId(idWorker.nextId());
        tradePayMapper.insert(tradePay);
        return new ResultVo(0,ShopConstant.SHOP_SUCCESS.getMessage(),ShopConstant.SHOP_SUCCESS.getSuccess());
    }

    @Override
    public ResultVo callbackPayment(TradePay tradePay) throws Exception {
        log.info("开始支付回调");
        // 1. 判断支付状态
        if (tradePay.getIsPaid().intValue() == ShopConstant.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode()){
            // 2. 更新支付状态
            var payId = tradePay.getPayId();
            var pay = tradePayMapper.selectByPrimaryKey(payId);
            if (Objects.isNull(pay)){
                CastException.cast(ShopConstant.SHOP_PAYMENT_NOT_FOUND);
            }
            pay.setIsPaid(ShopConstant.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());

            var i = tradePayMapper.updateByPrimaryKeySelective(pay);
            log.info("支付状态更新成功");
            if (i == 1){
                // 3. 创建支付成功消息
                var mqProducerTemp = new TradeMqProducerTemp();
                mqProducerTemp.setId(String.valueOf(idWorker.nextId()));
                mqProducerTemp.setGroupName(group);
                mqProducerTemp.setMsgTopic(topic);
                mqProducerTemp.setMsgTag(tag);
                mqProducerTemp.setMsgKey(String.valueOf(tradePay.getPayId()));
                mqProducerTemp.setMsgBody(JSON.toJSONString(tradePay));
                mqProducerTemp.setCreateTime(new Date());
                // 4. 存进数据
                mqProducerTempMapper.insert(mqProducerTemp);
                log.info("支付消息已存入数据库");
                // 在线程池中进行处理
                threadPoolTaskExecutor.submit(()->{
                    log.warn("这是线程哦");
                    // 5. 发送mq消息
                    if (sendMessage(tag,pay.getPayId().toString(),tradePay)){
                        log.info("消息发送成功");
                        // 6. 等待发送结果,如果MQ接受到消息,删除发送成功的消息
                        mqProducerTempMapper.deleteByPrimaryKey(mqProducerTemp.getId());
                        log.info("持久化到数据库的消息删除");
                    }else {
                        log.error("回调消息发送异常");
                    }
                });
            }
            return new ResultVo(0,ShopConstant.SHOP_SUCCESS.getMessage(),ShopConstant.SHOP_SUCCESS.getSuccess());
        }else {
            CastException.cast(ShopConstant.SHOP_PAYMENT_PAY_ERROR);
            return new ResultVo(1,ShopConstant.SHOP_FAIL.getMessage(),ShopConstant.SHOP_SUCCESS.getSuccess());
        }
    }

    /**
     * 发送回调消息
     * @param tag tag
     * @param key key
     * @param pay {@link TradePay}
     * @return 是否发送成功
     */
    private Boolean sendMessage(String tag, String key, TradePay pay){
        log.info("开始发送消息");
        return paySource.sendPayCallBack().send(MessageBuilder
                .withPayload(pay)
                .setHeader(RocketMQHeaders.TAGS, tag)
                .setHeader(RocketMQHeaders.KEYS, key).build());
    }
}
