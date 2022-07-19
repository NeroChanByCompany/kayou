package com.nut.driver.app.service;

import com.nut.driver.app.entity.CarExtEntity;
import com.nut.driver.app.form.UpdateCarExtForm;
import org.springframework.transaction.annotation.Transactional;

public interface CarExtService {

    CarExtEntity findById(Long carId);

    CarExtEntity findByClassisNum(String chassisNum);

    void save(UpdateCarExtForm updateCarExtCommand);

}
