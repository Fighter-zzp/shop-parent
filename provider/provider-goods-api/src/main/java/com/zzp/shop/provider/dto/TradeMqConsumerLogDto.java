package com.zzp.shop.provider.dto;

import lombok.Data;

import java.io.Serializable;
/**
 * TradeMqConsumerLog的传输类
 * <p>
 *  用来简化数据传输的属性
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/3 23:42
 * @see  TradeMqConsumerLogDto
 **/
@Data
public class TradeMqConsumerLogDto implements Serializable {
    private static final long serialVersionUID = -2848217757352788758L;
    private String groupName;

    private String msgTag;

    private String msgKey;
}
