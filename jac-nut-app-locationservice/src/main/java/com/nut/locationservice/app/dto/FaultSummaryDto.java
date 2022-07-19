package com.nut.locationservice.app.dto;

import com.nut.locationservice.app.pojo.QueryFaultSummaryPojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.dto
 * @Author: yzl
 * @CreateTime: 2021-06-17 10:25
 * @Version: 1.0
 */

public class FaultSummaryDto {

    public static List<QueryFaultSummaryDto> convert(List<QueryFaultSummaryPojo> list) {
        List<QueryFaultSummaryDto> dtos = new ArrayList<QueryFaultSummaryDto>();
        for (QueryFaultSummaryPojo pojo : list) {
            QueryFaultSummaryDto dto = new QueryFaultSummaryDto();
            dto.setId(pojo.getId().longValue());
            dto.setChassisNum(pojo.getChassisNum());
            dto.setPlateNum(pojo.getPlateNum());
            dto.setBdTerCode(pojo.getBdTerCode());
            dto.setFkTerCode(pojo.getFkTerCode());
            dto.setTName(pojo.getTName());
            dto.setBusinessName(pojo.getBusinessName());
            dto.setCarModel(pojo.getCarModel());
            dto.setEngineNumber(pojo.getEngineNumber());
            dto.setEngineType(pojo.getEngineType());
            dto.setStructureNum(pojo.getStructureNum());
            dto.setBreakdownDis(pojo.getBreakdownDis());
            dto.setSpn(pojo.getSpn());
            dto.setFmi(pojo.getFmi());
            dto.setSystemType(pojo.getSystemType());
            dto.setSystemSource(pojo.getSystemSource());
            dto.setTypeModel(pojo.getTypeModel());
            dto.setOccurDate(pojo.getOccurDate());
            dto.setDuration(pojo.getDuration());
            dto.setBLoction(pojo.getBLoction());
            dto.setELoction(pojo.getELoction());
            dto.setAakstatusValue(pojo.getAakstatusValue());
            dto.setFaultSolutions(pojo.getFaultSolutions());
            dto.setLinkTelpone(pojo.getLinkTelpone());
            dto.setSymbolCode(pojo.getSymbolCode());
            dtos.add(dto);
        }
        Collections.sort(dtos, new Comparator<QueryFaultSummaryDto>() {
            @Override
            public int compare(QueryFaultSummaryDto o1, QueryFaultSummaryDto o2) {
                return o2.getChassisNum().compareToIgnoreCase(o1.getChassisNum());
            }
        });
        return dtos;
    }

}
