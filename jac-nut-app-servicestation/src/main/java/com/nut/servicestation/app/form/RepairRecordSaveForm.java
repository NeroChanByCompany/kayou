package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@NutFormValidator
public class RepairRecordSaveForm extends BaseForm {

    /**
     * 工单号
     */
    @NotNull(message = "工单号不能为空")
    @NotBlank(message = "工单号不能为空")
    private String woCode;

    @NotNull(message = "维修ID不能为空")
    @NotBlank(message = "维修ID不能为空")
    private String operateId;

    /**
     * 故障描述
     */
    @Length(max = 300,message = "故障简述最多可输入300个字！")
    private String faultDescribe;

    /**
     * 处理方式（见字典“处理方式”，服务类型为保修 或 费用类型为“保内”时必填）
     */
    @Pattern(regexp = "^[12]?$", message = "处理方式格式不正确")
    private String dealType;

    @NotNull(message = "上传图片总数量不能为空")
    @NotBlank(message = "上传图片总数量不能为空")
    private String photoNum;

    /**
     * 服务类型（见字典“服务类型”，进出站工单必填）
     */
    @Pattern(regexp = "^[1-4]?$", message = "服务类型格式不正确")
    private String serviceType;

    /**
     * 费用类型（见字典“费用类型”，外出救援工单必填）
     */
    @Pattern(regexp = "^[12]?$", message = "费用类型格式不正确")
    private String chargeType;

    /**
     * 付费方式（见字典“付费方式”，费用类型为“保外”时必填）
     */
    @Pattern(regexp = "^[12]?$", message = "付费方式格式不正确")
    private String payType;

    /**
     * 服务类型或费用类型
     * 对应前端展示的一级选项（服务类型和费用类型糅合到一个字段）
     */
    @Pattern(regexp = "^[0-9]*$", message = "服务类型或费用类型只能为数字")
    private String type1;

    /**
     * 处理方式或付费方式
     * 对应前端展示的二级选项（处理方式和付费方式糅合到一个字段）
     */
    @Pattern(regexp = "^[0-9]*$", message = "处理方式或付费方式只能为数字")
    private String type2;
//    @NotNull(message = "设备Id不能为空")
//    @NotBlank(message = "设备Id不能为空")
    private String deviceId;

    private List<PhotoPartForm> partCodeList;

    /**
     * 提交标识
     */
    @Pattern(regexp = "^[123]?$", message = "提交标识格式不正确")
    private String commitMark;

}
