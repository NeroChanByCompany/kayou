package com.nut.jac.kafka.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuBing
 * @Classname WoStatusDTO
 * @Description TODO
 * @Date 2021/8/31 13:52
 */
@Data
@Accessors(chain = true)
public class WoStatusDTO {
    private String woCode;
    private Integer woStatus;
    private Integer maximumTime;
}
