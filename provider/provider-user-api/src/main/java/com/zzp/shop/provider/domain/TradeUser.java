package com.zzp.shop.provider.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 见名知义
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/3 14:45
 * @see  TradeUser
 **/
@Data
@Table(name = "trade_user")
public class TradeUser implements Serializable {
    /**
     * 用户ID
     */
    @Id
    @Column(name = "user_id")
    @GeneratedValue(generator = "JDBC")
    private Long userId;

    /**
     * 用户姓名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 用户密码
     */
    @Column(name = "user_password")
    private String userPassword;

    /**
     * 手机号
     */
    @Column(name = "user_mobile")
    private String userMobile;

    /**
     * 积分
     */
    @Column(name = "user_score")
    private Integer userScore;

    /**
     * 注册时间
     */
    @Column(name = "user_reg_time")
    private Date userRegTime;

    /**
     * 用户余额
     */
    @Column(name = "user_money")
    private Long userMoney;

    private static final long serialVersionUID = 1L;
}
