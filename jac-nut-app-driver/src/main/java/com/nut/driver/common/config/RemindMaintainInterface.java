package com.nut.driver.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "remind.maintain")
public class RemindMaintainInterface {

    private Integer preRemindDay;

    private Double preRemindMileage;

    public Integer getPreRemindDay() {
        return preRemindDay;
    }

    public void setPreRemindDay(Integer preRemindDay) {
        this.preRemindDay = preRemindDay;
    }

    public Double getPreRemindMileage() {
        return preRemindMileage;
    }

    public void setPreRemindMileage(Double preRemindMileage) {
        this.preRemindMileage = preRemindMileage;
    }
}
