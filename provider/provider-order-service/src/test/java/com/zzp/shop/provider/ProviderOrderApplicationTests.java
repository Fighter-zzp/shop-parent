package com.zzp.shop.provider;
import java.math.BigDecimal;

import com.zzp.shop.common.dto.MqDto;
import com.zzp.shop.common.utils.IDWorker;
import com.zzp.shop.common.vo.ResultVo;
import com.zzp.shop.provider.api.GoodService;
import com.zzp.shop.provider.api.OrderService;
import com.zzp.shop.provider.api.UserService;
import com.zzp.shop.provider.bind.OrderSource;
import com.zzp.shop.provider.domain.TradeOrder;
import com.zzp.shop.provider.domain.TradeUser;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProviderOrderApplication.class)
public class ProviderOrderApplicationTests {

    @Resource
    private OrderService orderService;

    /**
     * 测试订单
     */
    @Test
    public void test1() {
        var coupouId = 345988230098857984L;
        var userId = 345963634385633280L;
        var goodsId = 345959443973935104L;
        var o = new TradeOrder();
        o.setUserId(userId);
        o.setAddress("新西兰");
        o.setGoodsId(goodsId);
        o.setGoodsNumber(1);
        o.setGoodsPrice(new BigDecimal("5000"));
        o.setShippingFee(new BigDecimal("0"));
        o.setOrderAmount(new BigDecimal("5000"));
        o.setCouponId(coupouId);
        o.setMoneyPaid(new BigDecimal("100"));
        var resultVo = orderService.confirmOrder(o);
        System.out.println(resultVo);
    }


    @Value("${mq.order.tag.cancel}")
    private String tag;

    @Test
    public void test2(){
        var mqDto = new MqDto();
        mqDto.setOrderId(0L);
        mqDto.setCouponId(0L);
        mqDto.setUserId(0L);
        mqDto.setUserMoney(new BigDecimal("0"));
        mqDto.setGoodsId(0L);
        mqDto.setGoodsNum(0);
        sendOrderCancel(tag,"keys",mqDto);
    }
    @Resource
    private OrderSource orderSource;

    private void sendOrderCancel(String tag, String key, MqDto body){
        var s =orderSource.sendOrderCancel().send(MessageBuilder
                .withPayload(body)
                .setHeader(RocketMQHeaders.TAGS,tag)
                .setHeader(RocketMQHeaders.KEYS,key).build());
        System.out.printf("发送%s",s?"成功":"失败");
    }
}
