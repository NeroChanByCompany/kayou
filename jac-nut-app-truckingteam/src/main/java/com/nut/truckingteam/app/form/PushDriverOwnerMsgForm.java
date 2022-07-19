package com.nut.truckingteam.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 供司机app/车队app/车队web调用的司机车队相关的推送接口
 */
@Data
@NutFormValidator
public class PushDriverOwnerMsgForm extends BaseForm {

    /**
     * 参考IxinPushStaticLocarVal.java stype
     */
    @NotBlank(message = "stype不能为空")
    private String stype;

    private Long managerId;
    private String carId;
    private String carNum;
    private String driverId;
    private String oldCarNum;
    private String oldTeamName;
    private String receiverId;
    private String senderId;
    private Long teamId;
    private String teamName;
    private List<String> carIdList;
    private String role;


    @Override
    public String toString() {
        return "PushDriverOwnerMsgCommand{" +
                "stype='" + stype + '\'' +
                ", managerId=" + managerId +
                ", carId='" + carId + '\'' +
                ", carNum='" + carNum + '\'' +
                ", driverId='" + driverId + '\'' +
                ", oldCarNum='" + oldCarNum + '\'' +
                ", oldTeamName='" + oldTeamName + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", teamId=" + teamId +
                ", teamName='" + teamName + '\'' +
                ", carIdList=" + carIdList +
                ", role='" + role + '\'' +
                '}';
    }

}
