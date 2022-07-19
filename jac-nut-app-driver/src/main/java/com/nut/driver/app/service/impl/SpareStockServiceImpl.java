package com.nut.driver.app.service.impl;

import com.github.pagehelper.Page;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.dao.SpareStockDao;
import com.nut.driver.app.dto.SpareStockListDto;
import com.nut.driver.app.form.QuerySpareStockForm;
import com.nut.driver.app.pojo.SpareStockListPojo;
import com.nut.driver.common.utils.PageUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.nut.driver.app.service.SpareStockService;

import java.util.ArrayList;
import java.util.List;

@Service("hySpareStockService")
@Slf4j
public class SpareStockServiceImpl extends DriverBaseService implements SpareStockService {

    @Autowired
    private SpareStockDao spareStockDao;

    @Value("${database_name}")
    private String DbName;

    @Autowired
    PageUtil pageUtil;

    @SneakyThrows
    public HttpCommandResultWithData getSpareStockList(QuerySpareStockForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();

        Double num = 0d;
        Double lon = StringUtil.isEmpty(form.getLon()) ? num : Double.valueOf(form.getLon());
        Double lat = StringUtil.isEmpty(form.getLat()) ? num : Double.valueOf(form.getLat());
        // 无效的经纬度去查位置云
        if (lon <= num || lat <= num) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("无法获取当前所在位置！");
            return result;
        }
        if (StringUtil.isEmpty(form.getSearchInfo())) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            String resultMessage = "请输入备件代码！";
            if (form.getSearchType() == 1) {
                resultMessage = "请输入备件名称！";
            }
            result.setMessage(resultMessage);
            return result;
        }

        pageUtil.getPage(form);
        Page<SpareStockListPojo> page = spareStockDao.getSpareStockList(DbName,form);

        PagingInfo<SpareStockListDto> resultDto = new PagingInfo<>();
        List<SpareStockListDto> dtoList = new ArrayList<>();
        if (page != null && page.getResult() != null) {
            List<SpareStockListPojo> pojoList = page.getResult();
            for (SpareStockListPojo pojo : pojoList) {
                dtoList.add(pojoToDto(pojo));
            }
            resultDto.setPage_total(page.getPages());
            resultDto.setTotal(page.getTotal());
            resultDto.setList(dtoList);
        }
        result.setData(resultDto);
        result.setResultCode(ECode.SUCCESS.code());
        return result;
    }



    /**
     * pojo转dto
     * @param pojo
     * @return StationListDto 出参对象dto
     */
    private static SpareStockListDto pojoToDto(SpareStockListPojo pojo) {
        SpareStockListDto dto = new SpareStockListDto();
        dto.setId(pojo.getId());
        dto.setStock(pojo.getStock());
        dto.setDistance(pojo.getDistance());
        dto.setStockType(pojo.getStockType());
        dto.setStockName(pojo.getStockName());
        dto.setStockAddress(pojo.getStockAddress());
        dto.setLinkPerson(pojo.getLinkPerson());
        dto.setLinkPhone(pojo.getLinkPhone());
        dto.setSparePartCode(pojo.getSparePartCode());
        dto.setSparePartName(pojo.getSparePartName());
        dto.setStock(pojo.getStock());
        dto.setSupplierName(pojo.getSupplierName());

        if (pojo.getLongitude() != null && pojo.getLatitude() != null) {
            dto.setLongitude(pojo.getLongitude());
            dto.setLatitude(pojo.getLatitude());
        }
        return dto;
    }
}
