package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.dto.TempActionPictureSetDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/7
 */
public interface TempSetPictureInfoDao {

    List<TempActionPictureSetDto> tempGetSetPictureInfo(@Param("actionCode") String actionCode, @Param("type") String type);
}
