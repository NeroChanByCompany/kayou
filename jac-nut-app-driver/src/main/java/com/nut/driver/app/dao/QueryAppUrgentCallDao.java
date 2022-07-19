package com.nut.driver.app.dao;

import com.github.pagehelper.Page;
import com.nut.driver.app.dto.QueryAppUrgentCallDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface QueryAppUrgentCallDao {

    Page<QueryAppUrgentCallDto> selectByTypeAndName(Map<String, Object> map);
    void insert(@Param("scmId") String scmId, @Param("phone") String phone, @Param("userId") Long userId);
}
