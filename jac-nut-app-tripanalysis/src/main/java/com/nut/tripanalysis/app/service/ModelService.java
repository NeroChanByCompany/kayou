package com.nut.tripanalysis.app.service;

import com.nut.tripanalysis.app.dto.ModelInfoDto;
import com.nut.tripanalysis.app.form.QueryModelInfoForm;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/10
 */
public interface ModelService {

    /**
     * 查询当前车队下的全部车型信息
     */
    List<ModelInfoDto> queryModelInfo(QueryModelInfoForm command);
}
