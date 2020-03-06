package com.zzp.shop.business.controller;

import com.zzp.shop.common.vo.ResultVo;
import com.zzp.shop.provider.api.OrderService;
import com.zzp.shop.provider.domain.TradeOrder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
/**
 * 见名知义
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/4 17:29
 * @see  OrderController
 **/
@RestController
@RequestMapping("/order")
@Api(value = "订单测试接口",tags = "用来测试接口")
public class OrderController {
    @Reference(version = "0.0.1")
    private OrderService orderService;

    @ApiOperation(value = "测试下达订单",notes = "测试而已")
    @GetMapping("/test")
    public String test(){
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
        return resultVo.toString();
    }

    @ApiOperation(value = "确认订单",notes = "用来实现订单的实现")
    @ApiImplicitParams({@ApiImplicitParam(name = "order",required = true,paramType = "json")})
    @PostMapping("/confirm")
    public ResultVo confirmOrder(@RequestBody TradeOrder order){
        return orderService.confirmOrder(order);
    }
}
