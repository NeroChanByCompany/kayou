package com.nut.driver.common.utils;

import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.enums.AreaCodeEnum;
import com.nut.driver.app.form.ModifyUserInfoForm;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @author liuBing
 * @Classname UserUtil
 * @Description TODO 涉及到用户的一些公共方法
 * @Date 2021/6/17 14:57
 */
public class UserUtil {
    
    private UserUtil(){}

    /**
     * 计算用户是否需要加积分
     * @param form
     * @param userEntity
     * @return
     */
    public static int setUser(ModifyUserInfoForm form, UserEntity userEntity) {
        int result = 0;
        if (org.apache.commons.lang3.StringUtils.isNotBlank(form.getName())) {
            if (userEntity.getName() == null) {
                result ++;
            }
            userEntity.setName(form.getName());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(form.getUserPicUrl())) {
            if (userEntity.getUserPicUrl() == null) {
                result ++;
            }
            userEntity.setUserPicUrl(form.getUserPicUrl());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(form.getSignature())) {
            if (userEntity.getSignature() == null) {
                result ++;
            }
            userEntity.setSignature(form.getSignature());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(form.getSex())) {
            if (userEntity.getSex() == null) {
                result ++;
            }
            userEntity.setSex(form.getSex());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(form.getInterest())) {
            if (userEntity.getInterest() == null) {
                result ++;
            }
            userEntity.setInterest(form.getInterest());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(form.getRealName())) {
            if (userEntity.getRealName() == null) {
                result ++;
            }
            userEntity.setRealName(form.getRealName());
        }
        if (null != form.getBirthday()) {
            if (userEntity.getBirthday() == null) {
                result ++;
            }
            userEntity.setBirthday(form.getBirthday());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(form.getProvinceDesc())) {
            if (userEntity.getProvinceDesc() == null) {
                result ++;
            }
            userEntity.setProvinceDesc(form.getProvinceDesc());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(form.getCityDesc())) {
            if (userEntity.getCityDesc() == null) {
                result ++;
            }
            userEntity.setCityDesc(form.getCityDesc());
        }
        if (null != form.getDrivingAge()) {
            if (userEntity.getDrivingAge() == null) {
                result ++;
            }
            userEntity.setDrivingAge(form.getDrivingAge());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(form.getEmail())) {
            if (userEntity.getEmail() == null) {
                result ++;
            }
            userEntity.setEmail(form.getEmail());
        }
        if (null != form.getAnnualIncome()) {
            if (userEntity.getAnnualIncome() == null) {
                result ++;
            }
            userEntity.setAnnualIncome(form.getAnnualIncome());
        }
        return result;
    }

    /**
     * 是否需要添加 isInfoOk
     * @param userEntity
     * @return
     */
    public static boolean isInfoOk(UserEntity userEntity) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getName())) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getUserPicUrl())) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getSignature())) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getSex())) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getInterest())) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getRealName())) {
            return false;
        }
        if (null == userEntity.getBirthday()) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getProvinceDesc())) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getCityDesc())) {
            return false;
        }
        if (null == userEntity.getDrivingAge()) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(userEntity.getEmail())) {
            return false;
        }
        if (null == userEntity.getAnnualIncome()) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
            String n = "130099";
            String id = n.substring(0,2);
            switch (AreaCodeEnum.getTypebyCode(id)){
                case BEIJING:
                case TIANJIN:
                case SHANGHAI:
                case CHONGQING:
                    System.out.println(id +"0000");
                    break;
                default:
                    break;
            }

        System.out.println(id);
    }

}
