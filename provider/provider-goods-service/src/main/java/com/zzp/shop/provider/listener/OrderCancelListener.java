package com.zzp.shop.provider.listener;

import com.alibaba.fastjson.JSON;
import com.zzp.shop.common.constant.ShopConstant;
import com.zzp.shop.common.dto.MqDto;
import com.zzp.shop.common.utils.KPMUtils;
import com.zzp.shop.provider.domain.TradeGoodsNumberLog;
import com.zzp.shop.provider.domain.TradeMqConsumerLog;
import com.zzp.shop.provider.dto.TradeMqConsumerLogDto;
import com.zzp.shop.provider.mapper.TradeGoodsMapper;
import com.zzp.shop.provider.mapper.TradeGoodsNumberLogMapper;
import com.zzp.shop.provider.mapper.TradeMqConsumerLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 货物消息监听者
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/3 21:58
 * @see  OrderCancelListener
 **/
@Component
@Slf4j
public class OrderCancelListener {

    /**
     * 商品Mapper
     */
    @Resource
    private TradeGoodsMapper goodsMapper;

    /**
     * 库存日志mapper
     */
    @Resource
    private TradeGoodsNumberLogMapper goodsNumberLogMapper;

    /**
     * mq消费日志Mapper
     */
    @Resource
    private TradeMqConsumerLogMapper mqConsumerLogMapper;


    @StreamListener(value = "order-in2")
    public void get(@Payload MqDto dto, @Headers Map<String,Object> headers){

        String msgId;
        String tags;
        String keys;
        String group = "order-out-group";
        TradeMqConsumerLog tradeMqConsumerLog = null;
        try{
            // 1. 准备数据解析
            msgId = (String) headers.get("rocketmq_MESSAGE_ID");
            tags = (String) headers.get("rocketmq_TAGS");
            keys = (String) headers.get("rocketmq_KEYS");

            // 储存数据
            tradeMqConsumerLog = new TradeMqConsumerLog();
            tradeMqConsumerLog.setMsgTag(tags);
            tradeMqConsumerLog.setMsgKey(keys);
            tradeMqConsumerLog.setGroupName(group);
            tradeMqConsumerLog.setMsgBody(JSON.toJSONString(dto));
            tradeMqConsumerLog.setMsgId(msgId);

            log.info("goods-->消息到了{}",dto);
            // 2. 查询消费记录
            var mqConsumerLogDto = new TradeMqConsumerLogDto();
            mqConsumerLogDto.setGroupName(group);
            mqConsumerLogDto.setMsgTag(tags);
            mqConsumerLogDto.setMsgKey(keys);
            var mqConsumerLog = findByMqConsumerLogDto(mqConsumerLogDto);

            // 3. 处理消息
            if (Objects.nonNull(mqConsumerLog)){
                // 消息消费处理转态
                var consumerStatus = mqConsumerLog.getConsumerStatus();
                // 处理过...返回
                if(ShopConstant.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode().intValue()==consumerStatus.intValue()){
                    log.info("消息:"+msgId+",已经处理过");
                    return;
                }

                // 正在处理...返回
                if(ShopConstant.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode().intValue()==consumerStatus.intValue()){
                    log.info("消息:"+msgId+",正在处理");
                    return;
                }

                // 处理失败
                if (ShopConstant.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode()==consumerStatus.intValue()){
                    // 获取处理过的次数
                    var times = mqConsumerLog.getConsumerTimes();
                    if (times>3){
                        // 处理次数大于3次则不再处理
                        log.warn("消息:"+msgId+",消息处理超过3次,不能再进行处理了");
                        return;
                    }
                    // 继续处理
                    mqConsumerLog.setConsumerStatus(ShopConstant.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());

                    // 使用数据库乐观锁更新
                    var r = optimisticLock(mqConsumerLog);
                    if(r<=0){
                        //未修改成功,其他线程并发修改
                        log.info("并发修改,稍后处理");
                    }
                }
            }else {
                // 4. 未消费过
                tradeMqConsumerLog.setConsumerStatus(ShopConstant.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());
                tradeMqConsumerLog.setConsumerTimes(0);
                mqConsumerLog = new TradeMqConsumerLog();
                BeanUtils.copyProperties(tradeMqConsumerLog,mqConsumerLog);
                //将消息处理信息添加到数据库
                mqConsumerLogMapper.insert(tradeMqConsumerLog);
            }
            // 5. 回退库存
            var goodsId = dto.getGoodsId();
            var goods = goodsMapper.selectByPrimaryKey(goodsId);
            goods.setGoodsNumber(goods.getGoodsNumber()+dto.getGoodsNum());
            // 更改库存
            goodsMapper.updateByPrimaryKey(goods);
            deleteGoodsNum(dto);
            // 6. 将消息的处理状态改为成功
            mqConsumerLog.setConsumerStatus(ShopConstant.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode());
            mqConsumerLog.setConsumerTimestamp(new Date());
            mqConsumerLogMapper.updateByPrimaryKey(mqConsumerLog);
            log.info("回退库存成功");
        }catch (Exception e){
            e.printStackTrace();
            // 异常处理
            exceptionBack(tradeMqConsumerLog);
        }
    }

