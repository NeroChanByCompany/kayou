package com.nut.jac.kafka.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author liuBing
 * @Classname TrackPoint
 * @Description TODO
 * @Date 2021/8/31 14:17
 */
@Data
@Accessors(chain = true)
public class TrackPoint {
    public String woCode;
    public List<Point> points;
    public String userId;
}
