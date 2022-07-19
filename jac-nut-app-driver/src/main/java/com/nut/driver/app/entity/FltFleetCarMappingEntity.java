package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author liuBing
 * @Classname FltFleetCarMappingEntity
 * @Description TODO
 * @Date 2021/6/24 19:09
 */
@Data
public class FltFleetCarMappingEntity{
    private Long id;

    private Long teamId;

    private String carId;

    private Integer createType;

    private Long createUserId;

    private String tbossUserId;

    private Date createTime;

    private Date updateTime;
}
