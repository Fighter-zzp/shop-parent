package com.zzp.shop.business.controller;

import com.zzp.shop.common.vo.ResultVo;
import com.zzp.shop.provider.api.PayService;
import com.zzp.shop.provider.domain.TradePay;
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
/**
 * 支付层接口
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/5 16:14
 * @see  PayController
 **/
@RestController
@RequestMapping("/pay")
@Api(value = "支付接口",tags = "完成支付功能")
public class PayController {

    @Reference(version = "0.0.1",timeout = 10000,retries = 0)
    private PayService payService;

    @ApiOperation(value = "创建支付",notes = "用来实现支付的创建")
    @ApiImplicitParams({@ApiImplicitParam(name = "支付",required = true,paramType = "json")})
    @PostMapping("/createPayment")
    public ResultVo createPayment(@RequestBody TradePay pay) {
        return payService.createPayment(pay);
    }

    @ApiOperation(value = "支付回调",notes = "用来实现支付的回调")
    @ApiImplicitParams({@ApiImplicitParam(name = "支付",required = true,paramType = "json")})
    @PostMapping("/callBackPayment")
    public ResultVo callBackPayment(@RequestBody TradePay pay) throws Exception {
        return payService.callbackPayment(pay);
    }

}

