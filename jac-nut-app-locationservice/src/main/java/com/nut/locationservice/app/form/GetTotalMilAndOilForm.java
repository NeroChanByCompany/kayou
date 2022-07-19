package com.nut.locationservice.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description: 当日运营里程、油耗和车辆借口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.form
 * @Author: yzl
 * @CreateTime: 2021-06-17 13:34
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
@ToString
public class GetTotalMilAndOilForm extends BaseForm implements Serializable {
}
