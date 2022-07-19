package com.nut.tripanalysis.app.service.impl;

import com.nut.common.utils.StringUtil;
import com.nut.tripanalysis.app.dto.ModelInfoDto;
import com.nut.tripanalysis.app.form.QueryModelInfoForm;
import com.nut.tripanalysis.app.service.CommonService;
import com.nut.tripanalysis.app.service.ModelService;
import com.nut.truckingteam.app.dto.CarModelAndSeriseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/10
 */
@Slf4j
@Service("ModelService")
public class ModelServiceImpl implements ModelService {


    @Autowired
    private CommonService commonService;

    @Override
    public List<ModelInfoDto> queryModelInfo(QueryModelInfoForm command) {
        List<ModelInfoDto> returnList = new ArrayList<>();
        //调用接口获取用户所有车辆车型信息
        List<CarModelAndSeriseDto> carmodels = commonService.getModelInfoDto(command);
        String unknown = "未知";
        if (carmodels != null && carmodels.size() > 0) {
            for (CarModelAndSeriseDto modelAndSeriseDto : carmodels) {
                if (modelAndSeriseDto != null) {
                    ModelInfoDto modelInfoDto = new ModelInfoDto();
                    modelInfoDto.setModelId(modelAndSeriseDto.getModelId());
                    String sName = modelAndSeriseDto.getSeriseName() != null ? modelAndSeriseDto.getSeriseName() : "";
                    String dName = modelAndSeriseDto.getDriverTypeName() != null ? modelAndSeriseDto.getDriverTypeName() : "";
                    String cName = modelAndSeriseDto.getCarTypeName() != null ? modelAndSeriseDto.getCarTypeName() : "";
                    String eName = modelAndSeriseDto.getEmission() != null ? modelAndSeriseDto.getEmission() : "";
                    if (StringUtil.isEmpty(sName) && StringUtil.isEmpty(dName)
                            && StringUtil.isEmpty(cName) && StringUtil.isEmpty(eName)) {
                        // 整车平台、驱动形式、车辆类别和排放全部为空时跳过
                        continue;
                    }
                    if (StringUtil.isNotEmpty(sName) && unknown.equals(sName)
                            && StringUtil.isNotEmpty(dName) && unknown.equals(dName)
                            && StringUtil.isNotEmpty(cName) && unknown.equals(cName)
                            && StringUtil.isNotEmpty(eName) && unknown.equals(eName)) {
                        modelInfoDto.setModelName("未知车型");
                    } else {
                        String modelName = sName + " " + dName + " " + cName + " " + eName;
                        if (StringUtil.isNotEmpty(modelName.trim())) {
                            modelInfoDto.setModelName(modelName.trim());
                        } else {
                            // 如果整车平台、车辆类别、驱动形式和排放拼接出来的字符串是空值则跳过
                            continue;
                        }
                    }
                    returnList.add(modelInfoDto);
                }
            }
        }

        Map<String, ModelInfoDto> map = new HashMap<>();
        for (ModelInfoDto mo : returnList) {
            ModelInfoDto dto = map.get(mo.getModelName());
            if (dto != null) {
                dto.setModelId(dto.getModelId() + "," + mo.getModelId());
                dto.setCarNum(dto.getCarNum() + 1);
                map.put(dto.getModelName(), dto);
            } else {
                mo.setCarNum(1);
                map.put(mo.getModelName(), mo);
            }
        }

        returnList.clear();
        for (String in : map.keySet()) {
            ModelInfoDto dto = map.get(in);
            returnList.add(dto);
        }
        return returnList;
    }
}
