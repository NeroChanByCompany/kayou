package com.nut.locationservice.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author liuBing
 * @Classname CarHistoryResultDTO
 * @Description TODO 车辆历史信息实际返回DTO
 * @Date 2021/6/16 10:49
 */
@Data
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CarHistoryResultDTO implements Serializable {
    private static final long serialVersionUID = 6944600819655402889L;
    /**
     * 返回参数主键
     */
    private String key;
    /**
     * 具体返回参数
     */
    private CarHistoryLocationOutputDTO carHistoryLocationOutputDTO;
}
