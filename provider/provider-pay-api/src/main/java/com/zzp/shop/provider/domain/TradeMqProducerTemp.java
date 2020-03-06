package com.zzp.shop.provider.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 消息生成实体类
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/5 12:07
 * @see  TradeMqProducerTemp
 **/
@Data
@Table(name = "trade_mq_producer_temp")
public class TradeMqProducerTemp implements Serializable {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "msg_topic")
    private String msgTopic;

    @Column(name = "msg_tag")
    private String msgTag;

    @Column(name = "msg_key")
    private String msgKey;

    @Column(name = "msg_body")
    private String msgBody;

    /**
     * 0:未处理;1:已经处理
     */
    @Column(name = "msg_status")
    private Integer msgStatus;

    @Column(name = "create_time")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
