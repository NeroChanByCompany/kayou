package com.nut.locationservice.app.dao;

import com.nut.locationservice.app.pojo.CarInfoPojo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.dao
 * @Author: yzl
 * @CreateTime: 2021-06-17 13:27
 * @Version: 1.0
 */
@Repository
public interface CarDao {

    List<CarInfoPojo> queryCarInfoList(@Param("vinList") List<String> vinList);

}
