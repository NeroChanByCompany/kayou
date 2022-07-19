package com.nut.driver.app.service.impl;

import com.github.pagehelper.Page;
import com.nut.driver.app.dto.ServiceStationDetailDto;
import com.nut.driver.app.enums.AreaCodeEnum;
import com.nut.driver.app.form.QueryServiceStationDetailForm;
import com.nut.driver.app.form.QueryStationForm;
import com.nut.driver.app.pojo.ServiceStationDetailPojo;
import com.nut.driver.app.pojo.StationListPojo;
import com.nut.driver.app.service.StationService;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.LonLatUtil;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.dao.ServiceStationDao;
import com.nut.driver.app.dto.StationListDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-23 10:47
 * @Version: 1.0
 */
@Slf4j
@Service
public class StationServiceImpl extends DriverBaseService implements StationService {

    @Autowired
    private ServiceStationDao serviceStationDao;

    @Value("${database_name}")
    private String DbName;

    @SneakyThrows
    public PagingInfo<StationListDto> getStationList(QueryStationForm form) {

        this.replaceCode(form);
        Double num = 0d;
        Double lon = StringUtil.isEmpty(form.getLon()) ? num : Double.valueOf(form.getLon());
        Double lat = StringUtil.isEmpty(form.getLat()) ? num : Double.valueOf(form.getLat());
        // 无效的经纬度去查位置云
        if (lon <= num || lat <= num) {
            if (StringUtil.isNotEmpty(form.getDistance())) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "无法获取位置，请使用省市区域筛选！");
            }
        }
        getPage(form);
        Page<StationListPojo> page = serviceStationDao.getStationList(DbName, form);
        PagingInfo<StationListDto> resultDto = new PagingInfo<>();
        List<StationListDto> dtoList = new ArrayList<>();
        if (page != null && page.getResult() != null) {
            List<StationListPojo> pojoList = page.getResult();
            log.info("getStationList:{}", JsonUtil.toJson(pojoList));
            for (StationListPojo pojo : pojoList) {
                dtoList.add(pojoToDto(pojo));
            }
            resultDto.setPage_total(page.getPages());
            resultDto.setTotal(page.getTotal());
            resultDto.setList(dtoList);
        }
        log.info("appStations end return:{}",resultDto);
        return resultDto;
    }

    private void replaceCode(QueryStationForm form) {
        if (StringUtils.isNotBlank(form.getId())){
            String id = form.getId().substring(0,2);
            switch (AreaCodeEnum.getTypebyCode(id)){
                case BEIJING:
                case TIANJIN:
                case SHANGHAI:
                case CHONGQING:
                    form.setId(id+"0000");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * pojo转dto
     *
     * @param pojo
     * @return StationListDto 出参对象dto
     */
    private static StationListDto pojoToDto(StationListPojo pojo) {
        StationListDto dto = new StationListDto();
        dto.setId(pojo.getId());
        dto.setName(pojo.getName());
        dto.setLevel(pojo.getLevel());
        dto.setDistance(pojo.getDistance());
        dto.setAddress(pojo.getAddress());
        dto.setPhoto(pojo.getPhoto());
        dto.setAuthority(pojo.getScopeService());
        dto.setAptitude(pojo.getAptitude());
        dto.setExclusive(pojo.getExclusive());
        dto.setTelephone(pojo.getTelephone());
        if (pojo.getLongitude() != null && pojo.getLatitude() != null) {
            dto.setLongitude(LonLatUtil.convertLonLat(pojo.getLongitude()));
            dto.setLatitude(LonLatUtil.convertLonLat(pojo.getLatitude()));
        }
        return dto;
    }

    public ServiceStationDetailDto appStationDetail(QueryServiceStationDetailForm form) {
        ServiceStationDetailPojo pojo = serviceStationDao.getServiceStationDetail(DbName, form.getStationId());
        ServiceStationDetailDto dto = new ServiceStationDetailDto();
        if (pojo != null) {
            dto.setName(pojo.getName());
            dto.setAddress(pojo.getAddress());
            dto.setPhone(pojo.getPhone());
            dto.setLevel(pojo.getLevel());
            dto.setIntroduce(pojo.getIntroduce());
            dto.setPhoto(pojo.getPhoto());
            dto.setAuthority(pojo.getScopeService());
            dto.setAptitude(pojo.getAptitude());
            dto.setExclusive(pojo.getExclusive());
            if (pojo.getLon() != null && pojo.getLat() != null) {
                dto.setLon(LonLatUtil.convertLonLat(pojo.getLon()));
                dto.setLat(LonLatUtil.convertLonLat(pojo.getLat()));
            }
        }
        log.info("appStationDetail end return:{}",dto);
        return dto;
    }

}
