package com.nut.jac.kafka.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author liuBing
 * @Classname UploadRescueRoutePointListVo
 * @Description TODO
 * @Date 2021/8/31 13:24
 */
@Data
@Accessors(chain = true)
public class UploadRescueRoutePointListVO {

    private String userId;

    /**
     * 轨迹点列表
     */
    private List<UploadRescueRoutePointVO> pointList;

}
