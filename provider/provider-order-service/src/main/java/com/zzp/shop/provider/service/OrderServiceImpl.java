package com.zzp.shop.provider.service;

import com.zzp.shop.common.constant.ShopConstant;
import com.zzp.shop.common.dto.MqDto;
import com.zzp.shop.common.exception.CastException;
import com.zzp.shop.common.exception.CustomerException;
import com.zzp.shop.common.utils.IDWorker;
import com.zzp.shop.common.vo.ResultVo;
import com.zzp.shop.provider.api.CouponService;
import com.zzp.shop.provider.api.GoodService;
import com.zzp.shop.provider.api.OrderService;
import com.zzp.shop.provider.api.UserService;
import com.zzp.shop.provider.bind.OrderSource;
import com.zzp.shop.provider.domain.TradeGoodsNumberLog;
import com.zzp.shop.provider.domain.TradeOrder;
import com.zzp.shop.provider.domain.TradeUserMoneyLog;
import com.zzp.shop.provider.mapper.TradeOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 订单服务实现接口
 * <p>
 * 人皆知有用之用，而莫知无用之用也
 * </p>
 *
 * @author 佐斯特勒
 * @version v1.0.0
 * @date 2020/3/2 17:12
 * @see OrderServiceImpl
 **/
@Slf4j
@Service(version = "0.0.1")
public class OrderServiceImpl implements OrderService {

    @Resource
    private TradeOrderMapper orderMapper;

    /**
     * 货品服务
     */
    @Reference(version = "0.0.1")
    private GoodService goodService;

    /**
     * 用户服务
     */
    @Reference(version = "0.0.1")
    private UserService userService;

    /**
     * 优惠卷服务
     */
    @Reference(version = "0.0.1")
    private CouponService couponService;

    /**
     * id生成器
     */
    @Resource
    private IDWorker idWorker;

    /**
     * 订单资源
     */
    @Resource
    private OrderSource orderSource;

    /**
     * tag字段
     */
    @Value("${mq.order.tag.cancel}")
    private String tag;

    @Override
    public ResultVo confirmOrder(TradeOrder order) {
        //1.校验订单
        checkOrder(order);
        //2.生成预订单
        var orderId = savePreOrder(order);
        order.setOrderId(orderId);
        try {
            //3.扣减库存
            reduceGoodsNum(order);
            //4.扣减优惠券
            updateCouponStatus(order);
            //5.使用余额
            reduceMoneyPaid(order);

            // 订单出现异常
//            CastException.cast(ShopConstant.SHOP_FAIL);

            // 6.确认订单
            updateOrderStatus(order);
            log.info("OrderService--->订单:[" + orderId + "]确认成功");
            //7.返回成功状态
            return new ResultVo(0, ShopConstant.SHOP_SUCCESS.getMessage(), ShopConstant.SHOP_SUCCESS.getSuccess());
        } catch (Exception e) {
            //1.确认订单失败,发送消息
            var dto = new MqDto();
            BeanUtils.copyProperties(order, dto);
            dto.setGoodsNum(order.getGoodsNumber());
            dto.setUserMoney(order.getMoneyPaid());
//            tag = tag + chooseTag(e);
            sendOrderCancel(tag, order.getOrderId().toString(), dto);
            //2.返回失败状态
            log.error("OrderService--->订单:[" + orderId + "]确认订单失败");
            return new ResultVo(1, ShopConstant.SHOP_FAIL.getMessage(), ShopConstant.SHOP_FAIL.getSuccess());
        }
    }

    /**
     * 根据异常选取Tag类型
     * @param e 捕捉的异常
     * @return 得到的tag字符串
     */
    private String chooseTag(Exception e) {
        var shopCode = (CustomerException) e;
        var code = shopCode.getShopCode().getCode();
        var s = new StringBuilder();
        switch (code) {
            // 库存扣减异常
            case 20004:
                s.append("K");
                break;
            // 优惠卷异常
            case 40003:
                s.append("KY");
                break;
            // 减少余额异常
            case 30003:
                s.append("KYY");
                break;
            default:
                s.append("KYYQ");
                break;
        }
        return s.toString();
    }

