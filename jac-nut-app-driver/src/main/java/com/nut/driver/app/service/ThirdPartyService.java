package com.nut.driver.app.service;

import com.nut.driver.app.dto.ViolationDto;
import com.nut.driver.app.form.ViolationForm;

import java.util.List;

/**
 * @author liuBing
 * @Classname ThirdPartyService
 * @Description TODO
 * @Date 2021/8/20 13:20
 */
public interface ThirdPartyService {
    /**
     *  调用第三方接口获取违章信息
     * @param form
     * @return
     */
    List<ViolationDto> queryViolation(ViolationForm form);
}
