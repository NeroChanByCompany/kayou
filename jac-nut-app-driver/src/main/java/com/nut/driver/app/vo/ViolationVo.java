package com.nut.driver.app.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author liuBing
 * @Classname ViolationVo
 * @Description TODO
 * @Date 2021/8/21 11:38
 */
@Data
@Accessors(chain = true)
public class ViolationVo implements Serializable {

    private static final long serialVersionUID = -8291005438503140898L;
    /**
     * appkey
     */
    private String appKey;
    /**
     * 车辆类型 0燃油车 1新能源车
     */
    @ApiModelProperty(name = "type" , notes = "车辆类型" , dataType = "String")
    private String type;
    /**
     * 车牌号
     */
    @ApiModelProperty(name = "plateno" , notes = "车牌号" , dataType = "String")
    private String plateno;
    /**
     * 发动机号
     */
    @ApiModelProperty(name = "engineno" , notes = "发动机号" , dataType = "String")
    private String engineno;
    /**
     * 车架号
     */
    @ApiModelProperty(name = "frameno" , notes = "车架号" , dataType = "String")
    private String frameno;
}
