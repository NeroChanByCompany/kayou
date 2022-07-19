package com.nut.driver.app.dao;

import com.nut.driver.app.pojo.SignPojo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @Description: 签到
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dao
 * @Author: yzl
 * @CreateTime: 2021-08-09 16:04
 * @Version: 1.0
 */

public interface SignDao {
    List<SignPojo> selectSignNumber(@Param("year") Integer year, @Param("month") Integer month, @Param("ucId") String ucId);

    Integer selectContinueTime(@Param("ucId") String ucId);

    String selectLastTime(@Param("ucId") String ucId);

    void insertToNumber(@Param("ucId") String ucId, @Param("continueTime") Integer continueTime,@Param("lastTime") Date Date);

    Integer selectIdInNumber(@Param("ucId") String ucId);

    void updateToNumber(@Param("ucId") String ucId,@Param("continueTime") Integer continueTime,@Param("lastTime") Date date);

    void inserToRecord(@Param("year") Integer year, @Param("month") Integer month, @Param("day") Integer day, @Param("ucId") String ucId,@Param("lastTime")Date date);

    Integer selectByDate(@Param("year") Integer year, @Param("month") Integer month, @Param("day") Integer day,@Param("ucId") String ucId);

    /**
     * 添加是否今天条数限制
     * @param ucId
     * @return
     */
    Integer selectToNow(String ucId);
}
