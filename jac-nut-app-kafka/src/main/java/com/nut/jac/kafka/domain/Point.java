package com.nut.jac.kafka.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Point {
    public String index;
    public Long latitude;
    public Long longitude;
    public String time;
    public Double radius;

}