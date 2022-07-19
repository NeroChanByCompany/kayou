package com.nut.servicestation.app.form;


import com.nut.common.base.BaseForm;
import lombok.Data;

/**
 * 外出救援调件
 */
@Data
public class RescueTransferPartsListForm extends BaseForm {
    /* List参数用ValidateUtil校验并发cpu占用高，改手写校验 */

//    @NotBlank(message = "配件零件号至少6个字符，最多30个字！", groups = {GroupCommand.class})
//    @Length(min = 6, max = 30, message = "配件零件号至少6个字符，最多30个字！", groups = {GroupCommand.class})
    /** 配件零件号 */
    private String partsNo;

//    @NotBlank(message = "配件名称至少3个字，最多20个字！", groups = {GroupCommand.class})
//    @Length(min = 3, max = 20, message = "配件名称至少3个字，最多20个字！", groups = {GroupCommand.class})
    /** 配件名称 */
    private String partsName;

//    @NotBlank(message = "配件数量必须为大于等于1，小于10000的整数！", groups = {GroupCommand.class})
//    @Pattern(regexp = "^[1-9]\\d{0,3}$", message = "配件数量必须为大于等于1，小于10000的整数！", groups = {GroupCommand.class})
    /** 配件数量 */
    private String partsNum;

//    @NotBlank(message = "请选择是否纯正配件！", groups = {GroupCommand.class})
//    @Pattern(regexp = "^[01]$", message = "是否纯正配件格式不正确", groups = {GroupCommand.class})
    /** 是否纯正配件 0：否；1：是 */
    private String partsFlag;

}
