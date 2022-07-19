package com.nut.driver.app.dao;


import com.nut.driver.app.entity.CarExtEntity;
import com.nut.driver.app.form.UpdateCarExtForm;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CarExtDao {

    CarExtEntity findByCarId(Long carId);
    CarExtEntity findByClassesNum(String classesNum);
    int update(UpdateCarExtForm updateCarExtCommand);
    int insert(UpdateCarExtForm updateCarExtCommand);
    int countByCarId(Long carId);
}
