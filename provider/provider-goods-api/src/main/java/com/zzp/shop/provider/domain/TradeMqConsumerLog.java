package com.zzp.shop.provider.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "trade_mq_consumer_log")
public class TradeMqConsumerLog implements Serializable {
    @Column(name = "group_name")
    private String groupName;

    @Column(name = "msg_tag")
    private String msgTag;

    @Column(name = "msg_key")
    private String msgKey;

    @Column(name = "msg_id")
    private String msgId;

    @Column(name = "msg_body")
    private String msgBody;

    /**
     * 0:正在处理;1:处理成功;2:处理失败
     */
    @Column(name = "consumer_status")
    private Integer consumerStatus;

    @Column(name = "consumer_times")
    private Integer consumerTimes;

    @Column(name = "consumer_timestamp")
    private Date consumerTimestamp;

    @Column(name = "remark")
    private String remark;

    private static final long serialVersionUID = 1L;
}
