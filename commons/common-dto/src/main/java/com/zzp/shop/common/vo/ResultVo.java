package com.zzp.shop.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 佐斯特勒
 * <p>
 * 返回值类
 * </p>
 * @version v1.0.0
 * @see ResultVo
 **/
@Data
@ApiModel(description = "统一结果返回值")
public class ResultVo implements Serializable {
    private static final long serialVersionUID = 4394791551377994276L;

    @ApiModelProperty(value = "响应编码",notes = "0表示成功，1表示失败")
    private int code;

    @ApiModelProperty("响应信息")
    private String msg;

    @ApiModelProperty("响应体")
    private Object data;

    public ResultVo(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResultVo ok() {
        return new ResultVo(0, "OK", null);
    }

    public static ResultVo fail() {
        return new ResultVo(1, "ERROR", null);
    }

    public static ResultVo ok(Object obj) {
        return new ResultVo(0, "OK", obj);
    }

    public static ResultVo fail(String msg) {
        return new ResultVo(1, msg, null);
    }
}