    /**
     * 删除库存日志
     * @param dto .
     */
    private void deleteGoodsNum(MqDto dto) {
        var e = new Example(TradeGoodsNumberLog.class);
        e.createCriteria()
                .andEqualTo("goodsId",dto.getGoodsId())
                .andEqualTo("orderId",dto.getOrderId())
                .andLessThan("goodsNumber",0);
        var i = goodsNumberLogMapper.deleteByExample(e);
        if (i>0){
            log.info("日志清除成功");
        }else {
            log.warn("日志清除失败");
        }
    }

    void exceptionBack(TradeMqConsumerLog tradeMqConsumerLog){
        Assert.notNull(tradeMqConsumerLog,"参数不能为空");
        var primaryKey = new TradeMqConsumerLogDto();
        primaryKey.setMsgTag(tradeMqConsumerLog.getMsgTag());
        primaryKey.setMsgKey(tradeMqConsumerLog.getMsgKey());
        primaryKey.setGroupName(tradeMqConsumerLog.getGroupName());
        // 数据库查询到消费日志
        var consumerLog = findByMqConsumerLogDto(primaryKey);
        if (Objects.isNull(consumerLog)){
            tradeMqConsumerLog.setConsumerStatus(ShopConstant.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode());
            tradeMqConsumerLog.setConsumerTimes(1);
            //将消息处理信息添加到数据库
            mqConsumerLogMapper.insert(tradeMqConsumerLog);
        }else{
            // 次数+1更新数据库
            consumerLog.setConsumerTimes(consumerLog.getConsumerTimes()+1);
            mqConsumerLogMapper.updateByPrimaryKeySelective(consumerLog);
        }
    }
    /**
     * 使用数据库乐观锁更新
     * @param mqConsumerLog 。
     * @return 返回row
     */
    private Integer optimisticLock(TradeMqConsumerLog mqConsumerLog) {
        var e = new Example(TradeMqConsumerLog.class);
        e.createCriteria()
                .andEqualTo("groupName",mqConsumerLog.getGroupName())
                .andEqualTo("msgKey",mqConsumerLog.getMsgKey())
                .andEqualTo("msgTag",mqConsumerLog.getMsgTag())
                .andEqualTo("consumerTimes",mqConsumerLog.getConsumerTimes());
        return mqConsumerLogMapper.updateByExample(mqConsumerLog,e);
    }

    /**
     * 根据dto 查询TradeMqConsumerLog
     * @param dto .
     * @return .
     */
    private TradeMqConsumerLog findByMqConsumerLogDto(TradeMqConsumerLogDto dto){
        var e = new Example(TradeMqConsumerLog.class);
        e.createCriteria()
                .andEqualTo("groupName",dto.getGroupName())
                .andEqualTo("msgKey",dto.getMsgKey())
                .andEqualTo("msgTag",dto.getMsgTag());
        return mqConsumerLogMapper.selectOneByExample(e);
    }
}
