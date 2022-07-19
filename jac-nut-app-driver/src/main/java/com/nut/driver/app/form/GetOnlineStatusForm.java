package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author liuBing
 * @Classname GetOnlineStatusForm
 * @Description TODO
 * @Date 2021/6/24 19:37
 */
@Data
public class GetOnlineStatusForm extends BaseForm {

    /**
     * 通信号数组
     */
    private List<Long> tidList;

    /**
     * vin数组
     */
    private List<String> vinList;

    /**
     * 查询方式（true：按通信号，false：按vin。）
     */
    @NotNull(message = "查询方式不能为空")
    private Boolean sourceFlag;

    /**
     * 是否是高级查询（true：包含今日里程和今日油耗，false：普通查询。）
     */
    private Boolean flagStatus;
}