    /**
     * 发送订单取消
     *
     * @param tag  目标
     * @param keys 键
     * @param body 消息体
     */
    private void sendOrderCancel(String tag, String keys, MqDto body) {
        var s = orderSource.sendOrderCancel().send(MessageBuilder
                .withPayload(body)
                .setHeader(RocketMQHeaders.KEYS, keys)
                .setHeader(RocketMQHeaders.TAGS, tag)
                .build());
        log.warn("订单异常消息发送{} ", s ? "成功" : "失败");
    }

    /**
     * 确认订单
     * <p>更改订单状态</p>
     *
     * @param order 订单
     */
    private void updateOrderStatus(TradeOrder order) {
        order.setOrderStatus(ShopConstant.SHOP_ORDER_CONFIRM.getCode());
        order.setPayStatus(ShopConstant.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        order.setConfirmTime(new Date());
        var r = orderMapper.updateByPrimaryKey(order);
        if (r <= 0) {
            CastException.cast(ShopConstant.SHOP_ORDER_CONFIRM_FAIL);
        }
        log.info("OrderService--->订单:" + order.getOrderId() + "确认订单成功");
    }

    /**
     * 减少余额
     *
     * @param order 订单
     */
    private void reduceMoneyPaid(TradeOrder order) {
        if (Objects.nonNull(order.getMoneyPaid()) && order.getMoneyPaid().compareTo(BigDecimal.ZERO) > 0) {
            var userMoneyLog = new TradeUserMoneyLog();
            userMoneyLog.setOrderId(order.getOrderId());
            userMoneyLog.setUserId(order.getUserId());
            userMoneyLog.setUserMoney(order.getMoneyPaid());
            userMoneyLog.setMoneyLogType(ShopConstant.SHOP_USER_MONEY_PAID.getCode());
            var isSuccess = userService.updateMoneyPaid(userMoneyLog);
            if (isSuccess.equals(ShopConstant.SHOP_FAIL.getSuccess())) {
                CastException.cast(ShopConstant.SHOP_USER_MONEY_REDUCE_FAIL);
            }
            log.info("OrderService--->订单:" + order.getOrderId() + ",扣减余额成功");
        }
    }

    /**
     * 扣减优惠卷
     *
     * @param order 订单
     */
    private void updateCouponStatus(TradeOrder order) {
        if (Objects.nonNull(order.getOrderId())) {
            var coupon = couponService.findOne(order.getCouponId());
            coupon.setOrderId(order.getOrderId());
            coupon.setIsUsed(ShopConstant.SHOP_COUPON_ISUSED.getCode());
            coupon.setUsedTime(new Date());
            // 更改优惠卷状态
            var status = couponService.updateCouponStatus(coupon);
            if (status.equals(ShopConstant.SHOP_FAIL.getSuccess())) {
                CastException.cast(ShopConstant.SHOP_COUPON_USE_FAIL);
            }
            log.info("OrderService--->订单:" + order.getOrderId() + ",使用优惠券");
        }
    }

    /**
     * 扣减库存
     *
     * @param order 订单
     */
    private void reduceGoodsNum(TradeOrder order) {
        var goodsNumberLog = new TradeGoodsNumberLog();
        goodsNumberLog.setGoodsId(order.getGoodsId());
        goodsNumberLog.setGoodsNumber(order.getGoodsNumber());
        goodsNumberLog.setOrderId(order.getOrderId());
        var result = goodService.reduceGoodsNum(goodsNumberLog);
        if (result.equals(ShopConstant.SHOP_FAIL.getSuccess())) {
            CastException.cast(ShopConstant.SHOP_REDUCE_GOODS_NUM_FAIL);
        }
        log.info("OrderService--->订单:" + order.getOrderId() + "扣减库存成功");
    }

    /**
     * 校验订单
     *
     * @param order 订单
     */
    private void checkOrder(TradeOrder order) {
        // 1.订单是否存在
        if (Objects.isNull(order)) {
            CastException.cast(ShopConstant.SHOP_ORDER_INVALID);
        }
        // 2.校验订单中的商品是否存在
        var goods = goodService.findOne(order.getGoodsId());
        if (Objects.isNull(goods)) {
            CastException.cast(ShopConstant.SHOP_GOODS_NO_EXIST);
        }
        // 3.校验下单用户是否存在
        if (Objects.isNull(userService.findOne(order.getUserId()))) {
            CastException.cast(ShopConstant.SHOP_USER_NO_EXIST);
        }
        // 4.校验商品单价是否合法
        if (order.getGoodsPrice().compareTo(goods.getGoodsPrice()) != 0) {
            CastException.cast(ShopConstant.SHOP_GOODS_PRICE_INVALID);
        }
        // 5.校验订单商品数量是否合法
        if (order.getGoodsNumber() >= goods.getGoodsNumber()) {
            CastException.cast(ShopConstant.SHOP_GOODS_NUM_NOT_ENOUGH);
        }

        log.info("OrderService--->订单校验通过");
    }

    /**
     * 预订单生成
     *
     * @param order 订单
     * @return 订单id
     */
    private Long savePreOrder(TradeOrder order) {
        // 1. 设置订单状态不可见
        order.setOrderStatus(ShopConstant.SHOP_ORDER_NO_CONFIRM.getCode());
        // 2. 设置订单id
        var oid = idWorker.nextId();
        order.setOrderId(oid);
        // 3. 核算运费是否合法
        var shippingFee = calculateShippingFee(order.getOrderAmount());
        if (order.getShippingFee().compareTo(shippingFee) != 0) {
            CastException.cast(ShopConstant.SHOP_ORDERAMOUNT_INVALID);
        }
        // 4. 核心订单总金额是否合法
        var orderAmount = order.getGoodsPrice().multiply(new BigDecimal(order.getGoodsNumber()));
        order.setGoodsAmount(orderAmount);
        orderAmount = orderAmount.add(shippingFee);
        if (order.getOrderAmount().compareTo(orderAmount) != 0) {
            CastException.cast(ShopConstant.SHOP_ORDERAMOUNT_INVALID);
        }
        // 5. 判断用户是否使用余额
        var moneyPaid = order.getMoneyPaid();
        if (Objects.nonNull(moneyPaid)) {
            // 订余额是否合法
            var result = moneyPaid.compareTo(BigDecimal.ZERO);
            //余额小于0
            if (result < 0) {
                CastException.cast(ShopConstant.SHOP_MONEY_PAID_LESS_ZERO);
            }
            //余额大于0
            if (result > 0) {
                var user = userService.findOne(order.getUserId());
                // 支付金额是否大于用户余额
                if (moneyPaid.compareTo(new BigDecimal(user.getUserMoney())) > 0) {
                    CastException.cast(ShopConstant.SHOP_MONEY_PAID_INVALID);
                }
            }
        } else {
            order.setMoneyPaid(BigDecimal.ZERO);
        }
        // 判断用户是否使用优惠卷
        var couponId = order.getCouponId();
        if (Objects.nonNull(couponId)) {
            var coupon = couponService.findOne(couponId);
            // 查询是否有此优惠卷
            if (Objects.isNull(coupon)) {
                CastException.cast(ShopConstant.SHOP_COUPON_NO_EXIST);
            }
            //判断优惠卷是否已经被使用
            if (coupon.getIsUsed().intValue() == ShopConstant.SHOP_COUPON_ISUSED.getCode().intValue()) {
                CastException.cast(ShopConstant.SHOP_COUPON_ISUSED);
            }
            // 插入优惠金额
            order.setCouponPaid(coupon.getCouponPrice());
        } else {
            // 不优惠所以为0
            order.setCouponPaid(BigDecimal.ZERO);
        }
        // 7. 核算订单支付金额：订单总金额-余额-优惠券金额
        var payAmount = order.getOrderAmount().subtract(order.getMoneyPaid()).subtract(order.getCouponPaid());
        order.setPayAmount(payAmount);
        // 8. 设置下单时间
        order.setAddTime(new Date());
        //9. 保存订单到数据库
        orderMapper.insert(order);
        //10. 返回订单ID
        return oid;
    }

    /**
     * 计算运费是否合法
     *
     * @param orderAmount 订单价格
     * @return 0/10
     */

    private BigDecimal calculateShippingFee(BigDecimal orderAmount) {
        if (orderAmount.compareTo(new BigDecimal(100)) > 0) {
            return BigDecimal.ZERO;
        } else {
            return new BigDecimal(10);
        }
    }
}
