package com.nut.driver.app.dto;

import com.nut.common.utils.DateUtil;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.entity.CustomMaintainInfoEntity;
import com.nut.driver.app.form.AddMaintenanceInfoForm;
import com.nut.driver.app.form.EditMaintenanceInfoForm;
import com.nut.driver.app.pojo.QueryCustomMaintainItemCountPojo;
import org.apache.commons.beanutils.BeanUtils;


import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * @author liuBing
 * @Classname MaintainceCovertToDTO
 * @Description TODO
 * @Date 2021/6/24 14:43
 */

public class MaintainceCovertToDTO {

    /**
     * list转dto
     * @param customMaintainInfo
     * @return
     */
    public static QueryDriverMaintenanceDTO covToQueryDriverMaintenanceDTO(QueryCustomMaintainItemCountPojo customMaintainInfo){
        QueryDriverMaintenanceDTO dto = new QueryDriverMaintenanceDTO();
        dto.setMaintainId(customMaintainInfo.getId().toString());
        dto.setCarId(customMaintainInfo.getCarId());
        dto.setCarNumber(StringUtil.unifiedEmptyValue(customMaintainInfo.getCarNumber()));
        dto.setMaintainType(null == customMaintainInfo.getCustomMaintainType() ? null : String.valueOf(customMaintainInfo.getCustomMaintainType()));
        dto.setMaintainItemCount(null == customMaintainInfo.getMaintainCount() ? null : String.valueOf(customMaintainInfo.getMaintainCount()));
        if (2 == customMaintainInfo.getCustomMaintainType()){// 时间类型
            // 转换年月日时分
            dto.setMaintainDescribe(DateUtil.getDateFormat(DateUtil.time_pattern_min).
                    format(DateUtil.parseTime(customMaintainInfo.getCustomMaintainDescribe()))
            );
        }else{
            dto.setMaintainDescribe(customMaintainInfo.getCustomMaintainDescribe());
        }
        dto.setMaintainName(StringUtil.unifiedEmptyValue(customMaintainInfo.getCustomMaintainName()));
        dto.setMaintainStatus(null == customMaintainInfo.getMaintainStatus() ? null : String.valueOf(customMaintainInfo.getMaintainStatus()));
        //vin后8位
        String vin = customMaintainInfo.getVin() == null ? "" : customMaintainInfo.getVin();
        //vin展示底盘号后8位
        if (StringUtil.isNotEmpty(vin) && vin.length() > 8){
            dto.setVin(vin.substring(vin.length() - 8));
        }else {
            dto.setVin(StringUtil.unifiedEmptyValue(vin));
        }
        return dto;
    }


    public static MaintenanceInfoDTO maintainInfoToMaintenanceInfoDto(CustomMaintainInfoEntity customMaintainInfo){
        MaintenanceInfoDTO maintenanceInfoDto = new MaintenanceInfoDTO();
        maintenanceInfoDto.setCarId(customMaintainInfo.getCarId());
        maintenanceInfoDto.setCarNumber(customMaintainInfo.getCarNumber());
        maintenanceInfoDto.setMaintainId(customMaintainInfo.getId().toString());
        maintenanceInfoDto.setMaintainName(customMaintainInfo.getCustomMaintainName());
        maintenanceInfoDto.setMaintainType(customMaintainInfo.getCustomMaintainType().toString());
        if (2 == customMaintainInfo.getCustomMaintainType()){
            maintenanceInfoDto.setMaintainDescribe(DateUtil.getDateFormat(DateUtil.time_pattern_min).
                    format(DateUtil.parseTime(customMaintainInfo.getCustomMaintainDescribe()))
            );
        }else{
            maintenanceInfoDto.setMaintainDescribe(customMaintainInfo.getCustomMaintainDescribe());
        }
        maintenanceInfoDto.setRemarks(customMaintainInfo.getRemarks());
        maintenanceInfoDto.setMaintainStatus(customMaintainInfo.getMaintainStatus().toString());
        String vin = customMaintainInfo.getVin() == null ? "" : customMaintainInfo.getVin();
        //vin展示底盘号后8位
        if (StringUtil.isNotEmpty(vin) && vin.length() > 8){
            maintenanceInfoDto.setVin(vin.substring(vin.length() - 8));
        }else {
            maintenanceInfoDto.setVin(vin);
        }
        return maintenanceInfoDto;
    }

    /**
     * 添加保养command转换为实体bean
     * @param form
     * @return
     */
    public static CustomMaintainInfoEntity AddMaintenanceInfoCommandToCustomMaintainInfo(AddMaintenanceInfoForm form) throws InvocationTargetException, IllegalAccessException {
        CustomMaintainInfoEntity customMaintainInfo = editMaintenanceInfoCommandToCustomMaintainInfoWithoutId(beanCopy(form));
        customMaintainInfo.setCustomMaintainName(form.getMaintainName());
        customMaintainInfo.setCreateTime(new Date());
        customMaintainInfo.setUpdateTime(new Date());
        return customMaintainInfo;
    }

    public static CustomMaintainInfoEntity editMaintenanceInfoCommandToCustomMaintainInfoWithoutId(EditMaintenanceInfoForm form) throws InvocationTargetException, IllegalAccessException {
        CustomMaintainInfoEntity customMaintainInfo = new CustomMaintainInfoEntity();
        customMaintainInfo.setUserId(form.getUserId());
        customMaintainInfo.setCarId(form.getCarId());
        customMaintainInfo.setCarNumber(form.getCarNumber());
        customMaintainInfo.setCustomMaintainDescribe(form.getMaintainDescribe());
        customMaintainInfo.setCustomMaintainType(Integer.parseInt(form.getMaintainType()));
        customMaintainInfo.setRemarks(form.getRemarks());
        customMaintainInfo.setCustomMaintainName(form.getMaintainName());
        return customMaintainInfo;
    }

    /**
     * bean copy
     * @param form
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static EditMaintenanceInfoForm beanCopy(AddMaintenanceInfoForm form) throws InvocationTargetException, IllegalAccessException {
        EditMaintenanceInfoForm editMaintenanceInfoCommand = new EditMaintenanceInfoForm();
        BeanUtils.copyProperties(editMaintenanceInfoCommand,form);
        return editMaintenanceInfoCommand;
    }

    /**
     * 修改保养信息command转化实体bean
     * @param form
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static CustomMaintainInfoEntity editMaintenanceInfoCommandToCustomMaintainInfo(EditMaintenanceInfoForm form) throws InvocationTargetException, IllegalAccessException {
        CustomMaintainInfoEntity customMaintainInfo = editMaintenanceInfoCommandToCustomMaintainInfoWithoutId(form);
        customMaintainInfo.setId(Long.parseLong(form.getMaintainId()));
        customMaintainInfo.setUpdateTime(new Date());
        return customMaintainInfo;
    }

}
