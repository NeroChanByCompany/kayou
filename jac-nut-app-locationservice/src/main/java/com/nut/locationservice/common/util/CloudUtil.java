package com.nut.locationservice.common.util;


import com.nut.location.protocol.common.LCLocationData;
import com.nut.location.protocol.common.LCStatusType;
import com.nut.location.protocol.common.LCVehicleBreakdown;
import com.nut.location.protocol.common.LCVehicleStatusData;
import com.nut.locationservice.app.dto.BaseData;
import com.nut.locationservice.common.constants.CloudConstants;
import com.nut.locationservice.common.constants.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 位置云返回数据，属性转换Util
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.util
 * @Author: yzl
 * @CreateTime: 2021-06-16 19:36
 * @Version: 1.0
 */

public class CloudUtil {

    /**
     *  方向
     */
    public static String getDirection(int direction){
        String st="";
        if (direction == 0) {
            st=CloudConstants.Direction.NORTH.getValue();
        } else if (0 < direction&& direction< 90) {
            st=CloudConstants.Direction.NORTHEAST.getValue();
        } else if (direction == 90) {
            st=CloudConstants.Direction.EAST.getValue();
        } else if (90 < direction && direction < 180) {
            st=CloudConstants.Direction.SOUTHEAST.getValue();
        } else if (direction == 180) {
            st=CloudConstants.Direction.SOUTH.getValue();
        } else if (180 < direction && direction < 270) {
            st=CloudConstants.Direction.SOUTHWEST.getValue();
        } else if (direction == 270) {
            st= CloudConstants.Direction.WEST.getValue();
        } else if (270 < direction && direction < 359) {
            st=CloudConstants.Direction.NORTHWEST.getValue();
        } else {
            st=String.valueOf(direction);
        }
        return st;
    }
    /**
     * 车门状态
     *
     */
    public static String getDoorStatus(Long status) {
        String st = "";
        st += (status & Math.round(Math.pow(2, 13))) > 0 ? Constants.QIANMEN_OPEN
                : "";
        st += (status & Math.round(Math.pow(2, 14))) > 0 ? Constants.ZHONGMEN_OPEN
                : "";
        st += (status & Math.round(Math.pow(2, 15))) > 0 ? Constants.HOUMEN_OPEN
                : "";
        st += (status & Math.round(Math.pow(2, 16))) > 0 ? Constants.DRIVER_OPEN
                : "";
        return st;
    }
    /**
     * ACC状态
     *
     */
    public static String getAccStatus(Long status) {
        String st = "";
        st += (status & 1) > 0 ? CloudConstants.ACC_OPEN : CloudConstants.ACC_CLOSE;
        return st;
    }
    /**
     * 定位状态
     */
    public static String getGpsStatus(Long status) {
        String st = (status & Math.round(Math.pow(2, 1))) > 0 ? CloudConstants.GPS_TRUE : CloudConstants.GPS_FALSE;
        return st;
    }
    /**
     * 报警结果
     */
    public static String getAlarm(Long status) {
        String st = "";
        st += (status & 1) > 0 ? CloudConstants.alarm.emergencyAlarm.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 1))) > 0 ? CloudConstants.alarm.speedingAlarm.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 2))) > 0 ? CloudConstants.alarm.tiredAlarm.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 3))) > 0 ? CloudConstants.alarm.comingAlarm.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 4))) > 0 ? CloudConstants.alarm.faultGNSS.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 5))) > 0 ? CloudConstants.alarm.noConnectGNSS.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 6))) > 0 ? CloudConstants.alarm.shortCircuitGNSS.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 7))) > 0 ? CloudConstants.alarm.underPower.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 8))) > 0 ? CloudConstants.alarm.lossPower.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 9))) > 0 ? CloudConstants.alarm.faultLCD.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 10))) > 0 ? CloudConstants.alarm.faultTTS.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 11))) > 0 ? CloudConstants.alarm.faultCamera.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 12))) > 0 ? CloudConstants.alarm.icCardFailure.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 13))) > 0 ? CloudConstants.alarm.speedingWarningAlarm.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 14))) > 0 ? CloudConstants.alarm.tiredWarningAlarm.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 18))) > 0 ? CloudConstants.alarm.drivingTimeout.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 19))) > 0 ? CloudConstants.alarm.parkingTimeout.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 20))) > 0 ? CloudConstants.alarm.inOutArea.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 21))) > 0 ? CloudConstants.alarm.inOutRoute.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 22))) > 0 ? CloudConstants.alarm.routeLackOrOverTime.getValue()+",":"";
        st += (status & Math.round(Math.pow(2, 23))) > 0 ? CloudConstants.alarm.routeDeviates.getValue()+",":"";
        if(!"".equals(st)){
            return st.substring(0,st.length()-1);
        }
        return st;
    }
    /**
     * 获取故障码信息
     *
     */
    public static String getFault(List<LCVehicleBreakdown.VehicleBreakdown> fault, List<BaseData> faulist, String enginetype) {
        if(enginetype==null||fault == null || fault.size() == 0){
            return CloudConstants.NOTHING;
        }
        String st1 = "";
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < fault.size(); i++) {
            LCVehicleBreakdown.VehicleBreakdown t = fault.get(i);
            String st = t.getBreakdownSPNValue() + "_" + t.getBreakdownFMIValue();
            list.add(st);
        }
        for (String entry : list) {
            String str = "";
            for (BaseData en : faulist) {
                if (entry.equals(en.getCode())) {
                    str += en.getValue() + ";";
                }
            }
            st1 += str.equals("")?CloudConstants.UNKNOW:str;//防止未知故障码产生
        }
        if (null == st1 || st1.equals("")) {
            st1 = CloudConstants.NOTHING;;
        }
        return st1;
    }

    public static String getFault2(LCVehicleBreakdown.VehicleBreakdown breakDown, List<BaseData> faulist, String enginetype) {
        String result = "";
        if(enginetype == null){
            return CloudConstants.NOTHING;
        }
        String spn_fmi = breakDown.getBreakdownSPNValue() + "_" + breakDown.getBreakdownFMIValue();
        for (BaseData en : faulist) {
            if (spn_fmi.equals(en.getCode())) {
                result =  en.getValue();
            }
        }
        return result;
    }

    //获取防拆盒中6，7，8位报警信息
    public static String getAdditinAlarm(byte alarm6,byte alarm7,byte alarm8){
        String st = "";
        st += (alarm6 & 1) > 0 ? CloudConstants.additionalarm6.slideNeutral.getValue()+",":"";
        st += (alarm6 & Math.round(Math.pow(2, 1))) > 0 ? CloudConstants.additionalarm6.rapidAcceleration.getValue()+",":"";
        st += (alarm6 & Math.round(Math.pow(2, 2))) > 0 ? CloudConstants.additionalarm6.rapidDeceleration.getValue()+",":"";
        st += (alarm6 & Math.round(Math.pow(2, 3))) > 0 ? CloudConstants.additionalarm6.sharpTurning.getValue()+",":"";
        st += (alarm6 & Math.round(Math.pow(2, 4))) > 0 ? CloudConstants.additionalarm6.lowOilState.getValue()+",":"";
        st += (alarm6 & Math.round(Math.pow(2, 6))) > 0 ? CloudConstants.additionalarm6.slamTheAccelerator.getValue()+",":"";
        st += (alarm6 & Math.round(Math.pow(2, 7))) > 0 ? CloudConstants.additionalarm6.brakePressureLow.getValue()+",":"";
        st += (alarm7 & 1) > 0 ? CloudConstants.additionalarm7.longTimeBreaking.getValue()+",":"";
        st += (alarm7 & Math.round(Math.pow(2, 1))) > 0 ? CloudConstants.additionalarm7.longTimeClutch.getValue()+",":"";
        st += (alarm7 & Math.round(Math.pow(2, 2))) > 0 ? CloudConstants.additionalarm7.fastlyFlameout.getValue()+",":"";
        st += (alarm7 & Math.round(Math.pow(2, 3))) > 0 ? CloudConstants.additionalarm7.engineColdStart.getValue()+",":"";
        st += (alarm7 & Math.round(Math.pow(2, 4))) > 0 ? CloudConstants.additionalarm7.badDrivingHabits.getValue()+",":"";
        st += (alarm7 & Math.round(Math.pow(2, 6))) > 0 ? CloudConstants.additionalarm7.idNotMatch.getValue()+",":"";
        st += (alarm7 & Math.round(Math.pow(2, 7))) > 0 ? CloudConstants.additionalarm7.terminalRemove.getValue()+",":"";
        st += (alarm8 & 1) > 0 ? CloudConstants.additionalarm8.airCondition.getValue()+",":"";
        st += (alarm8 & Math.round(Math.pow(2, 1))) > 0 ? CloudConstants.additionalarm8.handBrake.getValue()+",":"";
        st += (alarm8 & Math.round(Math.pow(2, 2))) > 0 ? CloudConstants.additionalarm8.gpsActivation.getValue()+",":"";
        st += (alarm8 & Math.round(Math.pow(2, 3))) > 0 ? CloudConstants.additionalarm8.handshake.getValue()+",":"";
        st += (alarm8 & Math.round(Math.pow(2, 4))) > 0 ? CloudConstants.additionalarm8.lockVehicle.getValue()+",":"";
        st += (alarm8 & Math.round(Math.pow(2, 5))) > 0 ? CloudConstants.additionalarm8.keyMatch.getValue()+",":"";
        st += (alarm8 & Math.round(Math.pow(2, 6))) > 0 ? CloudConstants.additionalarm8.gpsIdMatch.getValue()+",":"";
        st += (alarm8 & Math.round(Math.pow(2, 7))) > 0 ? CloudConstants.additionalarm8.ecuAnticipation.getValue()+",":"";
        if(!"".equals(st)){
            return st.substring(0,st.length()-1);
        }
        return st;
    }

    //位置云指令状态转换成寰游指令状态，与sl版本一致,暂时只处理成功，失败的转换
		/*
		 *  failed = 0,                 //失败（超时/丢包）
	        success = 1,                //成功/确认
	        messageError = 2,           //消息有误
	        terminalNotSupport = 3,     //终端不支持该消息
	        receivingData = 4,          //接收数据中
            terminalOffLine = 5,        //终端离线
	        alarmHandle = 6,            //报警处理确认
	        notRegister = 7,            //终端未注册
	        waitExcuteResult = 8,       //等待车辆执行结果
		 */
    public static String statueToHyStatue(String statue){
        if(statue.equals("0")){ // 成功
            statue = "1";
        }else if(statue.equals("1")){ //失败
            statue = "0";
        }
        return statue;
    }

    /**
     * 剩余油量
     * @param gpslist LCLocationData.LocationData
     * @return 油量（百分比）
     */
    public static long getResOil(LCLocationData.LocationData gpslist){
        //返回结果
        long result = 0;
        //车辆状态数据
        if(gpslist.getStatusAddition() == null){
            return result;
        }
        List<LCVehicleStatusData.VehicleStatusData> statusList = gpslist.getStatusAddition().getStatusList();
        if(ListUtil.isEmpty(statusList)){
            return result;
        }
        //柴油车
        for (LCVehicleStatusData.VehicleStatusData vehicleStatusData : statusList)
        {
            if (vehicleStatusData.getTypes().getNumber() == LCStatusType.StatusType.oilValue_VALUE)
            {
                result = vehicleStatusData.getStatusValue();
                break;
            }
        }
        return result;
    }
    /**
     * 剩余汽量
     * @param gpslist LCLocationData.LocationData
     * @return 汽量（实际值）
     */
    public static double getResGas(LCLocationData.LocationData gpslist){
        //返回结果
        long result = 0;
        //车辆状态数据
        if(gpslist.getStatusAddition() == null){
            return result;
        }
        List<LCVehicleStatusData.VehicleStatusData> statusList = gpslist.getStatusAddition().getStatusList();
        if(ListUtil.isEmpty(statusList)){
            return result;
        }
        //气瓶1（剩余）
        long lng1Surplus = 0;
        //气瓶2（剩余）
        long lng2Surplus = 0;
        //气瓶3（剩余）
        long lng3Surplus = 0;
        //气瓶4（剩余）
        long lng4Surplus = 0;
        //循环取值
        for (LCVehicleStatusData.VehicleStatusData status : statusList)
        {
            if(status.getTypes() == null){
                continue;
            }
            int t = status.getTypes().getNumber();
            if (t == LCStatusType.StatusType.lng1Surplus_VALUE)
            {
                lng1Surplus = status.getStatusValue();
                continue;
            }

            if (t == LCStatusType.StatusType.lng2Surplus_VALUE)
            {
                lng2Surplus = status.getStatusValue();
                continue;
            }
            if (t == LCStatusType.StatusType.lng3Surplus_VALUE)
            {
                lng3Surplus = status.getStatusValue();
                continue;
            }
            if (t == LCStatusType.StatusType.lng4Surplus_VALUE)
            {
                lng4Surplus = status.getStatusValue();
                continue;
            }
        }
        result = lng1Surplus+lng2Surplus+lng3Surplus+lng4Surplus;
        return NumberFormatUtil.formatNumber(result/100, 2);
    }
    /**
     * 总汽量计算
     * @param gpslist LCLocationData.LocationData
     * @return 总汽量（实际值）
     */
    public static double getTotalGas(LCLocationData.LocationData gpslist){
        //返回结果
        long result = 0;
        //车辆状态数据
        if(gpslist.getStatusAddition() == null){
            return result;
        }
        List<LCVehicleStatusData.VehicleStatusData> statusList = gpslist.getStatusAddition().getStatusList();
        if(ListUtil.isEmpty(statusList)){
            return result;
        }
        //气瓶1（总容量）
        long lng1Gross = 0;
        //气瓶2（总容量）
        long lng2Gross = 0;
        //气瓶3（总容量）
        long lng3Gross = 0;
        //气瓶4（总容量）
        long lng4Gross = 0;
        //天然气车
        for (LCVehicleStatusData.VehicleStatusData status : statusList)
        {
            int t = status.getTypes().getNumber();
            if (t == LCStatusType.StatusType.lng1Gross_VALUE)
            {
                lng1Gross = status.getStatusValue();
                continue;
            }

            if (t == LCStatusType.StatusType.lng2Gross_VALUE)
            {
                lng2Gross = status.getStatusValue();
                continue;
            }
            if (t == LCStatusType.StatusType.lng3Gross_VALUE)
            {
                lng3Gross = status.getStatusValue();
                continue;
            }
            if (t == LCStatusType.StatusType.lng4Gross_VALUE)
            {
                lng4Gross = status.getStatusValue();
                continue;
            }
        }
        result = lng1Gross + lng2Gross + lng3Gross + lng4Gross;
        return NumberFormatUtil.formatNumber(result/100, 2);
    }
    /**
     * 汽量实际消耗量计算
     * @param gpslist LCLocationData.LocationData
     * @return 消耗总汽量（实际值）
     */
    public static double getUseGas(LCLocationData.LocationData gpslist){
        //返回结果
        long result = 0;
        //车辆状态数据
        if(gpslist.getStatusAddition() == null){
            return result;
        }
        List<LCVehicleStatusData.VehicleStatusData> statusList = gpslist.getStatusAddition().getStatusList();
        if(ListUtil.isEmpty(statusList)){
            return result;
        }
        //气瓶1（剩余）
        long lng1Surplus = 0;
        //气瓶2（剩余）
        long lng2Surplus = 0;
        //气瓶3（剩余）
        long lng3Surplus = 0;
        //气瓶4（剩余）
        long lng4Surplus = 0;
        //气瓶1（总容量）
        long lng1Gross = 0;
        //气瓶2（总容量）
        long lng2Gross = 0;
        //气瓶3（总容量）
        long lng3Gross = 0;
        //气瓶4（总容量）
        long lng4Gross = 0;
        //天然气车
        for (LCVehicleStatusData.VehicleStatusData status : statusList)
        {
            int t = status.getTypes().getNumber();
            if (t == LCStatusType.StatusType.lng1Gross_VALUE)
            {
                lng1Gross = status.getStatusValue();
                continue;
            }

            if (t == LCStatusType.StatusType.lng2Gross_VALUE)
            {
                lng2Gross = status.getStatusValue();
                continue;
            }
            if (t == LCStatusType.StatusType.lng3Gross_VALUE)
            {
                lng3Gross = status.getStatusValue();
                continue;
            }
            if (t == LCStatusType.StatusType.lng4Gross_VALUE)
            {
                lng4Gross = status.getStatusValue();
                continue;
            }
            if (t == LCStatusType.StatusType.lng1Surplus_VALUE)
            {
                lng1Surplus = status.getStatusValue();
                continue;
            }

            if (t == LCStatusType.StatusType.lng2Surplus_VALUE)
            {
                lng2Surplus = status.getStatusValue();
                continue;
            }
            if (t == LCStatusType.StatusType.lng3Surplus_VALUE)
            {
                lng3Surplus = status.getStatusValue();
                continue;
            }
            if (t == LCStatusType.StatusType.lng4Surplus_VALUE)
            {
                lng4Surplus = status.getStatusValue();
                continue;
            }
        }
        result = (lng1Gross + lng2Gross + lng3Gross + lng4Gross)-(lng1Surplus+lng2Surplus+lng3Surplus+lng4Surplus);
        return NumberFormatUtil.formatNumber(result/100, 2);
    }
}
