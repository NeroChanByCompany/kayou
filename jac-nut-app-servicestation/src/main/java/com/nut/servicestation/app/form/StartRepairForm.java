package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 检查结束/开始维修接口
 * Created by Administrator on 2018/5/10.
 */
@Data
@NutFormValidator
public class StartRepairForm extends BaseForm {

    /** 工单号 */
    @NotBlank(message = "工单号不能为空")
    private String woCode;

    /** 预检信息 */
    @NotBlank(message = "预检信息至少输入5个字")
    @Length(min = 5, max = 300, message = "预检信息至少输入5个字最多300个字")
    private String inspectResult;

    /** 操作唯一标识 */
    @NotBlank(message = "操作唯一标识不能为空")
    private String operateId;

    /** 照片总数量 */
    @NotNull(message = "照片总数量不能为空")
    private Integer photoNum;

    /** 预计完工时间 */
//    @NotNull(message = "预计完工时间不能为空")
    private Long estimateTime;

    /** 预估费用 */
//    @NotBlank(message = "预估费用不能为空")
    @Pattern(regexp = "^[1-9]\\d{0,19}$", message = "预估费用需大于0的整数")
    private String estimateFee;

    /** 指派人员ID*/
    private String assignTo;

}
